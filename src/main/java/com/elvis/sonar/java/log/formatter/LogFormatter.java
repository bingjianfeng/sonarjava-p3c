package com.elvis.sonar.java.log.formatter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * @author fengbingjian
 * @description 日志输出格式化
 * @since 2024/9/26 11:10
 **/
public class LogFormatter extends Formatter {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public String format(LogRecord record) {
        Date date = new Date(record.getMillis());
        String formattedDate = DATE_FORMAT.format(date);
        return String.format("[%s] %s\n", formattedDate, record.getMessage());
    }
}