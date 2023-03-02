package io.github.autumnforest.boot.utils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class DateUtil {
    public static final DateTimeFormatter yyyyMMddHHmmss = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


    public static Date str2Date(String str) {
        return Date.from(
                LocalDateTime
                        .parse(str, yyyyMMddHHmmss)
                        .atZone(ZoneId.systemDefault())
                        .toInstant()
        );
    }

    public static Date str2Date(String str, String format) {
        return Date.from(
                LocalDateTime
                        .parse(str, DateTimeFormatter.ofPattern(format))
                        .atZone(ZoneId.systemDefault())
                        .toInstant()
        );
    }

    public static Date str2Date(String str, DateTimeFormatter format) {
        return Date.from(
                LocalDateTime
                        .parse(str, format)
                        .atZone(ZoneId.systemDefault())
                        .toInstant()
        );
    }

    public static LocalDateTime date2LocalDateTime(Date date) {
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    }

    public static String date2Str(Date date) {
        return date2LocalDateTime(date).format(yyyyMMddHHmmss);
    }

    public static String date2Str(Date date, String format) {
        return date2LocalDateTime(date).format(DateTimeFormatter.ofPattern(format));
    }

    public static String date2Str(Date date, DateTimeFormatter format) {
        return date2LocalDateTime(date).format(format);
    }

}