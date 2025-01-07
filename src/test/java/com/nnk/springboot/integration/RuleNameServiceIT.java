package com.nnk.springboot.integration;

import com.nnk.springboot.domain.RuleName;
import com.nnk.springboot.dto.RuleNameDTO;
import com.nnk.springboot.exception.EntityNotFoundException;
import com.nnk.springboot.exception.EntitySaveException;
import com.nnk.springboot.repositories.RuleNameRepository;
import com.nnk.springboot.service.RuleNameService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class RuleNameServiceIT {

    @Autowired
    private RuleNameService ruleNameService;

    @Autowired
    private RuleNameRepository ruleNameRepository;

    @BeforeEach
    void setUp() {
        ruleNameRepository.deleteAll();
    }

    @Test
    void saveRuleName_ShouldSaveRuleSuccessfully() throws EntitySaveException {
        // Arrange
        RuleNameDTO ruleNameDTO = new RuleNameDTO();
        ruleNameDTO.setName("TestName");
        ruleNameDTO.setDescription("TestDescription");
        ruleNameDTO.setJson("TestJson");
        ruleNameDTO.setTemplate("TestTemplate");
        ruleNameDTO.setSql("TestSQL");
        ruleNameDTO.setSqlPart("TestSqlPart");

        // Act
        RuleName savedRule = ruleNameService.saveRuleName(ruleNameDTO);

        // Assert
        assertNotNull(savedRule.getId());
        assertEquals("TestName", savedRule.getName());
        assertEquals("TestDescription", savedRule.getDescription());
        assertEquals("TestJson", savedRule.getJson());
        assertEquals("TestTemplate", savedRule.getTemplate());
        assertEquals("TestSQL", savedRule.getSqlStr());
        assertEquals("TestSqlPart", savedRule.getSqlPart());
    }

    @Test
    void saveRuleName_ShouldThrowExceptionWhenDataInvalid() {
        // Arrange
        RuleNameDTO ruleNameDTO = new RuleNameDTO(); // Missing required fields

        // Act & Assert
        assertThrows(EntitySaveException.class, () -> ruleNameService.saveRuleName(ruleNameDTO));
    }

    @Test
    void getAllRuleName_ShouldReturnAllSavedRules() throws EntitySaveException {
        // Arrange
        RuleNameDTO rule1 = new RuleNameDTO();
        rule1.setName("Name1");
        rule1.setDescription("Description1");
        rule1.setJson("Json1");
        rule1.setTemplate("Template1");
        rule1.setSql("SQL1");
        rule1.setSqlPart("SqlPart1");
        ruleNameService.saveRuleName(rule1);

        RuleNameDTO rule2 = new RuleNameDTO();
        rule2.setName("Name2");
        rule2.setDescription("Description2");
        rule2.setJson("Json2");
        rule2.setTemplate("Template2");
        rule2.setSql("SQL2");
        rule2.setSqlPart("SqlPart2");
        ruleNameService.saveRuleName(rule2);

        // Act
        List<RuleName> allRules = ruleNameService.getAllRuleName();

        // Assert
        assertEquals(2, allRules.size());
    }

    @Test
    void getRuleNameDTOById_ShouldReturnRuleNameDTO() throws EntityNotFoundException, EntitySaveException {
        // Arrange
        RuleNameDTO ruleNameDTO = new RuleNameDTO();
        ruleNameDTO.setName("TestName");
        ruleNameDTO.setDescription("TestDescription");
        ruleNameDTO.setJson("TestJson");
        ruleNameDTO.setTemplate("TestTemplate");
        ruleNameDTO.setSql("TestSQL");
        ruleNameDTO.setSqlPart("TestSqlPart");
        RuleName savedRule = ruleNameService.saveRuleName(ruleNameDTO);

        // Act
        RuleNameDTO fetchedRule = ruleNameService.getRuleNameDTOById(savedRule.getId());

        // Assert
        assertNotNull(fetchedRule);
        assertEquals(savedRule.getId(), fetchedRule.getId());
    }

    @Test
    void getRuleNameDTOById_ShouldThrowExceptionWhenRuleNotFound() {
        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> ruleNameService.getRuleNameDTOById(999));
    }

    @Test
    void updateRuleName_ShouldUpdateRuleSuccessfully() throws EntitySaveException, EntityNotFoundException {
        // Arrange
        RuleNameDTO ruleNameDTO = new RuleNameDTO();
        ruleNameDTO.setName("TestName");
        ruleNameDTO.setDescription("TestDescription");
        ruleNameDTO.setJson("TestJson");
        ruleNameDTO.setTemplate("TestTemplate");
        ruleNameDTO.setSql("TestSQL");
        ruleNameDTO.setSqlPart("TestSqlPart");
        RuleName savedRule = ruleNameService.saveRuleName(ruleNameDTO);

        RuleNameDTO updatedRuleNameDTO = new RuleNameDTO();
        updatedRuleNameDTO.setName("UpdatedName");
        updatedRuleNameDTO.setDescription("UpdatedDescription");
        updatedRuleNameDTO.setJson("UpdatedJson");
        updatedRuleNameDTO.setTemplate("UpdatedTemplate");
        updatedRuleNameDTO.setSql("UpdatedSQL");
        updatedRuleNameDTO.setSqlPart("UpdatedSqlPart");

        // Act
        RuleName updatedRule = ruleNameService.updateRuleName(savedRule.getId(), updatedRuleNameDTO);

        // Assert
        assertEquals("UpdatedName", updatedRule.getName());
        assertEquals("UpdatedDescription", updatedRule.getDescription());
        assertEquals("UpdatedJson", updatedRule.getJson());
        assertEquals("UpdatedTemplate", updatedRule.getTemplate());
        assertEquals("UpdatedSQL", updatedRule.getSqlStr());
        assertEquals("UpdatedSqlPart", updatedRule.getSqlPart());
    }

    @Test
    void updateRuleName_ShouldThrowExceptionWhenRuleNotFound() {
        // Arrange
        RuleNameDTO updatedRuleNameDTO = new RuleNameDTO();
        updatedRuleNameDTO.setName("UpdatedName");
        updatedRuleNameDTO.setDescription("UpdatedDescription");
        updatedRuleNameDTO.setJson("UpdatedJson");
        updatedRuleNameDTO.setTemplate("UpdatedTemplate");
        updatedRuleNameDTO.setSql("UpdatedSQL");
        updatedRuleNameDTO.setSqlPart("UpdatedSqlPart");

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> ruleNameService.updateRuleName(999, updatedRuleNameDTO));
    }

    @Test
    void deleteRuleName_ShouldDeleteRuleSuccessfully() throws EntitySaveException {
        // Arrange
        RuleNameDTO ruleNameDTO = new RuleNameDTO();
        ruleNameDTO.setName("TestName");
        ruleNameDTO.setDescription("TestDescription");
        ruleNameDTO.setJson("TestJson");
        ruleNameDTO.setTemplate("TestTemplate");
        ruleNameDTO.setSql("TestSQL");
        ruleNameDTO.setSqlPart("TestSqlPart");
        RuleName savedRule = ruleNameService.saveRuleName(ruleNameDTO);

        // Act
        ruleNameService.deleteRuleNameById(savedRule.getId());
        boolean exists = ruleNameRepository.existsById(savedRule.getId());

        // Assert
        assertFalse(exists);
    }

    @Test
    void deleteRuleName_ShouldThrowExceptionWhenRuleNotFound() {
        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> ruleNameService.deleteRuleNameById(999));
    }
}
