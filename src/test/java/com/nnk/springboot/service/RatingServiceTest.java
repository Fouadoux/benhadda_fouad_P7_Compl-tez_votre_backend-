package com.nnk.springboot.service;

import com.nnk.springboot.domain.BidList;
import com.nnk.springboot.domain.Rating;
import com.nnk.springboot.dto.BidDTO;
import com.nnk.springboot.dto.RatingDTO;
import com.nnk.springboot.exception.EntityDeleteException;
import com.nnk.springboot.exception.EntityNotFoundException;
import com.nnk.springboot.exception.EntitySaveException;
import com.nnk.springboot.repositories.RatingRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;

import java.awt.*;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
class RatingServiceTest {

    @Mock
    RatingRepository ratingRepository;

    @InjectMocks
    RatingService ratingService;

    @Test
    void testGetAllRatings(){

        Rating r1 = new Rating();
        Rating r2 = new Rating();

        when(ratingRepository.findAll()).thenReturn(List.of(r1, r2));

        List<Rating> ratings = ratingService.getAllRatings();

        assertNotNull(ratings);

    }

    @Test
    void testConvertToDTO(){
        Rating r1 = new Rating();
        r1.setId(1);
        r1.setFitchRating("testFitchRating");
        r1.setMoodysRating("testMoodysRating");
        r1.setSandPRating("testSandRating");
        r1.setOrderNumber(1);

        RatingDTO dto = ratingService.convertToDTO(r1);
        assertNotNull(dto);
        assertEquals(dto.getFitchRating(), r1.getFitchRating());
        assertEquals(dto.getMoodysRating(), r1.getMoodysRating());
        assertEquals(dto.getSandPRating(), r1.getSandPRating());
        assertEquals(dto.getOrderNumber(), r1.getOrderNumber());

    }

    @Test
    void testConvertToDTOList(){
        Rating r1 = new Rating();
        r1.setId(1);
        r1.setFitchRating("testFitchRating");
        r1.setMoodysRating("testMoodysRating");
        r1.setSandPRating("testSandRating");
        r1.setOrderNumber(1);

        Rating r2 = new Rating();
        r2.setId(2);
        r2.setFitchRating("testFitchRating2");
        r2.setMoodysRating("testMoodysRating2");
        r2.setSandPRating("testSandRating2");
        r2.setOrderNumber(1);

        List<RatingDTO> dtos = ratingService.convertToDTOList(List.of(r1, r2));
        assertNotNull(dtos);
        assertEquals(dtos.size(), 2);
        assertEquals(dtos.get(0).getFitchRating(), r1.getFitchRating());
        assertEquals(dtos.get(0).getMoodysRating(), r1.getMoodysRating());
        assertEquals(dtos.get(0).getSandPRating(), r1.getSandPRating());
        assertEquals(dtos.get(0).getOrderNumber(), r1.getOrderNumber());
        assertEquals(dtos.get(1).getFitchRating(), r2.getFitchRating());
        assertEquals(dtos.get(1).getMoodysRating(), r2.getMoodysRating());
        assertEquals(dtos.get(1).getSandPRating(), r2.getSandPRating());
        assertEquals(dtos.get(1).getOrderNumber(), r2.getOrderNumber());

    }

    @Test
    void testSaveRating_Successfully(){
        RatingDTO dto = new RatingDTO();
        dto.setFitchRating("testFitchRating");
        dto.setMoodysRating("testMoodysRating");
        dto.setSandPRating("testSandRating");
        dto.setOrderNumber(1);

        Rating savedRating = new Rating();
        savedRating.setFitchRating("testFitchRating2");
        savedRating.setMoodysRating("testMoodysRating2");
        savedRating.setSandPRating("testSandRating2");
        savedRating.setOrderNumber(1);

        when(ratingRepository.save(any(Rating.class))).thenReturn(savedRating);

        Rating result = ratingService.saveRating(dto);

        assertNotNull(result);
        assertEquals(result.getFitchRating(), savedRating.getFitchRating());
        assertEquals(result.getMoodysRating(), savedRating.getMoodysRating());
        assertEquals(result.getSandPRating(), savedRating.getSandPRating());
        assertEquals(result.getOrderNumber(), savedRating.getOrderNumber());
    }

    @Test
    void testSaveRating_ThrowsEntitySaveException(){
        RatingDTO dto = new RatingDTO();
        dto.setFitchRating("testFitchRating");
        dto.setMoodysRating("testMoodysRating");
        dto.setSandPRating("testSandRating");
        dto.setOrderNumber(1);

        doThrow(new DataAccessException("Failed to create rating.") {}).when(ratingRepository).save(any(Rating.class));

        EntitySaveException ex=assertThrows(EntitySaveException.class,()->ratingService.saveRating(dto));

        assertEquals("Failed to create rating.", ex.getMessage());
        verify(ratingRepository,times(1)).save(any(Rating.class));
    }

    @Test
    void testUpdateRating_Successfully(){

        int id=1;
        RatingDTO dto = new RatingDTO();
        dto.setId(id);
        dto.setFitchRating("newFitchRating");
        dto.setMoodysRating("newMoodysRating");
        dto.setSandPRating("newSandRating");
        dto.setOrderNumber(1);

        Rating updateRating = new Rating();
        updateRating.setId(id);
        updateRating.setFitchRating("oldFitchRating2");
        updateRating.setMoodysRating("oldMoodysRating2");
        updateRating.setSandPRating("oldSandRating2");
        updateRating.setOrderNumber(2);

        when(ratingRepository.findById(id)).thenReturn(Optional.of(updateRating));
        when(ratingRepository.save(any(Rating.class))).thenReturn(updateRating);

        Rating result=ratingService.updateRating(id,dto);
        assertNotNull(result);
        assertEquals(result.getFitchRating(), dto.getFitchRating());
        assertEquals(result.getMoodysRating(), dto.getMoodysRating());
        assertEquals(result.getSandPRating(), dto.getSandPRating());
        assertEquals(result.getOrderNumber(), dto.getOrderNumber());
        assertEquals(id, result.getId());

        verify(ratingRepository,times(1)).findById(id);
        verify(ratingRepository,times(1)).save(any(Rating.class));

    }

