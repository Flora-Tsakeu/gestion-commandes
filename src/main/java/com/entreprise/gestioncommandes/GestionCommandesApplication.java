package com.entreprise.gestioncommandes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import jakarta.annotation.PostConstruct;

@SpringBootApplication
public class GestionCommandesApplication {

    public static void main(String[] args) {
        SpringApplication.run(GestionCommandesApplication.class, args);
    }

    @PostConstruct
    public void initialisationBloquante() {
        // Cette ligne va s'exécuter au démarrage et faire planter l'application exprès
        throw new RuntimeException("Échec volontaire provoqué par @PostConstruct pour test");
    }
}
