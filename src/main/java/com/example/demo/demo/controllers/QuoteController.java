package com.example.demo.demo.controllers;


import com.example.demo.model.Quote;
import com.example.demo.service.QuoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
class QuoteController {
    @Autowired
    private QuoteService quoteService;

    public QuoteController(QuoteService quoteService){
        this.quoteService = quoteService;
    }

    @GetMapping("/quotes")
    public List<Quote> getAllQuotes(){
        return quoteService.getAllQuotes();
    }

    @GetMapping("/quotes/{id}")
    public Optional<Quote> getQuote(@PathVariable Long id){
        return quoteService.getQuoteById(id);
    }

    @PostMapping("/quotes")
    public Quote addQuote(@RequestBody Quote quote){
        return quoteService.saveQuote(quote);
    }

    @PutMapping("/quotes/{id}")
    public Quote updateQuote(@PathVariable long id,@RequestBody Quote quote){
        return quoteService.updateQuote(id,quote);
    }

    @DeleteMapping("/quotes/{id}")
    public void deleteQuote(@PathVariable long id){
        quoteService.deleteQuote(id);
    }
}
