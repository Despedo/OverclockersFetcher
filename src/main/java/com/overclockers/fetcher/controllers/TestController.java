package com.overclockers.fetcher.controllers;

import com.overclockers.fetcher.mail.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

@Controller
@RequiredArgsConstructor
public class TestController {

    @NonNull
    private MailService mailService;

    @GetMapping(value = "/test")
    @ResponseStatus(value = HttpStatus.OK)
    public void showTestPage() {
        mailService.sendTestEmail();
    }
}
