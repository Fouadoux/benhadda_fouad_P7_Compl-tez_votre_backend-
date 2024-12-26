package com.nnk.springboot.service;

import com.nnk.springboot.domain.BidList;
import com.nnk.springboot.domain.RuleName;
import com.nnk.springboot.dto.BidDTO;
import com.nnk.springboot.dto.RuleNameDTO;
import com.nnk.springboot.exception.EntityDeleteException;
import com.nnk.springboot.exception.EntityNotFoundException;
import com.nnk.springboot.exception.EntitySaveException;
import com.nnk.springboot.repositories.BidListRepository;
import com.nnk.springboot.repositories.RuleNameRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@SpringBootTest
class RuleNameServiceTest {

    @Mock
    private RuleNameRepository ruleNameRepository;

    @InjectMocks
    private RuleNameService ruleNameService;


    @Test
    void testGetAllBidList() {
        //Arrange
        RuleName r1 = new RuleName();
        r1.setName("test1");
        r1.setDescription("description");
        r1.setJson("json");
        r1.setTemplate("template");
        r1.setSqlPart("SqlPart");
        r1.setSqlStr("sqlStr");

        RuleName r2 = new RuleName();
        r2.setName("test2");
        r2.setDescription("description");
        r2.setJson("json");
        r2.setTemplate("template");
        r2.setSqlPart("SqlPart");
        r2.setSqlStr("sqlStr");

        when(ruleNameRepository.findAll()).thenReturn(List.of(r1,r2));
        //ACT

        List<RuleName> ruleNames= ruleNameService.getAllRuleName();

        //Assert
        assertNotNull(ruleNames);
    }

    @Test
    void testConvertToDto() {
        RuleName r1 = new RuleName();
        r1.setName("test1");
        r1.setDescription("description");
        r1.setJson("json");
        r1.setTemplate("template");
        r1.setSqlPart("SqlPart");
        r1.setSqlStr("sqlStr");

        RuleNameDTO ruleNameDTO = ruleNameService.convertToDTO(r1);

        assertNotNull(ruleNameDTO);
        assertEquals("test1", ruleNameDTO.getName());
        assertEquals("description", ruleNameDTO.getDescription());
        assertEquals("json", ruleNameDTO.getJson());
        assertEquals("template", ruleNameDTO.getTemplate());
        assertEquals("SqlPart", ruleNameDTO.getSqlPart());
        assertEquals("sqlStr", ruleNameDTO.getSql());

    }

    @Test
    void testConvertToDTOList(){
        RuleName r1 = new RuleName();
        r1.setName("test1");
        r1.setDescription("description");
        r1.setJson("json");
        r1.setTemplate("template");
        r1.setSqlPart("SqlPart");
        r1.setSqlStr("sqlStr");

        RuleName r2 = new RuleName();
        r2.setName("test2");
        r2.setDescription("description");
        r2.setJson("json");
        r2.setTemplate("template");
        r2.setSqlPart("SqlPart");
        r2.setSqlStr("sqlStr");

        List<RuleNameDTO> ruleNameDTOS = ruleNameService.convertToDTOList(List.of(r1,r2));

        assertNotNull(ruleNameDTOS);
        assertEquals(2, ruleNameDTOS.size());
        assertEquals("test1", ruleNameDTOS.get(0).getName());
        assertEquals("test2", ruleNameDTOS.get(1).getName());

    }

    @Test
    void testSaveRuleName_Successfully() {

        RuleNameDTO ruleNameDTO = new RuleNameDTO();
        ruleNameDTO.setName("test1");
        ruleNameDTO.setDescription("description");
        ruleNameDTO.setJson("json");
        ruleNameDTO.setTemplate("template");
        ruleNameDTO.setSqlPart("SqlPart");
        ruleNameDTO.setSql("sqlStr");

        RuleName savedMock = new RuleName();
        savedMock.setName("test1");
        savedMock.setDescription("description");
        savedMock.setJson("json");
        savedMock.setTemplate("template");
        savedMock.setSqlPart("SqlPart");
        savedMock.setSqlStr("sqlStr");

        when(ruleNameRepository.save(any(RuleName.class))).thenReturn(savedMock);

        RuleName result = ruleNameService.saveRuleName(ruleNameDTO);

        assertNotNull(result);
        assertEquals("test1", result.getName());
        assertEquals("description", result.getDescription());
        assertEquals("json", result.getJson());
        assertEquals("template", result.getTemplate());
        assertEquals("SqlPart", result.getSqlPart());
        assertEquals("sqlStr", result.getSqlStr());
        verify(ruleNameRepository, times(1)).save(any(RuleName.class));

    }

