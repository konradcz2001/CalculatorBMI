package com.github.konradcz2001.bmimpact;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import java.util.Locale;

/**
 * Configuration class for Web MVC settings.
 * Sets up Internationalization (i18n) support.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * Resolves the current locale based on the user's session.
     * Defaults to English if no session locale is found.
     *
     * @return the configured SessionLocaleResolver
     */
    @Bean
    public LocaleResolver localeResolver() {
        SessionLocaleResolver slr = new SessionLocaleResolver();
        slr.setDefaultLocale(Locale.ENGLISH);
        return slr;
    }

    /**
     * Interceptor that detects a change in the user's locale based on a request parameter.
     * Example: /?lang=pl will switch the locale to Polish.
     *
     * @return the configured LocaleChangeInterceptor
     */
    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        LocaleChangeInterceptor lci = new LocaleChangeInterceptor();
        lci.setParamName("lang");
        return lci;
    }

    /**
     * Registers the LocaleChangeInterceptor in the application's interceptor registry.
     *
     * @param registry the InterceptorRegistry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(localeChangeInterceptor());
    }
}