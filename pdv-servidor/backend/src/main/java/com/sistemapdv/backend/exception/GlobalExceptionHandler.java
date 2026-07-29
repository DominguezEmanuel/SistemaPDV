package com.sistemapdv.backend.exception;

import com.sistemapdv.backend.dto.ErrorResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> reportNotFound(ResourceNotFoundException ex,
                                                           HttpServletRequest request){

        ErrorResponseDTO error = toError(HttpStatus.NOT_FOUND, ex, request);

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(error);
    }

    @ExceptionHandler(ResourceDuplicatedException.class)
    public ResponseEntity<ErrorResponseDTO> reportResourceDuplicated(ResourceDuplicatedException ex,
                                                                     HttpServletRequest request){
        ErrorResponseDTO error = toError(HttpStatus.CONFLICT, ex, request);

        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(error);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ErrorResponseDTO> reportInvalidCredentials(InvalidCredentialsException ex,
                                                                     HttpServletRequest request){

        ErrorResponseDTO error = toError(HttpStatus.UNAUTHORIZED, ex, request);

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(error);
    }

    public ErrorResponseDTO toError(HttpStatus status, Exception ex, HttpServletRequest request){

        ErrorResponseDTO error = new ErrorResponseDTO();

        error.setFecha(LocalDateTime.now());
        error.setStatus(status.value());
        error.setError(status.getReasonPhrase());
        error.setMensaje(ex.getMessage());
        error.setPath(request.getRequestURI());

        return error;
    }
}
