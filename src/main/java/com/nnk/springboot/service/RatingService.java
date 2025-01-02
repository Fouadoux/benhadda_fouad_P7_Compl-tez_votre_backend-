package com.nnk.springboot.service;

import com.nnk.springboot.domain.Rating;
import com.nnk.springboot.dto.RatingDTO;
import com.nnk.springboot.exception.EntityDeleteException;
import com.nnk.springboot.exception.EntityNotFoundException;
import com.nnk.springboot.exception.EntitySaveException;
import com.nnk.springboot.repositories.RatingRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class RatingService {

    private final RatingRepository ratingRepository;

    /**
     * Constructs a new instance of {@link RatingService}.
     *
     * @param ratingRepository the repository for accessing rating data
     */
    public RatingService(RatingRepository ratingRepository) {
        this.ratingRepository = ratingRepository;
    }

    /**
     * Converts a {@link Rating} entity to a {@link RatingDTO}.
     *
     * @param rating the rating entity to convert
     * @return the converted {@link RatingDTO}
     */
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

    /**
     * Converts a list of {@link Rating} entities to a list of {@link RatingDTO}s.
     *
     * @param ratingList the list of rating entities to convert
     * @return the list of converted {@link RatingDTO}s
     */
    public List<RatingDTO> convertToDTOList(List<Rating> ratingList) {
        log.info("Converting list of bid to DTOs");

        return ratingList.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves all ratings as a list.
     *
     * @return a list of all {@link Rating} entities
     */
    public List<Rating> getAllRatings() {
        return ratingRepository.findAll();
    }

    /**
     * Retrieves a {@link RatingDTO} by its ID.
     *
     * @param id the ID of the rating to retrieve
     * @return the retrieved {@link RatingDTO}
     * @throws IllegalArgumentException  if the ID is invalid
     * @throws EntityNotFoundException   if no rating is found with the given ID
     */
    public RatingDTO getRatingDTOById(int id){
        log.info("Fetching rate with ID: {}", id);

        if (id <= 0) {
            log.error("Invalid ID.");
            throw new IllegalArgumentException("ID must be a positive integer.");
        }

        Rating rating = ratingRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("Rating with id " + id + " not found"));
        return convertToDTO(rating);

    }

    /**
     * Saves a new rating based on a {@link RatingDTO}.
     *
     * @param ratingDTO the data transfer object containing rating details
     * @return the saved {@link Rating} entity
     * @throws EntitySaveException if saving the rating fails
     */
    public Rating saveRating(RatingDTO ratingDTO) {
        log.info("Adding a new bid to the bid list");

        Rating rating = new Rating();

        rating.setMoodysRating(ratingDTO.getMoodysRating());
        rating.setSandPRating(ratingDTO.getSandPRating());
        rating.setFitchRating(ratingDTO.getFitchRating());
        rating.setOrderNumber(ratingDTO.getOrderNumber());


        try {
            Rating saveRate= ratingRepository.save(rating);
            log.info("Rating added successfully");
            return saveRate;
        } catch (DataAccessException e) {
            log.error("Failed to save Rating", e);
            throw new EntitySaveException("Failed to create rating.");
        }
    }

    /**
     * Updates an existing rating based on its ID and a {@link RatingDTO}.
     *
     * @param id        the ID of the rating to update
     * @param ratingDTO the data transfer object containing updated rating details
     * @return the updated {@link Rating} entity
     * @throws IllegalArgumentException  if the {@link RatingDTO} is null
     * @throws EntityNotFoundException   if no rating is found with the given ID
     * @throws EntitySaveException       if updating the rating fails
     */
    public Rating updateRating(int id, RatingDTO ratingDTO) {
        log.info("Updating bid with ID: {}", id);

        if (ratingDTO == null) {
            log.error("BidDTO is null, cannot update.");
            throw new IllegalArgumentException("BidDTO cannot be null.");
        }

        Rating rating = ratingRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("Rate not found with Id: " +id));

        rating.setMoodysRating(ratingDTO.getMoodysRating());
        rating.setSandPRating(ratingDTO.getSandPRating());
        rating.setFitchRating(ratingDTO.getFitchRating());
        rating.setOrderNumber(ratingDTO.getOrderNumber());

        try {
            Rating saveRate=ratingRepository.save(rating);
            log.info("Rating with ID {} updated successfully", id);
            return saveRate;
        } catch (DataAccessException e) {
            log.error("Failed to update rating with ID {}", id, e);
            throw new EntitySaveException("Failed to update rating with ID " + id, e);
        }
    }

    /**
     * Deletes a rating by its ID.
     *
     * @param id the ID of the rating to delete
     * @throws IllegalArgumentException   if the ID is invalid
     * @throws EntityNotFoundException    if no rating is found with the given ID
     * @throws EntityDeleteException      if deleting the rating fails
     */
    public void deleteRating(int id) {
        log.info("Delete rating with ID: {}", id);

        if (id <= 0) {
            log.error("Invalid ID: {}", id);
            throw new IllegalArgumentException("ID must be a positive integer.");
        }

        if (!ratingRepository.existsById(id)) {
            log.error("Rating with ID {} not found", id);
            throw new EntityNotFoundException("Rating not found with ID: " + id);
        }

        try {
            ratingRepository.deleteById(id);
            log.info("Rating with ID {} deleted successfully", id);
        } catch (DataAccessException e) {
            log.error("Failed to delete Rating with ID {}", id, e);
            throw new EntityDeleteException("Failed to delete Rating with ID " + id, e);
        }
    }

}
