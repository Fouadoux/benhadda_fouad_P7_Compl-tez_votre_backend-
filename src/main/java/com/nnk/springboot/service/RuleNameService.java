package com.nnk.springboot.service;

import com.nnk.springboot.domain.RuleName;
import com.nnk.springboot.dto.RuleNameDTO;
import com.nnk.springboot.exception.EntityDeleteException;
import com.nnk.springboot.exception.EntityNotFoundException;
import com.nnk.springboot.exception.EntitySaveException;
import com.nnk.springboot.repositories.RuleNameRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class RuleNameService {

    private final RuleNameRepository ruleNameRepository;

    /**
     * Constructs a new instance of {@link RuleNameService}.
     *
     * @param ruleNameRepository the repository for accessing rule data
     */
    public RuleNameService(RuleNameRepository ruleNameRepository) {
        this.ruleNameRepository = ruleNameRepository;
    }

    /**
     * Converts a {@link RuleName} entity to a {@link RuleNameDTO}.
     *
     * @param ruleName the rule entity to convert
     * @return the converted {@link RuleNameDTO}
     */
    public RuleNameDTO convertToDTO(RuleName ruleName){
        log.info("Converting rule ID {} to DTO",ruleName.getId());

        RuleNameDTO dto=new RuleNameDTO();
        dto.setId(ruleName.getId());
        dto.setName(ruleName.getName());
        dto.setDescription(ruleName.getDescription());
        dto.setJson(ruleName.getJson());
        dto.setTemplate(ruleName.getTemplate());
        dto.setSql(ruleName.getSqlStr());
        dto.setSqlPart(ruleName.getSqlPart());

        return dto;
    }

    /**
     * Converts a list of {@link RuleName} entities to a list of {@link RuleNameDTO}s.
     *
     * @param ruleNameList the list of rule entities to convert
     * @return the list of converted {@link RuleNameDTO}s
     */
    public List<RuleNameDTO> convertToDTOList (List<RuleName> ruleNameList){
        log.info("Converting list of rules to DTOs");
        return ruleNameList.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves all rule entities.
     *
     * @return a list of all {@link RuleName} entities
     */
    public List<RuleName> getAllRuleName(){
        return ruleNameRepository.findAll();
    }

    /**
     * Retrieves a {@link RuleNameDTO} by its ID.
     *
     * @param id the ID of the rule to retrieve
     * @return the retrieved {@link RuleNameDTO}
     * @throws IllegalArgumentException  if the ID is invalid
     * @throws EntityNotFoundException   if no rule is found with the given ID
     */
    public RuleNameDTO getRuleNameDTOById(int id){
        log.info("Fetching BidDTO with ID: {}", id);

        if (id <= 0) {
            log.error("Invalid ID.");
            throw new IllegalArgumentException("ID must be a positive integer.");
        }

        RuleName ruleName = ruleNameRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("Rule with id " + id + " not found"));
        return convertToDTO(ruleName);
    }

    /**
     * Saves a new rule based on a {@link RuleNameDTO}.
     *
     * @param ruleNameDTO the data transfer object containing rule details
     * @return the saved {@link RuleName} entity
     * @throws EntitySaveException if saving the rule fails
     */
    public RuleName saveRuleName(RuleNameDTO ruleNameDTO) {
        log.info("Adding a new bid to the bid list");

        RuleName ruleName = new RuleName();

        ruleName.setName(ruleNameDTO.getName());
        ruleName.setDescription(ruleNameDTO.getDescription());
        ruleName.setJson(ruleNameDTO.getJson());
        ruleName.setTemplate(ruleNameDTO.getTemplate());
        ruleName.setSqlStr(ruleNameDTO.getSql());
        ruleName.setSqlPart(ruleNameDTO.getSqlPart());

        try {
           RuleName saveRule= ruleNameRepository.save(ruleName);
            log.info("Rule added successfully");
            return saveRule;
        } catch (DataAccessException e) {
            log.error("Failed to save rule", e);
            throw new EntitySaveException("Failed to create rule.");
        }

    }

    /**
     * Updates an existing rule based on its ID and a {@link RuleNameDTO}.
     *
     * @param id           the ID of the rule to update
     * @param ruleNameDTO  the data transfer object containing updated rule details
     * @return the updated {@link RuleName} entity
     * @throws EntityNotFoundException   if no rule is found with the given ID
     * @throws EntitySaveException       if updating the rule fails
     */
    public RuleName updateRuleName(int id, RuleNameDTO ruleNameDTO) {
        log.info("Updating bid with ID: {}", id);

        RuleName ruleName = ruleNameRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("Rule with id " + id + " not found"));

        ruleName.setName(ruleNameDTO.getName());
        ruleName.setDescription(ruleNameDTO.getDescription());
        ruleName.setJson(ruleNameDTO.getJson());
        ruleName.setTemplate(ruleNameDTO.getTemplate());
        ruleName.setSqlStr(ruleNameDTO.getSql());
        ruleName.setSqlPart(ruleNameDTO.getSqlPart());

        try {
            RuleName saveRule= ruleNameRepository.save(ruleName);
            log.info("Rule with ID {} updated successfully", id);
            return saveRule;
        } catch (Exception e) {
            log.error("Failed to update rule with ID {}", id, e);
            throw new EntitySaveException("Failed to update rule with ID " + id, e);
        }
    }
    /**
     * Deletes a rule by its ID.
     *
     * @param id the ID of the rule to delete
     * @throws IllegalArgumentException   if the ID is invalid
     * @throws EntityNotFoundException    if no rule is found with the given ID
     * @throws EntityDeleteException      if deleting the rule fails
     */
    public void deleteRuleNameById(int id) {
        log.info("Delete bid with ID: {}", id);

        if (id <= 0) {
            log.error("Invalid ID.");
            throw new IllegalArgumentException("ID must be a positive integer.");
        }

        if (!ruleNameRepository.existsById(id)) {
            log.error("Rule with ID {} not found", id);
            throw new EntityNotFoundException("Rule not found with ID: " + id);
        }

        try {
            ruleNameRepository.deleteById(id);
            log.info("Rule with ID {} deleted successfully", id);
        } catch (Exception e) {
            log.error("Failed to delete Rule with ID {}", id, e);
            throw new EntityDeleteException("Failed to delete rule with ID " + id, e);
        }
    }
}
