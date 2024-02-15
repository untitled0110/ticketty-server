package com.ticketty.tickettyapp.controller;

import com.ticketty.tickettyapp.controller.request.UserLoginRequest;
import com.ticketty.tickettyapp.controller.request.UserSignupRequest;
import com.ticketty.tickettyapp.controller.response.Response;
import com.ticketty.tickettyapp.controller.response.UserLoginResponse;
import com.ticketty.tickettyapp.controller.response.UserSignupResponse;
import com.ticketty.tickettyapp.model.User;
import com.ticketty.tickettyapp.service.MailService;
import com.ticketty.tickettyapp.service.UserService;
import com.ticketty.tickettyapp.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final MailService mailService;
    private final UserService userService;
    private final JwtUtil jwtUtil;

    @PostMapping("/signup")
    public Response<UserSignupResponse> verifyMailAndSignup(@RequestBody UserSignupRequest request) {

        // 이메일 인증
        mailService.verifyMail(request.getEmail(), request.getPassword(), request.getCode());

        // 이메일 인증 성공 시 -> 회원가입
        User user = userService.signUpUser(request.getEmail(), request.getPassword());
        return Response.success(UserSignupResponse.fromUser(user));
    }

    @PostMapping("/login")
    public Response<UserLoginResponse> login(@RequestBody UserLoginRequest request) {
        UserLoginResponse userLoginResponse = userService.login(request.getEmail(), request.getPassword());
        return Response.success(userLoginResponse);
    }



}
