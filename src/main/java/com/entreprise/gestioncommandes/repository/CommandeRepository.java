package com.entreprise.gestioncommandes.repository;

import com.entreprise.gestioncommandes.model.Commande;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface CommandeRepository extends JpaRepository<Commande, Long> {

    List<Commande> findByClientOrderByDateCreationDesc(String client);

    List<Commande> findAllByOrderByDateCreationDesc();

    List<Commande> findByAnnuleeOrderByDateCreationDesc(boolean annulee);

    List<Commande> findByDateCreationBetweenOrderByDateCreationDesc(LocalDateTime debut, LocalDateTime fin);

    long countByAnnulee(boolean annulee);

    @Query("select coalesce(sum(c.montantTotalTtc), 0) from Commande c where c.annulee = false")
    BigDecimal calculerChiffreAffairesActif();
}
