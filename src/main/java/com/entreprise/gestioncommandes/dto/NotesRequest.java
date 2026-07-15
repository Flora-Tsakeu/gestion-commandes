package com.entreprise.gestioncommandes.dto;

import jakarta.validation.constraints.Size;

public class NotesRequest {

    @Size(max = 500, message = "les notes ne peuvent pas depasser 500 caracteres")
    private String notes;

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
