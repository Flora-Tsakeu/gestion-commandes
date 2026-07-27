package com.entreprise.gestioncommandes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class IndicateurVariante implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(IndicateurVariante.class);
    private static final String VARIANTE = "run-386";

    @Override
    public void run(String... args) {
        log.info("contexte applicatif pret, variante={}", VARIANTE);
    }
}
