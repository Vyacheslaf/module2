package com.epam.esm.exception.controller;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.StringJoiner;

@Getter
@AllArgsConstructor
public class InvalidRequestException extends RuntimeException {
    private static final String JOINER_DELIMITER = ", ";
    private static final String JOINER_PREFIX = "Field(s) ";
    private static final String JOINER_POSTFIX = " cannot be null";
    private String errorMesage;

    public InvalidRequestException(BindingResult br) {
        StringJoiner joiner = new StringJoiner(JOINER_DELIMITER, JOINER_PREFIX, JOINER_POSTFIX);
        br.getAllErrors().stream()
                .filter(o -> o instanceof FieldError)
                .forEach(o -> joiner.add(((FieldError) o).getField()));
        this.errorMesage = joiner.toString();
    }
}

