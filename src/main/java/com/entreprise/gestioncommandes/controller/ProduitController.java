package com.entreprise.gestioncommandes.controller;

import com.entreprise.gestioncommandes.model.Produit;
import com.entreprise.gestioncommandes.service.ProduitService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/produits")
public class ProduitController {

    private final ProduitService produitService;

    public ProduitController(ProduitService produitService) {
        this.produitService = produitService;
    }

    @GetMapping
    public List<Produit> lister() {
        return produitService.listerProduits();
    }

    @GetMapping("/{id}")
    public Produit recuperer(@PathVariable Long id) {
        return produitService.recupererParId(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Produit creer(@Valid @RequestBody Produit produit) {
        return produitService.creerProduit(produit);
    }

    
}
