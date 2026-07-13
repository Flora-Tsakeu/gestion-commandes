package com.entreprise.gestioncommandes.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
public class LigneCommande {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JsonBackReference
    private Commande commande;

    @ManyToOne
    private Produit produit;

    @NotNull
    @Min(value = 1, message = "la quantite commandee doit etre superieure a zero")
    private Integer quantite;

    private BigDecimal prixUnitaireHtApplique;

    public LigneCommande() {
    }

    public LigneCommande(Produit produit, Integer quantite) {
        this.produit = produit;
        this.quantite = quantite;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Commande getCommande() {
        return commande;
    }

    public void setCommande(Commande commande) {
        this.commande = commande;
    }

    public Produit getProduit() {
        return produit;
    }

    public void setProduit(Produit produit) {
        this.produit = produit;
    }

    public Integer getQuantite() {
        return quantite;
    }

    public void setQuantite(Integer quantite) {
        this.quantite = quantite;
    }

    public BigDecimal getPrixUnitaireHtApplique() {
        return prixUnitaireHtApplique;
    }

    public void setPrixUnitaireHtApplique(BigDecimal prixUnitaireHtApplique) {
        this.prixUnitaireHtApplique = prixUnitaireHtApplique;
    }
}
