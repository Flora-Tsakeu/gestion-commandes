package com.entreprise.gestioncommandes.service;

import com.entreprise.gestioncommandes.dto.CommandeRequest;
import com.entreprise.gestioncommandes.dto.LigneCommandeRequest;
import com.entreprise.gestioncommandes.exception.StockInsuffisantException;
import com.entreprise.gestioncommandes.model.Commande;
import com.entreprise.gestioncommandes.model.Produit;
import com.entreprise.gestioncommandes.repository.CommandeRepository;
import com.entreprise.gestioncommandes.repository.ProduitRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommandeServiceTest {

    @Mock
    private CommandeRepository commandeRepository;

    @Mock
    private ProduitRepository produitRepository;

    @InjectMocks
    private CommandeService commandeService;

    private Produit ecran;

    @BeforeEach
    void init() {
        ecran = new Produit("ECR-027", "Ecran 27 pouces", new BigDecimal("199.00"), 5);
        ecran.setId(2L);
    }

    @Test
    void doitCalculerLeTotalTtcAvecTvaVingtPourcent() {
        when(produitRepository.findById(2L)).thenReturn(Optional.of(ecran));
        when(produitRepository.save(any(Produit.class))).thenAnswer(inv -> inv.getArgument(0));
        when(commandeRepository.save(any(Commande.class))).thenAnswer(inv -> inv.getArgument(0));

        CommandeRequest requete = new CommandeRequest();
        requete.setClient("Societe Dubois");
        LigneCommandeRequest ligne = new LigneCommandeRequest();
        ligne.setProduitId(2L);
        ligne.setQuantite(2);
        requete.setLignes(List.of(ligne));

        Commande resultat = commandeService.creerCommande(requete);

        assertThat(resultat.getMontantTotalHt()).isEqualByComparingTo("398.00");
        assertThat(resultat.getMontantTotalTtc()).isEqualByComparingTo("477.60");
    }

    @Test
    void doitRefuserLaCommandeSiStockInsuffisant() {
        when(produitRepository.findById(2L)).thenReturn(Optional.of(ecran));

        CommandeRequest requete = new CommandeRequest();
        requete.setClient("Societe Dubois");
        LigneCommandeRequest ligne = new LigneCommandeRequest();
        ligne.setProduitId(2L);
        ligne.setQuantite(50);
        requete.setLignes(List.of(ligne));

        assertThatThrownBy(() -> commandeService.creerCommande(requete))
                .isInstanceOf(StockInsuffisantException.class)
                .hasMessageContaining("ECR-027");
    }
}
