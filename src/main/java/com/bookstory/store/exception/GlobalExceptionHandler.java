package com.bookstory.store.exception;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDateTime;
import java.util.Date;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ModelAndView handleGlobalException(Exception ex, WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails(LocalDateTime.now(), ex.getMessage(), request.getDescription(false));
        ModelAndView modelAndView = new ModelAndView("error");
        modelAndView.addObject("oops", errorDetails);
        return modelAndView;
    }

    @ExceptionHandler(RuntimeException.class)
    public ModelAndView handleGlobalException(RuntimeException ex, WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails(LocalDateTime.now(), ex.getMessage(), request.getDescription(false));
        ModelAndView modelAndView = new ModelAndView("error");
        modelAndView.addObject("oops", errorDetails);
        return modelAndView;
    }

}