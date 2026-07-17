package com.entreprise.gestioncommandes.repository;

import com.entreprise.gestioncommandes.model.Commande;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CommandeRepository extends JpaRepository<Commande, Long> {

    List<Commande> findByClientOrderByDateCreationDesc(String client);

    List<Commande> findAllByOrderByDateCreationDesc();

    List<Commande> findByAnnuleeOrderByDateCreationDesc(boolean annulee);

    List<Commande> findByDateCreationBetweenOrderByDateCreationDesc(LocalDateTime debut, LocalDateTime fin);

    Page<Commande> findByClientOrderByDateCreationDesc(String client, Pageable pageable);

    Page<Commande> findAllByOrderByDateCreationDesc(Pageable pageable);

    Optional<Commande> findByNumeroSuivi(String numeroSuivi);

    long countByAnnulee(boolean annulee);

    @Query("select coalesce(sum(c.montantTotalTtc), 0) from Commande c where c.annulee = false")
    BigDecimal calculerChiffreAffairesActif();
}
