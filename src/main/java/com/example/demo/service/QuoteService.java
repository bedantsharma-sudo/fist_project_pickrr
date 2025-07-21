package com.example.demo.service;

import com.example.demo.model.Quote;
import com.example.demo.repository.QuoteRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;


@Service
public class QuoteService {
    private final QuoteRepository quoteRepo;

    public QuoteService(QuoteRepository quoteRepo) {
        this.quoteRepo = quoteRepo;
    }


    public Page<Quote> getAllQuotes(Pageable pageable) {
        return quoteRepo.findAll(pageable);
    }
    @Cacheable(value = "quotes", key = "#id")
    public Optional<Quote> getQuoteById(Long id) {
        return quoteRepo.findById(id);
    }

    @CachePut(value = "quotes", key="#result.id")
    public Quote saveQuote(Quote quote) {
        if(quote.getAuthor() == null || quote.getAuthor().isEmpty()) {
            quote.setAuthor("Anonymous");
        }
        if(quote.getContent() == null || quote.getContent().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Content cannot be empty");
        }
        return quoteRepo.save(quote);
    }

    @CachePut(value = "quotes", key = "#id")
    public Quote updateQuote(Long id, Quote quote) {
        return quoteRepo.findById(id)
                .map(existingQuote -> {
                    existingQuote.setContent(quote.getContent());
                    
                    if(quote.getAuthor()==null || quote.getAuthor().isEmpty()){
                        existingQuote.setAuthor("Anonymous");
                    } else if (!(quote.getAuthor().equals(existingQuote.getAuthor()))){
                        existingQuote.setAuthor(quote.getAuthor());
                    }
                    return quoteRepo.save(existingQuote);
                })
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Id doesn't exist" + id));
    }

    @CacheEvict(value = "quotes", key = "#id")
    public void deleteQuote(Long id) {
        if (!quoteRepo.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Id doesn't exist" + id);
        }
        quoteRepo.deleteById(id);
    }
    
}
