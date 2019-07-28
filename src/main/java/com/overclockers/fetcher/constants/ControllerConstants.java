package com.overclockers.fetcher.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ControllerConstants {
    public static final String REGISTER_VIEW = "register";
    public static final String LOGIN_VIEW = "login";
    public static final String CONFIRM_VIEW = "confirm";
    public static final String REQUEST_VIEW = "request";
    public static final String ERROR_VIEW = "error";
    public static final String REDIRECT = "redirect:/";
    public static final String DATE_TIME_PATTERN = "dd.MM.yyyy HH:mm";

    public static final String LOGIN_ERROR_ATTRIBUTE = "loginError";
    public static final String CONFIRMATION_MESSAGE_ATTRIBUTE = "confirmationMessage";
    public static final String ERROR_MESSAGE_ATTRIBUTE = "errorMessage";
    public static final String SUCCESS_MESSAGE_ATTRIBUTE = "successMessage";
    public static final String SEARCH_REQUEST_ATTRIBUTE = "searchRequest";
    public static final String EMPTY_MESSAGE_ATTRIBUTE = "emptyMessage";
    public static final String USER_ATTRIBUTE = "user";
    public static final String CONFIRMATION_TOKEN_ATTRIBUTE = "confirmationToken";
}
