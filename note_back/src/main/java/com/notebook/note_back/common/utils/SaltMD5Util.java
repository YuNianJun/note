package com.notebook.note_back.common.utils;

import org.apache.commons.codec.binary.Hex;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;


public class SaltMD5Util {
    private static final SecureRandom random = new SecureRandom();
    public static String MD5(String input) {
        MessageDigest md5 = null;
        try {
            // 生成普通的MD5密码
            md5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            return "check jdk";
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
        char[] charArray = input.toCharArray();
        byte[] byteArray = new byte[charArray.length];
        for (int i = 0; i < charArray.length; i++)
            byteArray[i] = (byte) charArray[i];
        byte[] md5Bytes = md5.digest(byteArray);
        StringBuffer hexValue = new StringBuffer();
        for (int i = 0; i < md5Bytes.length; i++) {
            int val = ((int) md5Bytes[i]) & 0xff;
            if (val < 16) hexValue.append("0");
            hexValue.append(Integer.toHexString(val));
        }
        return hexValue.toString();
    }


    public static String generateSaltPassword(String password) {
        Random random = new Random();
        //生成一个16位的随机数，盐
        byte[] saltBytes = new byte[8];
        random.nextBytes(saltBytes);
        String salt = Hex.encodeHexString(saltBytes);
        //将盐加到明文中，并生成新的MD5码
        String saltedPassword = md5Hex(password + salt);
        //将盐混到新生成的MD5码中，方便的校验明文和秘文
        char[] cs = new char[48];
        for (int i = 0; i < 48; i += 3) {
            cs[i] = saltedPassword.charAt(i / 3 * 2);
            cs[i + 1] = salt.charAt(i / 3);
            cs[i + 2] = saltedPassword.charAt(i / 3 * 2 + 1);
        }
        return new String(cs);
    }

    /**
     * 验证明文和加盐后的MD5码是否匹配
     **/
    public static boolean verifySaltPassword(String password, String md5) {
        // 检查 md5 字符串的长度是否至少为 48 个字符
        if (md5 == null || md5.length() < 48) {
            return false;
        }

        // 先从 MD5 码中取出之前加的盐和加盐后生成的 MD5 码
        char[] cs1 = new char[32];
        char[] cs2 = new char[16];
        for (int i = 0; i < 48; i += 3) {
            cs1[i / 3 * 2] = md5.charAt(i);
            cs1[i / 3 * 2 + 1] = md5.charAt(i + 2);
            cs2[i / 3] = md5.charAt(i + 1);
        }
        String salt = new String(cs2);
        // 比较二者是否相同
        return md5Hex(password + salt).equals(new String(cs1));
    }

    /**
     *  生成MD5密码
     **/
    private static String md5Hex(String src) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] bs = md5.digest(src.getBytes(StandardCharsets.UTF_8));
            return new String(new Hex().encode(bs));
        } catch (Exception e) {
            return null;
        }
    }

    public static void main(String args[]) {
        // 原密码
        String password = "1234561";
        System.out.println("明文(原生)密码：" + password);
        // MD5加密后的密码
        String MD5Password = MD5(password);
        System.out.println("普通MD5加密密码：" + MD5Password);
        // 获取加盐后的MD5值
        String SaltPassword = generateSaltPassword(password);
        System.out.println("加盐后的MD密码：" + SaltPassword);
        System.out.println("加盐后的密码和原生密码是否是同一字符串:" + verifySaltPassword(password, SaltPassword));
    }

}



