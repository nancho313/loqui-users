package com.nancho313.loqui.users.commons.validator;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class ObjectValidator {

    private ObjectValidator() {
    }

    public static boolean isEmptyString(String value) {

        return value == null || value.isBlank();
    }

    public static boolean isNotAnEmptyString(String value) {

        return !isEmptyString(value);
    }

    public static boolean isNull(Object value) {

        return value == null;
    }

    public static boolean isNotNull(Object value) {

        return !isNull(value);
    }

    public static <T> List<T> getImmutableList(List<T> list) {

        return list == null ? Collections.emptyList() : List.copyOf(list);
    }
}
