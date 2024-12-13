package com.nnk.springboot.controllers;

import com.nnk.springboot.domain.BidList;
import com.nnk.springboot.dto.BidDTO;
import com.nnk.springboot.exception.EntityDeleteException;
import com.nnk.springboot.exception.EntityNotFoundException;
import com.nnk.springboot.exception.EntitySaveException;
import com.nnk.springboot.repositories.BidListRepository;
import com.nnk.springboot.service.BidListService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

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
        List<BidList> bidLists= bidListService.getListToBigList();
        List<BidDTO> bidDTOList=bidListService.convertToDTOList(bidLists);
        model.addAttribute("bidLists",bidDTOList);
        return "bidList/list";
    }

   /* @GetMapping("/bidList/add")
    public String addBidForm(BidList bid) {
        return "bidList/add";
    }


    //version de base du projet
    @PostMapping("/bidList/validate")
    public String validate(@Valid BidList bid, BindingResult result, Model model) {
        // TODO: check data valid and save to db, after saving return bid list
        return "bidList/add";
    }*/

    //ma version

    @GetMapping("/bidList/add")
    public String addBidForm(Model model) {
        model.addAttribute("bid", new BidDTO());
        return "bidList/add";
    }


    @PostMapping("/bidList/validate")
    public String validate(@Valid @ModelAttribute("bid") BidDTO bid, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("bid", bid);
            return "bidList/add";
        }

        try {
            bidListService.addBidList(bid);
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
        BidDTO bidDTO= bidListService.getBidbyID(id);
        model.addAttribute("bidList",bidDTO);
        model.addAttribute("type",bidDTO.getType());
        model.addAttribute("account",bidDTO.getAccount());
        model.addAttribute("bidQuantity",bidDTO.getBidQuantity());
        return "bidList/update";
    }

    //ancien version
  /*  @PostMapping("/bidList/update/{id}")
    public String updateBid(@PathVariable("id") Integer id, @Valid BidList bidList,
                             BindingResult result, Model model) {
        // TODO: check required fields, if valid call service to update Bid and return list Bid
        return "redirect:/bidList/list";
    }*/

    @PostMapping("/bidList/update/{id}")
    public String updateBid(@PathVariable("id") Integer id,
                            @Valid @ModelAttribute("bidList") BidDTO bidDTO,
                            BindingResult result,
                            Model model) {
        log.info("Updating bid with ID: {}", id);

        // Validation des erreurs de formulaire
        if (result.hasErrors()) {
            log.warn("Validation errors while updating bid with ID: {}", id);
            model.addAttribute("id", id); // Ajouter l'ID pour la vue
            model.addAttribute("bidList", bidDTO); // Réinjecter l'objet pour pré-remplir le formulaire
            return "bidList/update"; // Vue de mise à jour du formulaire
        }

        try {
            // Appel du service pour la mise à jour
            bidListService.updateBidList(id, bidDTO);
            log.info("Bid with ID {} updated successfully", id);
            return "redirect:/bidList/list";
        } catch (IllegalArgumentException | EntitySaveException e) {
            log.error("Error occurred while updating bid with ID: {}", id, e);
            model.addAttribute("id", id); // Ajouter l'ID pour la vue
            model.addAttribute("bidList", bidDTO); // Réinjecter l'objet pour corriger les données
            model.addAttribute("errorMessage", "An error occurred while saving the bid. Please try again.");
            return "bidList/update"; // Retourner à la vue en cas d'erreur
        }
    }




   /*
    @GetMapping("/bidList/delete/{id}")
    public String deleteBid(@PathVariable("id") Integer id, Model model) {
        bidListService.deleteBidList(id);
        return "redirect:/bidList/list";
    }
    */

    @PostMapping("/bidList/delete/{id}")
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
}
