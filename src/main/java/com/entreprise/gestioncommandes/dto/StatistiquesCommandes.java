package com.entreprise.gestioncommandes.dto;

import java.math.BigDecimal;

public class StatistiquesCommandes {

    private final long nombreCommandesActives;
    private final long nombreCommandesAnnulees;
    private final long nombrePrioritairesNonTraitees;
    private final BigDecimal chiffreAffairesTtc;

    public StatistiquesCommandes(long nombreCommandesActives, long nombreCommandesAnnulees,
                                  long nombrePrioritairesNonTraitees, BigDecimal chiffreAffairesTtc) {
        this.nombreCommandesActives = nombreCommandesActives;
        this.nombreCommandesAnnulees = nombreCommandesAnnulees;
        this.nombrePrioritairesNonTraitees = nombrePrioritairesNonTraitees;
        this.chiffreAffairesTtc = chiffreAffairesTtc;
    }

    public long getNombreCommandesActives() {
        return nombreCommandesActives;
    }

    public long getNombreCommandesAnnulees() {
        return nombreCommandesAnnulees;
    }

    public long getNombrePrioritairesNonTraitees() {
        return nombrePrioritairesNonTraitees;
    }

    public BigDecimal getChiffreAffairesTtc() {
        return chiffreAffairesTtc;
    }
}
