package com.ticketty.tickettyapp.controller.email;

import com.ticketty.tickettyapp.dto.email.MailRequest;
import com.ticketty.tickettyapp.dto.email.MailResponse;
import com.ticketty.tickettyapp.service.email.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/email")
public class MailController {

    private final MailService mailService;
    @PostMapping("/verification-codes")
    public ResponseEntity<MailResponse> sendMail(@RequestBody MailRequest request) {
        MailResponse response = mailService.sendMail(request);

        if (response.isResult()) {
            return ResponseEntity.ok(response);
        } else {
            // 여기서 실패에 대한 응답을 처리하거나 다른 HttpStatus 코드를 사용할 수 있습니다.
            // ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            return ResponseEntity.badRequest().body(response);
        }
    }

}
