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

import com.poseidoncapitalsolutions.trading.dto.BidListDTO;
import com.poseidoncapitalsolutions.trading.mapper.BidListMapper;
import com.poseidoncapitalsolutions.trading.service.BidListService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * Controller responsible for handling operations related to BidLists.
 * Provides endpoints for viewing, adding, updating, and deleting bid lists.
 */
@Controller
@Tag(name = "Bid List Controller", description = "API for bid list management")
public class BidListController {

    private BidListService bidListService;
    private BidListMapper bidListMapper;

    /**
     * Constructs a BidListController with the given service and mapper.
     * 
     * @param bidListService The service to interact with BidList data.
     * @param bidListMapper  The mapper to convert BidList entities to DTOs.
     */
    public BidListController(BidListService bidListService, BidListMapper bidListMapper) {
        this.bidListService = bidListService;
        this.bidListMapper = bidListMapper;
    }

    /**
     * Displays a list of all bid lists.
     * 
     * @param model The model to add the bid lists to.
     * @return The view name for the bid list page.
     */
    @Operation(summary = "Get all bid lists", description = "Returns a page with the list of all bid lists")
    @GetMapping("/bidList/list")
    public String home(Model model) {
        List<BidListDTO> lists = bidListService.getListResponseDTO(bidListService.findAll());
        model.addAttribute("bidLists", lists);
        return "bidList/list";
    }

    /**
     * Displays the form to add a new bid list.
     * 
     * @param model The model to add a new empty BidListDTO to.
     * @return The view name for the bid list add form.
     */
    @Operation(summary = "Display bid list add form", description = "Returns a page with the form to add a new bid list")
    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/bidList/add")
    public String addBidForm(Model model) {
        model.addAttribute("newBidList", new BidListDTO());
        return "bidList/add";
    }

    /**
     * Validates and adds a new bid list.
     * 
     * @param bid    The new BidListDTO to be added.
     * @param result The result of validating the bid list.
     * @param model  The model to pass attributes to the view.
     * @return A redirect to the bid list page if successful, or the form page if
     *         validation fails.
     */
    @Operation(summary = "Validate and add a new bid list", description = "Adds a new bid list from form data")
    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/bidList/validate")
    public String validate(
            @Parameter(description = "New bid list data to add", required = true, schema = @Schema(implementation = BidListDTO.class)) @ModelAttribute("newBidList") @Valid BidListDTO bid,
            BindingResult result,
            Model model) {
        if (result.hasErrors()) {
            model.addAttribute("newBidList", bid);
            return "bidList/add";
        }

        bidListService.add(bidListMapper.toEntity(bid));
        return "redirect:/bidList/list";
    }

    /**
     * Displays the form to update an existing bid list.
     * 
     * @param id    The ID of the bid list to update.
     * @param model The model to add the existing bid list data to.
     * @return The view name for the bid list update form.
     */
    @Operation(summary = "Display bid list update form", description = "Returns a page with the form to update an existing bid list")
    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/bidList/update/{id}")
    public String showUpdateForm(
            @Parameter(description = "ID of the bid list to update", required = true) @PathVariable("id") int id,
            Model model) {
        model.addAttribute("updatedBidList", bidListMapper.toDto(bidListService.findById(id)));
        return "bidList/update";
    }

    /**
     * Updates an existing bid list.
     * 
     * @param id             The ID of the bid list to update.
     * @param updatedBidList The updated BidListDTO.
     * @param result         The result of validating the updated bid list.
     * @param model          The model to pass attributes to the view.
     * @return A redirect to the bid list page if successful, or the form page if
     *         validation fails.
     */
    @Operation(summary = "Update an existing bid list", description = "Updates a bid list with the provided form data")
    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/bidList/update/{id}")
    public String updateBid(
            @Parameter(description = "ID of the bid list to update", required = true) @PathVariable("id") int id,
            @Parameter(description = "Updated bid list data", required = true, schema = @Schema(implementation = BidListDTO.class)) @ModelAttribute("updatedBidList") @Valid BidListDTO updatedBidList,
            BindingResult result,
            Model model) {
        if (result.hasErrors()) {
            model.addAttribute("updatedBidList", updatedBidList);
            return "bidList/update";
        }
        bidListService.update(updatedBidList);
        return "redirect:/bidList/list";
    }

    /**
     * Deletes a bid list by its ID.
     * 
     * @param id    The ID of the bid list to delete.
     * @param model The model to pass attributes to the view.
     * @return A redirect to the bid list page after deletion.
     */
    @Operation(summary = "Delete a bid list", description = "Deletes a bid list by its ID")
    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/bidList/delete/{id}")
    public String deleteBid(
            @Parameter(description = "ID of the bid list to delete", required = true) @PathVariable("id") int id,
            Model model) {
        bidListService.delete(bidListService.findById(id));
        return "redirect:/bidList/list";
    }
}
