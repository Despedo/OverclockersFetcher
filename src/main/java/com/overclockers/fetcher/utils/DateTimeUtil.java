package com.overclockers.fetcher.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DateTimeUtil {
    public static ZonedDateTime getCurrentTime() {
        return ZonedDateTime.now(ZoneId.of("UTC"));
    }
}
