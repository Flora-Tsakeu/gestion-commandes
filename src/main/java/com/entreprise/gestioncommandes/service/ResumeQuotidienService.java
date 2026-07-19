package com.entreprise.gestioncommandes.service;

import com.entreprise.gestioncommandes.dto.ResumeQuotidien;
import com.entreprise.gestioncommandes.model.Commande;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class ResumeQuotidienService {

    private ResumeQuotidienService() {
    }

    public static List<ResumeQuotidien> regrouperParJour(List<Commande> commandes) {
        Map<LocalDate, List<Commande>> parJour = commandes.stream()
                .filter(commande -> !commande.isAnnulee())
                .collect(Collectors.groupingBy(commande -> commande.getDateCreation().toLocalDate()));

        return parJour.entrySet().stream()
                .map(entree -> {
                    BigDecimal chiffreAffaires = entree.getValue().stream()
                            .map(Commande::getMontantTotalTtc)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    return new ResumeQuotidien(entree.getKey(), entree.getValue().size(), chiffreAffaires);
                })
                .sorted(Comparator.comparing(ResumeQuotidien::getDate))
                .toList();
    }
}
