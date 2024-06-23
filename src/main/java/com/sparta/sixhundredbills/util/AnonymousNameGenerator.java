package com.sparta.sixhundredbills.util;

import java.util.UUID;

/**
 * UUID 사용 -> 게시물 생성 시 익명 닉네임 생성
 * UUID는 중복될 가능성이 거의 없는 고유 식별자를 생성하는데 사용
 */
public class AnonymousNameGenerator {
    public static String generate() {
        return "익명" + UUID.randomUUID().toString().substring(0, 6);
        //UUID.randomUUID().toString() -> 이 부분은 고유한 UUID를 생성하고 문자열로 변환
        //substring(0, 6) -> UUID 문자열의 첫 6문자를 추출. 닉네임으로 사용하기 위해 일부만 사용
        //생성된 익명닉네임 : 익명123456
    }
}