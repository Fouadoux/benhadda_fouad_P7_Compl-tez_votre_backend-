package com.nnk.springboot.integration;

import com.nnk.springboot.domain.Trade;
import com.nnk.springboot.dto.TradeDTO;
import com.nnk.springboot.exception.EntityDeleteException;
import com.nnk.springboot.exception.EntityNotFoundException;
import com.nnk.springboot.exception.EntitySaveException;
import com.nnk.springboot.repositories.TradeRepository;
import com.nnk.springboot.service.TradeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class TradeServiceIT {

    @Autowired
    private TradeService tradeService;

    @Autowired
    private TradeRepository tradeRepository;

    @BeforeEach
    void setUp() {
        tradeRepository.deleteAll();
    }

    @Test
    void saveTrade_ShouldSaveTradeSuccessfully() throws EntitySaveException {
        // Arrange
        TradeDTO tradeDTO = new TradeDTO();
        tradeDTO.setAccount("TestAccount");
        tradeDTO.setType("TestType");
        tradeDTO.setBuyQuantity(100.0);

        // Act
        Trade savedTrade = tradeService.saveTrade(tradeDTO);

        // Assert
        assertNotNull(savedTrade.getTradeId());
        assertEquals("TestAccount", savedTrade.getAccount());
        assertEquals("TestType", savedTrade.getType());
        assertEquals(100.0, savedTrade.getBuyQuantity());
    }

    @Test
    void saveTrade_ShouldThrowExceptionWhenDataInvalid() {
        // Arrange
        TradeDTO tradeDTO = new TradeDTO(); // Missing required fields

        // Act & Assert
        assertThrows(EntitySaveException.class, () -> tradeService.saveTrade(tradeDTO));
    }

    @Test
    void getListToTradeList_ShouldReturnAllSavedTrades() throws EntitySaveException {
        // Arrange
        TradeDTO trade1 = new TradeDTO();
        trade1.setAccount("Account1");
        trade1.setType("Type1");
        trade1.setBuyQuantity(50.0);
        tradeService.saveTrade(trade1);

        TradeDTO trade2 = new TradeDTO();
        trade2.setAccount("Account2");
        trade2.setType("Type2");
        trade2.setBuyQuantity(150.0);
        tradeService.saveTrade(trade2);

        // Act
        List<Trade> allTrades = tradeService.getListToTradeList();

        // Assert
        assertEquals(2, allTrades.size());
    }

    @Test
    void getTradeDTOById_ShouldReturnTradeDTO() throws EntityNotFoundException, EntitySaveException {
        // Arrange
        TradeDTO tradeDTO = new TradeDTO();
        tradeDTO.setAccount("TestAccount");
        tradeDTO.setType("TestType");
        tradeDTO.setBuyQuantity(100.0);
        Trade savedTrade = tradeService.saveTrade(tradeDTO);

        // Act
        TradeDTO fetchedTrade = tradeService.getTradeDTOById(savedTrade.getTradeId());

        // Assert
        assertNotNull(fetchedTrade);
        assertEquals(savedTrade.getTradeId(), fetchedTrade.getId());
    }

    @Test
    void getTradeDTOById_ShouldThrowExceptionWhenTradeNotFound() {
        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> tradeService.getTradeDTOById(999));
    }

    @Test
    void updateBidList_ShouldUpdateTradeSuccessfully() throws EntitySaveException, EntityNotFoundException {
        // Arrange
        TradeDTO tradeDTO = new TradeDTO();
        tradeDTO.setAccount("TestAccount");
        tradeDTO.setType("TestType");
        tradeDTO.setBuyQuantity(100.0);
        Trade savedTrade = tradeService.saveTrade(tradeDTO);

        TradeDTO updatedTradeDTO = new TradeDTO();
        updatedTradeDTO.setAccount("UpdatedAccount");
        updatedTradeDTO.setType("UpdatedType");
        updatedTradeDTO.setBuyQuantity(200.0);

        // Act
        Trade updatedTrade = tradeService.updateBidList(savedTrade.getTradeId(), updatedTradeDTO);

        // Assert
        assertEquals("UpdatedAccount", updatedTrade.getAccount());
        assertEquals("UpdatedType", updatedTrade.getType());
        assertEquals(200.0, updatedTrade.getBuyQuantity());
    }

    @Test
    void updateBidList_ShouldThrowExceptionWhenTradeNotFound() {
        // Arrange
        TradeDTO updatedTradeDTO = new TradeDTO();
        updatedTradeDTO.setAccount("UpdatedAccount");
        updatedTradeDTO.setType("UpdatedType");
        updatedTradeDTO.setBuyQuantity(200.0);

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> tradeService.updateBidList(999, updatedTradeDTO));
    }

    @Test
    void deleteTrade_ShouldDeleteTradeSuccessfully() throws EntitySaveException {
        // Arrange
        TradeDTO tradeDTO = new TradeDTO();
        tradeDTO.setAccount("TestAccount");
        tradeDTO.setType("TestType");
        tradeDTO.setBuyQuantity(100.0);
        Trade savedTrade = tradeService.saveTrade(tradeDTO);

        // Act
        tradeService.deleteTrade(savedTrade.getTradeId());
        boolean exists = tradeRepository.existsById(savedTrade.getTradeId());

        // Assert
        assertFalse(exists);
    }

    @Test
    void deleteTrade_ShouldThrowExceptionWhenTradeNotFound() {
        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> tradeService.deleteTrade(999));
    }
}
