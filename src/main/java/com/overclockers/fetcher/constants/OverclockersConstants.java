package com.overclockers.fetcher.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OverclockersConstants {
    public static final String HOST_URL = "http://forum.overclockers.ua/";
    public static final String FIRST_PAGE_SELLING_PATH = "viewforum.php?f=26";
    public static final String TOPIC_PATH = "viewtopic.php?f=26&t=";
    public static final String ELEMENT_CLASS_KEY = "class";
    public static final String ELEMENT_TOPIC_VALUE_REGEXP = "row bg[1,2]$|row bg[1,2]( sticky){1}$";
    public static final String ELEMENT_NOTICE_VALUE = "notice";
    public static final String ELEMENT_AUTHOR_VALUE = "author";
    public static final String ELEMENT_TOPIC_TITLE_VALUE = "topictitle";
}
