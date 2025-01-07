package com.nnk.springboot.integration;

import com.nnk.springboot.domain.Rating;
import com.nnk.springboot.dto.RatingDTO;
import com.nnk.springboot.exception.EntityDeleteException;
import com.nnk.springboot.exception.EntityNotFoundException;
import com.nnk.springboot.exception.EntitySaveException;
import com.nnk.springboot.repositories.RatingRepository;
import com.nnk.springboot.service.RatingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class RatingServiceIT {

    @Autowired
    private RatingService ratingService;

    @Autowired
    private RatingRepository ratingRepository;

    @BeforeEach
    void setUp() {
        ratingRepository.deleteAll();
    }

    @Test
    void saveRating_ShouldSaveRatingSuccessfully() throws EntitySaveException {
        // Arrange
        RatingDTO ratingDTO = new RatingDTO();
        ratingDTO.setMoodysRating("MoodysRatingTest");
        ratingDTO.setSandPRating("SandPRatingTest");
        ratingDTO.setFitchRating("FitchRatingTest");
        ratingDTO.setOrderNumber(1);

        // Act
        Rating savedRating = ratingService.saveRating(ratingDTO);

        // Assert
        assertNotNull(savedRating.getId());
        assertEquals("MoodysRatingTest", savedRating.getMoodysRating());
        assertEquals("SandPRatingTest", savedRating.getSandPRating());
        assertEquals("FitchRatingTest", savedRating.getFitchRating());
        assertEquals(1, savedRating.getOrderNumber());
    }

    @Test
    void saveRating_ShouldThrowExceptionWhenDataInvalid() {
        // Arrange
        RatingDTO ratingDTO = new RatingDTO(); // Missing required fields

        // Act & Assert
        assertThrows(EntitySaveException.class, () -> ratingService.saveRating(ratingDTO));
    }

    @Test
    void getAllRatings_ShouldReturnAllSavedRatings() throws EntitySaveException {
        // Arrange
        RatingDTO rating1 = new RatingDTO();
        rating1.setMoodysRating("MoodysRating1");
        rating1.setSandPRating("SandPRating1");
        rating1.setFitchRating("FitchRating1");
        rating1.setOrderNumber(1);
        ratingService.saveRating(rating1);

        RatingDTO rating2 = new RatingDTO();
        rating2.setMoodysRating("MoodysRating2");
        rating2.setSandPRating("SandPRating2");
        rating2.setFitchRating("FitchRating2");
        rating2.setOrderNumber(2);
        ratingService.saveRating(rating2);

        // Act
        List<Rating> allRatings = ratingService.getAllRatings();

        // Assert
        assertEquals(2, allRatings.size());
    }

    @Test
    void getRatingDTOById_ShouldReturnRatingDTO() throws EntityNotFoundException, EntitySaveException {
        // Arrange
        RatingDTO ratingDTO = new RatingDTO();
        ratingDTO.setMoodysRating("MoodysRatingTest");
        ratingDTO.setSandPRating("SandPRatingTest");
        ratingDTO.setFitchRating("FitchRatingTest");
        ratingDTO.setOrderNumber(1);
        Rating savedRating = ratingService.saveRating(ratingDTO);

        // Act
        RatingDTO fetchedRating = ratingService.getRatingDTOById(savedRating.getId());

        // Assert
        assertNotNull(fetchedRating);
        assertEquals(savedRating.getId(), fetchedRating.getId());
    }

    @Test
    void getRatingDTOById_ShouldThrowExceptionWhenRatingNotFound() {
        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> ratingService.getRatingDTOById(999));
    }

    @Test
    void updateRating_ShouldUpdateRatingSuccessfully() throws EntitySaveException, EntityNotFoundException {
        // Arrange
        RatingDTO ratingDTO = new RatingDTO();
        ratingDTO.setMoodysRating("MoodysRatingTest");
        ratingDTO.setSandPRating("SandPRatingTest");
        ratingDTO.setFitchRating("FitchRatingTest");
        ratingDTO.setOrderNumber(1);
        Rating savedRating = ratingService.saveRating(ratingDTO);

        RatingDTO updatedRatingDTO = new RatingDTO();
        updatedRatingDTO.setMoodysRating("UpdatedMoodysRating");
        updatedRatingDTO.setSandPRating("UpdatedSandPRating");
        updatedRatingDTO.setFitchRating("UpdatedFitchRating");
        updatedRatingDTO.setOrderNumber(2);

        // Act
        Rating updatedRating = ratingService.updateRating(savedRating.getId(), updatedRatingDTO);

        // Assert
        assertEquals("UpdatedMoodysRating", updatedRating.getMoodysRating());
        assertEquals("UpdatedSandPRating", updatedRating.getSandPRating());
        assertEquals("UpdatedFitchRating", updatedRating.getFitchRating());
        assertEquals(2, updatedRating.getOrderNumber());
    }

    @Test
    void updateRating_ShouldThrowExceptionWhenRatingNotFound() {
        // Arrange
        RatingDTO updatedRatingDTO = new RatingDTO();
        updatedRatingDTO.setMoodysRating("UpdatedMoodysRating");
        updatedRatingDTO.setSandPRating("UpdatedSandPRating");
        updatedRatingDTO.setFitchRating("UpdatedFitchRating");
        updatedRatingDTO.setOrderNumber(2);

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> ratingService.updateRating(999, updatedRatingDTO));
    }

    @Test
    void deleteRating_ShouldDeleteRatingSuccessfully() throws EntitySaveException {
        // Arrange
        RatingDTO ratingDTO = new RatingDTO();
        ratingDTO.setMoodysRating("MoodysRatingTest");
        ratingDTO.setSandPRating("SandPRatingTest");
        ratingDTO.setFitchRating("FitchRatingTest");
        ratingDTO.setOrderNumber(1);
        Rating savedRating = ratingService.saveRating(ratingDTO);

        // Act
        ratingService.deleteRating(savedRating.getId());
        boolean exists = ratingRepository.existsById(savedRating.getId());

        // Assert
        assertFalse(exists);
    }

    @Test
    void deleteRating_ShouldThrowExceptionWhenRatingNotFound() {
        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> ratingService.deleteRating(999));
    }
}
