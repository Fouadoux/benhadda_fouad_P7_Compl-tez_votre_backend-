package com.nnk.springboot.controllers;

import com.nnk.springboot.domain.BidList;
import com.nnk.springboot.dto.BidDTO;
import com.nnk.springboot.exception.EntitySaveException;
import com.nnk.springboot.service.BidListService;
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

class BidListControllerTest {

    @Mock
    private BidListService bidListService;

    @Mock
    private Model model;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private RedirectAttributes redirectAttributes;

    @InjectMocks
    private BidListController bidListController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void home_ShouldReturnBidListViewWithBids() {
        // Arrange
        List<BidList> bidLists = Arrays.asList(new BidList(), new BidList());
        List<BidDTO> bidDTOs = Arrays.asList(new BidDTO(), new BidDTO());
        when(bidListService.getAllBidList()).thenReturn(bidLists);
        when(bidListService.convertToDTOList(bidLists)).thenReturn(bidDTOs);

        // Act
        String viewName = bidListController.home(model);

        // Assert
        assertEquals("bidList/list", viewName);
        verify(model).addAttribute("bidLists", bidDTOs);
    }

    @Test
    void addBidForm_ShouldReturnAddBidView() {
        // Act
        String viewName = bidListController.addBidForm(model);

        // Assert
        assertEquals("bidList/add", viewName);
        verify(model).addAttribute(eq("bid"), any(BidDTO.class));
    }

    @Test
    void validate_ShouldSaveBidAndRedirectToBidList() throws EntitySaveException {
        // Arrange
        BidDTO bidDTO = new BidDTO();
        when(bindingResult.hasErrors()).thenReturn(false);

        // Act
        String viewName = bidListController.validate(bidDTO, bindingResult, model, redirectAttributes);

        // Assert
        assertEquals("redirect:/bidList/list", viewName);
        verify(bidListService).saveBidList(bidDTO);
        verify(redirectAttributes).addFlashAttribute("successMessage", "Bid successfully added");
    }

    @Test
    void validate_ShouldReturnAddBidViewWhenValidationFails() {
        // Arrange
        BidDTO bidDTO = new BidDTO();
        when(bindingResult.hasErrors()).thenReturn(true);

        // Act
        String viewName = bidListController.validate(bidDTO, bindingResult, model, redirectAttributes);

        // Assert
        assertEquals("bidList/add", viewName);
        verify(model).addAttribute("bid", bidDTO);
    }

    @Test
    void validate_ShouldReturnAddBidViewWhenSaveFails() throws EntitySaveException {
        // Arrange
        BidDTO bidDTO = new BidDTO();
        when(bindingResult.hasErrors()).thenReturn(false);
        doThrow(new EntitySaveException("Error saving bid")).when(bidListService).saveBidList(bidDTO);

        // Act
        String viewName = bidListController.validate(bidDTO, bindingResult, model, redirectAttributes);

        // Assert
        assertEquals("bidList/add", viewName);
        verify(model).addAttribute("errorMessage", "An error occurred while saving the bid");
    }

    @Test
    void showUpdateForm_ShouldReturnUpdateBidView() {
        // Arrange
        int id = 1;
        BidDTO bidDTO = new BidDTO();
        when(bidListService.getBidDTOById(id)).thenReturn(bidDTO);

        // Act
        String viewName = bidListController.showUpdateForm(id, model);

        // Assert
        assertEquals("bidList/update", viewName);
        verify(model).addAttribute("bidList", bidDTO);
    }

    @Test
    void updateBid_ShouldUpdateBidAndRedirectToBidList() throws EntitySaveException {
        // Arrange
        int id = 1;
        BidDTO bidDTO = new BidDTO();
        when(bindingResult.hasErrors()).thenReturn(false);

        // Act
        String viewName = bidListController.updateBid(id, bidDTO, bindingResult, model, redirectAttributes);

        // Assert
        assertEquals("redirect:/bidList/list", viewName);
        verify(bidListService).updateBidList(id, bidDTO);
        verify(redirectAttributes).addFlashAttribute("successMessage", "Bid successfully Updated");
    }

    @Test
    void updateBid_ShouldReturnUpdateBidViewWhenValidationFails() {
        // Arrange
        int id = 1;
        BidDTO bidDTO = new BidDTO();
        when(bindingResult.hasErrors()).thenReturn(true);

        // Act
        String viewName = bidListController.updateBid(id, bidDTO, bindingResult, model, redirectAttributes);

        // Assert
        assertEquals("bidList/update", viewName);
        verify(model).addAttribute("bidList", bidDTO);
    }

    @Test
    void deleteBid_ShouldDeleteBidAndRedirectToBidList() {
        // Arrange
        int id = 1;

        // Act
        String viewName = bidListController.deleteBid(id, redirectAttributes);

        // Assert
        assertEquals("redirect:/bidList/list", viewName);
        verify(bidListService).deleteBidList(id);
        verify(redirectAttributes).addFlashAttribute("successMessage", "Bidlist deleted successfully");
    }
}
