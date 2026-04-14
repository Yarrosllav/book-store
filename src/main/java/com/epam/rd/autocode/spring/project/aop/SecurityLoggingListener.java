package com.epam.rd.autocode.spring.project.aop;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SecurityLoggingListener {

    @EventListener
    public void onSuccess(AuthenticationSuccessEvent event){
        String username = event.getAuthentication().getName();
        log.info("SECURITY EVENT: User '{}' seccessfully logged in", username);
    }

    @EventListener
    public void onFailure(AbstractAuthenticationFailureEvent event){
        String username = event.getAuthentication().getName();
        String errorMessage = event.getException().getMessage();
        log.warn("SECURITY ALERT: User '{}' failed to log in. Reason: {}", username, errorMessage);
    }

}
