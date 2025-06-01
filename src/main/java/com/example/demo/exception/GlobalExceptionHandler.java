package com.example.demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<?> handleResourceNotFound(ResourceNotFoundException ex) {
        return buildResponseEntity(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<?> handleAccessDenied(AuthorizationDeniedException ex) {
        return buildResponseEntity(HttpStatus.FORBIDDEN, "Access Denied: You do not have the required permissions");
    }

    // Add this handler for Spring Security's AccessDeniedException
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<?> handleSpringSecurityAccessDenied(AccessDeniedException ex) {
        return buildResponseEntity(HttpStatus.FORBIDDEN, "Access Denied: You do not have the required permissions");
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<?> handleUnauthorizedException(UnauthorizedException ex) {
        return buildResponseEntity(HttpStatus.UNAUTHORIZED, ex.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArgument(IllegalArgumentException ex) {
        return buildResponseEntity(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleAllExceptions(Exception ex) {
        return buildResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred");
    }

    private ResponseEntity<Map<String, Object>> buildResponseEntity(HttpStatus status, String message) {
        Map<String, Object> body = Map.of(
                "timestamp", LocalDateTime.now(),
                "status", status.value(),
                "error", status.getReasonPhrase(),
                "message", message);
        return new ResponseEntity<>(body, status);
    }
}

// import org.springframework.http.HttpStatus;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.*;

// import java.time.LocalDateTime;
// import java.util.Map;

// @ControllerAdvice
// public class GlobalExceptionHandler {

//     @ExceptionHandler(ResourceNotFoundException.class)
//     public ResponseEntity<?> handleResourceNotFound(ResourceNotFoundException ex) {
//         return buildResponseEntity(HttpStatus.NOT_FOUND, ex.getMessage());
//     }

//     @ExceptionHandler(AuthorizationDeniedException.class)
//     public ResponseEntity<?> handleAccessDenied(AuthorizationDeniedException ex) {
//     return buildResponseEntity(HttpStatus.FORBIDDEN, "Access Denied: You do not have the required permissions");
//     }

//     @ExceptionHandler(UnauthorizedException.class)
//     public ResponseEntity<?> handleUnauthorizedException(UnauthorizedException ex) {
//         return buildResponseEntity(HttpStatus.UNAUTHORIZED, ex.getMessage());
//     }

//     @ExceptionHandler(IllegalArgumentException.class)
//     public ResponseEntity<?> handleIllegalArgument(IllegalArgumentException ex) {
//         return buildResponseEntity(HttpStatus.BAD_REQUEST, ex.getMessage());
//     }

//     @ExceptionHandler(Exception.class)
//     public ResponseEntity<?> handleAllExceptions(Exception ex) {
//         return buildResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred");
//     }

//     private ResponseEntity<Map<String, Object>> buildResponseEntity(HttpStatus status, String message) {
//         Map<String, Object> body = Map.of(
//                 "timestamp", LocalDateTime.now(),
//                 "status", status.value(),
//                 "error", status.getReasonPhrase(),
//                 "message", message);
//         return new ResponseEntity<>(body, status);
//     }
// }
