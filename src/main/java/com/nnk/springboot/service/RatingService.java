package com.nnk.springboot.service;

import com.nnk.springboot.domain.BidList;
import com.nnk.springboot.domain.Rating;
import com.nnk.springboot.dto.BidDTO;
import com.nnk.springboot.dto.RatingDTO;
import com.nnk.springboot.exception.EntityDeleteException;
import com.nnk.springboot.exception.EntityNotFoundException;
import com.nnk.springboot.exception.EntitySaveException;
import com.nnk.springboot.repositories.RatingRepository;
import io.micrometer.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class RatingService {

    private final RatingRepository ratingRepository;

    public RatingService(RatingRepository ratingRepository) {
        this.ratingRepository = ratingRepository;
    }

    private void validateRatingDTO(RatingDTO ratingDTO) {
        if (StringUtils.isBlank(ratingDTO.getMoodysRating())) {
            throw new IllegalArgumentException("Moody rating cannot be null or empty");
        }
        if (StringUtils.isBlank(ratingDTO.getSandPRating())) {
            throw new IllegalArgumentException("SandPRating cannot be null or empty");
        }
        if (StringUtils.isBlank(ratingDTO.getFitchRating())) {
            throw new IllegalArgumentException("SandPRating cannot be null or empty");
        }
        if (ratingDTO.getOrderNumber() == null || ratingDTO.getOrderNumber() < 0) {
            throw new IllegalArgumentException("Bid quantity must be positive and non-null");
        }
    }

    public RatingDTO convertToDTO(Rating rating) {
        log.info("Converting bid ID {} to DTO", rating.getId());

        RatingDTO dto = new RatingDTO();
        dto.setId(rating.getId());
        dto.setMoodysRating(rating.getMoodysRating());
        dto.setSandPRating(rating.getSandPRating());
        dto.setFitchRating(rating.getFitchRating());
        dto.setOrderNumber(rating.getOrderNumber());
        return dto;
    }

    public List<RatingDTO> convertToDTOList(List<Rating> ratingList) {
        log.info("Converting list of bid to DTOs");

        return ratingList.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<Rating> getListToRaitingList() {
        return ratingRepository.findAll();
    }

    public void addRating(RatingDTO ratingDTO) {
        log.info("Adding a new bid to the bid list");

        validateRatingDTO(ratingDTO);


        Rating rating = new Rating();

        rating.setMoodysRating(ratingDTO.getMoodysRating());
        rating.setSandPRating(ratingDTO.getSandPRating());
        rating.setFitchRating(ratingDTO.getFitchRating());
        rating.setOrderNumber(ratingDTO.getOrderNumber());


        try {
            ratingRepository.save(rating);
            log.info("Rating added successfully");
        } catch (EntitySaveException e) {
            log.error("Failed to save Rating", e);
            throw new EntitySaveException("Failed to create rating.");
        }
    }

    public RatingDTO getRatingDTOById(int id){
        Rating rating = ratingRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("Rating with id " + id + " not found"));
        return convertToDTO(rating);

    }

    public void updateRating(int id, RatingDTO ratingDTO) {
        log.info("Updating bid with ID: {}", id);

        validateRatingDTO(ratingDTO);

        Rating rating = ratingRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("Rating with id " + id + " not found"));

        rating.setMoodysRating(ratingDTO.getMoodysRating());
        rating.setSandPRating(ratingDTO.getSandPRating());
        rating.setFitchRating(ratingDTO.getFitchRating());
        rating.setOrderNumber(ratingDTO.getOrderNumber());

        try {
            ratingRepository.save(rating);
            log.info("Rating with ID {} updated successfully", id);
        } catch (Exception e) {
            log.error("Failed to update rating with ID {}", id, e);
            throw new EntitySaveException("Failed to update rating with ID " + id, e);
        }
    }

    public void deleteRating(int id) {
        log.info("Delete rating with ID: {}", id);

        if (!ratingRepository.existsById(id)) {
            log.error("Rating with ID {} not found", id);
            throw new EntityNotFoundException("Rating not found with ID: " + id);
        }

        try {
            ratingRepository.deleteById(id);
            log.info("Rating with ID {} deleted successfully", id);
        } catch (Exception e) {
            log.error("Failed to delete Rating with ID {}", id, e);
            throw new EntityDeleteException("Failed to delete Rating with ID " + id, e);
        }
    }

}
