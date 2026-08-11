package de.sxpl.pialgra.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.session.autoconfigure.DefaultCookieSerializerCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Shapes the Spring Session cookie in code rather than via
 * {@code server.servlet.session.cookie.*}, so the flags that authentication depends on
 * cannot be lost by an environment that overrides {@code application.yml}.
 */
@Configuration
public class SessionCookieConfig {

    @Bean
    public DefaultCookieSerializerCustomizer sessionCookieCustomizer(
            @Value("${app.session.cookie.secure:false}") boolean secure
    ) {
        return cookieSerializer -> {
            cookieSerializer.setCookieName("SESSION");
            cookieSerializer.setCookiePath("/");
            cookieSerializer.setUseHttpOnlyCookie(true);
            cookieSerializer.setSameSite("Lax");
            cookieSerializer.setUseSecureCookie(secure); // Set true once the API is served over HTTPS
        };
    }
}
