package com.nnk.springboot.controllers;

import com.nnk.springboot.domain.CurvePoint;
import com.nnk.springboot.dto.CurveDTO;
import com.nnk.springboot.exception.EntitySaveException;
import com.nnk.springboot.service.CurveService;
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

class CurveControllerTest {

    @Mock
    private CurveService curveService;

    @Mock
    private Model model;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private RedirectAttributes redirectAttributes;

    @InjectMocks
    private CurveController curveController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void home_ShouldReturnCurvePointListViewWithCurves() {
        // Arrange
        List<CurvePoint> curvePoints = Arrays.asList(new CurvePoint(), new CurvePoint());
        List<CurveDTO> curveDTOs = Arrays.asList(new CurveDTO(), new CurveDTO());
        when(curveService.getAllCurvePoint()).thenReturn(curvePoints);
        when(curveService.convertToDtoList(curvePoints)).thenReturn(curveDTOs);

        // Act
        String viewName = curveController.home(model);

        // Assert
        assertEquals("curvePoint/list", viewName);
        verify(model).addAttribute("curvePoints", curveDTOs);
    }

    @Test
    void addBidForm_ShouldReturnAddCurvePointView() {
        // Act
        String viewName = curveController.addBidForm(model);

        // Assert
        assertEquals("curvePoint/add", viewName);
        verify(model).addAttribute(eq("curvePoint"), any(CurveDTO.class));
    }

    @Test
    void validate_ShouldSaveCurvePointAndRedirectToCurvePointList() throws EntitySaveException {
        // Arrange
        CurveDTO curveDTO = new CurveDTO();
        when(bindingResult.hasErrors()).thenReturn(false);

        // Act
        String viewName = curveController.validate(curveDTO, bindingResult, model, redirectAttributes);

        // Assert
        assertEquals("redirect:/curvePoint/list", viewName);
        verify(curveService).saveCurvePoint(curveDTO);
        verify(redirectAttributes).addFlashAttribute("successMessage", "Curve successfully added");
    }

    @Test
    void validate_ShouldReturnAddCurvePointViewWhenValidationFails() {
        // Arrange
        CurveDTO curveDTO = new CurveDTO();
        when(bindingResult.hasErrors()).thenReturn(true);

        // Act
        String viewName = curveController.validate(curveDTO, bindingResult, model, redirectAttributes);

        // Assert
        assertEquals("curvePoint/add", viewName);
        verify(model).addAttribute("curvePoint", curveDTO);
    }

    @Test
    void validate_ShouldReturnAddCurvePointViewWhenSaveFails() throws EntitySaveException {
        // Arrange
        CurveDTO curveDTO = new CurveDTO();
        when(bindingResult.hasErrors()).thenReturn(false);
        doThrow(new EntitySaveException("Error saving curve point")).when(curveService).saveCurvePoint(curveDTO);

        // Act
        String viewName = curveController.validate(curveDTO, bindingResult, model, redirectAttributes);

        // Assert
        assertEquals("curvePoint/add", viewName);
        verify(model).addAttribute("errorMessage", "An error occurred while saving the bid");
    }

    @Test
    void showUpdateForm_ShouldReturnUpdateCurvePointView() {
        // Arrange
        int id = 1;
        CurveDTO curveDTO = new CurveDTO();
        when(curveService.getCurveDTOById(id)).thenReturn(curveDTO);

        // Act
        String viewName = curveController.showUpdateForm(id, model);

        // Assert
        assertEquals("curvePoint/update", viewName);
        verify(model).addAttribute("curvePoint", curveDTO);
    }

    @Test
    void updateBid_ShouldUpdateCurvePointAndRedirectToCurvePointList() throws EntitySaveException {
        // Arrange
        int id = 1;
        CurveDTO curveDTO = new CurveDTO();
        when(bindingResult.hasErrors()).thenReturn(false);

        // Act
        String viewName = curveController.updateBid(id, curveDTO, bindingResult, model, redirectAttributes);

        // Assert
        assertEquals("redirect:/curvePoint/list", viewName);
        verify(curveService).updateCurvePoint(id, curveDTO);
        verify(redirectAttributes).addFlashAttribute("successMessage", "Curve successfully Updated");
    }

    @Test
    void updateBid_ShouldReturnUpdateCurvePointViewWhenValidationFails() {
        // Arrange
        int id = 1;
        CurveDTO curveDTO = new CurveDTO();
        when(bindingResult.hasErrors()).thenReturn(true);

        // Act
        String viewName = curveController.updateBid(id, curveDTO, bindingResult, model, redirectAttributes);

        // Assert
        assertEquals("redirect:/curvePoint/list", viewName);
        verify(model).addAttribute("curvePoint", curveDTO);
    }

    @Test
    void deleteBid_ShouldDeleteCurvePointAndRedirectToCurvePointList() {
        // Arrange
        int id = 1;

        // Act
        String viewName = curveController.deleteBid(id, redirectAttributes);

        // Assert
        assertEquals("redirect:/curvePoint/list", viewName);
        verify(curveService).deleteCurvePoint(id);
        verify(redirectAttributes).addFlashAttribute("successMessage", "Curve point deleted successfully");
    }
}
