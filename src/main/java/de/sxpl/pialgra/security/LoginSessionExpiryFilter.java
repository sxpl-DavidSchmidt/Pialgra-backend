package de.sxpl.pialgra.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

public class LoginSessionExpiryFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain
    ) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session != null) {
            Long expiresAt = (Long) session.getAttribute(LoginSessionPolicy.EXPIRES_AT);
            boolean legacyLogin =
                    expiresAt == null &&
                    session.getAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY) != null;
            if (legacyLogin || (expiresAt != null && expiresAt <= System.currentTimeMillis())) {
                session.invalidate();
            } else if (expiresAt != null) {
                session.setMaxInactiveInterval((int) Math.max(1, (expiresAt - System.currentTimeMillis() + 999) / 1000));
            }
        }
        chain.doFilter(request, response);
    }
}
