package com.bookstory.store.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDateTime;
import java.util.Date;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ModelAndView handleGlobalException(Exception ex, WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails(LocalDateTime.now(), ex.getMessage(), request.getDescription(false));
        ModelAndView modelAndView = new ModelAndView("error");
        modelAndView.addObject("oops", errorDetails);
        log.error("Error occured {}", ex.getMessage(), ex);
        return modelAndView;
    }

    @ExceptionHandler(RuntimeException.class)
    public ModelAndView handleGlobalException(RuntimeException ex, WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails(LocalDateTime.now(), ex.getMessage(), request.getDescription(false));
        ModelAndView modelAndView = new ModelAndView("error");
        modelAndView.addObject("oops", errorDetails);
        log.error("Error occured {}", ex.getMessage(), ex);
        return modelAndView;
    }

}