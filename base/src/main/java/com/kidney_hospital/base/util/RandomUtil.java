package com.kidney_hospital.base.util;

import java.util.Random;

/**
 * 取随机数工具类
 * Created by Vampire on 2017/6/13.
 */

public class RandomUtil {
    public static int randomNumber(int min,int max){
        Random random = new Random();
        int s = random.nextInt(max)%(max-min+1) + min;
        return s;
    }
}
