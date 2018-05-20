package com.github.brittlefoot.treeumph.configuration;

import com.github.brittlefoot.treeumph.persistence.MongoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@ComponentScan({
        "com.github.brittlefoot.treeumph.configuration",
        "com.github.brittlefoot.treeumph.controller",
        "com.github.brittlefoot.treeumph.services"})
@Import({MongoConfiguration.class})
@EnableAutoConfiguration
@EnableTransactionManagement
public class ApplicationConfiguration {

}


