package com.entreprise.gestioncommandes.service;

import com.entreprise.gestioncommandes.model.Produit;
import com.entreprise.gestioncommandes.repository.ProduitRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
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
        when(produitRepository.save(any(Produit.class))).thenReturn(clavier);

        Produit resultat = produitService.creerProduit(clavier);

        assertThat(resultat.getReference()).isEqualTo("CLAV-001");
        assertThat(resultat.getQuantiteStock()).isEqualTo(25);
    }

    @Test
    void doitLeverUneExceptionSiProduitAbsent() {
        when(produitRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> produitService.recupererParId(99L))
                .hasMessageContaining("99");
    }

   
}
