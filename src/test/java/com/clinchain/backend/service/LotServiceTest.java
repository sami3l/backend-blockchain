package com.clinchain.backend.service;

import com.clinchain.backend.blockchain.SupplyChainGateway;
import com.clinchain.backend.dto.*;
import com.clinchain.backend.exception.InsufficientQuantityException;
import com.clinchain.backend.exception.ResourceNotFoundException;
import com.clinchain.backend.exception.UnauthorizedException;
import com.clinchain.backend.model.Lot;
import com.clinchain.backend.model.LotStatus;
import com.clinchain.backend.model.User;
import com.clinchain.backend.model.UserRole;
import com.clinchain.backend.repository.LotRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LotServiceTest {

    @Mock
    private LotRepository lotRepository;

    @Mock
    private SupplyChainGateway supplyChainGateway;

    @Mock
    private UserService userService;

    @InjectMocks
    private LotService lotService;

    private Lot testLot;
    private User grossisteUser;
    private User hospitaleUser;
    private User pharmacienUser;
    private User infirmierUser;

    @BeforeEach
    void setUp() {
        testLot = Lot.builder()
                .id("lot-123")
                .medName("Paracetamol")
                .quantity(100)
                .status(LotStatus.CREE_PAR_GROSSISTE)
                .createdBy("grossiste")
                .validated(false)
                .createdAt(LocalDateTime.now())
                .build();

        grossisteUser = User.builder()
                .id("1")
                .username("grossiste")
                .role(UserRole.GROSSISTE)
                .build();

        hospitaleUser = User.builder()
                .id("2")
                .username("hopital")
                .role(UserRole.HOPITALE)
                .build();

        pharmacienUser = User.builder()
                .id("3")
                .username("pharmacien")
                .role(UserRole.PHARMACIEN)
                .build();

        infirmierUser = User.builder()
                .id("4")
                .username("infirmier")
                .role(UserRole.INFIRMIER)
                .build();
    }

    @Test
    void testGetAllLots() {
        // Arrange
        List<Lot> lots = Arrays.asList(testLot);
        when(lotRepository.findAllByOrderByCreatedAtDesc()).thenReturn(lots);

        // Act
        List<Lot> result = lotService.getAllLots();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("lot-123", result.get(0).getId());
        verify(lotRepository).findAllByOrderByCreatedAtDesc();
    }

    @Test
    void testGetLotById_Success() {
        // Arrange
        when(lotRepository.findById("lot-123")).thenReturn(Optional.of(testLot));

        // Act
        Lot result = lotService.getLotById("lot-123");

        // Assert
        assertNotNull(result);
        assertEquals("lot-123", result.getId());
        verify(lotRepository).findById("lot-123");
    }

    @Test
    void testGetLotById_NotFound() {
        // Arrange
        when(lotRepository.findById("non-existent")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            lotService.getLotById("non-existent");
        });
    }

    @Test
    void testGetAllLotsPaginated() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Lot> lotsPage = new PageImpl<>(Arrays.asList(testLot), pageable, 1);
        when(lotRepository.findAllByOrderByCreatedAtDesc(pageable)).thenReturn(lotsPage);

        // Act
        Page<Lot> result = lotService.getAllLotsPaginated(pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("lot-123", result.getContent().get(0).getId());
    }

    @Test
    void testSearchLots() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Lot> lotsPage = new PageImpl<>(Arrays.asList(testLot), pageable, 1);
        when(lotRepository.findWithFilters(eq(LotStatus.CREE_PAR_GROSSISTE), eq("grossiste"),
                eq("Paracetamol"), eq(pageable))).thenReturn(lotsPage);

        // Act
        Page<Lot> result = lotService.searchLots(LotStatus.CREE_PAR_GROSSISTE, "grossiste",
                "Paracetamol", pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void testGetStats() {
        // Arrange
        when(lotRepository.count()).thenReturn(10L);
        when(lotRepository.countByStatus(LotStatus.CREE_PAR_GROSSISTE)).thenReturn(3L);
        when(lotRepository.countByStatus(LotStatus.VALIDE_PAR_HOPITAL)).thenReturn(4L);
        when(lotRepository.countByStatus(LotStatus.EN_STOCK_PHARMACIE)).thenReturn(2L);
        when(lotRepository.countByStatus(LotStatus.ADMINISTRE)).thenReturn(1L);
        when(lotRepository.sumTotalQuantity()).thenReturn(500);

        // Act
        LotStatsDto stats = lotService.getStats();

        // Assert
        assertNotNull(stats);
        assertEquals(10L, stats.getTotalLots());
        assertEquals(3L, stats.getCreatedLots());
        assertEquals(4L, stats.getValidatedLots());
        assertEquals(2L, stats.getInStockLots());
        assertEquals(1L, stats.getAdministeredLots());
        assertEquals(500, stats.getTotalQuantity());
    }

    @Test
    void testCreateLot_Success() {
        // Arrange
        CreateLotRequest request = new CreateLotRequest();
        request.setMedName("Ibuprofen");
        request.setQuantity(50);
        request.setCreatedBy("grossiste");

        when(userService.findByUsername("grossiste")).thenReturn(grossisteUser);
        when(lotRepository.save(any(Lot.class))).thenReturn(testLot);

        // Act
        Lot result = lotService.createLot(request);

        // Assert
        assertNotNull(result);
        verify(userService).findByUsername("grossiste");
        verify(lotRepository).save(any(Lot.class));
        verify(supplyChainGateway).creerLot(anyString(), anyString(), eq(UserRole.GROSSISTE));
    }

    @Test
    void testCreateLot_WrongRole() {
        // Arrange
        CreateLotRequest request = new CreateLotRequest();
        request.setMedName("Ibuprofen");
        request.setQuantity(50);
        request.setCreatedBy("pharmacien");

        when(userService.findByUsername("pharmacien")).thenReturn(pharmacienUser);

        // Act & Assert
        assertThrows(UnauthorizedException.class, () -> {
            lotService.createLot(request);
        });
    }

    @Test
    void testValidateLot_Success() {
        // Arrange
        ValidateRequest request = new ValidateRequest();
        request.setActor("hopital");

        when(lotRepository.findById("lot-123")).thenReturn(Optional.of(testLot));
        when(userService.findByUsername("hopital")).thenReturn(hospitaleUser);
        when(lotRepository.save(any(Lot.class))).thenReturn(testLot);

        // Act
        Lot result = lotService.validateLot("lot-123", request);

        // Assert
        assertNotNull(result);
        verify(supplyChainGateway).validerReceptionHopital("lot-123", UserRole.HOPITALE);
    }

    @Test
    void testValidateLot_WrongRole() {
        // Arrange
        ValidateRequest request = new ValidateRequest();
        request.setActor("grossiste");

        when(lotRepository.findById("lot-123")).thenReturn(Optional.of(testLot));
        when(userService.findByUsername("grossiste")).thenReturn(grossisteUser);

        // Act & Assert
        assertThrows(UnauthorizedException.class, () -> {
            lotService.validateLot("lot-123", request);
        });
    }

    @Test
    void testWithdrawFromLot_Success() {
        // Arrange
        WithdrawRequest request = new WithdrawRequest();
        request.setQty(30);
        request.setActor("grossiste");

        when(lotRepository.findById("lot-123")).thenReturn(Optional.of(testLot));
        when(lotRepository.save(any(Lot.class))).thenReturn(testLot);

        // Act
        Lot result = lotService.withdrawFromLot("lot-123", request);

        // Assert
        assertNotNull(result);
        verify(lotRepository).save(any(Lot.class));
    }

    @Test
    void testWithdrawFromLot_InsufficientQuantity() {
        // Arrange
        WithdrawRequest request = new WithdrawRequest();
        request.setQty(150); // More than available
        request.setActor("grossiste");

        when(lotRepository.findById("lot-123")).thenReturn(Optional.of(testLot));

        // Act & Assert
        assertThrows(InsufficientQuantityException.class, () -> {
            lotService.withdrawFromLot("lot-123", request);
        });
    }

    @Test
    void testMarkInPharmacy_Success() {
        // Arrange
        ActorRequest request = new ActorRequest();
        request.setActor("pharmacien");

        testLot.setStatus(LotStatus.VALIDE_PAR_HOPITAL);
        when(lotRepository.findById("lot-123")).thenReturn(Optional.of(testLot));
        when(userService.findByUsername("pharmacien")).thenReturn(pharmacienUser);
        when(lotRepository.save(any(Lot.class))).thenReturn(testLot);

        // Act
        Lot result = lotService.markInPharmacy("lot-123", request);

        // Assert
        assertNotNull(result);
        verify(supplyChainGateway).mettreEnPharmacie("lot-123", UserRole.PHARMACIEN);
    }

    @Test
    void testMarkInPharmacy_WrongStatus() {
        // Arrange
        ActorRequest request = new ActorRequest();
        request.setActor("pharmacien");

        // Lot is still in CREE_PAR_GROSSISTE status
        when(lotRepository.findById("lot-123")).thenReturn(Optional.of(testLot));
        when(userService.findByUsername("pharmacien")).thenReturn(pharmacienUser);

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> {
            lotService.markInPharmacy("lot-123", request);
        });
    }

    @Test
    void testAdministerLot_Success() {
        // Arrange
        ActorRequest request = new ActorRequest();
        request.setActor("infirmier");

        testLot.setStatus(LotStatus.EN_STOCK_PHARMACIE);
        when(lotRepository.findById("lot-123")).thenReturn(Optional.of(testLot));
        when(userService.findByUsername("infirmier")).thenReturn(infirmierUser);
        when(lotRepository.save(any(Lot.class))).thenReturn(testLot);

        // Act
        Lot result = lotService.administerLot("lot-123", request);

        // Assert
        assertNotNull(result);
        verify(supplyChainGateway).administrerPatient("lot-123", UserRole.INFIRMIER);
    }

    @Test
    void testAddHistoryToLot_Success() {
        // Arrange
        AddHistoryRequest request = new AddHistoryRequest();
        request.setAction("Test action");
        request.setActor("grossiste");

        when(lotRepository.findById("lot-123")).thenReturn(Optional.of(testLot));
        when(lotRepository.save(any(Lot.class))).thenReturn(testLot);

        // Act
        Lot result = lotService.addHistoryToLot("lot-123", request);

        // Assert
        assertNotNull(result);
        verify(lotRepository).save(any(Lot.class));
    }
}
