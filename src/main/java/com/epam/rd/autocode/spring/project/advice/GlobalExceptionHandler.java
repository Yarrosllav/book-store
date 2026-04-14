package com.epam.rd.autocode.spring.project.advice;

import com.epam.rd.autocode.spring.project.exception.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Locale;

@ControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class GlobalExceptionHandler {

    private final MessageSource messageSource;

    @ExceptionHandler({ClientNotFoundException.class, BookNotFoundException.class,
            EmployeeNotFoundException.class, OrderNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNotFoundException(RuntimeException ex, Model model) {
        String translatedMsg = translate(ex.getMessage());
        log.warn("Resource not found: {}", translatedMsg);
        return buildErrorPage(model, translatedMsg, 404);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNoHandlerFoundException(NoHandlerFoundException ex, Model model) {
        log.warn("Page not found: {}", ex.getRequestURL());
        return buildErrorPage(model, translate("error.exception.page_not_found"), 404);
    }

    @ExceptionHandler(BookAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public String handleAlreadyExistException(BookAlreadyExistsException ex, Model model) {
        String translatedMsg = complexTranslate(ex.getMessage(), ex.getArgs());
        log.warn("Conflict books: {}", translatedMsg);
        return buildErrorPage(model, translatedMsg, 409);
    }

    @ExceptionHandler(ClientAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public String handleAlreadyExistException(ClientAlreadyExistsException ex, Model model) {
        String translatedMsg = translate(ex.getMessage());
        log.warn("Conflict clients: {}", translatedMsg);
        return buildErrorPage(model, translatedMsg, 409);
    }

    @ExceptionHandler(InsufficientFundsException.class)
    public String handleInsufficientFundsException(
            InsufficientFundsException ex,
            RedirectAttributes redirectAttributes,
            HttpServletRequest request) {

        String translatedMsg = translate(ex.getMessage());
        log.warn("Insufficient funds: {}", translatedMsg);

        redirectAttributes.addFlashAttribute("errorMessage", translatedMsg);
        return "redirect:" + getSafeReferer(request);
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleAccessDeniedException(AccessDeniedException ex, Model model) {
        log.warn("Access Denied: {}", ex.getMessage());

        return buildErrorPage(model, translate("error.exception.page_not_found"), 404);
    }

    @ExceptionHandler(OrderProcessingException.class)
    public String handleOrderProcessingException(
            OrderProcessingException ex,
            RedirectAttributes redirectAttributes,
            HttpServletRequest request) {

        String translatedMsg = translate(ex.getMessage());
        log.warn("Order processing failed: {}", translatedMsg);

        redirectAttributes.addFlashAttribute("errorMessage", translatedMsg);

        return "redirect:" + getSafeReferer(request);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleGeneralException(Exception ex, Model model) {
        log.error("Internal Server Error: ", ex);
        return buildErrorPage(model, translate("error.exception.internal_server"), 500);
    }

    private String buildErrorPage(Model model, String translatedMessage, int statusCode) {
        model.addAttribute("errorCode", statusCode);
        model.addAttribute("errorMessage", translatedMessage);
        return "error/error-page";
    }

    private String translate(String messageKey) {
        Locale locale = LocaleContextHolder.getLocale();
        return messageSource.getMessage(messageKey, null, messageKey, locale);
    }

    private String complexTranslate(String messageKey, Object[] args) {
        Locale locale = LocaleContextHolder.getLocale();
        return messageSource.getMessage(messageKey, args, messageKey, locale);
    }

    private String getSafeReferer(HttpServletRequest request) {
        String referer = request.getHeader("Referer");

        if (referer != null) {
            String serverUrl = request.getScheme() + "://" + request.getServerName() +
                    (request.getServerPort() == 80 || request.getServerPort() == 443 ? "" : ":" + request.getServerPort());

            if (referer.startsWith(serverUrl)) {
                return referer.replace(serverUrl, "");
            }
        }
        return "/basket";
    }
}
