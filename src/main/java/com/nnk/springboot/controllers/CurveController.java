package com.nnk.springboot.controllers;

import com.nnk.springboot.domain.CurvePoint;
import com.nnk.springboot.dto.CurveDTO;
import com.nnk.springboot.exception.EntitySaveException;
import com.nnk.springboot.service.CurveService;
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
public class CurveController {

    private final CurveService curveService;

    /**
     * Constructs a new instance of {@link CurveController}.
     *
     * @param curveService the service for managing curve points
     */
    public CurveController(CurveService curveService) {
        this.curveService = curveService;
    }

    /**
     * Displays the list of all curve points.
     *
     * @param model the model to pass attributes to the view
     * @return the view name for the curve point list
     */
    @RequestMapping("/curvePoint/list")
    public String home(Model model)
    {
        List<CurvePoint> curvePointList=curveService.getAllCurvePoint();
        List<CurveDTO> curveDTOList=curveService.convertToDtoList(curvePointList);
        model.addAttribute("curvePoints",curveDTOList);

        return "curvePoint/list";
    }

    /**
     * Displays the form to add a new curve point.
     *
     * @param model the model to pass attributes to the view
     * @return the view name for the add curve point form
     */
    @GetMapping("/curvePoint/add")
    public String addBidForm(Model model) {

        model.addAttribute("curvePoint", new CurveDTO());
        return "curvePoint/add";
    }

    /**
     * Validates and saves a new curve point.
     *
     * @param curveDTO            the curve point data transfer object
     * @param result              the binding result for validation errors
     * @param model               the model to pass attributes to the view
     * @param redirectAttributes  the attributes to pass on redirection
     * @return the view name or redirect URL
     */
    @PostMapping("/curvePoint/validate")
    public String validate(@Valid @ModelAttribute("curvePoint") CurveDTO curveDTO, BindingResult result, Model model,RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("curvePoint", curveDTO);
            return "curvePoint/add";
        }

        try{
            curveService.saveCurvePoint(curveDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Curve successfully added");

            return "redirect:/curvePoint/list";
        }catch (IllegalArgumentException | EntitySaveException e) {
            model.addAttribute("errorMessage", "An error occurred while saving the bid");
            model.addAttribute("curvePoint", curveDTO);
            return "curvePoint/add";
        }

    }

    /**
     * Displays the form to update an existing curve point.
     *
     * @param id    the ID of the curve point to update
     * @param model the model to pass attributes to the view
     * @return the view name for the update curve point form
     */
    @GetMapping("/curvePoint/update/{id}")
    public String showUpdateForm(@PathVariable("id") Integer id, Model model) {
        CurveDTO curveDTO=curveService.getCurveDTOById(id);
        model.addAttribute("curvePoint", curveDTO);
        return "curvePoint/update";
    }

    /**
     * Validates and updates an existing curve point.
     *
     * @param id                 the ID of the curve point to update
     * @param curvePoint         the curve point data transfer object with updated details
     * @param result             the binding result for validation errors
     * @param model              the model to pass attributes to the view
     * @param redirectAttributes the attributes to pass on redirection
     * @return the view name or redirect URL
     */
    @PostMapping("/curvePoint/update/{id}")
    public String updateBid(@PathVariable("id") Integer id, @Valid @ModelAttribute("curvePoint") CurveDTO curvePoint,
                             BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        log.info("update curve point with id " + id);

        if (result.hasErrors()) {
            log.warn("Validation errors while updating bid with ID: {}", id);
            model.addAttribute("id", id);
            model.addAttribute("curvePoint", curvePoint);
        }
        try{
            curveService.updateCurvePoint(id, curvePoint);
            log.info("update curve point with id " + id);
            redirectAttributes.addFlashAttribute("successMessage", "Curve successfully Updated");

            return "redirect:/curvePoint/list";
        }catch (IllegalArgumentException | EntitySaveException e) {
            log.error("An error occurred while updating bid with ID: {}", id);
            model.addAttribute("errorMessage", "An error occurred while updating bid");
            model.addAttribute("curvePoint", curvePoint);
            model.addAttribute("id", id);
            return "curvePoint/update";
        }
    }

    /**
     * Deletes a curve point by its ID.
     *
     * @param id                 the ID of the curve point to delete
     * @param redirectAttributes the attributes to pass on redirection
     * @return the redirect URL for the curve point list
     */
    @GetMapping("/curvePoint/delete/{id}")
    public String deleteBid(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        curveService.deleteCurvePoint(id);
        redirectAttributes.addFlashAttribute("successMessage",
                "Curve point deleted successfully");
        return "redirect:/curvePoint/list";
    }
}
