package org.example.eatopia.common.core.interceptor;

import static org.springframework.http.HttpMethod.OPTIONS;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.example.eatopia.common.core.consts.Const;
import org.example.eatopia.common.core.exception.GlobalException;
import org.example.eatopia.common.core.exception.errorcode.CommonErrorCode;
import org.springframework.stereotype.Component;
import org.springframework.util.PatternMatchUtils;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class LoginInterceptor implements HandlerInterceptor {

    private static final String[] WHITE_LIST = {"/", "/signup", "/login"};

    @Override
    public boolean preHandle(
        HttpServletRequest request,
        HttpServletResponse response,
        Object handler
    ) {

        // CORS 프리플라이트는 통과
        if (OPTIONS.matches(request.getMethod())) {
            return true;
        }

        String uri = request.getRequestURI();
        if (isWhiteList(uri)) {
            return true;
        }

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute(Const.LOGIN_USER) == null) {
            throw new GlobalException(CommonErrorCode.UNAUTHORIZED);
        }

        return true;
    }

    private boolean isWhiteList(String uri) {
        return PatternMatchUtils.simpleMatch(WHITE_LIST, uri);
    }
}
