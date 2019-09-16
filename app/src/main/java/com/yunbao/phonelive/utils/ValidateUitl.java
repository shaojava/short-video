package com.yunbao.phonelive.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ValidateUitl {
    //判断手机号码的正则表达式
    private static final String MOBILE_NUM_REGEX = "^((13[0-9])|(15[^4,\\D])|(18[0-9])|(17[0-9]))\\d{8}$";


    /**
     * 验证一个号码是不是手机号
     *
     * @param mobileNumber
     */
    public static boolean validateMobileNumber(String mobileNumber) {
        Pattern p = Pattern.compile(MOBILE_NUM_REGEX);
        Matcher m = p.matcher(mobileNumber);
        return m.matches();
    }


}
