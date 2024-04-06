package com.ticketty.tickettyapp.controller;

import com.ticketty.tickettyapp.controller.request.UserLoginRequest;
import com.ticketty.tickettyapp.controller.request.UserSignupRequest;
import com.ticketty.tickettyapp.controller.response.*;
import com.ticketty.tickettyapp.model.User;
import com.ticketty.tickettyapp.service.MailService;
import com.ticketty.tickettyapp.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final MailService mailService;
    private final UserService userService;

    @PostMapping("/signup")
    public Response<UserSignupResponse> verifyMailAndSignup(@RequestBody UserSignupRequest request) {

        // 이메일 인증
        mailService.verifyMail(request.getEmail(), request.getPassword(), request.getCode(), "signup");

        // 이메일 인증 성공 시 -> 회원가입
        User user = userService.signUpUser(request.getEmail(), request.getPassword());
        return Response.success(UserSignupResponse.fromUser(user));
    }

    @PostMapping("/login")
    public Response<UserLoginResponse> login(@RequestBody UserLoginRequest request) {
        UserLoginResponse userLoginResponse = userService.login(request.getEmail(), request.getPassword());
        return Response.success(userLoginResponse);
    }

    @PostMapping("/reissue")
    public Response<UserLoginResponse> reissueAccessToken(HttpServletRequest httpServletRequest) {
        UserLoginResponse userLoginResponse = userService.reissueAccessToken(httpServletRequest);
        return Response.success(userLoginResponse);
    }

    @PostMapping("/auth-test")
    public String authTest(HttpServletRequest httpServletRequest) {
        return (String) httpServletRequest.getAttribute("email");
    }

    @PostMapping("/logout")
    public Response<Void> logout(HttpServletRequest httpServletRequest) {
        userService.logout(httpServletRequest);
        return Response.success(null);
    }

    @PutMapping("/password")
    public Response<Void> changePassword(@RequestBody UserSignupRequest request) {

        // 이메일 인증
        mailService.verifyMail(request.getEmail(), request.getPassword(), request.getCode(), "password");

        // 이메일 인증 성공 시 -> 패스워드 변경
        userService.changePassword(request.getEmail(), request.getPassword());
        return Response.success(null);
    }

    @GetMapping("/my-info")
    public Response<MyPageResponse> getUserInfo(HttpServletRequest httpServletRequest) {

        Integer userId = (Integer) httpServletRequest.getAttribute("userId");

        User user = userService.getUserInfo(userId);
        return Response.success(MyPageResponse.fromUser(user));
    }
}
