package com.entreprise.gestioncommandes.service;

import com.entreprise.gestioncommandes.model.Produit;
import com.entreprise.gestioncommandes.repository.ProduitRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExportProduitServiceTest {

    @Mock
    private ProduitRepository produitRepository;

    @InjectMocks
    private ExportProduitService exportProduitService;

    @Test
    void doitGenererUnCsvAvecEnteteEtUneLigneParProduit() {
        Produit clavier = new Produit("CLAV-001", "Clavier mecanique", new BigDecimal("59.90"), 25, "peripheriques");
        when(produitRepository.findAll()).thenReturn(List.of(clavier));

        String csv = exportProduitService.exporterCatalogueEnCsv();

        assertThat(csv).startsWith("reference;libelle;categorie;prixUnitaireHt;quantiteStock;seuilAlerte");
        assertThat(csv).contains("CLAV-001;Clavier mecanique;peripheriques;59.90;25");
    }

    @Test
    void doitEchapperLesPointsVirgulesDansLesChampsTexte() {
        Produit produit = new Produit("REF-099", "Cable; blinde 2m", new BigDecimal("4.50"), 100);
        when(produitRepository.findAll()).thenReturn(List.of(produit));

        String csv = exportProduitService.exporterCatalogueEnCsv();

        assertThat(csv).contains("Cable, blinde 2m");
        assertThat(csv).doesNotContain("Cable; blinde 2m");
    }
}
