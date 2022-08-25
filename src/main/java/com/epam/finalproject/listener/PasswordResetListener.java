package com.epam.finalproject.listener;


import com.epam.finalproject.framework.beans.annotation.Component;
import com.epam.finalproject.framework.context.ApplicationListener;
import com.epam.finalproject.model.entity.PasswordResetToken;
import com.epam.finalproject.model.entity.User;
import com.epam.finalproject.model.event.OnPasswordResetEvent;
import com.epam.finalproject.service.PasswordResetTokenService;
import org.slf4j.Logger;

@Component
public class PasswordResetListener implements ApplicationListener<OnPasswordResetEvent> {

    private static final Logger log = org.slf4j.LoggerFactory.getLogger(PasswordResetListener.class);
    PasswordResetTokenService passwordResetTokenService;

    public PasswordResetListener(PasswordResetTokenService passwordResetTokenService) {
        this.passwordResetTokenService = passwordResetTokenService;
    }

    @Override
    public void onApplicationEvent(final OnPasswordResetEvent event) {
        this.confirmRegistration(event);
    }

    @Override
    public Class<OnPasswordResetEvent> listenClass() {
        return OnPasswordResetEvent.class;
    }

    private void confirmRegistration(final OnPasswordResetEvent event) {
        final User user = event.getUser();
        PasswordResetToken token = passwordResetTokenService.createTokenForUser(user);
        log.info("Create for user " + user.getUsername() + " passwordResetToken : " + token);
    }

}