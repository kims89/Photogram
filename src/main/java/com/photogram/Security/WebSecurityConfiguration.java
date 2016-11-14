package com.photogram.Security;

import com.photogram.Photographer;
import com.photogram.Repository.PhotographerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@Configuration
public class WebSecurityConfiguration extends GlobalAuthenticationConfigurerAdapter {

    @Autowired
    PhotographerRepository photographerRepository;

    @Override
    public void init(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService());
    }

    @Bean
    UserDetailsService userDetailsService() {
        return new UserDetailsService() {

            //Her sjekkes det om brukeren eksisterer i mongoDB, og gis deretter tilgang.
            @Override
            public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
                Photographer photographer = photographerRepository.findByBrukernavn(username);
                if(photographer != null) {
                    return new User(photographer.getBrukernavn(), photographer.getPassord(), true, true, true, true,
                            AuthorityUtils.createAuthorityList("ADMIN"));
                } else {
                    throw new UsernameNotFoundException("could not find the user '"
                            + username + "'");
                }
            }
        };
    }
}