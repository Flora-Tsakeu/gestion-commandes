package com.entreprise.gestioncommandes.controller;

import com.entreprise.gestioncommandes.dto.CommandeRequest;
import com.entreprise.gestioncommandes.model.Commande;
import com.entreprise.gestioncommandes.service.CommandeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/commandes")
public class CommandeController {

    private final CommandeService commandeService;

    public CommandeController(CommandeService commandeService) {
        this.commandeService = commandeService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Commande creer(@Valid @RequestBody CommandeRequest requete) {
        return commandeService.creerCommande(requete);
    }

    @GetMapping("/{id}")
    public Commande recuperer(@PathVariable Long id) {
        return commandeService.recupererParId(id);
    }

    @GetMapping
    public List<Commande> lister(@RequestParam(required = false) String client) {
        if (client != null && !client.isBlank()) {
            return commandeService.listerParClient(client);
        }
        return commandeService.listerToutes();
    }

    @PostMapping("/{id}/annulation")
    public Commande annuler(@PathVariable Long id) {
        return commandeService.annuler(id);
    }
}
