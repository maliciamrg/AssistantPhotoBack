package com.malicia.mrg.assistant.photo.dto;

import java.util.List;

public class ValidationResult {
    private List<String> currentFields;
    private List<List<String>> validFields;
    private boolean valid = false;
    private String message = "";
    public ValidationResult(boolean valid, String message) {
        this.valid = valid;
        this.message = message;
    }
    public ValidationResult() {
    }

    public List<String> getCurrentFields() {
        return currentFields;
    }

    public void setCurrentFields(List<String> currentFields) {
        this.currentFields = currentFields;
    }

    public List<List<String>> getValidFields() {
        return validFields;
    }

    public void setValidFields(List<List<String>> validFields) {
        this.validFields = validFields;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
