package com.entreprise.gestioncommandes.controller;

import com.entreprise.gestioncommandes.dto.ReapprovisionnementRequest;
import com.entreprise.gestioncommandes.dto.ResumeStockCategorie;
import com.entreprise.gestioncommandes.model.Produit;
import com.entreprise.gestioncommandes.service.ExportProduitService;
import com.entreprise.gestioncommandes.service.ProduitService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/produits")
public class ProduitController {

    private final ProduitService produitService;
    private final ExportProduitService exportProduitService;

    public ProduitController(ProduitService produitService, ExportProduitService exportProduitService) {
        this.produitService = produitService;
        this.exportProduitService = exportProduitService;
    }
 
    @GetMapping
    public Page<Produit> lister(Pageable pageable,
                                 @RequestParam(required = false) String categorie,
                                 @RequestParam(required = false) String recherche) {
        if (recherche != null && !recherche.isBlank()) {
            return produitService.rechercherParLibelle(recherche, pageable);
        }
        if (categorie != null && !categorie.isBlank()) {
            return produitService.listerParCategorie(categorie, pageable);
        }
        return produitService.listerProduits(pageable);
    }

    @GetMapping("/stock-faible")
    public List<Produit> listerStockFaible(@RequestParam(required = false) Integer seuil) {
        if (seuil != null) {
            return produitService.listerStockFaible(seuil);
        }
        return produitService.listerEnDessousDeLeurSeuilAlerte();
    }

    @GetMapping("/resume-stock")
    public List<ResumeStockCategorie> resumerStockParCategorie() {
        return produitService.resumerStockParCategorie();
    }

    @GetMapping("/export")
    public ResponseEntity<String> exporter() {
        String csv = exportProduitService.exporterCatalogueEnCsv();
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("text/csv"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"catalogue-produits.csv\"")
                .body(csv);
    }

    @GetMapping("/{id}")
    public Produit recuperer(@PathVariable Long id) {
        return produitService.recupererParId(id);
    }

    @GetMapping("/reference/{reference}")
    public Produit recupererParReference(@PathVariable String reference) {
        return produitService.recupererParReference(reference);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Produit creer(@Valid @RequestBody Produit produit) {
        return produitService.creerProduit(produit);
    }

    @PutMapping("/{id}")
    public Produit modifier(@PathVariable Long id, @Valid @RequestBody Produit produit) {
        return produitService.mettreAJour(id, produit);
    }

    @PatchMapping("/{id}/reapprovisionnement")
    public Produit reapprovisionner(@PathVariable Long id, @Valid @RequestBody ReapprovisionnementRequest requete) {
        return produitService.reapprovisionner(id, requete.getQuantite());
    }

    @PatchMapping("/{id}/desactivation")
    public Produit desactiver(@PathVariable Long id) {
        return produitService.changerStatutActif(id, false);
    }

    @PatchMapping("/{id}/reactivation")
    public Produit reactiver(@PathVariable Long id) {
        return produitService.changerStatutActif(id, true);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void supprimer(@PathVariable Long id) {
        produitService.supprimer(id);
    }

    
}
