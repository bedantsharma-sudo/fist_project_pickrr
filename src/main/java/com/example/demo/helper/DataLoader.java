package com.example.demo.helper;


import com.example.demo.model.Quote;
import com.example.demo.repository.QuoteRepository;
import com.github.javafaker.Faker;
import jakarta.transaction.Transactional;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Component
class DataLoader implements CommandLineRunner {
    private final QuoteRepository quotesRepository;
    private Faker faker;
    private static final int Number_of_Entries = 150;
    private static final int BATCH_SIZE = 150;
    public DataLoader(QuoteRepository quotesRepository){
        this.quotesRepository = quotesRepository;
        this.faker= new Faker(new Locale("en-US"));
    }

    @Override
    @Transactional
    public void run(String... args){
        if(quotesRepository.count() < 100){
            List<Quote> initialQuote = new ArrayList<>();
            for(int i=0;i<Number_of_Entries;i++){
                Quote quote = new Quote();
                quote.setAuthor(faker.book().author());
                quote.setContent(faker.hobbit().quote());
                initialQuote.add(quote);
                if (initialQuote.size() == BATCH_SIZE || i == Number_of_Entries - 1) {
                    quotesRepository.saveAll(initialQuote);
                    System.out.println("Inserted " + initialQuote.size() + " quotes. Total so far: " + (i + 1));
                    initialQuote.clear();
                }

            }

        }
    }
}
