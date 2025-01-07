package com.nnk.springboot.controllers;

import com.nnk.springboot.domain.Rating;
import com.nnk.springboot.dto.RatingDTO;
import com.nnk.springboot.exception.EntitySaveException;
import com.nnk.springboot.service.RatingService;
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

class RatingControllerTest {

    @Mock
    private RatingService ratingService;

    @Mock
    private Model model;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private RedirectAttributes redirectAttributes;

    @InjectMocks
    private RatingController ratingController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void home_ShouldReturnRatingListViewWithRatings() {
        // Arrange
        List<Rating> ratings = Arrays.asList(new Rating(), new Rating());
        List<RatingDTO> ratingDTOs = Arrays.asList(new RatingDTO(), new RatingDTO());
        when(ratingService.getAllRatings()).thenReturn(ratings);
        when(ratingService.convertToDTOList(ratings)).thenReturn(ratingDTOs);

        // Act
        String viewName = ratingController.home(model);

        // Assert
        assertEquals("rating/list", viewName);
        verify(model).addAttribute("ratings", ratingDTOs);
    }

    @Test
    void addRatingForm_ShouldReturnAddRatingView() {
        // Act
        String viewName = ratingController.addRatingForm(model);

        // Assert
        assertEquals("rating/add", viewName);
        verify(model).addAttribute(eq("rating"), any(Rating.class));
    }

    @Test
    void validate_ShouldSaveRatingAndRedirectToRatingList() throws EntitySaveException {
        // Arrange
        RatingDTO ratingDTO = new RatingDTO();
        when(bindingResult.hasErrors()).thenReturn(false);

        // Act
        String viewName = ratingController.validate(ratingDTO, bindingResult, model, redirectAttributes);

        // Assert
        assertEquals("redirect:/rating/list", viewName);
        verify(ratingService).saveRating(ratingDTO);
        verify(redirectAttributes).addFlashAttribute("successMessage", "Rating successfully added");
    }

    @Test
    void validate_ShouldReturnAddRatingViewWhenValidationFails() {
        // Arrange
        RatingDTO ratingDTO = new RatingDTO();
        when(bindingResult.hasErrors()).thenReturn(true);

        // Act
        String viewName = ratingController.validate(ratingDTO, bindingResult, model, redirectAttributes);

        // Assert
        assertEquals("rating/add", viewName);
        verify(model).addAttribute("rating", ratingDTO);
    }

    @Test
    void validate_ShouldReturnAddRatingViewWhenSaveFails() throws EntitySaveException {
        // Arrange
        RatingDTO ratingDTO = new RatingDTO();
        when(bindingResult.hasErrors()).thenReturn(false);
        doThrow(new EntitySaveException("Error saving rating")).when(ratingService).saveRating(ratingDTO);

        // Act
        String viewName = ratingController.validate(ratingDTO, bindingResult, model, redirectAttributes);

        // Assert
        assertEquals("rating/add", viewName);
        verify(model).addAttribute("error", "Error saving rating");
    }

    @Test
    void showUpdateForm_ShouldReturnUpdateRatingView() {
        // Arrange
        int id = 1;
        RatingDTO ratingDTO = new RatingDTO();
        when(ratingService.getRatingDTOById(id)).thenReturn(ratingDTO);

        // Act
        String viewName = ratingController.showUpdateForm(id, model);

        // Assert
        assertEquals("rating/update", viewName);
        verify(model).addAttribute("rating", ratingDTO);
    }

    @Test
    void updateRating_ShouldUpdateRatingAndRedirectToRatingList() throws EntitySaveException {
        // Arrange
        int id = 1;
        RatingDTO ratingDTO = new RatingDTO();
        when(bindingResult.hasErrors()).thenReturn(false);

        // Act
        String viewName = ratingController.updateRating(id, ratingDTO, bindingResult, model, redirectAttributes);

        // Assert
        assertEquals("redirect:/rating/list", viewName);
        verify(ratingService).updateRating(id, ratingDTO);
        verify(redirectAttributes).addFlashAttribute("successMessage", "Rating successfully Updated");
    }

    @Test
    void updateRating_ShouldReturnUpdateRatingViewWhenValidationFails() {
        // Arrange
        int id = 1;
        RatingDTO ratingDTO = new RatingDTO();
        when(bindingResult.hasErrors()).thenReturn(true);

        // Act
        String viewName = ratingController.updateRating(id, ratingDTO, bindingResult, model, redirectAttributes);

        // Assert
        assertEquals("rating/update", viewName);
        verify(model).addAttribute("rating", ratingDTO);
    }

    @Test
    void deleteRating_ShouldDeleteRatingAndRedirectToRatingList() {
        // Arrange
        int id = 1;

        // Act
        String viewName = ratingController.deleteRating(id, redirectAttributes);

        // Assert
        assertEquals("redirect:/rating/list", viewName);
        verify(ratingService).deleteRating(id);
        verify(redirectAttributes).addFlashAttribute("successMessage", "Rating successfully deleted");
    }
}
