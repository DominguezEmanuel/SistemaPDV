package com.sistemapdv.backend.exception;

import com.sistemapdv.backend.dto.ErrorResponseDTO;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> reportNotFound(ResourceNotFoundException ex,
                                                           HttpServletRequest request){
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(buildError(HttpStatus.NOT_FOUND, ex.getMessage(), request));
    }

    @ExceptionHandler(ResourceDuplicatedException.class)
    public ResponseEntity<ErrorResponseDTO> reportResourceDuplicated(ResourceDuplicatedException ex,
                                                                     HttpServletRequest request){
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(buildError(HttpStatus.CONFLICT, ex.getMessage(), request));
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ErrorResponseDTO> reportInvalidCredentials(InvalidCredentialsException ex,
                                                                     HttpServletRequest request){
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(buildError(HttpStatus.UNAUTHORIZED, ex.getMessage(), request));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponseDTO> reportIllegalArguments(IllegalArgumentException ex,
                                                                   HttpServletRequest request){
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(buildError(HttpStatus.BAD_REQUEST, ex.getMessage(), request));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> reportValidationErrors(
            MethodArgumentNotValidException ex,
            HttpServletRequest request){

        List<String> errores = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getDefaultMessage())
                .distinct()
                .toList();

        ErrorResponseDTO error = ErrorResponseDTO.builder()
                .fecha(getFechaActual())
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .errores(errores)
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(EmailException.class)
    public ResponseEntity<ErrorResponseDTO> reportEmailNotSend(EmailException ex,
                                                               HttpServletRequest request){
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(buildError(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), request));
    }

    public ErrorResponseDTO buildError(HttpStatus status,
            String mensaje, HttpServletRequest request){

        return ErrorResponseDTO.builder()
                .fecha(getFechaActual())
                .status(status.value())
                .error(status.getReasonPhrase())
                .mensaje(mensaje)
                .path(request.getRequestURI())
                .build();
    }

    private String getFechaActual(){
        DateTimeFormatter formateador = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return LocalDate.now().format(formateador);
    }
}
