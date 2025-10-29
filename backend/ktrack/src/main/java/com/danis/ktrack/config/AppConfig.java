package com.danis.ktrack.config;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.util.logging.Logger;


@Configuration
@ComponentScan(basePackages = {
        "com.danis.ktrack.service",
        "com.danis.ktrack.config"

})
public class AppConfig {

    private static Logger logger = Logger.getLogger(AppConfig.class.getName());


    @Bean
    public Validator standardValidator() {
        logger.info("... Initializing Standard Jakarta Validator.");
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        return validatorFactory.getValidator();
    }


    @Bean
    public LocalValidatorFactoryBean springValidator() {
        logger.info("... Initializing Spring LocalValidatorFactoryBean.");
        return new LocalValidatorFactoryBean();
    }
}

