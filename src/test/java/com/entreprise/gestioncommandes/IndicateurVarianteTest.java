package com.entreprise.gestioncommandes;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;

class IndicateurVarianteTest {

    @Test
    void doitSExecuterSansLeverDException() {
        IndicateurVariante indicateur = new IndicateurVariante();

        assertThatCode(() -> indicateur.run()).doesNotThrowAnyException();
    }
}
