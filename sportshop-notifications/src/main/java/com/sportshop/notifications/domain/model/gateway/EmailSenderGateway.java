package com.sportshop.notifications.domain.model.gateway;

/**
 * Puerto de salida para envío de emails.
 * Arquitectura Hexagonal: el dominio define el CONTRATO, no la implementación.
 * El caso de uso llama este puerto sin saber si es Gmail, AWS SES u otro.
 */
public interface EmailSenderGateway {

    /**
     * Envía un email de notificación.
     *
     * @param to      Destinatario
     * @param subject Asunto del correo
     * @param body    Cuerpo del correo en HTML
     */
    void sendEmail(String to, String subject, String body);
}
