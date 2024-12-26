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

    public BidListService(BidListRepository bidListRepository) {
        this.bidListRepository = bidListRepository;
    }

    public BidDTO convertToDTO(BidList bidList){
         log.info("Converting bid  to DTO");

         BidDTO dto=new BidDTO();
         dto.setId(bidList.getId());
         dto.setAccount(bidList.getAccount());
         dto.setBidQuantity(bidList.getBidQuantity());
         dto.setType(bidList.getType());

         return dto;
    }

    public List<BidDTO> convertToDTOList (List<BidList> bidLists){
        log.info("Converting list of bid to DTOs");

        return bidLists.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<BidList> getAllBidList(){
        return bidListRepository.findAll();
    }

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
