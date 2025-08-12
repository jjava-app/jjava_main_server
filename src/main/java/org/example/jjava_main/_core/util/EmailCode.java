package org.example.jjava_main._core.util;

import java.security.SecureRandom;

/**
 * 앱 자체 로그인에서 사용하는 이메일 code 발행 메서드
 */
public class EmailCode {
    private static final SecureRandom random = new SecureRandom();
    private static final int CODE_LENGTH = 6;

    /**
     * 6자리 숫자 인증번호 생성 (ex. "583241")
     */
    public static String generateAuthCode() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < CODE_LENGTH; i++) {
            sb.append(random.nextInt(10)); // 0~9
        }
        return sb.toString();
    }
}
