package com.entreprise.gestioncommandes.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GestionnaireExceptionsGlobal {

    @ExceptionHandler(ProduitIntrouvableException.class)
    public ResponseEntity<Map<String, Object>> gererProduitIntrouvable(ProduitIntrouvableException ex) {
        return construireReponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(CommandeIntrouvableException.class)
    public ResponseEntity<Map<String, Object>> gererCommandeIntrouvable(CommandeIntrouvableException ex) {
        return construireReponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(StockInsuffisantException.class)
    public ResponseEntity<Map<String, Object>> gererStockInsuffisant(StockInsuffisantException ex) {
        return construireReponse(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> gererValidation(MethodArgumentNotValidException ex) {
        String details = ex.getBindingResult().getFieldErrors().stream()
                .map(err -> err.getField() + " : " + err.getDefaultMessage())
                .reduce((a, b) -> a + " ; " + b)
                .orElse("requete invalide");
        return construireReponse(HttpStatus.BAD_REQUEST, details);
    }

    private ResponseEntity<Map<String, Object>> construireReponse(HttpStatus statut, String message) {
        Map<String, Object> corps = new HashMap<>();
        corps.put("horodatage", Instant.now().toString());
        corps.put("statut", statut.value());
        corps.put("message", message);
        return ResponseEntity.status(statut).body(corps);
    }
}
