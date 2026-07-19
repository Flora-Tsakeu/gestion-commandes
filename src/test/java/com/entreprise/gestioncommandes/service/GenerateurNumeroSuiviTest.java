package com.entreprise.gestioncommandes.service;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

class GenerateurNumeroSuiviTest {

    @Test
    void doitRespecterLeFormatAttendu() {
        String numero = GenerateurNumeroSuivi.genererNouveauNumero();

        assertThat(numero).startsWith("CMD-");
        assertThat(numero).hasSize(16);
        assertThat(numero.substring(4)).matches("[A-F0-9]{12}");
    }

    @Test
    void doitProduireDesNumerosDistinctsSurUnGrandVolume() {
        Set<String> numeros = new HashSet<>();
        IntStream.range(0, 1000).forEach(i -> numeros.add(GenerateurNumeroSuivi.genererNouveauNumero()));

        assertThat(numeros).hasSize(1000);
    }
}
