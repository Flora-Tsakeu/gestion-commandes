package com.entreprise.gestioncommandes.controller;

import com.entreprise.gestioncommandes.dto.InfoApi;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/info")
public class InfoController {

    private static final String VERSION = "0.1.0";

    @GetMapping
    public InfoApi info() {
        return new InfoApi("gestion-commandes", VERSION, LocalDateTime.now());
    }
}
