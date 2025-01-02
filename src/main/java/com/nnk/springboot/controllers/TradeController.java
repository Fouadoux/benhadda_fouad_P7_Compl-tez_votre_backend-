package com.nnk.springboot.controllers;

import com.nnk.springboot.domain.Trade;
import com.nnk.springboot.dto.TradeDTO;
import com.nnk.springboot.service.TradeService;
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
public class TradeController {

    private final TradeService tradeService;

    /**
     * Constructs a new instance of {@link TradeController}.
     *
     * @param tradeService the service for managing trades
     */
    public TradeController(TradeService tradeService) {
        this.tradeService = tradeService;
    }

    /**
     * Displays the list of all trades.
     *
     * @param model the model to pass attributes to the view
     * @return the view name for the trade list
     */
    @RequestMapping("/trade/list")
    public String home(Model model)
    {
        List<Trade> tradeList = tradeService.getListToTradeList();
        List<TradeDTO> tradeDTOList=tradeService.convertToDTOList(tradeList);
        model.addAttribute("trades", tradeDTOList);
        return "trade/list";
    }

    /**
     * Displays the form to add a new trade.
     *
     * @param model the model to pass attributes to the view
     * @return the view name for the add trade form
     */
    @GetMapping("/trade/add")
    public String addUser(Model model) {
        model.addAttribute("trade", new Trade());
        return "trade/add";
    }

    /**
     * Validates and saves a new trade.
     *
     * @param trade              the trade data transfer object to save
     * @param result             the binding result for validation errors
     * @param model              the model to pass attributes to the view
     * @param redirectAttributes the attributes to pass on redirection
     * @return the view name or redirect URL
     */
    @PostMapping("/trade/validate")
    public String validate(@Valid @ModelAttribute TradeDTO trade, BindingResult result,
                           Model model, RedirectAttributes redirectAttributes) {
        if(result.hasErrors()) {
            log.warn(result.getAllErrors().toString());
            model.addAttribute("trade", trade);
            return "trade/add";
        }
        try{
            tradeService.saveTrade(trade);
            redirectAttributes.addFlashAttribute("successMessage", "Trade added successfully");
            return "redirect:/trade/list";
        }catch(Exception e){
            model.addAttribute("errorMessage", "An error occurred while saving the trade");
            model.addAttribute("trade", trade);
            return "trade/add";
        }
    }

    /**
     * Displays the form to update an existing trade.
     *
     * @param id    the ID of the trade to update
     * @param model the model to pass attributes to the view
     * @return the view name for the update trade form
     */
    @GetMapping("/trade/update/{id}")
    public String showUpdateForm(@PathVariable("id") Integer id, Model model) {
        TradeDTO tradeDTO = tradeService.getTradeDTOById(id);
        model.addAttribute("trade", tradeDTO);
        return "trade/update";
    }

    /**
     * Validates and updates an existing trade.
     *
     * @param id                 the ID of the trade to update
     * @param trade              the trade data transfer object with updated details
     * @param result             the binding result for validation errors
     * @param model              the model to pass attributes to the view
     * @param redirectAttributes the attributes to pass on redirection
     * @return the view name or redirect URL
     */
    @PostMapping("/trade/update/{id}")
    public String updateTrade(@PathVariable("id") Integer id, @Valid @ModelAttribute TradeDTO trade,
                             BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        log.info("Updating trade with id: " + id);

        if(result.hasErrors()) {
            log.warn("Validation errors while updating bid with ID: {}", id);
            model.addAttribute("id", id);
            model.addAttribute("trade", trade);
            return "trade/update";
        }
        try {
            tradeService.updateBidList(id, trade);
            log.info("trade with ID {} updated successfully", id);
            redirectAttributes.addFlashAttribute("successMessage", "Trade successfully Updated");
            return "redirect:/trade/list";
        } catch (Exception e) {
            log.error("Error occurred while updating trade with ID: {}", id, e);
            model.addAttribute("id", id);
            model.addAttribute("trade", trade);
            model.addAttribute("errorMessage", "An error occurred while saving the bid. Please try again.");
            return "trade/update";
        }
    }

    /**
     * Deletes a trade by its ID.
     *
     * @param id                 the ID of the trade to delete
     * @param redirectAttributes the attributes to pass on redirection
     * @return the redirect URL for the trade list
     */
    @GetMapping("/trade/delete/{id}")
    public String deleteTrade(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        tradeService.deleteTrade(id);
        redirectAttributes.addFlashAttribute("successMessage", "Trade deleted successfully");
        return "redirect:/trade/list";
    }
}
