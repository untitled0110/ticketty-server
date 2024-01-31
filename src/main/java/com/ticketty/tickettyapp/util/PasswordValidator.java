package com.ticketty.tickettyapp.util;

import org.springframework.stereotype.Service;

import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class PasswordValidator implements Predicate<String> {

    // 영문 1개이상, 숫자 1개이상, 특수문자 !@#$%^*+=- 중에 1개이상, 8~ 15자
    private static final String PASSWORD_REGEX = "^(?=.*[A-Za-z])(?=.*[0-9])(?=.*[!@#$%^*+=-]).{8,15}$";

    @Override
    public boolean test(String password) {
        Pattern pattern = Pattern.compile(PASSWORD_REGEX);
        Matcher matcher = pattern.matcher(password);

        return matcher.matches();
    }
}
