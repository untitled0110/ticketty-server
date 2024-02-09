package com.ticketty.tickettyapp.controller;

import com.ticketty.tickettyapp.controller.request.MailCodeRequest;
import com.ticketty.tickettyapp.controller.response.MailCodeResponse;
import com.ticketty.tickettyapp.service.MailService;
import com.ticketty.tickettyapp.util.EmailValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class MailController {

    private final MailService mailService;
    private final EmailValidator emailValidator;

    @PostMapping("/user/email-codes")
    public ResponseEntity<MailCodeResponse> sendMail(@RequestBody MailCodeRequest request) {

        if (!emailValidator.test(request.getEmail())) {
            MailCodeResponse validationErrorResponse = new MailCodeResponse("null", false, "EMAIL_VALIDATION");
            return ResponseEntity.ok(validationErrorResponse);
        }

        MailCodeResponse response = mailService.sendMail(request);

        return ResponseEntity.ok(response);
    }

}
