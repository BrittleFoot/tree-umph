package com.github.brittlefoot.treeumph;


import com.github.brittlefoot.treeumph.configuration.ApplicationConfiguration;
import org.springframework.boot.SpringApplication;


public class Application {

    public static void main(String[] args) {
        SpringApplication.run(ApplicationConfiguration.class, args);
    }

}
