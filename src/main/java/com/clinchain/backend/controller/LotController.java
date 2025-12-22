package com.clinchain.backend.controller;

import com.clinchain.backend.dto.*;
import com.clinchain.backend.model.Lot;
import com.clinchain.backend.model.LotStatus;
import com.clinchain.backend.service.LotService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/lots")
@RequiredArgsConstructor
public class LotController {

    private final LotService lotService;

    @GetMapping
    public ResponseEntity<List<Lot>> getAllLots(
            @RequestParam(required = false) LotStatus status,
            @RequestParam(required = false) String createdBy,
            @RequestParam(required = false) String medName,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {

        if (page != null && size != null) {
            Pageable pageable = PageRequest.of(page, size);
            Page<Lot> lotsPage;

            if (status != null || createdBy != null || medName != null) {
                lotsPage = lotService.searchLots(status, createdBy, medName, pageable);
            } else {
                lotsPage = lotService.getAllLotsPaginated(pageable);
            }

            return ResponseEntity.ok()
                    .header("X-Total-Count", String.valueOf(lotsPage.getTotalElements()))
                    .header("X-Total-Pages", String.valueOf(lotsPage.getTotalPages()))
                    .body(lotsPage.getContent());
        }

        List<Lot> lots = lotService.getAllLots();
        return ResponseEntity.ok(lots);
    }

    @GetMapping("/{lotId}")
    public ResponseEntity<Lot> getLotById(@PathVariable String lotId) {
        Lot lot = lotService.getLotById(lotId);
        return ResponseEntity.ok(lot);
    }

    @GetMapping("/{lotId}/blockchain")
    public ResponseEntity<BlockchainLotDto> getBlockchainState(@PathVariable String lotId) {
        BlockchainLotDto blockchainState = lotService.getBlockchainState(lotId);
        return ResponseEntity.ok(blockchainState);
    }

    @GetMapping("/stats")
    public ResponseEntity<LotStatsDto> getStats() {
        LotStatsDto stats = lotService.getStats();
        return ResponseEntity.ok(stats);
    }

    @PostMapping
    @PreAuthorize("hasRole('GROSSISTE')")
    public ResponseEntity<Lot> createLot(@Valid @RequestBody CreateLotRequest request) {
        Lot lot = lotService.createLot(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(lot);
    }

    @PostMapping("/{lotId}/validate")
    @PreAuthorize("hasRole('HOPITALE')")
    public ResponseEntity<Lot> validateLot(@PathVariable String lotId,
            @Valid @RequestBody ValidateRequest request) {
        Lot lot = lotService.validateLot(lotId, request);
        return ResponseEntity.ok(lot);
    }

    @PostMapping("/{lotId}/withdraw")
    @PreAuthorize("hasRole('PHARMACIEN')")
    public ResponseEntity<Lot> withdrawFromLot(@PathVariable String lotId,
            @Valid @RequestBody WithdrawRequest request) {
        Lot lot = lotService.withdrawFromLot(lotId, request);
        return ResponseEntity.ok(lot);
    }

    @PostMapping("/{lotId}/stock")
    @PreAuthorize("hasRole('PHARMACIEN')")
    public ResponseEntity<Lot> markInPharmacy(@PathVariable String lotId,
            @Valid @RequestBody ActorRequest request) {
        Lot lot = lotService.markInPharmacy(lotId, request);
        return ResponseEntity.ok(lot);
    }

    @PostMapping("/{lotId}/administer")
    @PreAuthorize("hasRole('INFIRMIER')")
    public ResponseEntity<Lot> administerLot(@PathVariable String lotId,
            @Valid @RequestBody ActorRequest request) {
        Lot lot = lotService.administerLot(lotId, request);
        return ResponseEntity.ok(lot);
    }

    @PostMapping("/{lotId}/history")
    @PreAuthorize("hasAnyRole('PHARMACIEN', 'INFIRMIER')")
    public ResponseEntity<Lot> addHistory(@PathVariable String lotId,
            @Valid @RequestBody AddHistoryRequest request) {
        Lot lot = lotService.addHistoryToLot(lotId, request);
        return ResponseEntity.ok(lot);
    }
}
