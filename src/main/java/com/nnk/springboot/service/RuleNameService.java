package com.nnk.springboot.service;

import com.nnk.springboot.domain.RuleName;
import com.nnk.springboot.dto.RuleNameDTO;
import com.nnk.springboot.exception.EntityDeleteException;
import com.nnk.springboot.exception.EntityNotFoundException;
import com.nnk.springboot.exception.EntitySaveException;
import com.nnk.springboot.repositories.RuleNameRepository;
import io.micrometer.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class RuleNameService {

    private final RuleNameRepository ruleNameRepository;

    public RuleNameService(RuleNameRepository ruleNameRepository) {
        this.ruleNameRepository = ruleNameRepository;
    }

    private void validateRuleDTO(RuleNameDTO ruleNameDTO) {
        if (StringUtils.isBlank(ruleNameDTO.getName())) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        if (StringUtils.isBlank(ruleNameDTO.getDescription())) {
            throw new IllegalArgumentException("Description cannot be null or empty");
        }
        if (StringUtils.isBlank(ruleNameDTO.getJson())) {
            throw new IllegalArgumentException("Json cannot be null or empty");
        }
        if (StringUtils.isBlank(ruleNameDTO.getTemplate())) {
            throw new IllegalArgumentException("Template cannot be null or empty");
        }
        if (StringUtils.isBlank(ruleNameDTO.getSql())) {
            throw new IllegalArgumentException("Sql cannot be null or empty");
        }
        if (StringUtils.isBlank(ruleNameDTO.getSqlPart())) {
            throw new IllegalArgumentException("SqlPart cannot be null or empty");
        }
    }

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

    public List<RuleNameDTO> convertToDTOList (List<RuleName> ruleNameList){
        log.info("Converting list of rules to DTOs");
        return ruleNameList.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<RuleName> getListToBigList(){
        return ruleNameRepository.findAll();
    }

    @Transactional
    public void addRuleName(RuleNameDTO ruleNameDTO) {
        log.info("Adding a new bid to the bid list");

        validateRuleDTO(ruleNameDTO);

        RuleName ruleName = new RuleName();

        ruleName.setName(ruleNameDTO.getName());
        ruleName.setDescription(ruleNameDTO.getDescription());
        ruleName.setJson(ruleNameDTO.getJson());
        ruleName.setTemplate(ruleNameDTO.getTemplate());
        ruleName.setSqlStr(ruleNameDTO.getSql());
        ruleName.setSqlPart(ruleNameDTO.getSqlPart());

        try {
            ruleNameRepository.save(ruleName);
            log.info("Rule added successfully");
        } catch (EntitySaveException e) {
            log.error("Failed to save rule", e);
            throw new EntitySaveException("Failed to create rule.");
        }

    }

    public RuleNameDTO getRuleNameDTOById(int id){
        RuleName ruleName = ruleNameRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("Rule with id " + id + " not found"));
        return convertToDTO(ruleName);
    }

    public void updateRuleName(int id, RuleNameDTO ruleNameDTO) {
        log.info("Updating bid with ID: {}", id);

        validateRuleDTO(ruleNameDTO);

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
            ruleNameRepository.save(ruleName);
            log.info("Rule with ID {} updated successfully", id);
        } catch (Exception e) {
            log.error("Failed to update rule with ID {}", id, e);
            throw new EntitySaveException("Failed to update rule with ID " + id, e);
        }
    }

    public void delteRuleNameById(int id) {
        log.info("Delete bid with ID: {}", id);

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
