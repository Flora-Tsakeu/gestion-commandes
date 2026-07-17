package com.entreprise.gestioncommandes.controller;

import com.entreprise.gestioncommandes.dto.CommandeRequest;
import com.entreprise.gestioncommandes.dto.NotesRequest;
import com.entreprise.gestioncommandes.dto.StatistiquesCommandes;
import com.entreprise.gestioncommandes.model.Commande;
import com.entreprise.gestioncommandes.service.CommandeService;
import com.entreprise.gestioncommandes.service.ExportCommandeService;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/commandes")
public class CommandeController {

    private final CommandeService commandeService;
    private final ExportCommandeService exportCommandeService;

    public CommandeController(CommandeService commandeService, ExportCommandeService exportCommandeService) {
        this.commandeService = commandeService;
        this.exportCommandeService = exportCommandeService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Commande creer(@Valid @RequestBody CommandeRequest requete) {
        return commandeService.creerCommande(requete);
    }

    @GetMapping("/periode")
    public List<Commande> listerParPeriode(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime debut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin) {
        return commandeService.listerParPeriode(debut, fin);
    }

    @GetMapping("/statistiques")
    public StatistiquesCommandes statistiques() {
        return commandeService.calculerStatistiques();
    }

    @GetMapping("/export")
    public ResponseEntity<String> exporter() {
        String csv = exportCommandeService.exporterCommandesActivesEnCsv();
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("text/csv"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"commandes-actives.csv\"")
                .body(csv);
    }

    @GetMapping("/{id}")
    public Commande recuperer(@PathVariable Long id) {
        return commandeService.recupererParId(id);
    }

    @GetMapping
    public List<Commande> lister(@RequestParam(required = false) String client,
                                  @RequestParam(required = false) Boolean annulee) {
        if (client != null && !client.isBlank()) {
            return commandeService.listerParClient(client);
        }
        if (annulee != null) {
            return commandeService.listerParStatutAnnulation(annulee);
        }
        return commandeService.listerToutes();
    }

    @GetMapping("/paginees")
    public Page<Commande> listerPaginees(Pageable pageable, @RequestParam(required = false) String client) {
        if (client != null && !client.isBlank()) {
            return commandeService.listerParClientPagine(client, pageable);
        }
        return commandeService.listerToutesPagine(pageable);
    }

    @PostMapping("/{id}/annulation")
    public Commande annuler(@PathVariable Long id) {
        return commandeService.annuler(id);
    }

    @PatchMapping("/{id}/notes")
    public Commande modifierNotes(@PathVariable Long id, @Valid @RequestBody NotesRequest requete) {
        return commandeService.modifierNotes(id, requete.getNotes());
    }
}
