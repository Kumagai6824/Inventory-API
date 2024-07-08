package com.raisetech.inventoryapi.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.ZonedDateTime;
import java.util.Map;

@RestControllerAdvice
public class InventoryProductExceptionHandler {

    @ExceptionHandler(value = InventoryStillExistsException.class)
    public ResponseEntity<Map<String, String>> handleInventoryStillExists(
            InventoryStillExistsException e, HttpServletRequest request
    ) {

        Map<String, String> body = Map.of(
                "timestamp", ZonedDateTime.now().toString(),
                "status", String.valueOf(HttpStatus.CONFLICT.value()),
                "error", HttpStatus.CONFLICT.getReasonPhrase(),
                "message", e.getMessage(),
                "path", request.getRequestURI());
        return new ResponseEntity(body, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(value = InvalidInputException.class)
    public ResponseEntity<Map<String, String>> handleInvalidRequest(
            InvalidInputException e, HttpServletRequest request
    ) {

        Map<String, String> body = Map.of(
                "timestamp", ZonedDateTime.now().toString(),
                "status", String.valueOf(HttpStatus.BAD_REQUEST.value()),
                "error", HttpStatus.BAD_REQUEST.getReasonPhrase(),
                "message", e.getMessage(),
                "path", request.getRequestURI());
        return new ResponseEntity(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = InventoryShortageException.class)
    public ResponseEntity<Map<String, String>> handleInventoryShortage(
            InventoryShortageException e, HttpServletRequest request
    ) {

        Map<String, String> body = Map.of(
                "timestamp", ZonedDateTime.now().toString(),
                "status", String.valueOf(HttpStatus.CONFLICT.value()),
                "error", HttpStatus.CONFLICT.getReasonPhrase(),
                "message", e.getMessage(),
                "path", request.getRequestURI());
        return new ResponseEntity(body, HttpStatus.CONFLICT);
    }
}
