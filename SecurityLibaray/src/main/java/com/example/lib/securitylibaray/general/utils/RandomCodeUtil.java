package com.example.lib.securitylibaray.general.utils;

import java.util.Random;
import java.util.UUID;

public class RandomCodeUtil {

    private static int codeLength=8;


    public static String getUUID() {
        String[] chars = new String[] { "a", "b", "c", "d", "e", "f",
                "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s",
                "t", "u", "v", "w", "x", "y", "z", "0", "1", "2", "3", "4", "5",
                "6", "7", "8", "9", "A", "B", "C", "D", "E", "F", "G", "H", "I",
                "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
                "W", "X", "Y", "Z" };
        //调用Java提供的生成随机字符串的对象：32位，十六进制，中间包含-
        String uuid= UUID.randomUUID().toString().replace("-", "");
        StringBuffer shortBuffer = new StringBuffer();
        for (int i = 0; i < 8; i++) { //分为8组
            String str = uuid.substring(i * 4, i * 4 + 4); //每组4位
            int x = Integer.parseInt(str, 16); //输出str在16进制下的表示
            shortBuffer.append(chars[x % 0x3E]); //用该16进制数取模62（十六进制表示为314（14即E）），结果作为索引取出字符
        }
        return shortBuffer.toString();//生成8位字符
    }

    public static String getCode(){
        return getCode(codeLength);
    }
    public static String getCode(int length) {
        char[] charArray = new char[length];
        short start = (short) '0';   //0的ASCII码是48
        short end = (short) 'z';    //z的ASCII码是122（0到z之间有特殊字符）
        for (int i = 0; i < length; i++) {
            while (true) {
                char cc1 = (char) ((Math.random() * (end - start)) + start);
                if (Character.isLetterOrDigit(cc1))  //判断字符是否是数字或者字母
                {
                    charArray[i] = cc1;
                    break;
                }
            }
        }
        String StringRes = new String(charArray);//把字符数组转化为字符串
        return StringRes;
    }

    public static String genRandomNum(){
        return genRandomNum(codeLength);
    }
    public static String genRandomNum(int length){
        int  maxNum = 36;
        int i;
        int count = 0;
        char[] str = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K',
                'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W',
                'X', 'Y', 'Z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };
        StringBuffer pwd = new StringBuffer("");
        Random r = new Random();
        while(count < length){
            i = Math.abs(r.nextInt(maxNum));
            if (i >= 0 && i < str.length) {
                pwd.append(str[i]);
                count ++;
            }
        }
        return pwd.toString();
    }

}
