package com.epam.esm.exception;

import com.epam.esm.exception.controller.InvalidRequestException;
import com.epam.esm.exception.dao.DaoDuplicateKeyException;
import com.epam.esm.exception.dao.DaoWrongIdException;
import com.epam.esm.exception.service.ServiceWrongTagNameException;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.sql.SQLException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Returns error if resource already exists
     *
     * Response:
     *     Content-Type: application/json
     *     Status Codes: 409
     *         Response Body: ErrorResponse
     *             application/json: {"errorMessage":"string","errorCode":40901}
     *
     * @param e DaoDuplicateKeyException
     * @return Error response
     */
    @ExceptionHandler(DaoDuplicateKeyException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateTagException(DaoDuplicateKeyException e) {
        long errorCode = 40901;
        String errorMessage = "Tag " + e.getKeyName() + " already exists";
        ErrorResponse errorResponse = new ErrorResponse(errorMessage, errorCode);
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    /**
     * Returns error if resource not found
     *
     * Response:
     *     Content-Type: application/json
     *     Status Codes: 404
     *         Response Body: ErrorResponse
     *             application/json: {"errorMessage":"string","errorCode":number}
     *
     * @param e DaoWrongIdException
     * @return Error response
     */
    @ExceptionHandler(DaoWrongIdException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(DaoWrongIdException e) {
        HttpStatus httpStatus = HttpStatus.NOT_FOUND;
        String errorMessage = "Requested " + e.getResourceName() + " not found (id=" + e.getId() + ")";
        long errorCode = httpStatus.value() * 100 + e.getId();
        ErrorResponse errorResponse = new ErrorResponse(errorMessage, errorCode);
        return new ResponseEntity<>(errorResponse, httpStatus);
    }

    /**
     * Returns error if no handler found for request
     *
     * Response:
     *     Content-Type: application/json
     *     Status Codes: 400
     *         Response Body: ErrorResponse
     *             application/json: {"errorMessage":"string","errorCode":40001}
     *
     * @param e NoHandlerFoundException
     * @return Error response
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoHandlerFoundException(NoHandlerFoundException e) {
        long errorCode = 40001;
        ErrorResponse errorResponse = new ErrorResponse(e.getMessage(), errorCode);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Returns error if database error occurs
     *
     * Response:
     *     Content-Type: application/json
     *     Status Codes: 500
     *         Response Body: ErrorResponse
     *             application/json: {"errorMessage":"string","errorCode":50001}
     *
     * @return Error response
     */
    @ExceptionHandler({SQLException.class, DataAccessException.class})
    public ResponseEntity<ErrorResponse> handleDatabaseError() {
        long errorCode = 50001;
        ErrorResponse errorResponse = new ErrorResponse("Database error", errorCode);
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Returns error if request contains wrong arguments or parameters
     *
     * Response:
     *     Content-Type: application/json
     *     Status Codes: 400
     *         Response Body: ErrorResponse
     *             application/json: {"errorMessage":"string","errorCode":40002}
     *
     * @param e Exception
     * @return Error response
     */
    @ExceptionHandler({MethodArgumentNotValidException.class,
            MethodArgumentTypeMismatchException.class,
            HttpMessageNotReadableException.class,
            HttpMediaTypeNotSupportedException.class})
    public ResponseEntity<ErrorResponse> handleNotValidException(Exception e) {
        long errorCode = 40002;
        ErrorResponse errorResponse = new ErrorResponse(e.getMessage(), errorCode);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Returns error if request does not contain at least one of required arguments
     *
     * Response:
     *     Content-Type: application/json
     *     Status Codes: 400
     *         Response Body: ErrorResponse
     *             application/json: {"errorMessage":"string","errorCode":40003}
     *
     * @param e InvalidRequestException
     * @return Error response
     */
    @ExceptionHandler(InvalidRequestException.class)
    public ResponseEntity<ErrorResponse> handleInvalidRequestException(InvalidRequestException e) {
        long errorCode = 40003;
        ErrorResponse errorResponse = new ErrorResponse(e.getErrorMesage(), errorCode);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Returns error if request does not contain tagName
     *
     * Response:
     *     Content-Type: application/json
     *     Status Codes: 400
     *         Response Body: ErrorResponse
     *             application/json: {"errorMessage":"string","errorCode":40004}
     *
     * @return Error response
     */
    @ExceptionHandler(ServiceWrongTagNameException.class)
    public ResponseEntity<ErrorResponse> handleWrongTagNameException() {
        long errorCode = 40004;
        String errorMessage = "tagName can not be null";
        ErrorResponse errorResponse = new ErrorResponse(errorMessage, errorCode);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Returns error if this error is not recognised
     *
     * Response:
     *     Content-Type: application/json
     *     Status Codes: 500
     *         Response Body: ErrorResponse
     *             application/json: {"errorMessage":"string","errorCode":50000}
     *
     * @param e InvalidRequestException
     * @return Error response
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllException(Exception e) {
        e.printStackTrace();
        long errorCode = 50000;
        ErrorResponse errorResponse = new ErrorResponse("Unrecognised server error", errorCode);
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private class ErrorResponse {
        private String errorMessage;
        private long errorCode;

        public ErrorResponse(String errorMessage, long errorCode) {
            this.errorMessage = errorMessage;
            this.errorCode = errorCode;
        }

        public String getErrorMessage() {
            return errorMessage;
        }

        public long getErrorCode() {
            return errorCode;
        }
    }
}
