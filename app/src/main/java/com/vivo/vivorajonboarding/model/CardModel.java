package com.vivo.vivorajonboarding.model;
import java.util.List;

public class CardModel {
    private final String title;
    private final List<String> formFields;

    public CardModel(String title, List<String> formFields) {
        this.title = title;
        this.formFields = formFields;
    }

    public String getTitle() {
        return title;
    }

    public List<String> getFormFields() {
        return formFields;
    }
}
