package com.v1.admin;

import de.codecentric.boot.admin.server.config.AdminServerProperties;
import de.codecentric.boot.admin.server.web.client.HttpHeadersProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
public class ServerConfig extends WebSecurityConfigurerAdapter {

    private final AdminServerProperties adminServerProperties;

    public ServerConfig(AdminServerProperties adminServerProperties) {
        this.adminServerProperties = adminServerProperties;
    }

    @Bean
    public HttpHeadersProvider customHttpHeadersProvider() {
        return  instance -> {
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add("authorization", "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbjRAbmF2ZXIuY29tIiwiZXhwIjozNTUwODk5NTc4fQ.8-tcRRoCjtRI9_RXSkhWJthwXJ_lngD46nNR-yIOJNE");
            return httpHeaders;
        };
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        String contextPath = this.adminServerProperties.getContextPath();

        SavedRequestAwareAuthenticationSuccessHandler successHandler = new SavedRequestAwareAuthenticationSuccessHandler();
        successHandler.setTargetUrlParameter("redirectTo");
        successHandler.setDefaultTargetUrl("/");

        http.authorizeRequests()
                .antMatchers(contextPath + "/assets/**").permitAll()
                        .antMatchers(contextPath + "/login").permitAll()
                        .antMatchers(HttpMethod.GET, "/actuator/**").permitAll()
                        .anyRequest().authenticated()
                        .and()
                                .formLogin().loginPage(contextPath + "/login").successHandler(successHandler)
                        .and()
                                .logout().logoutUrl(contextPath + "/logout")
                        .and()
                                .httpBasic()
                        .and()
                                .csrf()
                                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                                .ignoringAntMatchers(
                                        contextPath + "/instances",
                                        contextPath + "/actuator/**"
                                );
    }
}
