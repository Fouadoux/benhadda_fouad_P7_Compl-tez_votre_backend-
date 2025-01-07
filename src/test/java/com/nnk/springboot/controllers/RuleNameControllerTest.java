package com.nnk.springboot.controllers;

import com.nnk.springboot.domain.RuleName;
import com.nnk.springboot.dto.RuleNameDTO;
import com.nnk.springboot.exception.EntitySaveException;
import com.nnk.springboot.service.RuleNameService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class RuleNameControllerTest {

    @Mock
    private RuleNameService ruleNameService;

    @Mock
    private Model model;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private RedirectAttributes redirectAttributes;

    @InjectMocks
    private RuleNameController ruleNameController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void home_ShouldReturnRuleNameListViewWithRuleNames() {
        // Arrange
        List<RuleName> ruleNames = Arrays.asList(new RuleName(), new RuleName());
        List<RuleNameDTO> ruleNameDTOs = Arrays.asList(new RuleNameDTO(), new RuleNameDTO());
        when(ruleNameService.getAllRuleName()).thenReturn(ruleNames);
        when(ruleNameService.convertToDTOList(ruleNames)).thenReturn(ruleNameDTOs);

        // Act
        String viewName = ruleNameController.home(model);

        // Assert
        assertEquals("ruleName/list", viewName);
        verify(model).addAttribute("ruleNames", ruleNameDTOs);
    }

    @Test
    void addRuleForm_ShouldReturnAddRuleView() {
        // Act
        String viewName = ruleNameController.addRuleForm(model);

        // Assert
        assertEquals("ruleName/add", viewName);
        verify(model).addAttribute(eq("ruleName"), any(RuleNameDTO.class));
    }

    @Test
    void validate_ShouldSaveRuleNameAndRedirectToRuleNameList() throws EntitySaveException {
        // Arrange
        RuleNameDTO ruleNameDTO = new RuleNameDTO();
        when(bindingResult.hasErrors()).thenReturn(false);

        // Act
        String viewName = ruleNameController.validate(ruleNameDTO, bindingResult, model, redirectAttributes);

        // Assert
        assertEquals("redirect:/ruleName/list", viewName);
        verify(ruleNameService).saveRuleName(ruleNameDTO);
        verify(redirectAttributes).addFlashAttribute("successMessage", "Rule successfully added");
    }

    @Test
    void validate_ShouldReturnAddRuleViewWhenValidationFails() {
        // Arrange
        RuleNameDTO ruleNameDTO = new RuleNameDTO();
        when(bindingResult.hasErrors()).thenReturn(true);

        // Act
        String viewName = ruleNameController.validate(ruleNameDTO, bindingResult, model, redirectAttributes);

        // Assert
        assertEquals("ruleName/add", viewName);
        verify(model).addAttribute("ruleName", ruleNameDTO);
    }

    @Test
    void validate_ShouldReturnAddRuleViewWhenSaveFails() throws EntitySaveException {
        // Arrange
        RuleNameDTO ruleNameDTO = new RuleNameDTO();
        when(bindingResult.hasErrors()).thenReturn(false);
        doThrow(new EntitySaveException("Error saving ruleName")).when(ruleNameService).saveRuleName(ruleNameDTO);

        // Act
        String viewName = ruleNameController.validate(ruleNameDTO, bindingResult, model, redirectAttributes);

        // Assert
        assertEquals("ruleName/add", viewName);
        verify(model).addAttribute("ruleName", ruleNameDTO);
    }

    @Test
    void showUpdateForm_ShouldReturnUpdateRuleNameView() {
        // Arrange
        int id = 1;
        RuleNameDTO ruleNameDTO = new RuleNameDTO();
        when(ruleNameService.getRuleNameDTOById(id)).thenReturn(ruleNameDTO);

        // Act
        String viewName = ruleNameController.showUpdateForm(id, model);

        // Assert
        assertEquals("ruleName/update", viewName);
        verify(model).addAttribute("ruleName", ruleNameDTO);
    }

    @Test
    void updateRuleName_ShouldUpdateRuleNameAndRedirectToRuleNameList() throws EntitySaveException {
        // Arrange
        int id = 1;
        RuleNameDTO ruleNameDTO = new RuleNameDTO();
        when(bindingResult.hasErrors()).thenReturn(false);

        // Act
        String viewName = ruleNameController.updateRuleName(id, ruleNameDTO, bindingResult, model, redirectAttributes);

        // Assert
        assertEquals("redirect:/ruleName/list", viewName);
        verify(ruleNameService).updateRuleName(id, ruleNameDTO);
        verify(redirectAttributes).addFlashAttribute("successMessage", "Bid successfully Updated");
    }

    @Test
    void updateRuleName_ShouldReturnUpdateRuleNameViewWhenValidationFails() {
        // Arrange
        int id = 1;
        RuleNameDTO ruleNameDTO = new RuleNameDTO();
        when(bindingResult.hasErrors()).thenReturn(true);

        // Act
        String viewName = ruleNameController.updateRuleName(id, ruleNameDTO, bindingResult, model, redirectAttributes);

        // Assert
        assertEquals("ruleName/update", viewName);
        verify(model).addAttribute("ruleName", ruleNameDTO);
    }

    @Test
    void deleteRuleName_ShouldDeleteRuleNameAndRedirectToRuleNameList() {
        // Arrange
        int id = 1;

        // Act
        String viewName = ruleNameController.deleteRuleName(id, redirectAttributes);

        // Assert
        assertEquals("redirect:/ruleName/list", viewName);
        verify(ruleNameService).deleteRuleNameById(id);
        verify(redirectAttributes).addFlashAttribute("successMessage", "Rule successfully deleted");
    }
}
