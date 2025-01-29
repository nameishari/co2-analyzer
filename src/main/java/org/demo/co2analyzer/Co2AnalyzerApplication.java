package org.demo.co2analyzer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class Co2AnalyzerApplication {

    public static void main(String[] args) {
        SpringApplication.run(Co2AnalyzerApplication.class, args);
    }

}
