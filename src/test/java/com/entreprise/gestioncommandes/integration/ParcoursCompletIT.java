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

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ParcoursCompletIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProduitRepository produitRepository;

    @Autowired
    private CommandeRepository commandeRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void chargerDonnees() {
        commandeRepository.deleteAll();
        produitRepository.deleteAll();
    }

    @Test
    void doitDeroulerLeParcoursCompletProduitCommandeAnnulationEtStatistiques() throws Exception {
        Produit produit = new Produit("KIT-050", "Kit de demarrage", new BigDecimal("45.00"), 20);
        Long idProduit = produitRepository.save(produit).getId();

        String corpsCommande = """
                {
                  "client": "Client final",
                  "clientEmail": "client@example.com",
                  "lignes": [
                    { "produitId": %d, "quantite": 2 }
                  ]
                }
                """.formatted(idProduit);

        String reponseCreation = mockMvc.perform(post("/api/commandes")
                        .contentType(APPLICATION_JSON)
                        .content(corpsCommande))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.montantTotalHt").value(90.00))
                .andReturn().getResponse().getContentAsString();
        Long idCommande = objectMapper.readTree(reponseCreation).get("id").asLong();

        mockMvc.perform(get("/api/produits/" + idProduit))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantiteStock").value(18));

        mockMvc.perform(get("/api/commandes/" + idCommande + "/integrite"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.integre").value(true));

        mockMvc.perform(get("/api/commandes/statistiques"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombreCommandesActives").value(1));

        mockMvc.perform(post("/api/commandes/" + idCommande + "/annulation"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.annulee").value(true));

        mockMvc.perform(get("/api/produits/" + idProduit))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantiteStock").value(20));

        mockMvc.perform(get("/api/commandes/statistiques"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombreCommandesActives").value(0))
                .andExpect(jsonPath("$.nombreCommandesAnnulees").value(1));
    }
}
