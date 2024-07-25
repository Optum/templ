package com.optum.templ.exceptions;

public class MissingKeyTemplException extends TemplException {
    private static final long serialVersionUID = 1;

    final private String key;

    public MissingKeyTemplException(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
