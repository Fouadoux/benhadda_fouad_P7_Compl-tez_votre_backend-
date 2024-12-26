package com.nnk.springboot.service;

import com.nnk.springboot.domain.Trade;
import com.nnk.springboot.dto.TradeDTO;
import com.nnk.springboot.exception.EntityDeleteException;
import com.nnk.springboot.exception.EntityNotFoundException;
import com.nnk.springboot.exception.EntitySaveException;
import com.nnk.springboot.repositories.TradeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class TradeService {


    private final TradeRepository tradeRepository;


    public TradeService(TradeRepository tradeRepository) {
        this.tradeRepository = tradeRepository;
    }


    public TradeDTO convertToDTO(Trade trade){
        log.info("Converting bid ID {} to DTO",trade.getTradeId());

        TradeDTO dto=new TradeDTO();
        dto.setId(trade.getTradeId());
        dto.setAccount(trade.getAccount());
        dto.setType(trade.getType());
        dto.setBuyQuantity(trade.getBuyQuantity());
        return dto;
    }

    public List<TradeDTO> convertToDTOList (List<Trade> tradeList){
        log.info("Converting list of bid to DTOs");

        return tradeList.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<Trade> getListToTradeList(){
        return tradeRepository.findAll();
    }

    public TradeDTO getTradeDTOById(int id){
        log.info("Fetching trade with ID: {}", id);

        if (id <= 0) {
            log.error("Invalid ID.");
            throw new IllegalArgumentException("ID must be a positive integer.");
        }
        Trade trade = tradeRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("Trade with id " + id + " not found"));
        return convertToDTO(trade);

    }

    public Trade saveTrade(TradeDTO tradeDTO) {
        log.info("Adding a new trade to the bid list");

        if (tradeDTO == null) {
            log.error("Trade is null, cannot save.");
            throw new IllegalArgumentException("BidDTO cannot be null.");
        }

        Trade trade = new Trade();
        trade.setAccount(tradeDTO.getAccount());
        trade.setType(tradeDTO.getType());
        trade.setBuyQuantity(tradeDTO.getBuyQuantity());

        try {
            Trade result=tradeRepository.save(trade);
            log.info("Trade added successfully");
            return result;
        } catch (DataAccessException e) {
            log.error("Failed to save trade", e);
            throw new EntitySaveException("Failed to create trade.");
        }

    }

    public Trade updateBidList(int id, TradeDTO tradeDTO) {
        log.info("Updating bid with ID: {}", id);

        if (tradeDTO == null) {
            log.error("Trade is null, cannot save.");
            throw new IllegalArgumentException("BidDTO cannot be null.");
        }

        Trade trade = tradeRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("Trade with id " + id + " not found"));

        trade.setAccount(tradeDTO.getAccount());
        trade.setBuyQuantity(tradeDTO.getBuyQuantity());
        trade.setType(tradeDTO.getType());

        try {
            Trade result= tradeRepository.save(trade);
            log.info("Trade with ID {} updated successfully", id);
            return result;
        } catch (DataAccessException e) {
            log.error("Failed to update trade with ID {}", id, e);
            throw new EntitySaveException("Failed to update trade with ID " + id, e);
        }
    }

    public void deleteTrade(int id) {
        log.info("Delete trade with ID: {}", id);

        if (id <= 0) {
            log.error("Invalid ID: {}", id);
            throw new IllegalArgumentException("ID must be a positive integer.");
        }

        if (!tradeRepository.existsById(id)) {
            log.error("Trade with ID {} not found", id);
            throw new EntityNotFoundException("Trade not found with ID: " + id);
        }

        try {
            tradeRepository.deleteById(id);
            log.info("Trade with ID {} deleted successfully", id);
        } catch (DataAccessException e) {
            log.error("Failed to delete trade with ID {}", id, e);
            throw new EntityDeleteException("Failed to delete trade with ID " + id, e);
        }
    }
}
