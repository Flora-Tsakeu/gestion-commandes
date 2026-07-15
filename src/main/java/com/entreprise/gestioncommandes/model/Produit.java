package com.entreprise.gestioncommandes.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

@Entity
public class Produit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "la reference produit est obligatoire")
    @Size(max = 30, message = "la reference ne peut pas depasser 30 caracteres")
    private String reference;

    @NotBlank(message = "le libelle est obligatoire")
    @Size(max = 120, message = "le libelle ne peut pas depasser 120 caracteres")
    private String libelle;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = true, message = "le prix unitaire ne peut pas etre negatif")
    private BigDecimal prixUnitaireHt;

    @NotNull
    @Min(value = 0, message = "le stock ne peut pas etre negatif")
    private Integer quantiteStock;

    @Size(max = 40, message = "la categorie ne peut pas depasser 40 caracteres")
    private String categorie;

    public Produit() {
    }

    public Produit(String reference, String libelle, BigDecimal prixUnitaireHt, Integer quantiteStock) {
        this.reference = reference;
        this.libelle = libelle;
        this.prixUnitaireHt = prixUnitaireHt;
        this.quantiteStock = quantiteStock;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public BigDecimal getPrixUnitaireHt() {
        return prixUnitaireHt;
    }

    public void setPrixUnitaireHt(BigDecimal prixUnitaireHt) {
        this.prixUnitaireHt = prixUnitaireHt;
    }

    public Integer getQuantiteStock() {
        return quantiteStock;
    }

    public void setQuantiteStock(Integer quantiteStock) {
        this.quantiteStock = quantiteStock;
    }

    public String getCategorie() {
        return categorie;
    }

    public void setCategorie(String categorie) {
        this.categorie = categorie;
    }
}
