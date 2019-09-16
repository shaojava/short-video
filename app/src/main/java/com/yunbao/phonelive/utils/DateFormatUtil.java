package com.yunbao.phonelive.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by cxf on 2018/7/19.
 */

public class DateFormatUtil {

    private static SimpleDateFormat sFormat;

    static {
        sFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
    }


    public static String getCurTimeString() {
        return sFormat.format(new Date());
    }
}
