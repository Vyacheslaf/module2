package com.epam.esm.exception;

import com.epam.esm.exception.controller.NoContentException;
import com.epam.esm.exception.dao.DaoDuplicateKeyException;
import com.epam.esm.exception.dao.DaoTagForUserNotFoundException;
import com.epam.esm.exception.dao.DaoWrongIdException;
import com.epam.esm.exception.dao.DaoWrongOrderIdForUserException;
import com.epam.esm.exception.service.ServiceWrongTagNameException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.StringJoiner;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

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
        String errorCode = "40901";
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
        String errorCode = httpStatus.value() + "0" + e.getId();
        ErrorResponse errorResponse = new ErrorResponse(errorMessage, errorCode);
        return new ResponseEntity<>(errorResponse, httpStatus);
    }

    @ExceptionHandler(DaoTagForUserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleTagForUserNotFoundException(DaoTagForUserNotFoundException e) {
        HttpStatus httpStatus = HttpStatus.NOT_FOUND;
        String errorMessage = "Tag not found for user with id=" + e.getUserId();
        String errorCode = httpStatus.value() + "1" + e.getUserId();
        ErrorResponse errorResponse = new ErrorResponse(errorMessage, errorCode);
        return new ResponseEntity<>(errorResponse, httpStatus);
    }

    @ExceptionHandler(DaoWrongOrderIdForUserException.class)
    public ResponseEntity<ErrorResponse> handleWrongOrderIdForUserException(DaoWrongOrderIdForUserException e) {
        HttpStatus httpStatus = HttpStatus.NOT_FOUND;
        String errorMessage = "Order with id=" + e.getOrderId() + " not found for user with id=" + e.getUserId();
        String errorCode = httpStatus.value() + "2" + e.getOrderId();
        ErrorResponse errorResponse = new ErrorResponse(errorMessage, errorCode);
        return new ResponseEntity<>(errorResponse, httpStatus);
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
    public ResponseEntity<ErrorResponse> handleDatabaseError(Exception e) {
        e.printStackTrace();
        String errorCode = "50001";
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
    @ExceptionHandler({MethodArgumentTypeMismatchException.class})
    public ResponseEntity<ErrorResponse> handleNotValidException(Exception e) {
        String errorCode = "40002";
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
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleInvalidRequestException(ConstraintViolationException e) {
        String errorCode = "40003";
        StringJoiner joiner = new StringJoiner(", ");
        new ArrayList<>(e.getConstraintViolations()).forEach(cv -> joiner.add(cv.getMessage()));
        ErrorResponse errorResponse = new ErrorResponse(joiner.toString(), errorCode);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidSortRequestException.class)
    public ResponseEntity<ErrorResponse> handleInvalidSortRequestException(InvalidSortRequestException e) {
        String errorCode = "40004";
        String errorMessage = "wrong format of sort parameter, required: " + e.getMessage();
        ErrorResponse errorResponse = new ErrorResponse(errorMessage, errorCode);
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
        String errorCode = "40005";
        String errorMessage = "name of tag can not be null";
        ErrorResponse errorResponse = new ErrorResponse(errorMessage, errorCode);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidTagNameException.class)
    public ResponseEntity<ErrorResponse> handleInvalidTagNameException() {
        String errorCode = "40006";
        String errorMessage = "name of tag can not be null or empty";
        ErrorResponse errorResponse = new ErrorResponse(errorMessage, errorCode);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NoContentException.class)
    public ResponseEntity handleNoContentException() {
        return new ResponseEntity(HttpStatus.NO_CONTENT);
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
        String errorCode = "50000";
        ErrorResponse errorResponse = new ErrorResponse("Unrecognised server error", errorCode);
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private class ErrorResponse {
        private String errorMessage;
        private String errorCode;

        public ErrorResponse(String errorMessage, String errorCode) {
            this.errorMessage = errorMessage;
            this.errorCode = errorCode;
        }

        public String getErrorMessage() {
            return errorMessage;
        }

        public String getErrorCode() {
            return errorCode;
        }
    }
}
