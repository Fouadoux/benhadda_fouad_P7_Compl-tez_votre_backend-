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

    /**
     * Constructs a new instance of {@link TradeService}.
     *
     * @param tradeRepository the repository for accessing trade data
     */
    public TradeService(TradeRepository tradeRepository) {
        this.tradeRepository = tradeRepository;
    }

    /**
     * Converts a {@link Trade} entity to a {@link TradeDTO}.
     *
     * @param trade the trade entity to convert
     * @return the converted {@link TradeDTO}
     */
    public TradeDTO convertToDTO(Trade trade){
        log.info("Converting bid ID {} to DTO",trade.getTradeId());

        TradeDTO dto=new TradeDTO();
        dto.setId(trade.getTradeId());
        dto.setAccount(trade.getAccount());
        dto.setType(trade.getType());
        dto.setBuyQuantity(trade.getBuyQuantity());
        return dto;
    }

    /**
     * Converts a list of {@link Trade} entities to a list of {@link TradeDTO}s.
     *
     * @param tradeList the list of trade entities to convert
     * @return the list of converted {@link TradeDTO}s
     */
    public List<TradeDTO> convertToDTOList (List<Trade> tradeList){
        log.info("Converting list of bid to DTOs");

        return tradeList.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves all trades as a list.
     *
     * @return a list of all trades
     */
    public List<Trade> getListToTradeList(){
        return tradeRepository.findAll();
    }

    /**
     * Retrieves a {@link TradeDTO} by its ID.
     *
     * @param id the ID of the trade to retrieve
     * @return the retrieved {@link TradeDTO}
     * @throws IllegalArgumentException  if the ID is invalid
     * @throws EntityNotFoundException   if no trade is found with the given ID
     */
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

    /**
     * Saves a new trade based on a {@link TradeDTO}.
     *
     * @param tradeDTO the data transfer object containing trade details
     * @return the saved {@link Trade} entity
     * @throws IllegalArgumentException  if the {@link TradeDTO} is null
     * @throws EntitySaveException       if saving the trade fails
     */
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

    /**
     * Updates an existing trade based on its ID and a {@link TradeDTO}.
     *
     * @param id       the ID of the trade to update
     * @param tradeDTO the data transfer object containing updated trade details
     * @return the updated {@link Trade} entity
     * @throws IllegalArgumentException  if the {@link TradeDTO} is null
     * @throws EntityNotFoundException   if no trade is found with the given ID
     * @throws EntitySaveException       if updating the trade fails
     */
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

    /**
     * Deletes a trade by its ID.
     *
     * @param id the ID of the trade to delete
     * @throws IllegalArgumentException   if the ID is invalid
     * @throws EntityNotFoundException    if no trade is found with the given ID
     * @throws EntityDeleteException      if deleting the trade fails
     */
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
