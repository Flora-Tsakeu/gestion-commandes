package com.entreprise.gestioncommandes.integration;

import com.entreprise.gestioncommandes.model.Produit;
import com.entreprise.gestioncommandes.repository.CommandeRepository;
import com.entreprise.gestioncommandes.repository.ProduitRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@SpringBootTest
@AutoConfigureMockMvc
class CommandeIntegrationIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProduitRepository produitRepository;

    @Autowired
    private CommandeRepository commandeRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Long idProduitDispo;

    @BeforeEach
    void chargerDonnees() {
        commandeRepository.deleteAll(); 
        produitRepository.deleteAll();
        Produit produit = new Produit("SOU-014", "Souris sans fil", new BigDecimal("29.90"), 15);
        idProduitDispo = produitRepository.save(produit).getId();
    }

    @Test
    void doitCreerUneCommandeEtDecrementerLeStock() throws Exception {
        String corps = """
                {
                  "client": "Boutique Nord",
                  "lignes": [
                    { "produitId": %d, "quantite": 3 }
                  ]
                }
                """.formatted(idProduitDispo);

        mockMvc.perform(post("/api/commandes")
                        .contentType(APPLICATION_JSON)
                        .content(corps))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.client").value("Boutique Nord"))
                .andExpect(jsonPath("$.montantTotalTtc").value(107.64));

        mockMvc.perform(get("/api/produits/" + idProduitDispo))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantiteStock").value(12));
    }

    @Test
    void doitRenvoyerConflitSiStockInsuffisantLorsDeLIntegration() throws Exception {
        String corps = """
                {
                  "client": "Boutique Sud",
                  "lignes": [
                    { "produitId": %d, "quantite": 999 }
                  ]
                }
                """.formatted(idProduitDispo);

        mockMvc.perform(post("/api/commandes")
                        .contentType(APPLICATION_JSON)
                        .content(corps))
                .andExpect(status().isConflict());
    }

    @Test
    void doitReintegrerLeStockLorsDeLAnnulation() throws Exception {
        String corps = """
                {
                  "client": "Boutique Est",
                  "lignes": [
                    { "produitId": %d, "quantite": 4 }
                  ]
                }
                """.formatted(idProduitDispo);

        String reponse = mockMvc.perform(post("/api/commandes")
                        .contentType(APPLICATION_JSON)
                        .content(corps))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long idCommande = objectMapper.readTree(reponse).get("id").asLong();

        mockMvc.perform(post("/api/commandes/" + idCommande + "/annulation"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.annulee").value(true));

        mockMvc.perform(get("/api/produits/" + idProduitDispo))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantiteStock").value(15));
    }


}
