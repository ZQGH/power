package com.example.demo.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author ：chendaolong
 * @Description ：数字格式化工具类
 * @date ：2019/4/23
 */
public class NumFormatUtil_数字格式化 {
    /**
    *@author:chendaolong
    *@description: 一万以下的数字正常显示(如有小数点保留两位小数)
     *   万以上  亿以下的  以万为单位  小数点后保留两位小数
     *   亿以上的  以亿为单位  小食店后保存两位小数
    *@date:2019/4/23
    */

    public static  String formatNum(String num) {
        StringBuffer sb = new StringBuffer();
        if (!StringIsNumber(num))
            //不是数字
            return "0";
//        BigDecimal b0 = new BigDecimal("10000");
        BigDecimal b1 = new BigDecimal("10000");
        BigDecimal b2 = new BigDecimal("100000000");
        BigDecimal b3 = new BigDecimal(num);

        String formatNumStr = "";
        String nuit = "";

        // 1万以内处理处理
        if (b1.compareTo(b3) == 1 ) {
            if(num.contains(".")){
                return b3.setScale(2, RoundingMode.HALF_UP).toString();
            }
            return num;
        }


        // 以万为单位处理
        if ((b3.compareTo(b1) == 0 && b3.compareTo(b1) == 1)
                || b3.compareTo(b2) == -1) {
            formatNumStr = b3.divide(b1).toString();
            nuit = "万";
        } else if (b3.compareTo(b2) == 0 || b3.compareTo(b2) == 1) {
            formatNumStr = b3.divide(b2).toString();
            nuit = "亿";
        }
        if (!"".equals(formatNumStr)) {
            int i = formatNumStr.indexOf(".");
            if (i == -1) {
                System.out.println(formatNumStr);
                //无小数点  补.00 加单位
                sb.append(formatNumStr).append(".00").append(nuit);
            } else {
                System.out.println(formatNumStr);
                //小数点后一位补 0
                formatNumStr=formatNumStr+"00";
                sb.append(formatNumStr.substring(0, i + 3)).append(nuit);
            }
        }


        if (sb.length() == 0)
            return "0";
        return sb.toString();

    }

    public static boolean StringIsNumber(String num){
        Pattern pattern = Pattern.compile("-?[0-9]*.?[0-9]*");
        Matcher matcher = pattern.matcher((CharSequence) num);
        return matcher.matches();

    }

}
