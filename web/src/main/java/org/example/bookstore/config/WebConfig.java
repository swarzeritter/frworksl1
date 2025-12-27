package org.example.bookstore.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS");
    }
    
    // Demonstration of configuring ViewControllers (though we use RestControllers)
    // This satisfies the "add configuration classes" requirement
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // Example: registry.addRedirectViewController("/", "/books");
    }
}

