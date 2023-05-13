package com.epam.esm.exception.service;

public class ServiceException extends RuntimeException {
    public ServiceException() {
    }

    public ServiceException(Throwable cause) {
        super(cause);
    }
}
