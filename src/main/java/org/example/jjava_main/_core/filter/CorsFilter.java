package org.example.jjava_main._core.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class CorsFilter implements Filter {
    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        String origin = request.getHeader("Origin");
        log.debug("Origin : " + origin);

        response.setHeader("Access-Control-Allow-Origin", "http://127.0.0.1:5500");
//        response.setHeader("Access-Control-Expose-Headers", "Authorization"); // 응답 해줄 수 있는 헤더
        response.setHeader("Access-Control-Allow-Methods", "POST, PUT, GET, DELETE, OPTIONS"); // OPTIONS : Preflight
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Headers", "Origin, X-Key, Content-Type, Accept, Authorization"); // 요청 받을 수 있는 헤더
        // X-~~~ : Customized Header
        response.setHeader("Access-Control-Allow-Credentials", "true"); // 쿠키 세션 값 허용

        // Preflight 요청을 허용하고 바로 응답하는 코드
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            chain.doFilter(req, res);
        }
    }
}
