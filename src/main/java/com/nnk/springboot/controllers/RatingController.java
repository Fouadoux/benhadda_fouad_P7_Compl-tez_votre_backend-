package com.nnk.springboot.controllers;

import com.nnk.springboot.domain.Rating;
import com.nnk.springboot.dto.RatingDTO;
import com.nnk.springboot.exception.EntitySaveException;
import com.nnk.springboot.repositories.RatingRepository;
import com.nnk.springboot.service.RatingService;
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
public class RatingController {
    // TODO: Inject Rating service

    private final RatingService ratingService;

    public RatingController(RatingService ratingService) {
        this.ratingService = ratingService;
    }

    @RequestMapping("/rating/list")
    public String home(Model model)
    {
        // TODO: find all Rating, add to model
        List<Rating> ratings = ratingService.getListToRaitingList();
        List<RatingDTO> ratingDTOList = ratingService.convertToDTOList(ratings);
        model.addAttribute("ratings", ratingDTOList);
        return "rating/list";
    }

    @GetMapping("/rating/add")
    public String addRatingForm(Model model) {
        model.addAttribute("rating", new Rating());
        return "rating/add";
    }

    @PostMapping("/rating/validate")
    public String validate(@Valid @ModelAttribute("rating") RatingDTO rating, BindingResult result,
                           Model model, RedirectAttributes redirectAttributes) {
        // TODO: check data valid and save to db, after saving return Rating list
        log.info("validating rating {}", rating);

        if (result.hasErrors()) {
           model.addAttribute("rating", rating);
           return "rating/add";
        }

        try{
            log.info("successfully added rating {}", rating);
            ratingService.addRating(rating);
            redirectAttributes.addFlashAttribute("successMessage", "Rating successfully added");

            return "redirect:/rating/list";

        }catch (IllegalArgumentException | EntitySaveException e){
            model.addAttribute("rating", rating);
            model.addAttribute("error", e.getMessage());
            return "rating/add";
        }

    }

    @GetMapping("/rating/update/{id}")
    public String showUpdateForm(@PathVariable("id") Integer id, Model model) {
        // TODO: get Rating by Id and to model then show to the form
        RatingDTO ratingDTO = ratingService.getRatingDTOById(id);
        model.addAttribute("rating", ratingDTO);
        return "rating/update";
    }

    @PostMapping("/rating/update/{id}")
    public String updateRating(@PathVariable("id") Integer id, @Valid @ModelAttribute RatingDTO rating,
                             BindingResult result, Model model, RedirectAttributes redirectAttributes) {

        log.info("updating rating {}", rating);
        if (result.hasErrors()) {
            log.warn("error updating rating {}", rating);
            model.addAttribute("id", id);
            model.addAttribute("rating", rating);
            return "rating/update";
        }
        try {
            log.info("successfully updated rating {}", rating);
            ratingService.updateRating(id, rating);
            redirectAttributes.addFlashAttribute("successMessage", "Rating successfully Updated");

            return "redirect:/rating/list";
        }catch (IllegalArgumentException | EntitySaveException e){
            log.warn("error updating rating {}", rating);
            model.addAttribute("id", id);
            model.addAttribute("rating", rating);
            model.addAttribute("error", e.getMessage());
            return "rating/update";
        }
    }

    @GetMapping("/rating/delete/{id}")
    public String deleteRating(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {

        log.info("deleting rating {}", id);
        ratingService.deleteRating(id);
        redirectAttributes.addFlashAttribute("successMessage", "Rating successfully deleted");
        return "redirect:/rating/list";
    }
}
