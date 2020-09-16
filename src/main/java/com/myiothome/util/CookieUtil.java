package com.myiothome.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpRequest;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

public class CookieUtil {
    public static String getCookies(HttpServletRequest request, String target) {
        if (request == null || StringUtils.isBlank(target)) {
            throw new IllegalArgumentException("cookie参数为空");
        }

        Cookie[] cookies = request.getCookies();

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(target)) {
                    return cookie.getValue();
                }
            }
        }

        return null;

    }
}
