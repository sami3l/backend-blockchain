package com.clinchain.backend.service;

import com.clinchain.backend.blockchain.SupplyChainGateway;
import com.clinchain.backend.dto.*;
import com.clinchain.backend.exception.InsufficientQuantityException;
import com.clinchain.backend.exception.ResourceNotFoundException;
import com.clinchain.backend.exception.UnauthorizedException;
import com.clinchain.backend.model.Lot;
import com.clinchain.backend.model.LotHistory;
import com.clinchain.backend.model.LotStatus;
import com.clinchain.backend.model.User;
import com.clinchain.backend.model.UserRole;
import com.clinchain.backend.repository.LotRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class LotService {

    private final LotRepository lotRepository;
    private final SupplyChainGateway supplyChainGateway;
    private final UserService userService;

    public List<Lot> getAllLots() {
        return lotRepository.findAllByOrderByCreatedAtDesc();
    }

    public Lot getLotById(String lotId) {
        return findById(lotId);
    }

    public Page<Lot> getAllLotsPaginated(Pageable pageable) {
        return lotRepository.findAllByOrderByCreatedAtDesc(pageable);
    }

    public Page<Lot> searchLots(LotStatus status, String createdBy, String medName, Pageable pageable) {
        return lotRepository.findWithFilters(status, createdBy, medName, pageable);
    }

    public LotStatsDto getStats() {
        long totalLots = lotRepository.count();
        long createdLots = lotRepository.countByStatus(LotStatus.CREE_PAR_GROSSISTE);
        long validatedLots = lotRepository.countByStatus(LotStatus.VALIDE_PAR_HOPITAL);
        long inStockLots = lotRepository.countByStatus(LotStatus.EN_STOCK_PHARMACIE);
        long administeredLots = lotRepository.countByStatus(LotStatus.ADMINISTRE);
        Integer totalQuantity = lotRepository.sumTotalQuantity();

        return LotStatsDto.builder()
                .totalLots(totalLots)
                .createdLots(createdLots)
                .validatedLots(validatedLots)
                .inStockLots(inStockLots)
                .administeredLots(administeredLots)
                .totalQuantity(totalQuantity != null ? totalQuantity : 0)
                .build();
    }

    public BlockchainLotDto getBlockchainState(String lotId) {
        Lot lot = findById(lotId);
        BlockchainLotDto blockchainLot = supplyChainGateway.getLotFromBlockchain(lotId);

        // Compare blockchain status with database
        boolean synced = lot.getStatus().ordinal() == blockchainLot.getBlockchainStatus();
        blockchainLot.setSyncedWithDatabase(synced);

        return blockchainLot;
    }

    @Transactional
    public Lot createLot(CreateLotRequest request) {
        User creator = resolveUser(request.getCreatedBy());
        ensureRole(creator.getRole(), UserRole.GROSSISTE, "création de lot");

        Lot lot = Lot.builder()
                .id(UUID.randomUUID().toString())
                .medName(request.getMedName())
                .quantity(request.getQuantity())
                .validated(false)
                .status(LotStatus.CREE_PAR_GROSSISTE)
                .createdBy(request.getCreatedBy())
                .createdAt(LocalDateTime.now())
                .build();

        addHistory(lot, "Lot créé", request.getCreatedBy());

        Lot savedLot = lotRepository.save(lot);
        supplyChainGateway.creerLot(savedLot.getId(), savedLot.getMedName(), creator.getRole());

        log.info("Created new lot: {} with quantity: {}", savedLot.getId(), savedLot.getQuantity());
        return savedLot;
    }

    @Transactional
    public Lot validateLot(String lotId, ValidateRequest request) {
        Lot lot = findById(lotId);
        User validator = resolveUser(request.getActor());
        ensureRole(validator.getRole(), UserRole.HOPITALE, "validation par l'hôpital");
        validateStatusTransition(lot.getStatus(), LotStatus.VALIDE_PAR_HOPITAL);

        lot.setValidated(true);
        lot.setStatus(LotStatus.VALIDE_PAR_HOPITAL);

        addHistory(lot, "Lot validé", request.getActor());

        Lot savedLot = lotRepository.save(lot);
        supplyChainGateway.validerReceptionHopital(savedLot.getId(), validator.getRole());

        log.info("Validated lot: {} by {}", lotId, request.getActor());
        return savedLot;
    }

    @Transactional
    public Lot withdrawFromLot(String lotId, WithdrawRequest request) {
        Lot lot = findById(lotId);

        if (lot.getQuantity() < request.getQty()) {
            throw new InsufficientQuantityException(
                    "Not enough quantity. Available: " + lot.getQuantity() + ", Requested: " + request.getQty());
        }

        lot.setQuantity(lot.getQuantity() - request.getQty());

        addHistory(lot, "Retrait de " + request.getQty() + " unités", request.getActor());

        log.info("Withdrew {} units from lot: {}. Remaining: {}", request.getQty(), lotId, lot.getQuantity());
        return lotRepository.save(lot);
    }

    @Transactional
    public Lot markInPharmacy(String lotId, ActorRequest request) {
        Lot lot = findById(lotId);
        User pharmacist = resolveUser(request.getActor());
        ensureRole(pharmacist.getRole(), UserRole.PHARMACIEN, "mise en stock pharmacie");
        validateStatusTransition(lot.getStatus(), LotStatus.EN_STOCK_PHARMACIE);
        lot.setStatus(LotStatus.EN_STOCK_PHARMACIE);

        addHistory(lot, "Lot mis en stock pharmacie", request.getActor());

        Lot savedLot = lotRepository.save(lot);
        supplyChainGateway.mettreEnPharmacie(savedLot.getId(), pharmacist.getRole());

        log.info("Marked lot {} as in pharmacy stock by {}", lotId, request.getActor());
        return savedLot;
    }

    @Transactional
    public Lot administerLot(String lotId, ActorRequest request) {
        Lot lot = findById(lotId);
        User nurse = resolveUser(request.getActor());
        ensureRole(nurse.getRole(), UserRole.INFIRMIER, "administration patient");
        validateStatusTransition(lot.getStatus(), LotStatus.ADMINISTRE);
        lot.setStatus(LotStatus.ADMINISTRE);

        addHistory(lot, "Lot administré au patient", request.getActor());

        Lot savedLot = lotRepository.save(lot);
        supplyChainGateway.administrerPatient(savedLot.getId(), nurse.getRole());

        log.info("Administered lot {} by {}", lotId, request.getActor());
        return savedLot;
    }

    @Transactional
    public Lot addHistoryToLot(String lotId, AddHistoryRequest request) {
        Lot lot = findById(lotId);

        addHistory(lot, request.getAction(), request.getActor());

        log.info("Added history to lot: {} - {}", lotId, request.getAction());
        return lotRepository.save(lot);
    }

    private Lot findById(String id) {
        return lotRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lot not found: " + id));
    }

    private void addHistory(Lot lot, String action, String actor) {
        LotHistory history = LotHistory.builder()
                .lot(lot)
                .action(action)
                .actor(actor)
                .timestamp(LocalDateTime.now())
                .build();

        lot.addHistory(history);
    }

    private User resolveUser(String username) {
        return userService.findByUsername(username);
    }

    private void ensureRole(UserRole actual, UserRole expected, String actionName) {
        if (actual != expected) {
            throw new UnauthorizedException("Role " + actual + " cannot effectuer " + actionName);
        }
    }

    private void validateStatusTransition(LotStatus current, LotStatus next) {
        switch (next) {
            case VALIDE_PAR_HOPITAL:
                if (current != LotStatus.CREE_PAR_GROSSISTE) {
                    throw new IllegalStateException("Lot doit être en statut CREE_PAR_GROSSISTE pour être validé");
                }
                break;
            case EN_STOCK_PHARMACIE:
                if (current != LotStatus.VALIDE_PAR_HOPITAL) {
                    throw new IllegalStateException("Lot doit être validé par l'hôpital avant mise en stock pharmacie");
                }
                break;
            case ADMINISTRE:
                if (current != LotStatus.EN_STOCK_PHARMACIE) {
                    throw new IllegalStateException("Lot doit être en stock pharmacie avant administration");
                }
                break;
            default:
                break;
        }
    }
}
