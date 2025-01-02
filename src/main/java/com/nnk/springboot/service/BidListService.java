package com.nnk.springboot.service;

import com.nnk.springboot.domain.BidList;
import com.nnk.springboot.dto.BidDTO;
import com.nnk.springboot.exception.EntityDeleteException;
import com.nnk.springboot.exception.EntityNotFoundException;
import com.nnk.springboot.exception.EntitySaveException;
import com.nnk.springboot.repositories.BidListRepository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class BidListService {

    private final BidListRepository bidListRepository;

    /**
     * Constructs a new instance of {@link BidListService}.
     *
     * @param bidListRepository the repository for accessing bid list data
     */
    public BidListService(BidListRepository bidListRepository) {
        this.bidListRepository = bidListRepository;
    }

    /**
     * Converts a {@link BidList} entity to a {@link BidDTO}.
     *
     * @param bidList the bid list entity to convert
     * @return the converted {@link BidDTO}
     */
    public BidDTO convertToDTO(BidList bidList){
         log.info("Converting bid  to DTO");

         BidDTO dto=new BidDTO();
         dto.setId(bidList.getId());
         dto.setAccount(bidList.getAccount());
         dto.setBidQuantity(bidList.getBidQuantity());
         dto.setType(bidList.getType());

         return dto;
    }

    /**
     * Converts a list of {@link BidList} entities to a list of {@link BidDTO}s.
     *
     * @param bidLists the list of bid list entities to convert
     * @return the list of converted {@link BidDTO}s
     */
    public List<BidDTO> convertToDTOList (List<BidList> bidLists){
        log.info("Converting list of bid to DTOs");

        return bidLists.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves all bid lists as a list.
     *
     * @return a list of all {@link BidList} entities
     */
    public List<BidList> getAllBidList(){
        return bidListRepository.findAll();
    }

    /**
     * Retrieves a {@link BidDTO} by its ID.
     *
     * @param id the ID of the bid list to retrieve
     * @return the retrieved {@link BidDTO}
     * @throws IllegalArgumentException  if the ID is invalid
     * @throws EntityNotFoundException   if no bid list is found with the given ID
     */
    public BidDTO getBidDTOById(int id) {
        log.info("Fetching BidDTO with ID: {}", id);

        if (id <= 0) {
            log.error("Invalid ID.");
            throw new IllegalArgumentException("ID must be a positive integer.");
        }

        BidList bidList = bidListRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("Bid list with id " + id + " not found")
                );

        return convertToDTO(bidList);
    }

    /**
     * Saves a new bid list based on a {@link BidDTO}.
     *
     * @param bidDTO the data transfer object containing bid list details
     * @return the saved {@link BidList} entity
     * @throws IllegalArgumentException  if the {@link BidDTO} is null
     * @throws EntitySaveException       if saving the bid list fails
     */
    public BidList saveBidList(BidDTO bidDTO) {
        log.info("Saving a new Bid into the bid list");

        if (bidDTO == null) {
            log.error("BidDTO is null, cannot save.");
            throw new IllegalArgumentException("BidDTO cannot be null.");
        }

        BidList bidList = new BidList();
        bidList.setAccount(bidDTO.getAccount());
        bidList.setType(bidDTO.getType());
        bidList.setBidQuantity(bidDTO.getBidQuantity());

        try {
            BidList savedBid = bidListRepository.save(bidList);
            log.info("BidList added successfully, generated ID = {}", savedBid.getId());
            return savedBid;
        } catch (DataAccessException e) {
            log.error("Failed to save bid", e);
            throw new EntitySaveException("Failed to create bid.",e);
        }
    }

    /**
     * Updates an existing bid list based on its ID and a {@link BidDTO}.
     *
     * @param id     the ID of the bid list to update
     * @param bidDTO the data transfer object containing updated bid list details
     * @return the updated {@link BidList} entity
     * @throws IllegalArgumentException  if the {@link BidDTO} is null
     * @throws EntityNotFoundException   if no bid list is found with the given ID
     * @throws EntitySaveException       if updating the bid list fails
     */
    public BidList updateBidList(int id, BidDTO bidDTO) {
        log.info("Updating bid with ID: {}", id);

        if (bidDTO == null) {
            log.error("BidDTO is null, cannot update.");
            throw new IllegalArgumentException("BidDTO cannot be null.");
        }

        BidList existingBidList = bidListRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("BidList not found with Id: " +id));

        existingBidList.setAccount(bidDTO.getAccount());
        existingBidList.setType(bidDTO.getType());
        existingBidList.setBidQuantity(bidDTO.getBidQuantity());

        try {
            BidList savedBidList = bidListRepository.save(existingBidList);
            log.info("BidList with ID {} updated successfully", savedBidList.getId());
            return savedBidList;
        } catch (DataAccessException e) {
            log.error("Failed to update BidList with ID {}", id, e);
            throw new EntitySaveException("Failed to update bid with ID " + id, e);
        }
    }

    /**
     * Deletes a bid list by its ID.
     *
     * @param id the ID of the bid list to delete
     * @throws IllegalArgumentException   if the ID is invalid
     * @throws EntityNotFoundException    if no bid list is found with the given ID
     * @throws EntityDeleteException      if deleting the bid list fails
     */
    public void deleteBidList(int id) {
        log.info("Deleting bid with ID: {}", id);

        if (id <= 0) {
            log.error("Invalid ID: {}", id);
            throw new IllegalArgumentException("ID must be a positive integer.");
        }

        if (!bidListRepository.existsById(id)) {
            log.error("BidList with ID {} not found", id);
            throw new EntityNotFoundException("BidList not found with ID: " + id);
        }

        try {
            bidListRepository.deleteById(id);
            log.info("BidList with ID {} deleted successfully", id);
        } catch (DataAccessException e) {
            log.error("Failed to delete BidList with ID {}", id, e);
            throw new EntityDeleteException("Failed to delete bid with ID " + id, e);
        }
    }
}
