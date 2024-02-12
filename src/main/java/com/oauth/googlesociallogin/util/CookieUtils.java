package com.oauth.googlesociallogin.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.util.SerializationUtils;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Base64;
import java.util.Optional;

public class CookieUtils {

    public static void addCookie(HttpServletResponse response, String cookieName, String value, int maxAge) {
        if (response != null) {
            var cookie = new Cookie(cookieName, value);

            cookie.setPath("/");
            cookie.setHttpOnly(true);
            cookie.setMaxAge(maxAge);

            response.addCookie(cookie);
        }
    }

    public static Optional<Cookie> getCookie(HttpServletRequest request, String cookieName) {
        if (request != null && cookieName != null) {
            var cookies = request.getCookies();

            if (cookies != null && cookies.length > 0) {
                for (Cookie cookie : cookies) {
                    if (cookie.getName().equals(cookieName)) {
                        return Optional.of(cookie);
                    }
                }
            }
        }

        return Optional.empty();
    }

    public static void deleteCookie(HttpServletRequest request, HttpServletResponse response, String cookieName) {
        if (request != null && response != null && cookieName != null) {
            var cookies = request.getCookies();

            if (cookies != null && cookies.length > 0) {
                for (Cookie cookie : cookies) {
                    if (cookie.getName().equals(cookieName)) {
                        cookie.setValue("");
                        cookie.setPath("/");
                        cookie.setMaxAge(0);
                        response.addCookie(cookie);
                    }
                }
            }
        }
    }

    public static String serializeCookie(Object object) {
        return Base64.getUrlEncoder().encodeToString(SerializationUtils.serialize(object));
    }

    public static <T> T deserializeCookie(Cookie cookie, Class<T> cls) {
        if (cookie == null) throw new IllegalArgumentException();

        byte[] data = Base64.getUrlDecoder().decode(cookie.getValue());

        try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data))) {
            return cls.cast(ois.readObject());
        } catch (IOException ex) {
            throw new IllegalArgumentException("Failed to deserialize object", ex);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
