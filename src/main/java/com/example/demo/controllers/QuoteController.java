package com.example.demo.controllers;


import com.example.demo.model.Quote;
import com.example.demo.service.QuoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Pageable;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Controller
class QuoteController {
    @Autowired
    private QuoteService quoteService;

    public QuoteController(QuoteService quoteService){
        this.quoteService = quoteService;
    }

    @GetMapping("/quotes")
    public String getAllQuote(Model model, @RequestParam(defaultValue = "0") int page,
                                    @RequestParam(defaultValue = "10") int size,
                                    @RequestParam(defaultValue = "id") String sortBy, // New: for sorting
                                    @RequestParam(defaultValue = "desc") String sortDir){

        Pageable pageable;
        if (sortDir.equalsIgnoreCase("asc")) {
            pageable = PageRequest.of(page, size, org.springframework.data.domain.Sort.by(sortBy).ascending());
        } else {
            pageable = PageRequest.of(page, size, org.springframework.data.domain.Sort.by(sortBy).descending());
        }
        Page<Quote> quotesPage = quoteService.getAllQuotes(pageable);
        model.addAttribute("quotes", quotesPage.getContent()); // Get the list of quotes for the current page
        model.addAttribute("currentPage", quotesPage.getNumber());
        model.addAttribute("totalPages", quotesPage.getTotalPages());
        model.addAttribute("totalItems", quotesPage.getTotalElements());
        model.addAttribute("pageSize", quotesPage.getSize());
        model.addAttribute("hasPrevious", quotesPage.hasPrevious());
        model.addAttribute("hasNext", quotesPage.hasNext());
        model.addAttribute("sortField", sortBy);
        model.addAttribute("sortDirection", sortDir);


        return "quotes";
    }

    @GetMapping("/user/quotes/{id}")
    public String showSingleQuote(@PathVariable Long id, Model model) {
        Optional<Quote> quoteOptional = quoteService.getQuoteById(id);
        if (quoteOptional.isPresent()) {
            model.addAttribute("quote", quoteOptional.get());
            return "single-quote";
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Quote not found with ID: " + id);
        }
    }

    @PostMapping("/user/quotes")
    public String makeQuote(@ModelAttribute Quote quote){

        Quote newQuote = quoteService.saveQuote(quote);


        if (newQuote.getId() == null) {
            System.err.println("ERROR: New Quote ID is NULL after saving! Cannot redirect to detail page.");
            return "redirect:/quotes?error=saveFailed";
        }

        return "redirect:/user/quotes/" + newQuote.getId();
    }

    @PutMapping("/user/quotes/{id}")
    public String updateQuote(@PathVariable long id, @ModelAttribute Quote quote){
        Quote updatedQuote = quoteService.updateQuote(id, quote);
        return "redirect:/user/quotes/" + updatedQuote.getId();
    }

    @DeleteMapping("/user/quotes/{id}")
    public String deleteQuote(@PathVariable long id){
        quoteService.deleteQuote(id);
        return "redirect:/quotes";
    }
}
