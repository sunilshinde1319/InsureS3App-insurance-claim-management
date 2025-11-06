package com.insurance.notificationservice.controller;

import com.insurance.notificationservice.dto.EmailRequestDto;
import com.insurance.notificationservice.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private EmailService emailService;

    @PostMapping("/send")
    public ResponseEntity<String> sendNotification(@RequestBody EmailRequestDto emailRequest) {
        emailService.sendEmail(
                emailRequest.to(),
                emailRequest.subject(),
                emailRequest.body()
        );
        return ResponseEntity.ok("Notification request received and is being processed.");
    }
}