package com.overclockers.fetcher.utils;

import java.time.ZoneId;
import java.time.ZonedDateTime;

public class DateTimeUtil {

    private DateTimeUtil() {
    }

    public static ZonedDateTime getCurrentTime() {
        return ZonedDateTime.now(ZoneId.of("UTC"));
    }
}
