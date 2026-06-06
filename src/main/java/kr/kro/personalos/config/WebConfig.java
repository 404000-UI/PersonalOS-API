package kr.kro.personalos.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    public WebConfig() {
        System.out.println("===== WebConfig Loaded =====");
    }
    
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        System.out.println("===== CORS Applied =====");
        
        registry.addMapping("/**").allowedOriginPatterns("*").allowedMethods("*").allowedHeaders("*");
    }

}