    @Test
    void testSaveRuleName_ThrowsEntitySaveException() {
        // GIVEN
        RuleNameDTO ruleNameDTO = new RuleNameDTO();
        ruleNameDTO.setName("test1");
        ruleNameDTO.setDescription("description");
        ruleNameDTO.setJson("json");
        ruleNameDTO.setTemplate("template");
        ruleNameDTO.setSqlPart("SqlPart");
        ruleNameDTO.setSql("sqlStr");

        // Simuler une exception lors de la sauvegarde
        doThrow(new DataAccessException("DB error") {}).when(ruleNameRepository).save(any(RuleName.class));

        // WHEN + THEN
        EntitySaveException ex = assertThrows(EntitySaveException.class, () -> {
            ruleNameService.saveRuleName(ruleNameDTO);
        });

        assertEquals("Failed to create rule.", ex.getMessage());
        verify(ruleNameRepository, times(1)).save(any(RuleName.class));
    }

    @Test
    void testUpdateRuleName_Success() {
        // GIVEN
        int id = 1;
        RuleNameDTO ruleNameDTO = new RuleNameDTO();
        ruleNameDTO.setName("testNew");
        ruleNameDTO.setDescription("newDescription");
        ruleNameDTO.setJson("newJson");
        ruleNameDTO.setTemplate("newTemplate");
        ruleNameDTO.setSqlPart("newSqlPart");
        ruleNameDTO.setSql("newSqlStr");

        RuleName existingRule = new RuleName();
        existingRule.setName("test1");
        existingRule.setDescription("description");
        existingRule.setJson("json");
        existingRule.setTemplate("template");
        existingRule.setSqlPart("SqlPart");
        existingRule.setSqlStr("sqlStr");

        when(ruleNameRepository.findById(id)).thenReturn(Optional.of(existingRule));
        when(ruleNameRepository.save(any(RuleName.class))).thenReturn(existingRule);

        // WHEN
        RuleName result = ruleNameService.updateRuleName(id, ruleNameDTO);

        // THEN
        assertNotNull(result);
        assertEquals("testNew", result.getName());
        assertEquals("newDescription", result.getDescription());
        assertEquals("newJson", result.getJson());
        assertEquals("newTemplate", result.getTemplate());
        assertEquals("newSqlPart", result.getSqlPart());
        assertEquals("newSqlStr", result.getSqlStr());

        verify(ruleNameRepository).findById(id);
        verify(ruleNameRepository).save(existingRule);
    }

    @Test
    void testUpdateRuleName_NotFound() {
        // GIVEN
        int id = 999;
        RuleNameDTO dto = new RuleNameDTO();
        // ...

        when(ruleNameRepository.findById(id)).thenReturn(Optional.empty());

        // WHEN + THEN
        assertThrows(EntityNotFoundException.class, () -> {
            ruleNameService.updateRuleName(id, dto);
        });

        verify(ruleNameRepository).findById(id);
        verify(ruleNameRepository, never()).save(any());
    }

    @Test
    void testUpdateRuleName_DataAccessException() {
        // GIVEN
        int id = 1;
        RuleNameDTO ruleNameDTO = new RuleNameDTO();
        ruleNameDTO.setName("testNew");
        ruleNameDTO.setDescription("newDescription");
        ruleNameDTO.setJson("newJson");
        ruleNameDTO.setTemplate("newTemplate");
        ruleNameDTO.setSqlPart("newSqlPart");
        ruleNameDTO.setSql("newSqlStr");

        RuleName existingRule = new RuleName();
        existingRule.setName("test1");
        existingRule.setDescription("description");
        existingRule.setJson("json");
        existingRule.setTemplate("template");
        existingRule.setSqlPart("SqlPart");
        existingRule.setSqlStr("sqlStr");

        when(ruleNameRepository.findById(id)).thenReturn(Optional.of(existingRule));
        doThrow(new DataAccessException("DB error") {}).when(ruleNameRepository).save(existingRule);

        // WHEN + THEN
        EntitySaveException ex = assertThrows(EntitySaveException.class, () -> {
            ruleNameService.updateRuleName(id, ruleNameDTO);
        });
        assertEquals("Failed to update rule with ID 1", ex.getMessage());

        verify(ruleNameRepository).findById(id);
        verify(ruleNameRepository).save(existingRule);
    }

