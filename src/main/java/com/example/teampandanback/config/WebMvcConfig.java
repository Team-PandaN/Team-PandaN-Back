package com.example.teampandanback.config;

import com.example.teampandanback.utils.CORSFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Bean
    public FilterRegistrationBean getFilterRegistrationBean() {
        FilterRegistrationBean registrationBean = new FilterRegistrationBean<>(new CORSFilter());
        registrationBean.addUrlPatterns("/**");
        return registrationBean;
    }

    private final long MAX_AGE_SECS = 3600;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*://*")
                .allowedMethods("*")
                .allowedHeaders("*")
//                .allowCredentials(true)
                .maxAge(MAX_AGE_SECS);
    }
}
