package com.overclockers.fetcher.controllers;

import com.overclockers.fetcher.utils.SearchRequestConverter;
import com.overclockers.fetcher.entity.ApplicationUser;
import com.overclockers.fetcher.authrization.ApplicationUserDetails;
import com.overclockers.fetcher.entity.SearchRequest;
import com.overclockers.fetcher.service.ApplicationUserService;
import com.overclockers.fetcher.service.SearchRequestService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static com.overclockers.fetcher.constants.ControllerConstants.*;

@Controller
@Log4j2
@RequiredArgsConstructor
public class SearchRequestController {

    @Value("${no.requests.message}")
    private String noRequestsMessage;

    @Value("${simplejavamail.smtp.password}")
    private String pass;


    @NonNull
    private SearchRequestConverter requestConverter;
    @NonNull
    private SearchRequestService searchRequestService;
    @NonNull
    private ApplicationUserService userService;

    @GetMapping(value = {"/"})
    public ModelAndView showMainPage(ModelAndView modelAndView) {
        modelAndView.setViewName(REDIRECT + REQUEST_VIEW);

        return modelAndView;
    }

    @GetMapping(value = {"/request"})
    public ModelAndView showRequests(ModelAndView modelAndView, Authentication authentication) {

        ApplicationUserDetails userDetails = (ApplicationUserDetails) authentication.getPrincipal();

        List<SearchRequest> requests = searchRequestService.findSearchRequestsByUserName(userDetails.getUsername());

        modelAndView.addObject(SEARCH_REQUEST_ATTRIBUTE, requestConverter.convertSearchRequests(requests));
        modelAndView.addObject(EMPTY_MESSAGE_ATTRIBUTE, noRequestsMessage);
        modelAndView.setViewName(REQUEST_VIEW);

        return modelAndView;
    }

    @PostMapping(value = {"/request"})
    public ModelAndView processRequest(ModelAndView modelAndView, @RequestParam Map<String, String> requestParams, @AuthenticationPrincipal Authentication authentication) {

        String searchRequest = requestParams.get(SEARCH_REQUEST_ATTRIBUTE);

        if (searchRequest == null || searchRequest.isEmpty()) {
            log.info("Search request is empty");
        } else {

            ApplicationUserDetails userDetails = (ApplicationUserDetails) authentication.getPrincipal();

            ApplicationUser loggedUser = userService.findUserByEmail(userDetails.getUsername());

            if (loggedUser != null && loggedUser.isEnabled()) {
                SearchRequest request = SearchRequest.builder()
                        .request(searchRequest)
                        .createdDateTime(LocalDateTime.now())
                        .user(loggedUser)
                        .build();

                searchRequestService.saveSearchRequest(request);
            }
        }

        modelAndView.setViewName(REDIRECT + REQUEST_VIEW);

        return modelAndView;
    }

    @RequestMapping(value = {"/request/remove/{id}"})
    public ModelAndView removeRequest(ModelAndView modelAndView, @PathVariable("id") Long id) {

        searchRequestService.deleteSearchRequest(id);

        modelAndView.setViewName(REDIRECT + REQUEST_VIEW);

        return modelAndView;
    }

}
