package com.tp.opencourse.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Email;
import com.sendgrid.helpers.mail.objects.Personalization;
import com.tp.opencourse.dto.event.NotificationEvent;
import com.tp.opencourse.entity.User;
import com.tp.opencourse.exceptions.ResourceNotFoundExeption;
import com.tp.opencourse.repository.UserRepository;
import com.tp.opencourse.service.EmailService;
import io.github.cdimascio.dotenv.Dotenv;
import lombok.RequiredArgsConstructor;
import org.cloudinary.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class EmailServiceImpl implements EmailService {

    private String sendGridApiKey;
    private String templateConfirmPaymentId;

    public EmailServiceImpl() {
//        this.sendGridApiKey = dotenv.get("SENDGRID_API_KEY");
//        this.templateConfirmPaymentId = dotenv.get("SENDGRID_TEMPLATE_ID");
    }

    @Override
    public void sendNotification(String toEmail, NotificationEvent notificationEvent) {
//        User teacher = notificationEvent.getNotification().getTeacher();
//        Email from = new Email(teacher.getEmail());
        Email to = new Email(toEmail);
        Mail mail = new Mail();
//        mail.setFrom(from);
        mail.setTemplateId(templateConfirmPaymentId);
        Personalization personalization = new Personalization();
        personalization.addTo(to);
//        JsonNode content = notificationEvent.getNotification().getContent();
//
//        personalization.addDynamicTemplateData("courseUrl", content.get("courseUrl"));
//        personalization.addDynamicTemplateData("courseName",  content.get("courseName"));
//        personalization.addDynamicTemplateData("teacherName", teacher.getFullName());
//        personalization.addDynamicTemplateData("teacherAvt", teacher.getAvt());
//        personalization.addDynamicTemplateData("courseImg",  content.get("courseBanner"));
//        personalization.addDynamicTemplateData("createdAt", notificationEvent.getEventDate());

        mail.addPersonalization(personalization);
        SendGrid sg = new SendGrid(sendGridApiKey);
        Request request = new Request();

        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sg.api(request);

        } catch (IOException ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }
}
