package com.entreprise.gestioncommandes.integration;

import com.entreprise.gestioncommandes.model.Produit;
import com.entreprise.gestioncommandes.repository.CommandeRepository;
import com.entreprise.gestioncommandes.repository.ProduitRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ProduitIntegrationIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProduitRepository produitRepository;

    @Autowired
    private CommandeRepository commandeRepository;


    private Long idEcran;

    @BeforeEach
    void chargerDonnees() {
        commandeRepository.deleteAll();
        produitRepository.deleteAll();
        Produit ecran = new Produit("ECR-030", "Ecran 30 pouces", new BigDecimal("249.00"), 6);
        idEcran = produitRepository.save(ecran).getId();
    }

    @Test
    void doitAugmenterLeStockViaLEndpointDeReapprovisionnement() throws Exception {
        String corps = """
                { "quantite": 10 }
                """;

        mockMvc.perform(patch("/api/produits/" + idEcran + "/reapprovisionnement")
                        .contentType(APPLICATION_JSON)
                        .content(corps))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantiteStock").value(16));
    }

    @Test
    void doitRefuserLaCreationDunProduitAvecReferenceExistante() throws Exception {
        String corps = """
                {
                  "reference": "ECR-030",
                  "libelle": "Ecran concurrent",
                  "prixUnitaireHt": 199.00,
                  "quantiteStock": 4
                }
                """;

        mockMvc.perform(post("/api/produits")
                        .contentType(APPLICATION_JSON)
                        .content(corps))
                .andExpect(status().isConflict());
    }

    @Test
    void doitAutoriserLaSuppressionDunProduitNonReference() throws Exception {
        mockMvc.perform(delete("/api/produits/" + idEcran))
                .andExpect(status().isNoContent());
    }

    @Test
    void doitRefuserLaSuppressionDunProduitReferenceParUneCommande() throws Exception {
        String corpsCommande = """
                {
                  "client": "Boutique Nord",
                  "lignes": [
                    { "produitId": %d, "quantite": 1 }
                  ]
                }
                """.formatted(idEcran);
        mockMvc.perform(post("/api/commandes")
                        .contentType(APPLICATION_JSON)
                        .content(corpsCommande))
                .andExpect(status().isCreated());

        mockMvc.perform(delete("/api/produits/" + idEcran))
                .andExpect(status().isConflict());
    }

    @Test
    void doitExporterLeCatalogueEnCsv() throws Exception {
        mockMvc.perform(get("/api/produits/export"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("reference;libelle;categorie")))
                .andExpect(content().string(containsString("ECR-030")));
    }

    @Test
    void doitRetrouverLEcranDansLaPlageDePrixCorrespondante() throws Exception {
        mockMvc.perform(get("/api/produits/plage-prix")
                        .param("prixMin", "200.00")
                        .param("prixMax", "300.00"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].reference").value("ECR-030"));
    }

    @Test
    void doitRemonterLEcranDansLeTopVentesApresUneCommande() throws Exception {
        String corps = """
                {
                  "client": "Boutique Nord",
                  "lignes": [
                    { "produitId": %d, "quantite": 2 }
                  ]
                }
                """.formatted(idEcran);
        mockMvc.perform(post("/api/commandes")
                        .contentType(APPLICATION_JSON)
                        .content(corps))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/produits/top-ventes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].reference").value("ECR-030"))
                .andExpect(jsonPath("$[0].quantiteTotaleVendue").value(2));
    }
}
