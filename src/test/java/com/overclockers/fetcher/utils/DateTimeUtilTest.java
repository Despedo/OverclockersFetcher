package com.overclockers.fetcher.utils;

import org.junit.jupiter.api.Test;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DateTimeUtilTest {

    @Test
    void getCurrentTimeInUTCTest() {
        ZonedDateTime currentTime = DateTimeUtil.getCurrentTime();

        assertEquals(ZoneOffset.UTC, currentTime.getOffset());
    }
}