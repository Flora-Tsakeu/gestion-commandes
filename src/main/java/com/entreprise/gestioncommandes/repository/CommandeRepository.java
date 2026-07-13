package com.entreprise.gestioncommandes.repository;

import com.entreprise.gestioncommandes.model.Commande;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommandeRepository extends JpaRepository<Commande, Long> {
}
