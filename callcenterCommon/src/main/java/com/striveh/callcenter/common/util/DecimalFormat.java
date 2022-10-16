package com.striveh.callcenter.common.util;

import java.text.NumberFormat;

public class DecimalFormat {
    //默认最多五位小数
    public static String decimalToPercent(Double num){
        if(num==null){
            throw new NullPointerException("num不能为空值");
        }
        NumberFormat percentInstance = java.text.DecimalFormat.getPercentInstance();
        percentInstance.setMaximumFractionDigits(4);
        return percentInstance.format(num);
    }


    public static void main(String[] args) {
        System.out.println(decimalToPercent(0.0025D));
        System.out.println(decimalToPercent(0.003D));
        System.out.println(decimalToPercent(0.05D));
        System.out.println(decimalToPercent(0.1D));
        System.out.println(decimalToPercent(0.12D));
        System.out.println(decimalToPercent(0.0008D));
        System.out.println(decimalToPercent(0.000013D));
        System.out.println(decimalToPercent(13D));
    }

}
