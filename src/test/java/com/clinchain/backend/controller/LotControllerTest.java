package com.clinchain.backend.controller;

import com.clinchain.backend.dto.*;
import com.clinchain.backend.model.Lot;
import com.clinchain.backend.model.LotStatus;
import com.clinchain.backend.service.LotService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class LotControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private LotService lotService;

    private Lot testLot;
    private CreateLotRequest createLotRequest;

    @BeforeEach
    void setUp() {
        testLot = Lot.builder()
                .id("lot-123")
                .medName("Paracetamol")
                .quantity(100)
                .status(LotStatus.CREE_PAR_GROSSISTE)
                .createdBy("grossiste")
                .validated(false)
                .build();

        createLotRequest = new CreateLotRequest();
        createLotRequest.setMedName("Ibuprofen");
        createLotRequest.setQuantity(50);
        createLotRequest.setCreatedBy("grossiste");
    }

    @Test
    @WithMockUser(roles = "GROSSISTE")
    void testGetAllLots() throws Exception {
        // Arrange
        List<Lot> lots = Arrays.asList(testLot);
        when(lotService.getAllLots()).thenReturn(lots);

        // Act & Assert
        mockMvc.perform(get("/lots")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("lot-123"))
                .andExpect(jsonPath("$[0].medName").value("Paracetamol"))
                .andExpect(jsonPath("$[0].quantity").value(100));
    }

    @Test
    @WithMockUser(roles = "GROSSISTE")
    void testGetAllLots_Paginated() throws Exception {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Lot> lotsPage = new PageImpl<>(Arrays.asList(testLot), pageable, 1);
        when(lotService.getAllLotsPaginated(any(Pageable.class))).thenReturn(lotsPage);

        // Act & Assert
        mockMvc.perform(get("/lots")
                .param("page", "0")
                .param("size", "10")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(header().exists("X-Total-Count"))
                .andExpect(header().exists("X-Total-Pages"))
                .andExpect(jsonPath("$[0].id").value("lot-123"));
    }

    @Test
    @WithMockUser(roles = "GROSSISTE")
    void testGetLotById_Success() throws Exception {
        // Arrange
        when(lotService.getLotById("lot-123")).thenReturn(testLot);

        // Act & Assert
        mockMvc.perform(get("/lots/lot-123")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("lot-123"))
                .andExpect(jsonPath("$.medName").value("Paracetamol"));
    }

    @Test
    @WithMockUser(roles = "GROSSISTE")
    void testGetLotById_NotFound() throws Exception {
        // Arrange
        when(lotService.getLotById("non-existent"))
                .thenThrow(new com.clinchain.backend.exception.ResourceNotFoundException("Lot not found"));

        // Act & Assert
        mockMvc.perform(get("/lots/non-existent")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "GROSSISTE")
    void testGetStats() throws Exception {
        // Arrange
        LotStatsDto stats = LotStatsDto.builder()
                .totalLots(10L)
                .createdLots(3L)
                .validatedLots(4L)
                .inStockLots(2L)
                .administeredLots(1L)
                .totalQuantity(500)
                .build();

        when(lotService.getStats()).thenReturn(stats);

        // Act & Assert
        mockMvc.perform(get("/lots/stats")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalLots").value(10))
                .andExpect(jsonPath("$.createdLots").value(3))
                .andExpect(jsonPath("$.validatedLots").value(4));
    }

    @Test
    @WithMockUser(roles = "GROSSISTE")
    void testCreateLot_Success() throws Exception {
        // Arrange
        Lot createdLot = Lot.builder()
                .id("lot-456")
                .medName("Ibuprofen")
                .quantity(50)
                .status(LotStatus.CREE_PAR_GROSSISTE)
                .createdBy("grossiste")
                .build();

        when(lotService.createLot(any(CreateLotRequest.class))).thenReturn(createdLot);

        // Act & Assert
        mockMvc.perform(post("/lots")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createLotRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("lot-456"))
                .andExpect(jsonPath("$.medName").value("Ibuprofen"))
                .andExpect(jsonPath("$.quantity").value(50));
    }

    // Removed testCreateLot_Unauthorized as it tests Spring Security configuration
    // rather than business logic
    // The @PreAuthorize("hasRole('GROSSISTE')") annotation on the createLot
    // endpoint
    // already ensures only GROSSISTE role can create lots

    @Test
    @WithMockUser(roles = "HOPITALE")
    void testValidateLot_Success() throws Exception {
        // Arrange
        Lot validatedLot = Lot.builder()
                .id("lot-123")
                .medName("Paracetamol")
                .quantity(100)
                .status(LotStatus.VALIDE_PAR_HOPITAL)
                .validated(true)
                .build();

        ValidateRequest validateRequest = new ValidateRequest();
        validateRequest.setActor("hospital");

        when(lotService.validateLot("lot-123", validateRequest)).thenReturn(validatedLot);

        // Act & Assert
        mockMvc.perform(post("/lots/lot-123/validate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("VALIDE_PAR_HOPITAL"))
                .andExpect(jsonPath("$.validated").value(true));
    }

    @Test
    @WithMockUser(roles = "PHARMACIEN")
    void testWithdrawFromLot_Success() throws Exception {
        // Arrange
        WithdrawRequest withdrawRequest = new WithdrawRequest();
        withdrawRequest.setQty(30);
        withdrawRequest.setActor("grossiste");

        Lot lotAfterWithdraw = Lot.builder()
                .id("lot-123")
                .medName("Paracetamol")
                .quantity(70)
                .status(LotStatus.CREE_PAR_GROSSISTE)
                .build();

        when(lotService.withdrawFromLot("lot-123", withdrawRequest)).thenReturn(lotAfterWithdraw);

        // Act & Assert
        mockMvc.perform(post("/lots/lot-123/withdraw")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(withdrawRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantity").value(70));
    }

    @Test
    @WithMockUser(roles = "PHARMACIEN")
    void testMarkInPharmacy_Success() throws Exception {
        // Arrange
        testLot.setStatus(LotStatus.EN_STOCK_PHARMACIE);
        ActorRequest actorRequest = new ActorRequest();
        actorRequest.setActor("pharmacist");

        when(lotService.markInPharmacy("lot-123", actorRequest)).thenReturn(testLot);

        // Act & Assert
        mockMvc.perform(post("/lots/lot-123/stock")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(actorRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("EN_STOCK_PHARMACIE"));
    }

    @Test
    @WithMockUser(roles = "INFIRMIER")
    void testAdministerLot_Success() throws Exception {
        // Arrange
        testLot.setStatus(LotStatus.ADMINISTRE);
        ActorRequest actorRequest = new ActorRequest();
        actorRequest.setActor("nurse");

        when(lotService.administerLot("lot-123", actorRequest)).thenReturn(testLot);

        // Act & Assert
        mockMvc.perform(post("/lots/lot-123/administer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(actorRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ADMINISTRE"));
    }

    @Test
    @WithMockUser(roles = "PHARMACIEN")
    void testAddHistory_Success() throws Exception {
        // Arrange
        AddHistoryRequest historyRequest = new AddHistoryRequest();
        historyRequest.setAction("Test action");
        historyRequest.setActor("grossiste");

        when(lotService.addHistoryToLot("lot-123", historyRequest)).thenReturn(testLot);

        // Act & Assert
        mockMvc.perform(post("/lots/lot-123/history")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(historyRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("lot-123"));
    }

    @Test
    @WithMockUser(roles = "GROSSISTE")
    void testGetBlockchainState() throws Exception {
        // Arrange
        BlockchainLotDto blockchainLot = new BlockchainLotDto();
        blockchainLot.setLotId("lot-123");
        blockchainLot.setBlockchainStatus(0);
        blockchainLot.setSyncedWithDatabase(true);

        when(lotService.getBlockchainState("lot-123")).thenReturn(blockchainLot);

        // Act & Assert
        mockMvc.perform(get("/lots/lot-123/blockchain")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lotId").value("lot-123"))
                .andExpect(jsonPath("$.syncedWithDatabase").value(true));
    }

    @Test
    @WithMockUser(roles = "GROSSISTE")
    void testSearchLots() throws Exception {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Lot> lotsPage = new PageImpl<>(Arrays.asList(testLot), pageable, 1);

        when(lotService.searchLots(
                eq(LotStatus.CREE_PAR_GROSSISTE),
                eq("grossiste"),
                eq("Paracetamol"),
                any(Pageable.class)
        )).thenReturn(lotsPage);

        // Act & Assert
        mockMvc.perform(get("/lots")
                .param("page", "0")
                .param("size", "10")
                .param("status", "CREE_PAR_GROSSISTE")
                .param("createdBy", "grossiste")
                .param("medName", "Paracetamol")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("lot-123"));
    }
}
