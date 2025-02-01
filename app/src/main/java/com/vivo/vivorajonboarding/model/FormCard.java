package com.vivo.vivorajonboarding.model;

import java.io.Serializable;
import java.util.List;

public class FormCard implements Serializable {
    private String title;
    private List<FormField> fields;

    public FormCard(String title, List<FormField> fields) {
        this.title = title;
        this.fields = fields;
    }

    // Getters
    public String getTitle() { return title; }
    public List<FormField> getFields() { return fields; }
}
