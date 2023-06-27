package ua.svyry.ewallet.ui.controller.advice;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ua.svyry.ewallet.exception.DeletedEntityException;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.GONE;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

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

    @ExceptionHandler(EntityExistsException.class)
    @ResponseStatus(CONFLICT)
    public ErrorResponse entityExistsException(HttpServletRequest request, EntityExistsException exception) {
        return ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(CONFLICT.value())
                .error(exception.getMessage())
                .path(request.getRequestURI())
                .build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(BAD_REQUEST)
    public ErrorResponse methodArgumentNotValidException(HttpServletRequest request, MethodArgumentNotValidException exception) {
        return ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(BAD_REQUEST.value())
                .error(exception
                        .getFieldErrors()
                        .stream()
                        .map(e ->
                                String.format("%s[Field: %s; rejected value: '%s']",
                                        e.getDefaultMessage(), e.getField(), e.getRejectedValue()))
                        .collect(Collectors.joining(", ")))
                .path(request.getRequestURI())
                .build();
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(FORBIDDEN)
    public ErrorResponse accessDeniedException(HttpServletRequest request, AccessDeniedException exception) {
        return ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(FORBIDDEN.value())
                .error(exception.getMessage())
                .path(request.getRequestURI())
                .build();
    }

    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(UNAUTHORIZED)
    public ErrorResponse authenticationException(HttpServletRequest request, AuthenticationException exception) {
        return ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(UNAUTHORIZED.value())
                .error(exception.getMessage())
                .path(request.getRequestURI())
                .build();
    }

    @ExceptionHandler(DeletedEntityException.class)
    @ResponseStatus(GONE)
    public ErrorResponse deletedEntityException(HttpServletRequest request, DeletedEntityException exception) {
        return ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(GONE.value())
                .error(exception.getMessage())
                .path(request.getRequestURI())
                .build();
    }

    @ExceptionHandler(ExpiredJwtException.class)
    @ResponseStatus(FORBIDDEN)
    public ErrorResponse expiredJwtException(HttpServletRequest request, ExpiredJwtException exception) {
        return ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(FORBIDDEN.value())
                .error(exception.getMessage())
                .path(request.getRequestURI())
                .build();
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(INTERNAL_SERVER_ERROR)
    public ErrorResponse generalException(HttpServletRequest request, Exception exception) {
        return ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(INTERNAL_SERVER_ERROR.value())
                .error(exception.getClass().getName() + exception.getMessage())
                .path(request.getRequestURI())
                .build();
    }
}
