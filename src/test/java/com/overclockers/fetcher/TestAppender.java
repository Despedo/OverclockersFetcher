package com.overclockers.fetcher;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.layout.PatternLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TestAppender extends AbstractAppender {
    private List<LogEvent> logEvents = new ArrayList<>();

    private TestAppender(String name) {
        super(name, null, PatternLayout.createDefaultLayout());
    }

    public static TestAppender createAppender(Class<?> clazz) {
        String appenderUuid = UUID.randomUUID().toString();
        TestAppender testAppender = new TestAppender(appenderUuid);
        testAppender.start();
        Logger logger = (Logger) LogManager.getLogger(clazz);
        logger.getContext().getConfiguration().addLoggerAppender(logger, testAppender);
        return testAppender;
    }

    @Override
    public void append(LogEvent logEvent) {
        logEvents.add(logEvent);
    }

    public List<LogEvent> getLogEvents() {
        return logEvents;
    }
}
