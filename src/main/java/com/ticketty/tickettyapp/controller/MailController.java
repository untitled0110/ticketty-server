package com.ticketty.tickettyapp.controller;

import com.ticketty.tickettyapp.controller.request.MailCodeRequest;
import com.ticketty.tickettyapp.controller.response.MailCodeResponse;
import com.ticketty.tickettyapp.controller.response.Response;
import com.ticketty.tickettyapp.service.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class MailController {

    private final MailService mailService;

    @PostMapping("/email-codes")
    public Response<MailCodeResponse> sendMail(@RequestBody MailCodeRequest request, @RequestParam String action) {

        String emailAuthenticationCode = mailService.sendMail(request.getEmail(), action);
        return Response.success(new MailCodeResponse(emailAuthenticationCode));
    }
}
