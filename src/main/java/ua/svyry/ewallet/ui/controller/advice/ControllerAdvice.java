package ua.svyry.ewallet.ui.controller.advice;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestControllerAdvice
public class ControllerAdvice {

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(NOT_FOUND)
    public ErrorResponse entityNotFoundException(HttpServletRequest request, EntityNotFoundException exception) {
        return ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(NOT_FOUND.value())
                .error(exception.getMessage())
                .path(request.getRequestURI())
                .build();
    }

    @ExceptionHandler(ExpiredJwtException.class)
    @ResponseStatus(FORBIDDEN)
    public ErrorResponse entityNotFoundException(HttpServletRequest request, ExpiredJwtException exception) {
        return ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(FORBIDDEN.value())
                .error(exception.getMessage())
                .path(request.getRequestURI())
                .build();
    }
}
