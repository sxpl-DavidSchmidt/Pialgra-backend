package de.sxpl.pialgra.security;

import jakarta.servlet.http.HttpSession;

public final class LoginSessionPolicy {
    public static final int DEFAULT_SECONDS = 16 * 60 * 60;
    public static final int REMEMBER_SECONDS = 30 * 24 * 60 * 60;
    public static final String REMEMBER_ME = "pialgra.rememberMe";
    public static final String RECORDED_AT = "pialgra.loginChoiceRecordedAt";
    public static final String EXPIRES_AT = "pialgra.loginExpiresAt";

    public static void initialize(HttpSession session, boolean rememberMe) {
        long now = System.currentTimeMillis();
        int lifetime = rememberMe ? REMEMBER_SECONDS : DEFAULT_SECONDS;
        session.setAttribute(REMEMBER_ME, rememberMe);
        session.setAttribute(RECORDED_AT, now);
        session.setAttribute(EXPIRES_AT, now + lifetime * 1000L);
        session.setMaxInactiveInterval(lifetime);
    }
}
