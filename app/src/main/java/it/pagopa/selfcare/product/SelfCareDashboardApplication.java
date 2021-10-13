package it.pagopa.selfcare.product;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
//@ComponentScan(basePackages = {"it.pagopa.selfcare"})
public class SelfCareDashboardApplication {

    public static void main(String[] args) {

        SpringApplication.run(SelfCareDashboardApplication.class, args);
    }

}
