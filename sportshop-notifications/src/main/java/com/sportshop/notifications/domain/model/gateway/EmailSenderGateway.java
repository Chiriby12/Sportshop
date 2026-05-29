package com.sportshop.notifications.domain.model.gateway;


public interface EmailSenderGateway {


    void sendEmail(String to, String subject, String body);
}
