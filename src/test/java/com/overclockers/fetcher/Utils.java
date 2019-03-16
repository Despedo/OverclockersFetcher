package com.overclockers.fetcher;

import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;

public class Utils {

    private Utils() {
    }

    public static String buildUrlEncodedFormEntity(String... params) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < params.length; i += 2) {
            if (i > 0) {
                result.append('&');
            }
            try {
                result.
                        append(URLEncoder.encode(params[i], StandardCharsets.UTF_8.name())).
                        append('=').
                        append(URLEncoder.encode(params[i + 1], StandardCharsets.UTF_8.name()));
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        }
        return result.toString();
    }

    public static MockHttpSession getUserSession(MockMvc mockMvc, String username, String pass) throws Exception {
        MvcResult mvcResult = mockMvc.perform(formLogin().user(username).password(pass)).andReturn();
        return (MockHttpSession) mvcResult.getRequest().getSession();
    }
}
