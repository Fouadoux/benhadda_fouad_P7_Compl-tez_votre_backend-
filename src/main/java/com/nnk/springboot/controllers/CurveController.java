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
    // TODO: Inject Curve Point service

    private final CurveService curveService;

    public CurveController(CurveService curveService) {
        this.curveService = curveService;
    }

    @RequestMapping("/curvePoint/list")
    public String home(Model model)
    {
        // TODO: find all Curve Point, add to model
        List<CurvePoint> curvePointList=curveService.getAllCurvePoint();
        List<CurveDTO> curveDTOList=curveService.convertToDtoList(curvePointList);
        model.addAttribute("curvePoints",curveDTOList);


        return "curvePoint/list";
    }

    @GetMapping("/curvePoint/add")
    public String addBidForm(Model model) {

        model.addAttribute("curvePoint", new CurveDTO());
        return "curvePoint/add";
    }

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

    @GetMapping("/curvePoint/update/{id}")
    public String showUpdateForm(@PathVariable("id") Integer id, Model model) {
        // TODO: get CurvePoint by Id and to model then show to the form
        CurveDTO curveDTO=curveService.getCurveDTOById(id);
        model.addAttribute("curvePoint", curveDTO);
        return "curvePoint/update";
    }

    @PostMapping("/curvePoint/update/{id}")
    public String updateBid(@PathVariable("id") Integer id, @Valid @ModelAttribute("curvePoint") CurveDTO curvePoint,
                             BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        // TODO: check required fields, if valid call service to update Curve and return Curve list
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

    @GetMapping("/curvePoint/delete/{id}")
    public String deleteBid(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        curveService.deleteCurvePoint(id);
        redirectAttributes.addFlashAttribute("successMessage",
                "Curve point deleted successfully");
        return "redirect:/curvePoint/list";
    }
}
