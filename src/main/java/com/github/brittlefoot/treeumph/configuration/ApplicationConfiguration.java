package com.github.brittlefoot.treeumph.configuration;

import com.github.brittlefoot.treeumph.persistence.MongoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.EnableTransactionManagement;


@Configuration
@ComponentScan({
        "com.github.brittlefoot.treeumph.servlet.controller",
        "com.github.brittlefoot.treeumph.servlet.services"})
@Import({MongoConfiguration.class})
@EnableTransactionManagement
public class ApplicationConfiguration {

}

