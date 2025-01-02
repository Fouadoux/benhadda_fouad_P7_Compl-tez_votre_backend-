package com.nnk.springboot.controllers;

import com.nnk.springboot.domain.BidList;
import com.nnk.springboot.dto.BidDTO;
import com.nnk.springboot.exception.EntitySaveException;
import com.nnk.springboot.service.BidListService;
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
public class BidListController {

    private final BidListService bidListService;

    /**
     * Constructs a new instance of {@link BidListController}.
     *
     * @param bidListService the service for managing bid lists
     */
    public BidListController(BidListService bidListService) {
        this.bidListService = bidListService;
    }

    /**
     * Displays the list of all bids.
     *
     * @param model the model to pass attributes to the view
     * @return the view name for the bid list page
     */
    @GetMapping("/bidList/list")
    public String home(Model model)
    {
        List<BidList> bidLists= bidListService.getAllBidList();
        List<BidDTO> bidDTOList=bidListService.convertToDTOList(bidLists);
        model.addAttribute("bidLists",bidDTOList);
        return "bidList/list";
    }

    /**
     * Displays the form to add a new bid.
     *
     * @param model the model to pass attributes to the view
     * @return the view name for the add bid form
     */
    @GetMapping("/bidList/add")
    public String addBidForm(Model model) {
        model.addAttribute("bid", new BidDTO());
        return "bidList/add";
    }

    /**
     * Validates and saves a new bid.
     *
     * @param bid                 the bid data transfer object to save
     * @param result              the binding result for validation errors
     * @param model               the model to pass attributes to the view
     * @param redirectAttributes  the attributes to pass on redirection
     * @return the view name or redirect URL
     */
    @PostMapping("/bidList/validate")
    public String validate(@Valid @ModelAttribute("bid") BidDTO bid, BindingResult result, Model model,
                           RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("bid", bid);
            return "bidList/add";
        }

        try {
            bidListService.saveBidList(bid);
            redirectAttributes.addFlashAttribute("successMessage", "Bid successfully added");

            return "redirect:/bidList/list";
        } catch (IllegalArgumentException | EntitySaveException e) {
            model.addAttribute("errorMessage", "An error occurred while saving the bid");
            model.addAttribute("bid", bid);
            return "bidList/add";
        }
    }

    /**
     * Displays the form to update an existing bid.
     *
     * @param id    the ID of the bid to update
     * @param model the model to pass attributes to the view
     * @return the view name for the update bid form
     */
    @GetMapping("/bidList/update/{id}")
    public String showUpdateForm(@PathVariable("id") Integer id, Model model) {
        // TODO: get Bid by Id and to model then show to the form
        BidDTO bidDTO= bidListService.getBidDTOById(id);
        model.addAttribute("bidList",bidDTO);
        return "bidList/update";
    }

    /**
     * Validates and updates an existing bid.
     *
     * @param id                 the ID of the bid to update
     * @param bidDTO             the bid data transfer object with updated details
     * @param result             the binding result for validation errors
     * @param model              the model to pass attributes to the view
     * @param redirectAttributes the attributes to pass on redirection
     * @return the view name or redirect URL
     */
    @PostMapping("/bidList/update/{id}")
    public String updateBid(@PathVariable("id") Integer id,
                            @Valid @ModelAttribute("bidList") BidDTO bidDTO,
                            BindingResult result, Model model,
                            RedirectAttributes redirectAttributes) {
        log.info("Updating bid with ID: {}", id);

        if (result.hasErrors()) {
            log.warn("Validation errors while updating bid with ID: {}", id);
            model.addAttribute("id", id);
            model.addAttribute("bidList", bidDTO);
            return "bidList/update";
        }

        try {
            bidListService.updateBidList(id, bidDTO);
            log.info("Bid with ID {} updated successfully", id);
            redirectAttributes.addFlashAttribute("successMessage", "Bid successfully Updated");

            return "redirect:/bidList/list";
        } catch (IllegalArgumentException | EntitySaveException e) {
            log.error("Error occurred while updating bid with ID: {}", id, e);
            model.addAttribute("id", id);
            model.addAttribute("bidList", bidDTO);
            model.addAttribute("errorMessage", "An error occurred while saving the bid. Please try again.");
            return "bidList/update";
        }
    }

    /**
     * Deletes a bid by its ID.
     *
     * @param id                 the ID of the bid to delete
     * @param redirectAttributes the attributes to pass on redirection
     * @return the redirect URL for the bid list page
     */
    @GetMapping("/bidList/delete/{id}")
    public String deleteBid(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        bidListService.deleteBidList(id);
        redirectAttributes.addFlashAttribute("successMessage",
                "Bidlist deleted successfully");
        return "redirect:/bidList/list";
    }

}
