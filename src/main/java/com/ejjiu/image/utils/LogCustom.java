package com.ejjiu.image.utils;

import org.slf4j.ILoggerFactory;
import org.slf4j.LoggerFactory;
import org.slf4j.impl.StaticLoggerBinder;

import java.util.Date;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.LoggerContextListener;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.rolling.RollingFileAppender;

/**
 *
 * 创建人  liangsong
 * 创建时间 2020/01/07 14:15
 */
public class LogCustom {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(LogCustom.class);
    private final LoggerContext context;

    public LogCustom() {
        ILoggerFactory loggerFactory = StaticLoggerBinder.getSingleton().getLoggerFactory();

        if (loggerFactory instanceof LoggerContext) {
            context = (LoggerContext) loggerFactory;
            context.addListener(new LoggerContextListener() {
                @Override
                public boolean isResetResistant() {
                    return false;
                }

                @Override
                public void onStart(LoggerContext loggerContext) {
                    printLog("onStart");

                }


                @Override
                public void onReset(LoggerContext loggerContext) {
                    printLog("onReset");
                    //                    switchAll();
                }

                @Override
                public void onStop(LoggerContext loggerContext) {
                    printLog("onStop");
                }

                @Override
                public void onLevelChange(Logger logger, Level level) {
                    printLog("onLevelChange" + level.levelStr);
                }
            });
        } else {
            context = null;
        }
    }

    public void detach() {
        Logger root = context.getLogger("ROOT");
        root.detachAndStopAllAppenders();
    }

    private void printLog(String start) {
        System.out.println("thread:" + Thread.currentThread().getName() + " date:" + new Date() + " log = " + start);
    }

    public void switchAll() {
        printLog("switchAll");
        switchStdout();
        switchFile();
    }

    public void switchStdout() {
        String logName = "STDOUT";
        Appender<ILoggingEvent> appender = getAppender(logName);
        if (appender == null) {
            return;
        }
        if (appender.isStarted()) {
            stopLog(logName);
        } else {
            startLog(logName);
        }
    }

    public void switchFile() {
        String logName = "FILE";
        Appender<ILoggingEvent> appender = getAppender(logName);
        if (appender == null) {
            return;
        }
        if (appender.isStarted()) {
            stopLog(logName);
        } else {
            startLog(logName);
        }
    }

    public boolean startLog(String logName) {

        Appender<ILoggingEvent> appender = getAppender(logName);
        if (appender == null) {
            return false;
        }
        if (appender instanceof RollingFileAppender) {
            ((RollingFileAppender<ILoggingEvent>) appender).getTriggeringPolicy().start();
        }
        appender.start();
        printLog(logName + " startLog success");
        return true;

    }

    public boolean stopLog(String logName) {

        Appender<ILoggingEvent> appender = getAppender(logName);
        if (appender == null) {
            return false;
        }
        appender.stop();
        printLog(logName + " stopLog success");
        return true;
    }

    private Appender<ILoggingEvent> getAppender(String logName) {
        if (context == null) {
            return null;
        }
        Logger root = context.getLogger("ROOT");
        return root.getAppender(logName);
    }
}
