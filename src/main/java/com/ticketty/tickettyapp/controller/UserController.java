package com.ticketty.tickettyapp.controller;

import com.ticketty.tickettyapp.controller.request.*;
import com.ticketty.tickettyapp.controller.response.*;
import com.ticketty.tickettyapp.exception.ErrorCode;
import com.ticketty.tickettyapp.exception.TickettyAppApplicationException;
import com.ticketty.tickettyapp.model.User;
import com.ticketty.tickettyapp.service.MailService;
import com.ticketty.tickettyapp.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Objects;

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

    @PutMapping("/nickname")
    public Response<Void> changeNickname(HttpServletRequest httpServletRequest, @Valid @RequestBody ChangeNicknameRequest request, Errors errors) {
        Integer userId = (Integer) httpServletRequest.getAttribute("userId");
        String newNickname = request.getNewNickname();

        if (errors.hasErrors()) {
            String errorMessage = Objects.requireNonNull(errors.getFieldError("newNickname")).getDefaultMessage();
            throw new TickettyAppApplicationException(ErrorCode.NICKNAME_VALIDATION, (errorMessage));
        }

        userService.changeNickname(userId, newNickname);
        return Response.success(null);
    }

    @PutMapping("/phone")
    public Response<Void> changePhone(HttpServletRequest httpServletRequest, @Valid @RequestBody ChangePhoneRequest request, Errors errors) {
        Integer userId = (Integer) httpServletRequest.getAttribute("userId");
        String phoneNumber= request.getPhone();

        if (errors.hasErrors()) {
            String errorMessage = Objects.requireNonNull(errors.getFieldError("phone")).getDefaultMessage();
            throw new TickettyAppApplicationException(ErrorCode.PHONE_VALIDATION, (errorMessage));
        }

        userService.changePhone(userId, phoneNumber);
        return Response.success(null);
    }

    @PutMapping("/account")
    public Response<Void> changeAccount(HttpServletRequest httpServletRequest, @Valid @RequestBody ChangeAccountRequest request, Errors errors) {
        Integer userId = (Integer) httpServletRequest.getAttribute("userId");
        String bankName= request.getBankName();
        String accountNumber= request.getAccountNumber();
        String accountHolder= request.getAccountHolder();

        if (errors.hasErrors()) {
            throw new TickettyAppApplicationException(ErrorCode.ACCOUNT_VALIDATION);
        }

        userService.changeAccount(userId, accountNumber, bankName, accountHolder);
        return Response.success(null);
    }

}
