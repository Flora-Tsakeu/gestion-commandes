package com.entreprise.gestioncommandes.service;

import com.entreprise.gestioncommandes.dto.CommandeRequest;
import com.entreprise.gestioncommandes.dto.LigneCommandeRequest;
import com.entreprise.gestioncommandes.dto.StatistiquesCommandes;
import com.entreprise.gestioncommandes.exception.AnnulationImpossibleException;
import com.entreprise.gestioncommandes.exception.CommandeIntrouvableException;
import com.entreprise.gestioncommandes.exception.ProduitInactifException;
import com.entreprise.gestioncommandes.exception.ReferenceExterneDejaUtiliseeException;
import com.entreprise.gestioncommandes.exception.StockInsuffisantException;
import com.entreprise.gestioncommandes.model.Commande;
import com.entreprise.gestioncommandes.model.LigneCommande;
import com.entreprise.gestioncommandes.model.Produit;
import com.entreprise.gestioncommandes.repository.CommandeRepository;
import com.entreprise.gestioncommandes.repository.ProduitRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommandeServiceTest {

    @Mock
    private CommandeRepository commandeRepository;

    @Mock
    private ProduitRepository produitRepository;

    @Mock
    private NotificationService notificationService;

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

    @Test
    void doitRefuserLaCommandeSiProduitDesactive() {
        ecran.setActif(false);
        when(produitRepository.findById(2L)).thenReturn(Optional.of(ecran));

        CommandeRequest requete = new CommandeRequest();
        requete.setClient("Societe Dubois");
        LigneCommandeRequest ligne = new LigneCommandeRequest();
        ligne.setProduitId(2L);
        ligne.setQuantite(1);
        requete.setLignes(List.of(ligne));

        assertThatThrownBy(() -> commandeService.creerCommande(requete))
                .isInstanceOf(ProduitInactifException.class)
                .hasMessageContaining("ECR-027");
    }

    @Test
    void doitAutoriserLAnnulationDansLeDelai() {
        Commande commande = new Commande("Societe Dubois");
        commande.setId(10L);
        commande.setDateCreation(LocalDateTime.now().minusDays(5));
        commande.setLignes(List.of(new LigneCommande(ecran, 1)));
        when(commandeRepository.findById(10L)).thenReturn(Optional.of(commande));
        when(produitRepository.save(any(Produit.class))).thenAnswer(inv -> inv.getArgument(0));
        when(commandeRepository.save(any(Commande.class))).thenAnswer(inv -> inv.getArgument(0));

        Commande resultat = commandeService.annuler(10L);

        assertThat(resultat.isAnnulee()).isTrue();
    }

    @Test
    void doitRefuserLAnnulationAuDelaDuDelai() {
        Commande commande = new Commande("Societe Dubois");
        commande.setId(11L);
        commande.setDateCreation(LocalDateTime.now().minusDays(45));
        commande.setLignes(List.of(new LigneCommande(ecran, 1)));
        when(commandeRepository.findById(11L)).thenReturn(Optional.of(commande));

        assertThatThrownBy(() -> commandeService.annuler(11L))
                .isInstanceOf(AnnulationImpossibleException.class)
                .hasMessageContaining("11");
    }

    @Test
    void doitDeclencherLaNotificationDeCreationSiEmailFourni() {
        when(produitRepository.findById(2L)).thenReturn(Optional.of(ecran));
        when(produitRepository.save(any(Produit.class))).thenAnswer(inv -> inv.getArgument(0));
        when(commandeRepository.save(any(Commande.class))).thenAnswer(inv -> inv.getArgument(0));

        CommandeRequest requete = new CommandeRequest();
        requete.setClient("Societe Dubois");
        requete.setClientEmail("contact@societe-dubois.fr");
        LigneCommandeRequest ligne = new LigneCommandeRequest();
        ligne.setProduitId(2L);
        ligne.setQuantite(1);
        requete.setLignes(List.of(ligne));

        Commande resultat = commandeService.creerCommande(requete);

        assertThat(resultat.getClientEmail()).isEqualTo("contact@societe-dubois.fr");
        verify(notificationService).notifierCreation(resultat);
    }

    @Test
    void doitCalculerLesStatistiquesGlobales() {
        when(commandeRepository.countByAnnulee(false)).thenReturn(42L);
        when(commandeRepository.countByAnnulee(true)).thenReturn(8L);
        when(commandeRepository.calculerChiffreAffairesActif()).thenReturn(new BigDecimal("15320.40"));

        StatistiquesCommandes resultat = commandeService.calculerStatistiques();

        assertThat(resultat.getNombreCommandesActives()).isEqualTo(42L);
        assertThat(resultat.getNombreCommandesAnnulees()).isEqualTo(8L);
        assertThat(resultat.getChiffreAffairesTtc()).isEqualByComparingTo("15320.40");
    }

    @Test
    void doitListerLesCommandesDunClientDeFaconPaginee() {
        Commande commande = new Commande("Boutique Nord");
        Pageable pageable = PageRequest.of(0, 5);
        when(commandeRepository.findByClientOrderByDateCreationDesc(eq("Boutique Nord"), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(commande)));

        Page<Commande> resultat = commandeService.listerParClientPagine("Boutique Nord", pageable);

        assertThat(resultat.getContent()).hasSize(1);
        assertThat(resultat.getContent().get(0).getClient()).isEqualTo("Boutique Nord");
    }

    @Test
    void doitRetrouverUneCommandeParSonNumeroDeSuivi() {
        Commande commande = new Commande("Boutique Nord");
        commande.setNumeroSuivi("CMD-ABCDEF12");
        when(commandeRepository.findByNumeroSuivi("CMD-ABCDEF12")).thenReturn(Optional.of(commande));

        Commande resultat = commandeService.recupererParNumeroSuivi("CMD-ABCDEF12");

        assertThat(resultat.getClient()).isEqualTo("Boutique Nord");
    }

    @Test
    void doitLeverUneExceptionSiNumeroDeSuiviInconnu() {
        when(commandeRepository.findByNumeroSuivi("CMD-INEXISTANT")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> commandeService.recupererParNumeroSuivi("CMD-INEXISTANT"))
                .isInstanceOf(CommandeIntrouvableException.class)
                .hasMessageContaining("CMD-INEXISTANT");
    }

    @Test
    void doitRefuserUneReferenceExterneDejaUtilisee() {
        when(commandeRepository.existsByReferenceExterne("ERP-2026-004512")).thenReturn(true);

        CommandeRequest requete = new CommandeRequest();
        requete.setClient("Societe Dubois");
        requete.setReferenceExterne("ERP-2026-004512");
        LigneCommandeRequest ligne = new LigneCommandeRequest();
        ligne.setProduitId(2L);
        ligne.setQuantite(1);
        requete.setLignes(List.of(ligne));

        assertThatThrownBy(() -> commandeService.creerCommande(requete))
                .isInstanceOf(ReferenceExterneDejaUtiliseeException.class)
                .hasMessageContaining("ERP-2026-004512");
    }
}
