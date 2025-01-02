package com.nnk.springboot.controllers;

import com.nnk.springboot.domain.Rating;
import com.nnk.springboot.dto.RatingDTO;
import com.nnk.springboot.exception.EntitySaveException;
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

    private final RatingService ratingService;

    /**
     * Constructs a new instance of {@link RatingController}.
     *
     * @param ratingService the service for managing ratings
     */
    public RatingController(RatingService ratingService) {
        this.ratingService = ratingService;
    }

    /**
     * Displays the list of all ratings.
     *
     * @param model the model to pass attributes to the view
     * @return the view name for the rating list
     */
    @RequestMapping("/rating/list")
    public String home(Model model)
    {
        List<Rating> ratings = ratingService.getAllRatings();
        List<RatingDTO> ratingDTOList = ratingService.convertToDTOList(ratings);
        model.addAttribute("ratings", ratingDTOList);
        return "rating/list";
    }

    /**
     * Displays the form to add a new rating.
     *
     * @param model the model to pass attributes to the view
     * @return the view name for the add rating form
     */
    @GetMapping("/rating/add")
    public String addRatingForm(Model model) {
        model.addAttribute("rating", new Rating());
        return "rating/add";
    }

    /**
     * Validates and saves a new rating.
     *
     * @param rating             the rating data transfer object to save
     * @param result             the binding result for validation errors
     * @param model              the model to pass attributes to the view
     * @param redirectAttributes the attributes to pass on redirection
     * @return the view name or redirect URL
     */
    @PostMapping("/rating/validate")
    public String validate(@Valid @ModelAttribute("rating") RatingDTO rating, BindingResult result,
                           Model model, RedirectAttributes redirectAttributes) {
        log.info("validating rating {}", rating);

        if (result.hasErrors()) {
           model.addAttribute("rating", rating);
           return "rating/add";
        }

        try{
            log.info("successfully added rating {}", rating);
            ratingService.saveRating(rating);
            redirectAttributes.addFlashAttribute("successMessage", "Rating successfully added");

            return "redirect:/rating/list";

        }catch (IllegalArgumentException | EntitySaveException e){
            model.addAttribute("rating", rating);
            model.addAttribute("error", e.getMessage());
            return "rating/add";
        }

    }

    /**
     * Displays the form to update an existing rating.
     *
     * @param id    the ID of the rating to update
     * @param model the model to pass attributes to the view
     * @return the view name for the update rating form
     */
    @GetMapping("/rating/update/{id}")
    public String showUpdateForm(@PathVariable("id") Integer id, Model model) {
        RatingDTO ratingDTO = ratingService.getRatingDTOById(id);
        model.addAttribute("rating", ratingDTO);
        return "rating/update";
    }

    /**
     * Validates and updates an existing rating.
     *
     * @param id                 the ID of the rating to update
     * @param rating             the rating data transfer object with updated details
     * @param result             the binding result for validation errors
     * @param model              the model to pass attributes to the view
     * @param redirectAttributes the attributes to pass on redirection
     * @return the view name or redirect URL
     */
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

    /**
     * Deletes a rating by its ID.
     *
     * @param id                 the ID of the rating to delete
     * @param redirectAttributes the attributes to pass on redirection
     * @return the redirect URL for the rating list
     */
    @GetMapping("/rating/delete/{id}")
    public String deleteRating(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {

        log.info("deleting rating {}", id);
        ratingService.deleteRating(id);
        redirectAttributes.addFlashAttribute("successMessage", "Rating successfully deleted");
        return "redirect:/rating/list";
    }
}
