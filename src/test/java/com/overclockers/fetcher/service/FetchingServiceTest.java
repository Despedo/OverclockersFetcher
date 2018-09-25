package com.overclockers.fetcher.service;

import com.overclockers.fetcher.configuration.AppConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = AppConfig.class)
public class FetchingServiceTest {

    @Autowired
    FetchingService fetchingService;

    @Test
    public void testPrintFirstPage() throws IOException {
        fetchingService.printFirstPage();
    }
}