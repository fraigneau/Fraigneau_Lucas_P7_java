package com.poseidoncapitalsolutions.trading.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.poseidoncapitalsolutions.trading.dto.TradeDTO;
import com.poseidoncapitalsolutions.trading.mapper.TradeMapper;
import com.poseidoncapitalsolutions.trading.service.TradeService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * Controller responsible for handling operations related to Trades.
 * Provides endpoints for viewing, adding, updating, and deleting trades.
 */
@Controller
@Tag(name = "Trade Controller", description = "API for trade management")
public class TradeController {

    private TradeService tradeService;
    private TradeMapper tradeMapper;

    /**
     * Constructs a TradeController with the given service and mapper.
     * 
     * @param tradeService The service to interact with Trade data.
     * @param tradeMapper  The mapper to convert Trade entities to DTOs.
     */
    public TradeController(TradeService tradeService, TradeMapper tradeMapper) {
        this.tradeService = tradeService;
        this.tradeMapper = tradeMapper;
    }

    /**
     * Displays a list of all trades.
     * 
     * @param model The model to add the trades to.
     * @return The view name for the trades page.
     */
    @Operation(summary = "Get all trades", description = "Returns a page with the list of all trades")
    @GetMapping("/trade/list")
    public String home(Model model) {
        List<TradeDTO> lists = tradeService.getListResponseDTO(tradeService.findAll());
        model.addAttribute("trades", lists);
        return "trade/list";
    }

    /**
     * Displays the form to add a new trade.
     * 
     * @param model The model to add a new empty TradeDTO to.
     * @return The view name for the trade add form.
     */
    @Operation(summary = "Display trade add form", description = "Returns a page with the form to add a new trade")
    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/trade/add")
    public String addUser(Model model) {
        model.addAttribute("newTrade", new TradeDTO());
        return "trade/add";
    }

    /**
     * Validates and adds a new trade.
     * 
     * @param trade  The new TradeDTO to be added.
     * @param result The result of validating the trade.
     * @param model  The model to pass attributes to the view.
     * @return A redirect to the trade list page if successful, or the form page if
     *         validation fails.
     */
    @Operation(summary = "Validate and add a new trade", description = "Adds a new trade from form data")
    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/trade/validate")
    public String validate(
            @Parameter(description = "New trade data to add", required = true, schema = @Schema(implementation = TradeDTO.class)) @ModelAttribute("newTrade") @Valid TradeDTO trade,
            BindingResult result,
            Model model) {
        if (result.hasErrors()) {
            model.addAttribute("newTrade", trade);
            return "trade/add";
        }

        tradeService.add(tradeMapper.toEntity(trade));
        return "redirect:/trade/list";
    }

    /**
     * Displays the form to update an existing trade.
     * 
     * @param id    The ID of the trade to update.
     * @param model The model to add the existing trade data to.
     * @return The view name for the trade update form.
     */
    @Operation(summary = "Display trade update form", description = "Returns a page with the form to update an existing trade")
    @PreAuthorize("hasAuthority('ADMIN')")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully displayed update form"),
            @ApiResponse(responseCode = "404", description = "Trade not found")
    })
    @GetMapping("/trade/update/{id}")
    public String showUpdateForm(
            @Parameter(description = "ID of the trade to update", required = true) @PathVariable("id") int id,
            Model model) {
        model.addAttribute("updatedTrade", tradeMapper.toDto(tradeService.findById(id)));
        return "trade/update";
    }

    /**
     * Updates an existing trade.
     * 
     * @param id     The ID of the trade to update.
     * @param trade  The updated TradeDTO.
     * @param result The result of validating the updated trade.
     * @param model  The model to pass attributes to the view.
     * @return A redirect to the trade list page if successful, or the form page if
     *         validation fails.
     */
    @Operation(summary = "Update an existing trade", description = "Updates a trade with the provided form data")
    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/trade/update/{id}")
    public String updateTrade(
            @Parameter(description = "ID of the trade to update", required = true) @PathVariable("id") int id,
            @Parameter(description = "Updated trade data", required = true, schema = @Schema(implementation = TradeDTO.class)) @ModelAttribute("updatedTrade") @Valid TradeDTO trade,
            BindingResult result,
            Model model) {
        if (result.hasErrors()) {
            model.addAttribute("updatedTrade", trade);
            return "trade/update";
        }

        tradeService.update(trade);
        return "redirect:/trade/list";
    }

    /**
     * Deletes a trade by its ID.
     * 
     * @param id    The ID of the trade to delete.
     * @param model The model to pass attributes to the view.
     * @return A redirect to the trade list page after deletion.
     */
    @Operation(summary = "Delete a trade", description = "Deletes a trade by its ID")
    @PreAuthorize("hasAuthority('ADMIN')")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "302", description = "Trade successfully deleted, redirecting to list"),
            @ApiResponse(responseCode = "404", description = "Trade not found")
    })
    @GetMapping("/trade/delete/{id}")
    public String deleteTrade(
            @Parameter(description = "ID of the trade to delete", required = true) @PathVariable("id") int id,
            Model model) {
        tradeService.delete(tradeService.findById(id));
        return "redirect:/trade/list";
    }
}
