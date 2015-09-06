package edu.columbia.rascal.cumc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

import java.io.IOException;

@Configuration
@EnableAutoConfiguration
@ImportResource({"application-context.xml"})
@ComponentScan("edu.columbia.rascal")
public class Application {

    private static final Logger log = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) throws IOException {

        log.info("application start...");
        ApplicationContext ctx = SpringApplication.run(Application.class, args);

        Extractor extractor = ctx.getBean(Extractor.class);
        extractor.start();

        SpringApplication.exit(ctx);
        log.info("application done...");
    }
}
