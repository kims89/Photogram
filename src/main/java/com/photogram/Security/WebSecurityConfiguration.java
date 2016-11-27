package com.photogram.Security;

import com.photogram.POJO.User;
import com.photogram.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * Her er WebSecurityConfiguration-klassen. Når en bruker authentiserer seg vil det gjøres en sjekk opp mot user-databasen om at brukeren finnes og hvordan
 * rolle som han skal ha.
 */

@Configuration
public class WebSecurityConfiguration extends GlobalAuthenticationConfigurerAdapter {

    @Autowired
    UserRepository userRepository;

    @Override
    public void init(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService());
    }

    @Bean
    UserDetailsService userDetailsService() {
        return new UserDetailsService() {

            @Override
            public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
                User user = userRepository.findByBrukernavn(username);
                if (user != null) {
                    return new org.springframework.security.core.userdetails.User(user.getBrukernavn(), user.getPassord(), true, true, true, true,
                            AuthorityUtils.createAuthorityList(user.getRolle()));
                } else {
                    throw new UsernameNotFoundException("Finner ikke bruker '"
                            + username + "'");
                }
            }
        };
    }
}