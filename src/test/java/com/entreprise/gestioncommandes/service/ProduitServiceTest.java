package com.entreprise.gestioncommandes.service;

import com.entreprise.gestioncommandes.dto.ResumeStockCategorie;
import com.entreprise.gestioncommandes.exception.ProduitIntrouvableException;
import com.entreprise.gestioncommandes.exception.ReferenceProduitDejaUtiliseeException;
import com.entreprise.gestioncommandes.model.Produit;
import com.entreprise.gestioncommandes.repository.ProduitRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProduitServiceTest {

    @Mock
    private ProduitRepository produitRepository;

    @InjectMocks
    private ProduitService produitService;

    private Produit clavier;

    @BeforeEach
    void init() {
        clavier = new Produit("CLAV-001", "Clavier mecanique", new BigDecimal("59.90"), 25);
        clavier.setId(1L);
    }

    @Test
    void doitCreerUnProduitEtLeRetourner() {
        when(produitRepository.findByReference("CLAV-001")).thenReturn(Optional.empty());
        when(produitRepository.save(any(Produit.class))).thenReturn(clavier);

        Produit resultat = produitService.creerProduit(clavier);

        assertThat(resultat.getReference()).isEqualTo("CLAV-001");
        assertThat(resultat.getQuantiteStock()).isEqualTo(25);
    }

    @Test
    void doitRefuserLaCreationSiReferenceDejaUtilisee() {
        when(produitRepository.findByReference("CLAV-001")).thenReturn(Optional.of(clavier));

        assertThatThrownBy(() -> produitService.creerProduit(clavier))
                .isInstanceOf(ReferenceProduitDejaUtiliseeException.class)
                .hasMessageContaining("CLAV-001");
    }

    @Test
    void doitLeverUneExceptionSiProduitAbsent() {
        when(produitRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> produitService.recupererParId(99L))
                .isInstanceOf(ProduitIntrouvableException.class)
                .hasMessageContaining("99");
    }

    @Test
    void doitMettreAJourLeStockLorsDeLaModification() {
        when(produitRepository.findById(1L)).thenReturn(Optional.of(clavier));
        Produit modifications = new Produit("CLAV-001", "Clavier mecanique RGB", new BigDecimal("64.90"), 10);
        when(produitRepository.save(any(Produit.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Produit resultat = produitService.mettreAJour(1L, modifications);

        assertThat(resultat.getQuantiteStock()).isEqualTo(10);
        assertThat(resultat.getLibelle()).isEqualTo("Clavier mecanique RGB");
    }

    @Test
    void doitRetrouverUnProduitParSaReference() {
        when(produitRepository.findByReference("CLAV-001")).thenReturn(Optional.of(clavier));

        Produit resultat = produitService.recupererParReference("CLAV-001");

        assertThat(resultat.getId()).isEqualTo(1L);
    }

    @Test
    void doitLeverUneExceptionSiReferenceInconnue() {
        when(produitRepository.findByReference("INEXISTANT")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> produitService.recupererParReference("INEXISTANT"))
                .isInstanceOf(ProduitIntrouvableException.class);
    }

    @Test
    void doitRemonterLesProduitsEnDessousDuSeuil() {
        Produit stockBas = new Produit("CLAV-002", "Clavier basique", new BigDecimal("15.00"), 2);
        when(produitRepository.findByQuantiteStockLessThanEqual(5)).thenReturn(List.of(stockBas));

        List<Produit> resultat = produitService.listerStockFaible(5);

        assertThat(resultat).hasSize(1);
        assertThat(resultat.get(0).getReference()).isEqualTo("CLAV-002");
    }

    @Test
    void doitFiltrerLesProduitsParCategorie() {
        Produit peripherique = new Produit("CLAV-003", "Clavier compact", new BigDecimal("39.90"), 8, "peripheriques");
        Pageable pageable = PageRequest.of(0, 10);
        when(produitRepository.findByCategorie(eq("peripheriques"), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(peripherique)));

        var resultat = produitService.listerParCategorie("peripheriques", pageable);

        assertThat(resultat.getContent()).hasSize(1);
        assertThat(resultat.getContent().get(0).getCategorie()).isEqualTo("peripheriques");
    }

    @Test
    void doitRemonterLesProduitsSousLeurPropreSeuilAlerte() {
        Produit stockCritique = new Produit("CLAV-004", "Clavier premium", new BigDecimal("89.00"), 1);
        stockCritique.setSeuilAlerte(3);
        when(produitRepository.findEnDessousDeLeurSeuilAlerte()).thenReturn(List.of(stockCritique));

        List<Produit> resultat = produitService.listerEnDessousDeLeurSeuilAlerte();

        assertThat(resultat).hasSize(1);
        assertThat(resultat.get(0).getReference()).isEqualTo("CLAV-004");
    }

    @Test
    void doitResumerLaValeurDuStockParCategorie() {
        ResumeStockCategorie resume = new ResumeStockCategorie("peripheriques", 3L, new BigDecimal("450.00"));
        when(produitRepository.resumerStockParCategorie()).thenReturn(List.of(resume));

        List<ResumeStockCategorie> resultat = produitService.resumerStockParCategorie();

        assertThat(resultat).hasSize(1);
        assertThat(resultat.get(0).getValeurStockTotaleHt()).isEqualByComparingTo("450.00");
    }

    @Test
    void doitAugmenterLeStockLorsDuReapprovisionnement() {
        when(produitRepository.findById(1L)).thenReturn(Optional.of(clavier));
        when(produitRepository.save(any(Produit.class))).thenAnswer(inv -> inv.getArgument(0));

        Produit resultat = produitService.reapprovisionner(1L, 15);

        assertThat(resultat.getQuantiteStock()).isEqualTo(40);
    }

    @Test
    void doitRefuserUnReapprovisionnementNegatifOuNul() {
        assertThatThrownBy(() -> produitService.reapprovisionner(1L, 0))
                .isInstanceOf(IllegalArgumentException.class);
    }

   
}