    @Test
    void testDeleteRuleName_Successfully() {
        // GIVEN
        int id = 1;

        // Simuler l'existence de l'entité
        when(ruleNameRepository.existsById(id)).thenReturn(true);

        // WHEN
        ruleNameService.delteRuleNameById(id);

        // THEN
        verify(ruleNameRepository).existsById(id);
        verify(ruleNameRepository).deleteById(id);
    }

    @Test
    void testDeleteRuleName_NotFound() {
        // GIVEN
        int id = 999;

        // Simuler l'absence de l'entité
        when(ruleNameRepository.existsById(id)).thenReturn(false);

        // WHEN + THEN
        assertThrows(EntityNotFoundException.class, () -> {
            ruleNameService.delteRuleNameById(id);
        });

        // Vérifier que deleteById n'a pas été appelé
        verify(ruleNameRepository, never()).deleteById(anyInt());
    }

    @Test
    void testDeleteRuleName_DataAccessException() {
        // GIVEN
        int id = 2;
        when(ruleNameRepository.existsById(id)).thenReturn(true);

        // Simuler une exception au moment de la suppression
        doThrow(new DataAccessException("DB error") {})
                .when(ruleNameRepository).deleteById(id);

        // WHEN + THEN
        assertThrows(EntityDeleteException.class, () -> {
            ruleNameService.delteRuleNameById(id);
        });

        verify(ruleNameRepository).deleteById(id);
    }

    @Test
    void testDeleteRuleName_InvalidId() {
        // GIVEN
        int invalidId = 0;

        // WHEN + THEN
        assertThrows(IllegalArgumentException.class, () -> {
            ruleNameService.delteRuleNameById(invalidId);
        });

        // Vérifier que le repository n'est jamais appelé
        verify(ruleNameRepository, never()).existsById(anyInt());
        verify(ruleNameRepository, never()).deleteById(anyInt());
    }

    @Test
    void testGetRuleNameDTOById_Success() {
        // GIVEN
        int validId = 1;
        RuleName mockRuleName = new RuleName();
        mockRuleName.setName("testNew");
        mockRuleName.setDescription("newDescription");
        mockRuleName.setJson("newJson");
        mockRuleName.setTemplate("newTemplate");
        mockRuleName.setSqlPart("newSqlPart");
        mockRuleName.setSqlStr("newSqlStr");

        // Simulation : l'entité est trouvée
        when(ruleNameRepository.findById(1))
                .thenReturn(Optional.of(mockRuleName));

        // WHEN
        RuleNameDTO result = ruleNameService.getRuleNameDTOById(validId);

        // THEN
        assertNotNull(result);
        assertEquals("testNew", result.getName());
        assertEquals("newDescription", result.getDescription());
        assertEquals("newJson", result.getJson());
        assertEquals("newTemplate", result.getTemplate());

    }

    @Test
    void testGetRuleNameDTOById_NotFound() {
        // GIVEN
        int nonExistentId = 999;
        when(ruleNameRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // WHEN + THEN
        assertThrows(EntityNotFoundException.class, () -> {
            ruleNameService.getRuleNameDTOById(nonExistentId);
        });
        verify(ruleNameRepository).findById(nonExistentId);
    }

    @Test
    void testGetRoleNameDTOById_InvalidId() {
        // GIVEN
        int invalidId = 0; // ou -1

        // WHEN + THEN
        assertThrows(IllegalArgumentException.class, () -> {
            ruleNameService.getRuleNameDTOById(invalidId);
        });

        // Vérifie que le repository n'est jamais appelé
        verify(ruleNameRepository, never()).findById(anyInt());
    }


}