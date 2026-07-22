package com.onlinejobportal.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.ui.Model;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.stream.Collectors;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleResourceNotFoundException(ResourceNotFoundException ex, Model model) {
        log.warn("Resource not found: {}", ex.getMessage());
        model.addAttribute("status", 404);
        model.addAttribute("title", "Resource Not Found");
        model.addAttribute("message", ex.getMessage());
        return "error/error";
    }

    @ExceptionHandler(DuplicateResourceException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public String handleDuplicateResourceException(DuplicateResourceException ex, Model model) {
        log.warn("Duplicate resource: {}", ex.getMessage());
        model.addAttribute("status", 409);
        model.addAttribute("title", "Duplicate Resource");
        model.addAttribute("message", ex.getMessage());
        return "error/error";
    }

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleBadRequestException(BadRequestException ex, Model model) {
        log.warn("Bad request: {}", ex.getMessage());
        model.addAttribute("status", 400);
        model.addAttribute("title", "Bad Request");
        model.addAttribute("message", ex.getMessage());
        return "error/error";
    }

    @ExceptionHandler(UnauthorizedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public String handleUnauthorizedException(UnauthorizedException ex, Model model) {
        log.warn("Unauthorized access: {}", ex.getMessage());
        model.addAttribute("status", 403);
        model.addAttribute("title", "Access Denied");
        model.addAttribute("message", ex.getMessage());
        return "error/error";
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public String handleAccessDeniedException(AccessDeniedException ex, Model model) {
        log.warn("Access denied: {}", ex.getMessage());
        model.addAttribute("status", 403);
        model.addAttribute("title", "Access Denied");
        model.addAttribute("message", "You do not have permission to access this resource.");
        return "error/error";
    }

    @ExceptionHandler(FileStorageException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleFileStorageException(FileStorageException ex, Model model) {
        log.error("File storage error: {}", ex.getMessage());
        model.addAttribute("status", 500);
        model.addAttribute("title", "File Upload Error");
        model.addAttribute("message", "There was an error processing your file. Please try again.");
        return "error/error";
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    @ResponseStatus(HttpStatus.PAYLOAD_TOO_LARGE)
    public String handleMaxUploadSizeExceededException(MaxUploadSizeExceededException ex, Model model) {
        log.warn("File upload size exceeded: {}", ex.getMessage());
        model.addAttribute("status", 413);
        model.addAttribute("title", "File Too Large");
        model.addAttribute("message", "The uploaded file exceeds the maximum allowed file size (10MB). Please upload a smaller file.");
        return "error/error";
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleValidationExceptions(MethodArgumentNotValidException ex, Model model) {
        String errors = ex.getBindingResult().getAllErrors().stream()
                .map(error -> {
                    if (error instanceof FieldError fieldError) {
                        return fieldError.getField() + ": " + fieldError.getDefaultMessage();
                    }
                    return error.getDefaultMessage();
                })
                .collect(Collectors.joining("<br/>"));

        log.warn("Validation failed: {}", errors);
        model.addAttribute("status", 400);
        model.addAttribute("title", "Validation Error");
        model.addAttribute("message", errors);
        return "error/error";
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNoHandlerFoundException(NoHandlerFoundException ex, Model model) {
        log.warn("Page not found: {}", ex.getRequestURL());
        model.addAttribute("status", 404);
        model.addAttribute("title", "Page Not Found");
        model.addAttribute("message", "The page you are looking for does not exist.");
        return "error/error";
    }

    @ExceptionHandler(NoResourceFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNoResourceFoundException(NoResourceFoundException ex, Model model) {
        log.warn("Static resource not found: {}", ex.getResourcePath());
        model.addAttribute("status", 404);
        model.addAttribute("title", "Resource Not Found");
        model.addAttribute("message", "The requested resource was not found.");
        return "error/error";
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleIllegalArgumentException(IllegalArgumentException ex, Model model) {
        log.warn("Illegal argument: {}", ex.getMessage());
        model.addAttribute("status", 400);
        model.addAttribute("title", "Invalid Request");
        model.addAttribute("message", ex.getMessage());
        return "error/error";
    }

    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleIllegalStateException(IllegalStateException ex, Model model) {
        log.error("Illegal state: {}", ex.getMessage());
        model.addAttribute("status", 500);
        model.addAttribute("title", "Application Error");
        model.addAttribute("message", "An unexpected application error occurred. Please try again.");
        return "error/error";
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleGenericException(Exception ex, Model model) {
        log.error("Unexpected error occurred: {}", ex.getMessage(), ex);
        model.addAttribute("status", 500);
        model.addAttribute("title", "Internal Server Error");
        model.addAttribute("message", "An unexpected error occurred. Please try again later.");
        return "error/error";
    }

}

