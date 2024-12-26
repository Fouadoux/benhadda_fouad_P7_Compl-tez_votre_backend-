package com.nnk.springboot.service;

import com.nnk.springboot.domain.Trade;
import com.nnk.springboot.dto.TradeDTO;
import com.nnk.springboot.exception.EntityDeleteException;
import com.nnk.springboot.exception.EntityNotFoundException;
import com.nnk.springboot.exception.EntitySaveException;
import com.nnk.springboot.repositories.TradeRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class TradeServiceTest {

    @Mock
    private TradeRepository tradeRepository;

    @InjectMocks
    private TradeService tradeService;

    @Test
    void testGetListToTradeList() {
        // GIVEN
        Trade t1 = new Trade();
        t1.setTradeId(1);
        t1.setAccount("Account1");
        t1.setType("Type1");
        t1.setBuyQuantity(100.0);

        Trade t2 = new Trade();
        t2.setTradeId(2);
        t2.setAccount("Account2");
        t2.setType("Type2");
        t2.setBuyQuantity(200.0);

        when(tradeRepository.findAll()).thenReturn(List.of(t1, t2));

        // WHEN
        List<Trade> trades = tradeService.getListToTradeList();

        // THEN
        assertFalse(trades.isEmpty());
        assertEquals(2, trades.size());
        verify(tradeRepository, times(1)).findAll();
    }

    @Test
    void testConvertToDTO() {
        // GIVEN
        Trade trade = new Trade();
        trade.setTradeId(10);
        trade.setAccount("TradeAccount");
        trade.setType("TradeType");
        trade.setBuyQuantity(300.0);

        // WHEN
        TradeDTO dto = tradeService.convertToDTO(trade);

        // THEN
        assertNotNull(dto);
        assertEquals(trade.getTradeId(), dto.getId());
        assertEquals(trade.getAccount(), dto.getAccount());
        assertEquals(trade.getType(), dto.getType());
        assertEquals(trade.getBuyQuantity(), dto.getBuyQuantity());
    }

    @Test
    void testConvertToDTOList() {
        // GIVEN
        Trade t1 = new Trade();
        t1.setTradeId(1);
        t1.setAccount("Account1");
        t1.setType("Type1");
        t1.setBuyQuantity(111.0);

        Trade t2 = new Trade();
        t2.setTradeId(2);
        t2.setAccount("Account2");
        t2.setType("Type2");
        t2.setBuyQuantity(222.0);

        when(tradeRepository.findAll()).thenReturn(List.of(t1, t2));

        // WHEN
        List<Trade> allTrades = tradeRepository.findAll();
        List<TradeDTO> tradeDTOs = tradeService.convertToDTOList(allTrades);

        // THEN
        assertNotNull(tradeDTOs);
        assertEquals(2, tradeDTOs.size());

        assertEquals(t1.getTradeId(), tradeDTOs.get(0).getId());
        assertEquals(t1.getAccount(), tradeDTOs.get(0).getAccount());
        assertEquals(t1.getType(), tradeDTOs.get(0).getType());
        assertEquals(t1.getBuyQuantity(), tradeDTOs.get(0).getBuyQuantity());

        assertEquals(t2.getTradeId(), tradeDTOs.get(1).getId());
        assertEquals(t2.getAccount(), tradeDTOs.get(1).getAccount());
        assertEquals(t2.getType(), tradeDTOs.get(1).getType());
        assertEquals(t2.getBuyQuantity(), tradeDTOs.get(1).getBuyQuantity());
    }

    @Test
    void testGetTradeDTOById_Success() {
        // GIVEN
        int existingId = 5;
        Trade mockTrade = new Trade();
        mockTrade.setTradeId(existingId);
        mockTrade.setAccount("TestAccount");
        mockTrade.setType("TestType");
        mockTrade.setBuyQuantity(123.45);

        when(tradeRepository.findById(existingId)).thenReturn(Optional.of(mockTrade));

        // WHEN
        TradeDTO result = tradeService.getTradeDTOById(existingId);

        // THEN
        assertNotNull(result);
        assertEquals(existingId, result.getId());
        assertEquals("TestAccount", result.getAccount());
        assertEquals("TestType", result.getType());
        assertEquals(123.45, result.getBuyQuantity());
        verify(tradeRepository).findById(existingId);
    }

    @Test
    void testGetTradeDTOById_InvalidId() {
        // GIVEN
        int invalidId = 0;

        // WHEN + THEN
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            tradeService.getTradeDTOById(invalidId);
        });
        assertTrue(ex.getMessage().contains("ID must be a positive integer."));
        verify(tradeRepository, never()).findById(anyInt());
    }

    @Test
    void testGetTradeDTOById_NotFound() {
        // GIVEN
        int nonExistentId = 999;
        when(tradeRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // WHEN + THEN
        assertThrows(EntityNotFoundException.class, () -> {
            tradeService.getTradeDTOById(nonExistentId);
        });
        verify(tradeRepository).findById(nonExistentId);
    }

    @Test
    void testSaveTrade_Success() {
        // GIVEN
        TradeDTO tradeDTO = new TradeDTO();
        tradeDTO.setAccount("TestAccount");
        tradeDTO.setType("TestType");
        tradeDTO.setBuyQuantity(100.0);

        Trade savedTrade = new Trade();
        savedTrade.setTradeId(1);
        savedTrade.setAccount("TestAccount");
        savedTrade.setType("TestType");
        savedTrade.setBuyQuantity(100.0);

        when(tradeRepository.save(any(Trade.class))).thenReturn(savedTrade);

        // WHEN
        tradeService.saveTrade(tradeDTO);

        // THEN
        verify(tradeRepository, times(1)).save(any(Trade.class));
    }

    @Test
    void testSaveTrade_NullTradeDTO() {
        // GIVEN
        TradeDTO tradeDTO = null;

        // WHEN + THEN
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            tradeService.saveTrade(tradeDTO);
        });
        assertTrue(ex.getMessage().contains("cannot be null"));
        verify(tradeRepository, never()).save(any(Trade.class));
    }

    @Test
    void testSaveTrade_ThrowsEntitySaveException() {
        // GIVEN
        TradeDTO tradeDTO = new TradeDTO();
        tradeDTO.setAccount("FailAccount");
        tradeDTO.setType("FailType");
        tradeDTO.setBuyQuantity(999.99);

        doThrow(new DataAccessException("DB error") {}).when(tradeRepository).save(any(Trade.class));

        // WHEN + THEN
        EntitySaveException ex = assertThrows(EntitySaveException.class, () -> {
            tradeService.saveTrade(tradeDTO);
        });

        assertEquals("Failed to create trade.", ex.getMessage());
        verify(tradeRepository).save(any(Trade.class));
    }

    @Test
    void testUpdateTrade_Success() {
        // GIVEN
        int tradeId = 10;
        TradeDTO tradeDTO = new TradeDTO();
        tradeDTO.setId(tradeId);
        tradeDTO.setAccount("UpdatedAccount");
        tradeDTO.setType("UpdatedType");
        tradeDTO.setBuyQuantity(555.5);

        Trade existingTrade = new Trade();
        existingTrade.setTradeId(tradeId);
        existingTrade.setAccount("OldAccount");
        existingTrade.setType("OldType");
        existingTrade.setBuyQuantity(100.0);

        when(tradeRepository.findById(tradeId)).thenReturn(Optional.of(existingTrade));
        when(tradeRepository.save(any(Trade.class))).thenReturn(existingTrade);

        // WHEN
       Trade result = tradeService.updateBidList(tradeId, tradeDTO);

        // THEN
        verify(tradeRepository).findById(tradeId);
        verify(tradeRepository).save(existingTrade);
        assertEquals("UpdatedAccount", result.getAccount());
        assertEquals("UpdatedType", result.getType());
        assertEquals(555.5, existingTrade.getBuyQuantity());
    }

    @Test
    void testUpdateTrade_NullTradeDTO() {
        // GIVEN
        int tradeId = 1;
        TradeDTO tradeDTO = null;

        // WHEN + THEN
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            tradeService.updateBidList(tradeId, tradeDTO);
        });
        assertTrue(ex.getMessage().contains("cannot be null"));
        verify(tradeRepository, never()).findById(anyInt());
        verify(tradeRepository, never()).save(any(Trade.class));
    }

    @Test
    void testUpdateTrade_NotFound() {
        // GIVEN
        int tradeId = 999;
        TradeDTO tradeDTO = new TradeDTO();
        tradeDTO.setAccount("NewAccount");
        tradeDTO.setType("NewType");
        tradeDTO.setBuyQuantity(10.0);

        when(tradeRepository.findById(tradeId)).thenReturn(Optional.empty());

        // WHEN + THEN
        assertThrows(EntityNotFoundException.class, () -> {
            tradeService.updateBidList(tradeId, tradeDTO);
        });
        verify(tradeRepository).findById(tradeId);
        verify(tradeRepository, never()).save(any(Trade.class));
    }

    @Test
    void testUpdateTrade_DataAccessException() {
        // GIVEN
        int tradeId = 2;
        TradeDTO dto = new TradeDTO();
        dto.setId(tradeId);
        dto.setAccount("FailAccount");
        dto.setType("FailType");
        dto.setBuyQuantity(999.99);

        Trade existingTrade = new Trade();
        existingTrade.setTradeId(tradeId);
        existingTrade.setAccount("OldAccount");
        existingTrade.setType("OldType");
        existingTrade.setBuyQuantity(10.0);

        when(tradeRepository.findById(tradeId)).thenReturn(Optional.of(existingTrade));
        doThrow(new DataAccessException("DB error") {}).when(tradeRepository).save(existingTrade);

        // WHEN + THEN
        EntitySaveException ex = assertThrows(EntitySaveException.class, () -> {
            tradeService.updateBidList(tradeId, dto);
        });
        assertTrue(ex.getMessage().contains("Failed to update trade with ID"));
        verify(tradeRepository).findById(tradeId);
        verify(tradeRepository).save(existingTrade);
    }

    @Test
    void testDeleteTrade_Success() {
        // GIVEN
        int tradeId = 2;
        when(tradeRepository.existsById(tradeId)).thenReturn(true);

        // WHEN
        tradeService.deleteTrade(tradeId);

        // THEN
        verify(tradeRepository).existsById(tradeId);
        verify(tradeRepository).deleteById(tradeId);
    }

    @Test
    void testDeleteTrade_InvalidId() {
        // GIVEN
        int invalidId = 0;

        // WHEN + THEN
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            tradeService.deleteTrade(invalidId);
        });
        assertTrue(ex.getMessage().contains("must be a positive integer."));
        verify(tradeRepository, never()).existsById(anyInt());
        verify(tradeRepository, never()).deleteById(anyInt());
    }

    @Test
    void testDeleteTrade_NotFound() {
        // GIVEN
        int tradeId = 999;
        when(tradeRepository.existsById(tradeId)).thenReturn(false);

        // WHEN + THEN
        assertThrows(EntityNotFoundException.class, () -> {
            tradeService.deleteTrade(tradeId);
        });
        verify(tradeRepository).existsById(tradeId);
        verify(tradeRepository, never()).deleteById(anyInt());
    }

    @Test
    void testDeleteTrade_DataAccessException() {
        // GIVEN
        int tradeId = 3;
        when(tradeRepository.existsById(tradeId)).thenReturn(true);
        doThrow(new DataAccessException("DB error") {}).when(tradeRepository).deleteById(tradeId);

        // WHEN + THEN
        assertThrows(EntityDeleteException.class, () -> tradeService.deleteTrade(tradeId));
        verify(tradeRepository).deleteById(tradeId);
    }
}
