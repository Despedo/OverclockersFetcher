package com.overclockers.fetcher.configuration;

import com.overclockers.fetcher.authrization.ApplicationUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.security.web.firewall.StrictHttpFirewall;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/register").permitAll()
                .antMatchers("/confirm").permitAll()
                .antMatchers("/test").permitAll()
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .loginPage("/login")
                .defaultSuccessUrl("/request", true)
                .failureUrl("/login?error=true")
                .permitAll()
                .and()
                .logout()
                .logoutSuccessUrl("/login")
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .deleteCookies("JSESSIONID");
    }

    @Override
    public void configure(WebSecurity web) {
        web
                .httpFirewall(allowUrlEncodedSlashHttpFirewall())
                .ignoring()
                .antMatchers("/css/**")
                .antMatchers("/images/**")
                .antMatchers("/js/**");
    }

    @Bean
    public HttpFirewall allowUrlEncodedSlashHttpFirewall() {
        StrictHttpFirewall firewall = new StrictHttpFirewall();
        firewall.setAllowUrlEncodedSlash(true);
        return firewall;
    }

    @Bean
    BCryptPasswordEncoder getEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth, ApplicationUserDetailsService detailsService) throws Exception {
        auth.userDetailsService(detailsService).passwordEncoder(getEncoder());
    }
}
