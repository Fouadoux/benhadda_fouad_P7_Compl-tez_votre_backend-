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
    // TODO: Inject Bid service
    private final BidListService bidListService;

    public BidListController(BidListService bidListService) {
        this.bidListService = bidListService;
    }


    @GetMapping("/bidList/list")
    public String home(Model model)
    {
        List<BidList> bidLists= bidListService.getAllBidList();
        List<BidDTO> bidDTOList=bidListService.convertToDTOList(bidLists);
        model.addAttribute("bidLists",bidDTOList);
        return "bidList/list";
    }

    @GetMapping("/bidList/add")
    public String addBidForm(Model model) {
        model.addAttribute("bid", new BidDTO());
        return "bidList/add";
    }

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


    @GetMapping("/bidList/update/{id}")
    public String showUpdateForm(@PathVariable("id") Integer id, Model model) {
        // TODO: get Bid by Id and to model then show to the form
        BidDTO bidDTO= bidListService.getBidDTOById(id);
        model.addAttribute("bidList",bidDTO);
        return "bidList/update";
    }

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

    @GetMapping("/bidList/delete/{id}")
    public String deleteBid(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        bidListService.deleteBidList(id);
        redirectAttributes.addFlashAttribute("successMessage",
                "Bidlist deleted successfully");
        return "redirect:/bidList/list";
    }






  /*  @PostMapping("/bidList/delete/{id}")
    public String deleteBid(@PathVariable("id") Integer id, Model model) {
        try {
            bidListService.deleteBidList(id);
            return "redirect:/bidList/list";
        } catch (EntityNotFoundException e) {
            model.addAttribute("errorMessage", "BidList not found with ID: " + id);
            return "redirect:/bidList/list";
        } catch (EntityDeleteException e) {
            model.addAttribute("errorMessage", "Failed to delete BidList with ID: " + id);
            return "redirect:/bidList/list";
        }
    }

   */
}
