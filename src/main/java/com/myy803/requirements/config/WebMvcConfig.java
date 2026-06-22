package com.myy803.requirements.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
    	/*
    	 * ViewControllerRegistry allows to create 
    	 * simple automated controllers pre-configured 
    	 * with status code and/or a view
    	 * In Fowler's Terms this is a Page controller
    	 */
        registry.addViewController("/").setViewName("homepage");
    }
}
