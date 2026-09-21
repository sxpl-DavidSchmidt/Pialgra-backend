package de.sxpl.pialgra.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.session.autoconfigure.DefaultCookieSerializerCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SessionCookieConfig {

    @Bean
    public DefaultCookieSerializerCustomizer sessionCookieCustomizer(
            @Value("${app.session.cookie.secure:false}") boolean secure
    ) {
        return cookieSerializer -> {
            cookieSerializer.setCookieName("SESSION");
            cookieSerializer.setCookieMaxAge(30 * 24 * 60 * 60);
            cookieSerializer.setCookiePath("/");
            cookieSerializer.setUseHttpOnlyCookie(true);
            cookieSerializer.setSameSite("Lax");
            cookieSerializer.setUseSecureCookie(secure); // Set true once the API is served over HTTPS
        };
    }
}
