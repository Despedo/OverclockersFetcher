package com.overclockers.fetcher.controllers;

import lombok.extern.log4j.Log4j2;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

import static com.overclockers.fetcher.constants.ControllerConstants.ERROR_VIEW;

@Controller
@Log4j2
public class WhitelabelErrorController implements ErrorController {

    @RequestMapping("/error")
    public ModelAndView handleError(ModelAndView modelAndView, HttpServletRequest request) {
        modelAndView.setViewName(ERROR_VIEW);

        Object statusCodeObject = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        Object errorExceptionObject = request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);
        Integer statusCode = statusCodeObject != null ? Integer.valueOf(statusCodeObject.toString()) : null;
        String errorException = errorExceptionObject != null ? errorExceptionObject.toString() : null;
        log.error("Http request error, status code: '{}', error exception: '{}'", statusCode, errorException);

        return modelAndView;
    }

    @Override
    public String getErrorPath() {
        return "/error";
    }
}