    @Test
    void testUpdateRating_NotFound() {
        // GIVEN
        int bidId = 999;
        RatingDTO dto = new RatingDTO();
        // ...

        when(ratingRepository.findById(bidId)).thenReturn(Optional.empty());

        // WHEN + THEN
        assertThrows(EntityNotFoundException.class, () -> {
            ratingService.updateRating(bidId, dto);
        });

        verify(ratingRepository).findById(bidId);
        verify(ratingRepository, never()).save(any());
    }

    @Test
    void testUpdateRating_DataAccessException() {
        // GIVEN
        int id = 1;
        RatingDTO dto = new RatingDTO();
        dto.setId(id);
        dto.setFitchRating("newFitchRating");
        dto.setMoodysRating("newMoodysRating");
        dto.setSandPRating("newSandRating");
        dto.setOrderNumber(1);


        Rating updateRating = new Rating();
        updateRating.setId(id);
        updateRating.setFitchRating("oldFitchRating2");
        updateRating.setMoodysRating("oldMoodysRating2");
        updateRating.setSandPRating("oldSandRating2");
        updateRating.setOrderNumber(1);

        when(ratingRepository.findById(id)).thenReturn(Optional.of(updateRating));
        doThrow(new DataAccessException("DB error") {}).when(ratingRepository).save(updateRating);

        // WHEN + THEN
        EntitySaveException ex = assertThrows(EntitySaveException.class, () -> {
            ratingService.updateRating(id, dto);
        });

        assertEquals("Failed to update rating with ID 1", ex.getMessage());
        verify(ratingRepository).findById(id);
        verify(ratingRepository).save(updateRating);
    }

    @Test
    void testDeleteRating_Successfully() {
        // GIVEN
        int bidId = 1;

        // Simuler l'existence de l'entité
        when(ratingRepository.existsById(bidId)).thenReturn(true);

        // WHEN
        ratingService.deleteRating(bidId);

        // THEN
        verify(ratingRepository).existsById(bidId);
        verify(ratingRepository).deleteById(bidId);
    }

    @Test
    void testDeleteRating_NotFound() {
        // GIVEN
        int bidId = 999;

        // Simuler l'absence de l'entité
        when(ratingRepository.existsById(bidId)).thenReturn(false);

        // WHEN + THEN
        assertThrows(EntityNotFoundException.class, () -> {
            ratingService.deleteRating(bidId);
        });

        // Vérifier que deleteById n'a pas été appelé
        verify(ratingRepository, never()).deleteById(anyInt());
    }

    @Test
    void testDeleteRating_DataAccessException() {
        // GIVEN
        int bidId = 2;
        when(ratingRepository.existsById(bidId)).thenReturn(true);

        // Simuler une exception au moment de la suppression
        doThrow(new DataAccessException("DB error") {})
                .when(ratingRepository).deleteById(bidId);

        // WHEN + THEN
        assertThrows(EntityDeleteException.class, () -> {
            ratingService.deleteRating(bidId);
        });

        verify(ratingRepository).deleteById(bidId);
    }

    @Test
    void testDeleteRatingList_InvalidId() {
        // GIVEN
        int invalidId = 0;

        // WHEN + THEN
        assertThrows(IllegalArgumentException.class, () -> {
            ratingService.deleteRating(invalidId);
        });

        // Vérifier que le repository n'est jamais appelé
        verify(ratingRepository, never()).existsById(anyInt());
        verify(ratingRepository, never()).deleteById(anyInt());
    }

    @Test
    void testGetRatingDTOById_Success() {
        // GIVEN
        int id = 1;
        Rating rating = new Rating();
        rating.setId(id);
        rating.setFitchRating("FitchRating");
        rating.setMoodysRating("MoodysRating");
        rating.setSandPRating("SandRating");
        rating.setOrderNumber(2);

        // Simulation : l'entité est trouvée
        when(ratingRepository.findById(1))
                .thenReturn(Optional.of(rating));

        // WHEN
        RatingDTO result = ratingService.getRatingDTOById(id);

        // THEN
        assertNotNull(result);
        assertEquals(result.getFitchRating(), rating.getFitchRating());
        assertEquals(result.getMoodysRating(), rating.getMoodysRating());
        assertEquals(result.getSandPRating(), rating.getSandPRating());
        assertEquals(result.getOrderNumber(), rating.getOrderNumber());
    }

    @Test
    void testGetRatingDTOById_NotFound() {
        // GIVEN
        int nonExistentId = 999;
        when(ratingRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // WHEN + THEN
        assertThrows(EntityNotFoundException.class, () -> {
            ratingService.getRatingDTOById(nonExistentId);
        });
        verify(ratingRepository).findById(nonExistentId);
    }

    @Test
    void testGetRatingDTOById_InvalidId() {
        // GIVEN
        int invalidId = 0; // ou -1

        // WHEN + THEN
        assertThrows(IllegalArgumentException.class, () -> {
            ratingService.getRatingDTOById(invalidId);
        });

        // Vérifie que le repository n'est jamais appelé
        verify(ratingRepository, never()).findById(anyInt());
    }

}