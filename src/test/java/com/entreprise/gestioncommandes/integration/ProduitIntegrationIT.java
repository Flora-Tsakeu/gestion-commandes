package com.entreprise.gestioncommandes.integration;

import com.entreprise.gestioncommandes.model.Produit;
import com.entreprise.gestioncommandes.repository.ProduitRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ProduitIntegrationIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProduitRepository produitRepository;

    private Long idEcran;

    @BeforeEach
    void chargerDonnees() {
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
}
