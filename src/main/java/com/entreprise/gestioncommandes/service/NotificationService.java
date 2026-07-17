package com.entreprise.gestioncommandes.service;

import com.entreprise.gestioncommandes.model.Commande;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    public void notifierCreation(Commande commande) {
        if (commande.getClientEmail() == null || commande.getClientEmail().isBlank()) {
            log.debug("aucune adresse email fournie, notification de creation ignoree, commande id={}", commande.getId());
            return;
        }
        log.info("notification envoyee a {} : commande {} confirmee, total ttc {}",
                commande.getClientEmail(), commande.getNumeroSuivi(), commande.getMontantTotalTtc());
    }

    public void notifierAnnulation(Commande commande) {
        if (commande.getClientEmail() == null || commande.getClientEmail().isBlank()) {
            log.debug("aucune adresse email fournie, notification d'annulation ignoree, commande id={}", commande.getId());
            return;
        }
        log.info("notification envoyee a {} : commande {} annulee", commande.getClientEmail(), commande.getNumeroSuivi());
    }
}
