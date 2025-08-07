package org.example.jjava_main._core.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class RespFilterUtil {
    private static ObjectMapper mapper = new ObjectMapper();

    public static String fail(Integer status, String msg) {
        Resp<?> resp = new Resp(status, msg, null);
        try {
            return mapper.writeValueAsString(resp);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("json 변환 실패");
        }
    }
}
