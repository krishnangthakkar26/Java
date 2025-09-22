package com.example.library.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Map all /images/** URLs to the local "uploads/" directory
        registry
                .addResourceHandler("/images/**")                 // URL pattern
                .addResourceLocations("file:uploads/");           // Actual directory

        // Explanation:
        // /images/book1.jpg → maps to file system at uploads/book1.jpg
    }
}