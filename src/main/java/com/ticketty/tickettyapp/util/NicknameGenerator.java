package com.ticketty.tickettyapp.util;

import java.util.Random;

public class NicknameGenerator {

    private static final String[] adjectives = {"싱그러운", "기분좋은", "신바람나는", "상쾌한", "짜릿한", "그리운", "자유로운", "낙천적인", "사랑스러운", "활기찬", "응원하는", "밝은", "유쾌한", "당당한", "배부른", "수줍은", "행복한", "멋있는", "즐거운", "심심한", "잘생긴", "이쁜", "웃음짓는", "열정적인", "긍정적인", "희망찬", "빛나는", "자신있는", "훌륭한"};
    private static final String[] animals = {"사자", "코끼리", "호랑이", "곰", "여우", "늑대", "너구리", "침팬치", "고릴라", "참새", "고슴도치", "강아지", "고양이", "거북이", "토끼", "앵무새", "하이에나", "돼지", "하마", "원숭이", "물소", "얼룩말", "치타", "악어", "기린", "수달", "염소", "다람쥐", "판다", "펭귄", "오리", "독수리", "하이에나", "뱀", "해마", "햄스터", "앵무새", "오소리", "수달", "백조", "연어", "날다람쥐"};

    public static String generateNickname() {
        Random random = new Random();

        String adjective = adjectives[random.nextInt(adjectives.length)];
        String animal = animals[random.nextInt(animals.length)];
        String randomString = getRandomString();

        return adjective + animal + randomString;
    }

    private static String getRandomString() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < 7; i++) {
            sb.append(characters.charAt(random.nextInt(characters.length())));
        }

        return sb.toString();
    }
}
