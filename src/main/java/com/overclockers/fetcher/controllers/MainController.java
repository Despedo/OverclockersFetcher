package com.overclockers.fetcher.controllers;

import com.overclockers.fetcher.SearchRequestConverter;
import com.overclockers.fetcher.entity.ApplicationUser;
import com.overclockers.fetcher.entity.SearchRequest;
import com.overclockers.fetcher.service.ApplicationUserService;
import com.overclockers.fetcher.service.SearchRequestService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Controller
public class MainController {

    @Autowired
    private SearchRequestConverter requestConverter;

    @Autowired
    private SearchRequestService searchRequestService;

    @Autowired
    private ApplicationUserService userService;

    private String errorMessage;
    private String successMessage;


    @GetMapping(value = {"/request"})
    public String addRequestForm(Model model) {
        model.addAttribute("errorMessage", errorMessage);
        model.addAttribute("successMessage", successMessage);

        ApplicationUser user = userService.findUserByUserName("test");
        List<SearchRequest> requests = searchRequestService.findSearchRequestByUserId(user.getUserId());

        model.addAttribute("requestsList", requestConverter.convertSearchRequests(requests));

        errorMessage = null;
        successMessage = null;

        return "request";
    }

    @PostMapping(value = {"/request"})
    public String addRequest(@ModelAttribute("searchRequest") String searchRequest) {

        ApplicationUser user = userService.findUserByUserName("test");
        if (StringUtils.isEmpty(searchRequest)) {
            errorMessage = "Добавьте запрос для поиска!";
        } else {
            SearchRequest request = SearchRequest.builder()
                    .request(searchRequest)
                    .createdDateTime(LocalDateTime.now())
                    .user(user)
                    .build();

            searchRequestService.saveSearchRequest(request);
            successMessage = "Запрос создан";
        }

        return "redirect:/request";
    }

    @RequestMapping(value = {"/request/remove/{id}"})
    public String removeRequest(@PathVariable("id") Long id) {

        searchRequestService.deleteSearchRequest(id);

        return "redirect:/request";
    }

}
