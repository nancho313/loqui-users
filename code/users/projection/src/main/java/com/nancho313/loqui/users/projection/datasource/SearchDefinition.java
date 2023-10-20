package com.nancho313.loqui.users.projection.datasource;

public record SearchDefinition(Page page, Sort sort) {

    public record Page(int index, int quantity) {
    }

    public record Sort(String field, boolean ascendant) {
    }
}
