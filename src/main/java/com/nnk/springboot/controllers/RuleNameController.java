package com.nnk.springboot.controllers;

import com.nnk.springboot.domain.RuleName;
import com.nnk.springboot.dto.RuleNameDTO;
import com.nnk.springboot.exception.EntitySaveException;
import com.nnk.springboot.service.RuleNameService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Slf4j
@Controller
public class RuleNameController {

    private final RuleNameService ruleNameService;

    /**
     * Constructs a new instance of {@link RuleNameController}.
     *
     * @param ruleNameService the service for managing rule names
     */
    public RuleNameController(RuleNameService ruleNameService) {
        this.ruleNameService = ruleNameService;
    }

    /**
     * Displays the list of all rule names.
     *
     * @param model the model to pass attributes to the view
     * @return the view name for the rule name list
     */
    @RequestMapping("/ruleName/list")
    public String home(Model model) {
        List<RuleName> ruleNameList = ruleNameService.getAllRuleName();
        List<RuleNameDTO> ratingDTOList = ruleNameService.convertToDTOList(ruleNameList);
        model.addAttribute("ruleNames", ratingDTOList);
        return "ruleName/list";
    }

    /**
     * Displays the form to add a new rule name.
     *
     * @param model the model to pass attributes to the view
     * @return the view name for the add rule name form
     */
    @GetMapping("/ruleName/add")
    public String addRuleForm(Model model) {
        model.addAttribute("ruleName", new RuleNameDTO());
        return "ruleName/add";
    }

    /**
     * Validates and saves a new rule name.
     *
     * @param ruleName           the rule name data transfer object to save
     * @param result             the binding result for validation errors
     * @param model              the model to pass attributes to the view
     * @param redirectAttributes the attributes to pass on redirection
     * @return the view name or redirect URL
     */
    @PostMapping("/ruleName/validate")
    public String validate(@Valid @ModelAttribute("ruleName") RuleNameDTO ruleName, BindingResult result,
                           Model model, RedirectAttributes redirectAttributes) {
        log.info("Validating ruleName {}", ruleName);
        if (result.hasErrors()) {
            model.addAttribute("ruleName", ruleName);
            return "ruleName/add";
        }
        try {
            log.info("Validating ruleName {}", ruleName);
            ruleNameService.saveRuleName(ruleName);
            redirectAttributes.addFlashAttribute("successMessage", "Rule successfully added");
            return "redirect:/ruleName/list";
        } catch (IllegalArgumentException | EntitySaveException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "An error occurred while saving the bid");
            model.addAttribute("ruleName", ruleName);
            return "ruleName/add";
        }

    }

    /**
     * Displays the form to update an existing rule name.
     *
     * @param id    the ID of the rule name to update
     * @param model the model to pass attributes to the view
     * @return the view name for the update rule name form
     */
    @GetMapping("/ruleName/update/{id}")
    public String showUpdateForm(@PathVariable("id") Integer id, Model model) {
        // TODO: get RuleName by Id and to model then show to the form
        RuleNameDTO ruleNameDTO = ruleNameService.getRuleNameDTOById(id);
        model.addAttribute("ruleName", ruleNameDTO);
        return "ruleName/update";
    }

    /**
     * Validates and updates an existing rule name.
     *
     * @param id                 the ID of the rule name to update
     * @param ruleName           the rule name data transfer object with updated details
     * @param result             the binding result for validation errors
     * @param model              the model to pass attributes to the view
     * @param redirectAttributes the attributes to pass on redirection
     * @return the view name or redirect URL
     */
    @PostMapping("/ruleName/update/{id}")
    public String updateRuleName(@PathVariable("id") Integer id, @Valid @ModelAttribute("ruleName") RuleNameDTO ruleName,
                                 BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        log.info("Updating ruleName {}", ruleName);
        if (result.hasErrors()) {
            log.warn("Validation errors while updating rule with ID: {}", id);
            model.addAttribute("ruleName", ruleName);
            model.addAttribute("id", id);
            return "ruleName/update";
        }
        try {
            ruleNameService.updateRuleName(id, ruleName);
            redirectAttributes.addFlashAttribute("successMessage", "Bid successfully Updated");
            log.info("Updating ruleName successfully");
            return "redirect:/ruleName/list";
        } catch (IllegalArgumentException | EntitySaveException e) {
            log.error("An error occurred while updating rule with ID: {}", id);
            model.addAttribute("ruleName", ruleName);
            model.addAttribute("id", id);
            return "ruleName/update";
        }
    }

    /**
     * Deletes a rule name by its ID.
     *
     * @param id                 the ID of the rule name to delete
     * @param redirectAttributes the attributes to pass on redirection
     * @return the redirect URL for the rule name list
     */
    @GetMapping("/ruleName/delete/{id}")
    public String deleteRuleName(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        // TODO: Find RuleName by Id and delete the RuleName, return to Rule list
        ruleNameService.deleteRuleNameById(id);
        redirectAttributes.addFlashAttribute("successMessage", "Rule successfully deleted");
        return "redirect:/ruleName/list";
    }
}
