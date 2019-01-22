package com.overclockers.fetcher.controllers;

import com.nulabinc.zxcvbn.Strength;
import com.nulabinc.zxcvbn.Zxcvbn;
import com.overclockers.fetcher.dto.UserDTO;
import com.overclockers.fetcher.entity.ApplicationUser;
import com.overclockers.fetcher.mail.MailService;
import com.overclockers.fetcher.service.ApplicationUserService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.UUID;

@Controller
@Log4j2
public class RegisterController {

    private static final String REGISTER_VIEW = "register";
    private static final String LOGIN_VIEW = "login";
    private static final String CONFIRM_VIEW = "confirm";
    private static final String REDIRECT = "redirect:/";
    private static final String DATE_TIME_PATTERN = "dd.MM.yyyy HH:mm";

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    private ApplicationUserService userService;
    @Autowired
    private MailService mailService;

    @GetMapping(value = "/login")
    public ModelAndView showLoginPage(ModelAndView modelAndView, @RequestParam(required = false, value = "error", defaultValue = "false") boolean error) {
        modelAndView.setViewName(LOGIN_VIEW);
        modelAndView.addObject("loginError", error);
        return modelAndView;
    }

    @GetMapping(value = "/register")
    public ModelAndView showRegistrationPage(ModelAndView modelAndView) {
        modelAndView.setViewName(REGISTER_VIEW);
        modelAndView.addObject("user", new UserDTO());
        return modelAndView;
    }

    @PostMapping(value = "/register")
    public ModelAndView processRegistrationForm(ModelAndView modelAndView, @Valid UserDTO user, BindingResult bindingResult, HttpServletRequest request) {

        // Lookup user in database by e-mail
        ApplicationUser existingUser = userService.findUserByEmail(user.getEmail());

        if (existingUser != null) {
            log.info("User already exists: {}", existingUser);
            modelAndView.setViewName(REGISTER_VIEW);
            modelAndView.addObject("user", existingUser);
            modelAndView.addObject("errorMessage", "Oops!  There is already a user registered with the email provided.");
            bindingResult.reject("email");
        } else {

            ApplicationUser applicationUser = ApplicationUser.builder()
                    .email(user.getEmail())
                    .firstName(user.getFirstName())
                    .lastName(user.getLastName())
                    .enabled(true)
                    .confirmationToken(UUID.randomUUID().toString())
                    .createdDateTime(LocalDateTime.now())
                    .build();

            userService.saveUser(applicationUser);

            String appUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
            mailService.prepareAndSendRegistrationEmail(applicationUser, appUrl);

            modelAndView.setViewName(REGISTER_VIEW);
            modelAndView.addObject("user", user);
            modelAndView.addObject("confirmationMessage", "A confirmation e-mail has been sent to " + user.getEmail());
        }

        return modelAndView;
    }

    @GetMapping(value = "/confirm")
    public ModelAndView showConfirmationPage(ModelAndView modelAndView, @RequestParam("token") String token) {

        modelAndView.setViewName(CONFIRM_VIEW);

        ApplicationUser user = userService.findUserByConfirmationToken(token);

        if (user == null) {
            log.info("Invalid confirmation token: {}", token);
            modelAndView.addObject("errorMessage", "Oops!  This is an invalid confirmation link.");
        } else if (user.isEnabled()) {
            log.info("User is already activated: {}", user.getEmail());
            String createdDateTime = user.getCreatedDateTime().format(DateTimeFormatter.ofPattern(DATE_TIME_PATTERN));
            modelAndView.addObject("errorMessage", "Oops!  User is already activated." + "\n" + createdDateTime);
        } else {
            modelAndView.addObject("confirmationToken", user.getConfirmationToken());
        }

        return modelAndView;
    }

    @PostMapping(value = "/confirm")
    public ModelAndView processConfirmationForm(ModelAndView modelAndView, BindingResult bindingResult, @RequestParam Map<String, String> requestParams, RedirectAttributes redirectAttr) {

        String password = requestParams.get("password");
        String token = requestParams.get("token");

        Zxcvbn passwordCheck = new Zxcvbn();

        Strength strength = passwordCheck.measure(password);

        if (strength.getScore() < 3) {
            bindingResult.reject("password");
            redirectAttr.addFlashAttribute("errorMessage", "Your password is too weak.  Choose a stronger one.");
            modelAndView.setViewName(REDIRECT + CONFIRM_VIEW + "?token=" + token);

            return modelAndView;
        }

        ApplicationUser user = userService.findUserByConfirmationToken(token);

        user.setPassword(bCryptPasswordEncoder.encode(password));
        user.setEnabled(true);

        userService.saveUser(user);

        modelAndView.setViewName(CONFIRM_VIEW);
        modelAndView.addObject("successMessage", "Your password has been set!");

        return modelAndView;
    }

}
