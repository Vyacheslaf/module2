package com.epam.esm.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class InvalidSortRequestException extends RuntimeException {
    private String message;
}
