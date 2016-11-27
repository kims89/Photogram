package com.photogram.Security;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * Her er WebSecurityConfig. Her settes policy for Web Security. Vi har satt slik at photoadmin kun kan nås med ROLE_ADMIN, det vil dermed si
 * at det kun er fotograf som kan nå /photoadmin-siden. Det er også gitt at oppretting av ny bruker (/newUser) og ressurs-side (/res) skal nås med uansett tilgang.
 * /403 feil har også fått en egen side, slik at det virker logisk for brukeren når han ser at han ikke har tilgang.
 */

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/photoadmin").access("hasRole('ROLE_ADMIN')")
                .antMatchers("/NewUser","/res/*").permitAll().anyRequest().authenticated()
                .and()
                .formLogin().loginPage("/login").permitAll()
                .and()
                .logout().permitAll()
                .and()
                .exceptionHandling().accessDeniedPage("/403")
                .and()
                .csrf();
    }

}