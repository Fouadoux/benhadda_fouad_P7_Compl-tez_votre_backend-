package com.nnk.springboot.controllers;

import com.nnk.springboot.domain.Trade;
import com.nnk.springboot.dto.TradeDTO;
import com.nnk.springboot.service.TradeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class TradeControllerTest {

    @Mock
    private TradeService tradeService;

    @Mock
    private Model model;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private RedirectAttributes redirectAttributes;

    @InjectMocks
    private TradeController tradeController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void home_ShouldReturnTradeListViewWithTrades() {
        // Arrange
        List<Trade> trades = Arrays.asList(new Trade(), new Trade());
        List<TradeDTO> tradeDTOs = Arrays.asList(new TradeDTO(), new TradeDTO());
        when(tradeService.getListToTradeList()).thenReturn(trades);
        when(tradeService.convertToDTOList(trades)).thenReturn(tradeDTOs);

        // Act
        String viewName = tradeController.home(model);

        // Assert
        assertEquals("trade/list", viewName);
        verify(model).addAttribute("trades", tradeDTOs);
    }

    @Test
    void addUser_ShouldReturnAddTradeView() {
        // Act
        String viewName = tradeController.addUser(model);

        // Assert
        assertEquals("trade/add", viewName);
        verify(model).addAttribute(eq("trade"), any(Trade.class));
    }

    @Test
    void validate_ShouldSaveTradeAndRedirectToTradeList() {
        // Arrange
        TradeDTO tradeDTO = new TradeDTO();
        when(bindingResult.hasErrors()).thenReturn(false);

        // Act
        String viewName = tradeController.validate(tradeDTO, bindingResult, model, redirectAttributes);

        // Assert
        assertEquals("redirect:/trade/list", viewName);
        verify(tradeService).saveTrade(tradeDTO);
        verify(redirectAttributes).addFlashAttribute("successMessage", "Trade added successfully");
    }

    @Test
    void validate_ShouldReturnAddTradeViewWhenValidationFails() {
        // Arrange
        TradeDTO tradeDTO = new TradeDTO();
        when(bindingResult.hasErrors()).thenReturn(true);

        // Act
        String viewName = tradeController.validate(tradeDTO, bindingResult, model, redirectAttributes);

        // Assert
        assertEquals("trade/add", viewName);
        verify(model).addAttribute("trade", tradeDTO);
    }

    @Test
    void validate_ShouldReturnAddTradeViewWhenSaveFails() {
        // Arrange
        TradeDTO tradeDTO = new TradeDTO();
        when(bindingResult.hasErrors()).thenReturn(false);
        doThrow(new RuntimeException("Error saving trade")).when(tradeService).saveTrade(tradeDTO);

        // Act
        String viewName = tradeController.validate(tradeDTO, bindingResult, model, redirectAttributes);

        // Assert
        assertEquals("trade/add", viewName);
        verify(model).addAttribute("errorMessage", "An error occurred while saving the trade");
        verify(model).addAttribute("trade", tradeDTO);
    }

    @Test
    void showUpdateForm_ShouldReturnUpdateTradeView() {
        // Arrange
        int id = 1;
        TradeDTO tradeDTO = new TradeDTO();
        when(tradeService.getTradeDTOById(id)).thenReturn(tradeDTO);

        // Act
        String viewName = tradeController.showUpdateForm(id, model);

        // Assert
        assertEquals("trade/update", viewName);
        verify(model).addAttribute("trade", tradeDTO);
    }

    @Test
    void updateTrade_ShouldUpdateTradeAndRedirectToTradeList() {
        // Arrange
        int id = 1;
        TradeDTO tradeDTO = new TradeDTO();
        when(bindingResult.hasErrors()).thenReturn(false);

        // Act
        String viewName = tradeController.updateTrade(id, tradeDTO, bindingResult, model, redirectAttributes);

        // Assert
        assertEquals("redirect:/trade/list", viewName);
        verify(tradeService).updateBidList(id, tradeDTO);
        verify(redirectAttributes).addFlashAttribute("successMessage", "Trade successfully Updated");
    }

    @Test
    void updateTrade_ShouldReturnUpdateTradeViewWhenValidationFails() {
        // Arrange
        int id = 1;
        TradeDTO tradeDTO = new TradeDTO();
        when(bindingResult.hasErrors()).thenReturn(true);

        // Act
        String viewName = tradeController.updateTrade(id, tradeDTO, bindingResult, model, redirectAttributes);

        // Assert
        assertEquals("trade/update", viewName);
        verify(model).addAttribute("trade", tradeDTO);
    }

    @Test
    void deleteTrade_ShouldDeleteTradeAndRedirectToTradeList() {
        // Arrange
        int id = 1;

        // Act
        String viewName = tradeController.deleteTrade(id, redirectAttributes);

        // Assert
        assertEquals("redirect:/trade/list", viewName);
        verify(tradeService).deleteTrade(id);
        verify(redirectAttributes).addFlashAttribute("successMessage", "Trade deleted successfully");
    }
}
