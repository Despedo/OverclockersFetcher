package com.overclockers.fetcher.controllers;

import com.nulabinc.zxcvbn.Strength;
import com.nulabinc.zxcvbn.Zxcvbn;
import com.overclockers.fetcher.dto.UserDTO;
import com.overclockers.fetcher.entity.ApplicationUser;
import com.overclockers.fetcher.mail.MailService;
import com.overclockers.fetcher.service.ApplicationUserService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.UUID;

import static com.overclockers.fetcher.constants.ControllerConstants.*;
import static com.overclockers.fetcher.utils.DateTimeUtil.getCurrentTime;

@Controller
@Log4j2
@RequiredArgsConstructor
public class RegisterController {

    @Value("${user.registered.message}")
    private String userRegisteredMessage;
    @Value("${email.sent.message}")
    private String emailSentMessage;
    @Value("${invalid.confirmation.link.message}")
    private String invalidConfirmationLinkMessage;
    @Value("${activated.user.message}")
    private String activatedUserMessage;
    @Value("${weak.pass.message}")
    private String weakPassMessage;
    @Value("${pass.set.message}")
    private String passSetMessage;

    @NonNull
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @NonNull
    private ApplicationUserService userService;
    @NonNull
    private MailService mailService;

    @GetMapping(value = "/login")
    public ModelAndView showLoginPage(ModelAndView modelAndView, @RequestParam(required = false, value = "error", defaultValue = "false") boolean error) {
        modelAndView.setViewName(LOGIN_VIEW);
        modelAndView.addObject(LOGIN_ERROR_ATTRIBUTE, error);
        return modelAndView;
    }

    @GetMapping(value = "/register")
    public ModelAndView showRegistrationPage(ModelAndView modelAndView) {
        modelAndView.setViewName(REGISTER_VIEW);
        modelAndView.addObject(USER_ATTRIBUTE, new UserDTO());
        return modelAndView;
    }

    @PostMapping(value = "/register")
    public ModelAndView processRegistrationForm(ModelAndView modelAndView, @Valid @ModelAttribute("user") UserDTO user, BindingResult bindingResult, HttpServletRequest request) {
        modelAndView.setViewName(REGISTER_VIEW);

        if (bindingResult.hasFieldErrors()) {
            log.info("User validation error: {}", user.getEmail());
            modelAndView.addObject(USER_ATTRIBUTE, user);
        } else {
            // Lookup user in database by e-mail
            ApplicationUser existingUser = userService.findUserByEmail(user.getEmail());
            if (existingUser != null) {
                log.info("User already exists: {}", existingUser.getEmail());
                modelAndView.addObject(USER_ATTRIBUTE, user);
                modelAndView.addObject(ERROR_MESSAGE_ATTRIBUTE, userRegisteredMessage);
                bindingResult.reject("email");
            } else {
                processRegistration(user, request);
                modelAndView.addObject(USER_ATTRIBUTE, user);
                modelAndView.addObject(CONFIRMATION_MESSAGE_ATTRIBUTE, emailSentMessage + user.getEmail());
            }
        }

        return modelAndView;
    }

    private void processRegistration(UserDTO user, HttpServletRequest request) {
        ApplicationUser applicationUser = ApplicationUser.builder()
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .enabled(false)
                .confirmationToken(UUID.randomUUID().toString())
                .createdDateTime(getCurrentTime())
                .build();

        userService.saveUser(applicationUser);

        String appUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
        mailService.processRegistrationEmail(applicationUser, appUrl);
    }

    @GetMapping(value = "/confirm")
    public ModelAndView showConfirmationPage(ModelAndView modelAndView, @RequestParam("token") String token) {
        modelAndView.setViewName(CONFIRM_VIEW);

        ApplicationUser user = userService.findUserByConfirmationToken(token);
        if (user == null) {
            log.info("Invalid confirmation token: {}", token);
            modelAndView.addObject(ERROR_MESSAGE_ATTRIBUTE, invalidConfirmationLinkMessage);
        } else if (user.isEnabled()) {
            log.info("User is already activated: {}", user.getEmail());
            String createdDateTime = user.getCreatedDateTime().format(DateTimeFormatter.ofPattern(DATE_TIME_PATTERN));
            modelAndView.addObject(ERROR_MESSAGE_ATTRIBUTE, activatedUserMessage + "\n" + createdDateTime);
        } else {
            modelAndView.addObject(CONFIRMATION_TOKEN_ATTRIBUTE, user.getConfirmationToken());
        }

        return modelAndView;
    }

    @PostMapping(value = "/confirm")
    public ModelAndView processConfirmationForm(ModelAndView modelAndView, @RequestParam Map<String, String> requestParams) {

        String password = requestParams.get("password");
        String token = requestParams.get("token");

        ApplicationUser user = userService.findUserByConfirmationToken(token);

        if (user == null) {
            log.info("Invalid confirmation token: {}", token);
            modelAndView.addObject(ERROR_MESSAGE_ATTRIBUTE, invalidConfirmationLinkMessage);
        } else if (user.isEnabled()) {
            log.info("User is already activated: {}", user.getEmail());
            String createdDateTime = user.getCreatedDateTime().format(DateTimeFormatter.ofPattern(DATE_TIME_PATTERN));
            modelAndView.addObject(ERROR_MESSAGE_ATTRIBUTE, activatedUserMessage + "\n" + createdDateTime);
        } else {

            if (isPasswordWeak(password)) {
                modelAndView.addObject(ERROR_MESSAGE_ATTRIBUTE, weakPassMessage);
                modelAndView.setViewName(REDIRECT + CONFIRM_VIEW + "?token=" + token);
                return modelAndView;
            }

            user.setPassword(bCryptPasswordEncoder.encode(password));
            user.setEnabled(true);
            userService.saveUser(user);

            modelAndView.setViewName(CONFIRM_VIEW);
            modelAndView.addObject(SUCCESS_MESSAGE_ATTRIBUTE, passSetMessage);
        }

        return modelAndView;
    }

    private boolean isPasswordWeak(String password) {
        Zxcvbn passwordCheck = new Zxcvbn();
        Strength strength = passwordCheck.measure(password);
        return strength.getScore() < 3;
    }
}
