package com.codefororlando.orlandowalkingtours.log;

public interface Logger {
    void debug(String s);

    void debug(String format, Object... objects);

    void info(String s);

    void error(String message, Throwable throwable);
}
