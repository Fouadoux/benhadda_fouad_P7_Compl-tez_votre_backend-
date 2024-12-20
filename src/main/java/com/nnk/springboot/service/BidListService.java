package com.nnk.springboot.service;

import com.nnk.springboot.domain.BidList;
import com.nnk.springboot.dto.BidDTO;
import com.nnk.springboot.exception.EntityDeleteException;
import com.nnk.springboot.exception.EntityNotFoundException;
import com.nnk.springboot.exception.EntitySaveException;
import com.nnk.springboot.repositories.BidListRepository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class BidListService {

    private BidListRepository bidListRepository;


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

    public BidDTO getBidDTOById(int id){
        BidList bidList = bidListRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("Bid list with id " + id + " not found"));
        return convertToDTO(bidList);

    }
    public void addBidList(BidDTO bidDTO) {
        log.info("Adding a new bid to the bid list");

        BidList bidList = new BidList();

        bidList.setAccount(bidDTO.getAccount());
        bidList.setType(bidDTO.getType());
        bidList.setBidQuantity(bidDTO.getBidQuantity());

        try {
            bidListRepository.save(bidList);
            log.info("BidList added successfully");
        } catch (EntitySaveException e) {
            log.error("Failed to save bid", e);
            throw new EntitySaveException("Failed to create bid.");
        }

    }



    public void updateBidList(int id, BidDTO bidDTO) {
        log.info("Updating bid with ID: {}", id);


        BidList bidList = bidListRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("BidList not found with Id: " + id));

        bidList.setAccount(bidDTO.getAccount());
        bidList.setType(bidDTO.getType());
        bidList.setBidQuantity(bidDTO.getBidQuantity());

        try {
            bidListRepository.save(bidList);
            log.info("BidList with ID {} updated successfully", id);
        } catch (Exception e) {
            log.error("Failed to update BidList with ID {}", id, e);
            throw new EntitySaveException("Failed to update bid with ID " + id, e);
        }
    }

    public void deleteBidList(int id) {
        log.info("Delete bid with ID: {}", id);

        if (!bidListRepository.existsById(id)) {
            log.error("BidList with ID {} not found", id);
            throw new EntityNotFoundException("BidList not found with ID: " + id);
        }

        try {
            bidListRepository.deleteById(id);
            log.info("BidList with ID {} deleted successfully", id);
        } catch (Exception e) {
            log.error("Failed to delete BidList with ID {}", id, e);
            throw new EntityDeleteException("Failed to delete bid with ID " + id, e);
        }
    }


}
