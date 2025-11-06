package com.insurance.userservice.controller;


import com.insurance.userservice.dto.ContactMessageDto;
import com.insurance.userservice.dto.FeedbackDto;
import com.insurance.userservice.entity.ContactMessage;
import com.insurance.userservice.entity.Feedback;
import com.insurance.userservice.repository.ContactMessageRepository;
import com.insurance.userservice.repository.FeedbackRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/public")
public class PublicController {

    @Autowired
    private ContactMessageRepository contactMessageRepository;
    @Autowired private FeedbackRepository feedbackRepository;

    @PostMapping("/contact")
    public ResponseEntity<String> submitContactForm(@Valid @RequestBody ContactMessageDto dto) {
        ContactMessage message = new ContactMessage();
        message.setName(dto.name());
        message.setEmail(dto.email());
        message.setSubject(dto.subject());
        message.setMessage(dto.message());
        contactMessageRepository.save(message);
        return ResponseEntity.ok("Message received. Thank you!");
    }

    @PostMapping("/feedback")
    public ResponseEntity<String> submitFeedback(@Valid @RequestBody FeedbackDto dto) {
        Feedback feedback = new Feedback();
        feedback.setRating(dto.rating());
        feedback.setTag(dto.tag());
        feedback.setText(dto.text());
        feedbackRepository.save(feedback);
        return ResponseEntity.ok("Feedback received. Thank you!");
    }
}
