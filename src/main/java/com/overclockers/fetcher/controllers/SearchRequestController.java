package com.overclockers.fetcher.controllers;

import com.overclockers.fetcher.SearchRequestConverter;
import com.overclockers.fetcher.entity.ApplicationUser;
import com.overclockers.fetcher.entity.SearchRequest;
import com.overclockers.fetcher.service.ApplicationUserService;
import com.overclockers.fetcher.service.SearchRequestService;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Controller
@Log4j2
public class SearchRequestController {

    private static final String REQUEST_VIEW = "request";
    private static final String REDIRECT = "redirect:/";

    @Autowired
    private SearchRequestConverter requestConverter;
    @Autowired
    private SearchRequestService searchRequestService;
    @Autowired
    private ApplicationUserService userService;

    @GetMapping(value = {"/"})
    public ModelAndView mainPage(ModelAndView modelAndView) {
        modelAndView.setViewName(REDIRECT + REQUEST_VIEW);

        return modelAndView;
    }

    @GetMapping(value = {"/request"})
    public ModelAndView addRequestForm(ModelAndView modelAndView) {

        ApplicationUser user = userService.findUserByEmail("despedo@gmail.com");
        List<SearchRequest> requests = searchRequestService.findSearchRequestByUserId(user.getUserId());

        modelAndView.addObject("searchRequests", requestConverter.convertSearchRequests(requests));
        modelAndView.addObject("emptyMessage", "No requests");
        modelAndView.setViewName(REQUEST_VIEW);

        return modelAndView;
    }

    @PostMapping(value = {"/request"})
    public ModelAndView addRequest(ModelAndView modelAndView, @RequestParam Map<String, String> requestParams) {

        String searchRequest = requestParams.get("searchRequest");

        if (StringUtils.isEmpty(searchRequest)) {
            log.info("Search request is empty");
        } else {
            ApplicationUser user = userService.findUserByEmail("despedo@gmail.com");

            SearchRequest request = SearchRequest.builder()
                    .request(searchRequest)
                    .createdDateTime(LocalDateTime.now())
                    .user(user)
                    .build();

            searchRequestService.saveSearchRequest(request);
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
