package com.example.demo.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EnvConfig {
    private final Dotenv dotenv = Dotenv.configure()
            .directory("./") // project root
            .ignoreIfMissing() // optional
            .load();

    public String getGoogleClientId() {
        return dotenv.get("GOOGLE_CLIENT_ID");
    }

    public String getGoogleClientSecret() {
        return dotenv.get("GOOGLE_CLIENT_SECRET");
    }

    public String getJwtSecret() {
        return dotenv.get("JWT_SECRET");
    }
}

