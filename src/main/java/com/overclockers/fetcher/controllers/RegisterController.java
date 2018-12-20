package com.overclockers.fetcher.controllers;

import com.nulabinc.zxcvbn.Strength;
import com.nulabinc.zxcvbn.Zxcvbn;
import com.overclockers.fetcher.entity.ApplicationUser;
import com.overclockers.fetcher.mail.MailService;
import com.overclockers.fetcher.service.ApplicationUserService;
import com.overclockers.fetcher.service.SearchRequestService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Controller
@Log4j2
public class RegisterController {

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    ApplicationUserService userService;
    @Autowired
    MailService mailService;

    @GetMapping(value = "/login")
    public ModelAndView showLoginPage(@RequestParam(required = false, value = "error", defaultValue = "false") boolean error) {
        ModelAndView modelAndView = new ModelAndView("login");
        modelAndView.addObject("loginError", error);
        return modelAndView;
    }

    @GetMapping(value = "/register")
    public ModelAndView showRegistrationPage(ApplicationUser user) {
        ModelAndView modelAndView = new ModelAndView("register");
        modelAndView.addObject("user", user);
        return modelAndView;
    }

    @PostMapping(value = "/register")
    public ModelAndView processRegistrationForm(@Valid ApplicationUser user, BindingResult bindingResult, HttpServletRequest request) {

        ModelAndView modelAndView = new ModelAndView("register");

        // Lookup user in database by e-mail
        ApplicationUser existingUser = userService.findUserByEmail(user.getEmail());

        if (existingUser != null) {
            log.info("User already exists: {}", existingUser);
            modelAndView.addObject("user", existingUser);
            modelAndView.addObject("errorMessage", "Oops!  There is already a user registered with the email provided.");
            modelAndView.setViewName("register");
            bindingResult.reject("email");
        }

        // Disable user until they click on confirmation link in email
        user.setEnabled(false);

        // Generate random 36-character string token for confirmation link
        user.setConfirmationToken(UUID.randomUUID().toString());

        user.setCreatedDateTime(LocalDateTime.now());

        userService.saveUser(user);

        String appUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();

        mailService.prepareAndSendRegistrationEmail(user, appUrl);

        modelAndView.addObject("user", user);
        modelAndView.addObject("confirmationMessage", "A confirmation e-mail has been sent to " + user.getEmail());
        modelAndView.setViewName("register");

        return modelAndView;
    }

    @GetMapping(value = "/confirm")
    public ModelAndView showConfirmationPage(@RequestParam("token") String token) {

        ModelAndView modelAndView = new ModelAndView("confirm");

        ApplicationUser user = userService.findUserByConfirmationToken(token);

        if (user == null) {
            log.info("invalid confirmation token: {}", token);
            modelAndView.addObject("invalidToken", "Oops!  This is an invalid confirmation link.");
        } else {
            modelAndView.addObject("confirmationToken", user.getConfirmationToken());
        }

        return modelAndView;
    }

    @PostMapping(value = "/confirm")
    public ModelAndView processConfirmationForm(BindingResult bindingResult, @RequestParam Map<String, String> requestParams, RedirectAttributes redirectAttr) {

        ModelAndView modelAndView = new ModelAndView("redirect:confirm?token=" + requestParams.get("token"));

        modelAndView.setViewName("confirm");

        Zxcvbn passwordCheck = new Zxcvbn();

        Strength strength = passwordCheck.measure(requestParams.get("password"));

        if (strength.getScore() < 3) {
            bindingResult.reject("password");

            redirectAttr.addFlashAttribute("errorMessage", "Your password is too weak.  Choose a stronger one.");
            return modelAndView;
        }

        // Find the user associated with the reset token
        ApplicationUser user = userService.findUserByConfirmationToken(requestParams.get("token"));

        // Set new password
        user.setPassword(bCryptPasswordEncoder.encode(requestParams.get("password")));

        // Set user to enabled
        user.setEnabled(true);

        // Save user
        userService.saveUser(user);

        modelAndView.addObject("successMessage", "Your password has been set!");
        return modelAndView;
    }

}
