package com.flashRide.ride_service.exceptions;

import com.flashRide.ride_service.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {
      @ExceptionHandler(RideIdNotFoundException.class)
       public ResponseEntity<ErrorResponse> handleRideNotFound(RideIdNotFoundException ex, HttpServletRequest request){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse
             (LocalDateTime.now(), 404, "RIDE_NOT_FOUND", ex.getMessage(), request.getRequestURI()));
    }

    @ExceptionHandler(RiderIdNotFoundException.class)
       public ResponseEntity<ErrorResponse>handleRiderNotFound(RiderIdNotFoundException ex, HttpServletRequest request){
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(new ErrorResponse(LocalDateTime.now(), 404,"RIDER_NOT_FOUND", ex.getMessage(), request.getRequestURI() ));
    }

    @ExceptionHandler(CancelRideException.class)
    public ResponseEntity<ErrorResponse>handleCancelRide(CancelRideException ex, HttpServletRequest request){
                 return ResponseEntity.status(HttpStatus.CONFLICT)
                  .body(new ErrorResponse(LocalDateTime.now(), 409, "CANNOT_CANCEL_RIDE _IN CURRENT STATE", ex.getMessage(), request.getRequestURI()));
    }

    // for validation

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse>handleValidationError(MethodArgumentNotValidException ex, HttpServletRequest request){
          String message = ex.getBindingResult().getFieldErrors().stream().map
                  (error -> error.getField() + " : " + error.getDefaultMessage()).collect(Collectors.joining());
          return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                  .body(new ErrorResponse(LocalDateTime.now(), 400, "INVALID_DETAILS", message, request.getRequestURI()));
    }

    // Generic Exception

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericEx(Exception ex, HttpServletRequest request){
               return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                  .body(new ErrorResponse(LocalDateTime.now(), 500, "INTERNAL_SERVER_ERROR", ex.getMessage(), request.getRequestURI()));
    }

//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<?> handleException(Exception ex) {
//
//        ex.printStackTrace();
//
//        return ResponseEntity
//                .status(HttpStatus.INTERNAL_SERVER_ERROR)
//                .body(ex.getMessage());
//    }
}
