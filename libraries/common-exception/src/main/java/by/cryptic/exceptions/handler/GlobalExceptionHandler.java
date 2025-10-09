package by.cryptic.exceptions.handler;

import by.cryptic.exceptions.*;
import by.cryptic.utils.response.ErrorResponse;
import feign.FeignException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.MessagingException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.jwt.BadJwtException;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFoundException(EntityNotFoundException e, HttpServletRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(e.getMessage(),
                HttpStatus.NOT_FOUND.value(), request.getRequestURI());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<ErrorResponse> handleNullPointerException(NullPointerException e, HttpServletRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(e.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(), request.getRequestURI());
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(PaymentTooManyRequestException.class)
    public ResponseEntity<ErrorResponse> handlePaymentTooManyRequestException(NullPointerException e, HttpServletRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(e.getMessage(),
                HttpStatus.TOO_MANY_REQUESTS.value(), request.getRequestURI());
        return new ResponseEntity<>(errorResponse, HttpStatus.TOO_MANY_REQUESTS);
    }

    @ExceptionHandler(EmptyCartException.class)
    public ResponseEntity<ErrorResponse> handleEmptyCartException(EmptyCartException e, HttpServletRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(e.getMessage(),
                HttpStatus.BAD_REQUEST.value(), request.getRequestURI());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException e, HttpServletRequest request) {
        ErrorResponse errorResponse = new ErrorResponse("Illegal argument: " + e.getMessage(),
                HttpStatus.BAD_REQUEST.value(), request.getRequestURI());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(OutOfStockException.class)
    public ResponseEntity<ErrorResponse> handleOutOfStockException(OutOfStockException e, HttpServletRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(e.getMessage(),
                HttpStatus.CONFLICT.value(), request.getRequestURI());
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(CreatingException.class)
    public ResponseEntity<ErrorResponse> handleCreatingException(CreatingException e, HttpServletRequest request) {
        ErrorResponse errorResponse = new ErrorResponse("Creating failed after retrying attempts." +
                "Try again later: " + e.getMessage(), HttpStatus.SERVICE_UNAVAILABLE.value(), request.getRequestURI());
        return new ResponseEntity<>(errorResponse, HttpStatus.SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler(UpdatingException.class)
    public ResponseEntity<ErrorResponse> handleUpdatingException(UpdatingException e, HttpServletRequest request) {
        ErrorResponse errorResponse = new ErrorResponse("Updating failed after retrying attempts." +
                "Try again later: " + e.getMessage(), HttpStatus.SERVICE_UNAVAILABLE.value(), request.getRequestURI());
        return new ResponseEntity<>(errorResponse, HttpStatus.SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler(DeletingException.class)
    public ResponseEntity<ErrorResponse> handleDeletingException(DeletingException e, HttpServletRequest request) {
        ErrorResponse errorResponse = new ErrorResponse("Deleting failed after retrying attempts." +
                "Try again later: " + e.getMessage(), HttpStatus.SERVICE_UNAVAILABLE.value(), request.getRequestURI());
        return new ResponseEntity<>(errorResponse, HttpStatus.SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalStateException(IllegalStateException e, HttpServletRequest request) {
        ErrorResponse errorResponse = new ErrorResponse("Illegal state: " + e.getMessage(),
                HttpStatus.CONFLICT.value(), request.getRequestURI());
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(NotEnoughProductsException.class)
    public ResponseEntity<ErrorResponse> handleNotEnoughProductsException(NotEnoughProductsException e, HttpServletRequest request) {
        ErrorResponse errorResponse = new ErrorResponse("Not enough products: " + e.getMessage(),
                HttpStatus.CONFLICT.value(), request.getRequestURI());
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(
                err -> errors.put(err.getField(), err.getDefaultMessage()));
        errors.put("path", request.getRequestURI());
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(FeignException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(FeignException ex, HttpServletRequest request) {
        ErrorResponse errorResponse = new ErrorResponse("Error to get response from client: " + ex.getMessage(),
                ex.status(), request.getRequestURI());
        return new ResponseEntity<>(errorResponse, HttpStatusCode.valueOf(ex.status()));
    }

    @ExceptionHandler(MessagingException.class)
    public ResponseEntity<ErrorResponse> handleMessagingException(MessagingException ex, HttpServletRequest request) {
        ErrorResponse errorResponse = new ErrorResponse("Sending message failed: " + ex.getMessage(),
                HttpStatus.BAD_REQUEST.value(), request.getRequestURI());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationException(AuthenticationException ex, HttpServletRequest request) {
        ErrorResponse errorResponse = new ErrorResponse("Authentication failed: " + ex.getMessage(),
                HttpStatus.UNAUTHORIZED.value(), request.getRequestURI());
        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }


    @ExceptionHandler({
            JwtException.class,
            BadJwtException.class,
            InvalidBearerTokenException.class
    })
    public ResponseEntity<ErrorResponse> handleJwtException(JwtException ex, HttpServletRequest request) {
        ErrorResponse errorResponse = new ErrorResponse("Authentication failed," +
                " because your token is invalid: " + ex.getMessage(), HttpStatus.UNAUTHORIZED.value(),
                request.getRequestURI());
        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAuthorizationDeniedException(AuthorizationDeniedException ex, HttpServletRequest request) {
        ErrorResponse errorResponse = new ErrorResponse("Authorization denied: " + ex.getMessage(),
                HttpStatus.FORBIDDEN.value(), request.getRequestURI());
        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException ex, HttpServletRequest request) {
        ErrorResponse errorResponse = new ErrorResponse("Access denied: " + ex.getMessage(),
                HttpStatus.FORBIDDEN.value(), request.getRequestURI());
        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }
}
