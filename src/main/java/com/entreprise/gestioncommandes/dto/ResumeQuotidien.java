package com.entreprise.gestioncommandes.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ResumeQuotidien {

    private final LocalDate date;
    private final long nombreCommandes;
    private final BigDecimal chiffreAffairesTtc;

    public ResumeQuotidien(LocalDate date, long nombreCommandes, BigDecimal chiffreAffairesTtc) {
        this.date = date;
        this.nombreCommandes = nombreCommandes;
        this.chiffreAffairesTtc = chiffreAffairesTtc;
    }

    public LocalDate getDate() {
        return date;
    }

    public long getNombreCommandes() {
        return nombreCommandes;
    }

    public BigDecimal getChiffreAffairesTtc() {
        return chiffreAffairesTtc;
    }
}
