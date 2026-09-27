package de.sxpl.pialgra.config;

import org.springframework.beans.factory.annotation.Value;
import de.sxpl.pialgra.security.LoginSessionPolicy;
import jakarta.servlet.http.HttpSession;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SessionCookieConfig {

    @Bean
    public CookieSerializer cookieSerializer(
            @Value("${app.session.cookie.secure:false}") boolean secure
    ) {
        DefaultCookieSerializer cookieSerializer = new DefaultCookieSerializer() {
            @Override
            public void writeCookieValue(CookieValue value) {
                if (!value.getCookieValue().isEmpty()) {
                    HttpSession session = value.getRequest().getSession(false);
                    if (session != null && Boolean.TRUE.equals(session.getAttribute(LoginSessionPolicy.REMEMBER_ME))) {
                        Long expiresAt = (Long) session.getAttribute(LoginSessionPolicy.EXPIRES_AT);
                        if (expiresAt != null) {
                            value.setCookieMaxAge((int) Math.max(0,
                                    (expiresAt - System.currentTimeMillis() + 999) / 1000));
                        }
                    }
                }
                super.writeCookieValue(value);
            }
        };

        cookieSerializer.setCookieName("SESSION");
        cookieSerializer.setCookieMaxAge(-1);
        cookieSerializer.setCookiePath("/");
        cookieSerializer.setUseHttpOnlyCookie(true);
        cookieSerializer.setSameSite("Lax");
        cookieSerializer.setUseSecureCookie(secure);
        return cookieSerializer;
    }
}
