package com.entreprise.gestioncommandes.service;

import com.entreprise.gestioncommandes.model.Commande;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExportCommandeService {

    private static final String ENTETE = "numeroSuivi;client;dateCreation;modeLivraison;montantTotalHt;montantTotalTtc";

    private final CommandeService commandeService;

    public ExportCommandeService(CommandeService commandeService) {
        this.commandeService = commandeService;
    }

    public String exporterCommandesActivesEnCsv() {
        List<Commande> commandesActives = commandeService.listerParStatutAnnulation(false);

        String lignes = commandesActives.stream()
                .map(this::formaterLigne)
                .collect(Collectors.joining("\n"));

        return ENTETE + "\n" + lignes;
    }

    private String formaterLigne(Commande commande) {
        return String.join(";",
                nettoyer(commande.getNumeroSuivi()),
                nettoyer(commande.getClient()),
                String.valueOf(commande.getDateCreation()),
                String.valueOf(commande.getModeLivraison()),
                String.valueOf(commande.getMontantTotalHt()),
                String.valueOf(commande.getMontantTotalTtc()));
    }

    private String nettoyer(String valeur) {
        return valeur == null ? "" : valeur.replace(";", ",");
    }
}
