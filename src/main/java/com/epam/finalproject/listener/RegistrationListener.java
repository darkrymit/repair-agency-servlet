package com.epam.finalproject.listener;


import com.epam.finalproject.framework.beans.annotation.Component;
import com.epam.finalproject.framework.context.ApplicationListener;
import com.epam.finalproject.model.entity.User;
import com.epam.finalproject.model.entity.VerificationToken;
import com.epam.finalproject.model.event.OnRegistrationCompleteEvent;
import com.epam.finalproject.service.EmailService;
import com.epam.finalproject.service.VerificationTokenService;
import org.slf4j.Logger;

@Component
public class RegistrationListener implements ApplicationListener<OnRegistrationCompleteEvent> {

    private static final Logger log = org.slf4j.LoggerFactory.getLogger(RegistrationListener.class);
    private final VerificationTokenService verificationTokenService;

    private final EmailService emailService;

    public RegistrationListener(VerificationTokenService verificationTokenService, EmailService emailService) {
        this.verificationTokenService = verificationTokenService;
        this.emailService = emailService;
    }

    @Override
    public void onApplicationEvent(final OnRegistrationCompleteEvent event) {
        this.confirmRegistration(event);
    }

    @Override
    public Class<OnRegistrationCompleteEvent> listenClass() {
        return OnRegistrationCompleteEvent.class;
    }

    private void confirmRegistration(final OnRegistrationCompleteEvent event) {
        final User user = event.getUser();
        VerificationToken token = verificationTokenService.createTokenForUser(user);
        log.info("Create for user {} verifyToken : {}", user.getUsername(), token);
        emailService.sendVerificationLetter(event, token);
    }

}
