package com.yhcdhp.cai.daydays.utils;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Util {

    private static final String ALGORIGTHM_MD5 = "MD5";
    private static final int CACHE_SIZE = 2048;
    private static MD5Util instance = null;

    public static String encode(String string) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(string.getBytes());
            byte b[] = md.digest();
            int i;
            StringBuffer buf = new StringBuffer("");
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0)
                    i += 256;
                if (i < 16)
                    buf.append("0");
                buf.append(Integer.toHexString(i));
            }
            return buf.toString();
            // if (type) {
            // return buf.toString(); // 32
            // } else {
            // return buf.toString().substring(8, 24);// 16
            // }
        } catch (Exception e) {
            return null;
        }
    }

    public static String generateFileMD5(String filePath) throws Exception {
        String md5 = "";
        File file = new File(filePath);
        if (file.exists()) {
            MessageDigest messageDigest = getMD5();
            InputStream in = new FileInputStream(file);
            byte[] cache = new byte[CACHE_SIZE];
            int nRead = 0;
            while ((nRead = in.read(cache)) != -1) {
                messageDigest.update(cache, 0, nRead);
            }
            in.close();
            byte data[] = messageDigest.digest();
            md5 = byteArrayToHexString(data);
        }
        return md5;
    }

    /**
     * <p>
     * MD5摘要字节数组转换为16进制字符串
     * </p>
     *
     * @param data MD5摘要
     * @return
     */
    private static String byteArrayToHexString(byte[] data) {
        // 用来将字节转换成 16 进制表示的字符
        char hexDigits[] = {
                '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'
        };
        // 每个字节用 16 进制表示的话，使用两个字符，所以表示成 16 进制需要 32 个字符
        char arr[] = new char[16 * 2];
        int k = 0; // 表示转换结果中对应的字符位置
        // 从第一个字节开始，对 MD5 的每一个字节转换成 16 进制字符的转换
        for (int i = 0; i < 16; i++) {
            byte b = data[i]; // 取第 i 个字节
            // 取字节中高 4 位的数字转换, >>>为逻辑右移，将符号位一起右移
            arr[k++] = hexDigits[b >>> 4 & 0xf];
            // 取字节中低 4 位的数字转换
            arr[k++] = hexDigits[b & 0xf];
        }
        // 换后的结果转换为字符串
        return new String(arr);
    }

    /**
     * <p>
     * 获取MD5实例
     * </p>
     *
     * @return
     * @throws java.security.NoSuchAlgorithmException
     */
    private static MessageDigest getMD5() throws NoSuchAlgorithmException {
        return MessageDigest.getInstance(ALGORIGTHM_MD5);
    }

    private static MessageDigest md5 = null;

    public static MD5Util getInstance() {
        try {
            instance = new MD5Util();
            md5 = MessageDigest.getInstance("MD5");
        } catch (Exception e) {
            return null;
        }
        return instance;
    }

    /**
     * 获得一个字符串的MD5值
     *
     * @param source
     * @return
     */
    public String getStringHash(String source) {
        String hash = null;
        try {
            ByteArrayInputStream in = new ByteArrayInputStream(source.getBytes());
            hash = getStreamHash(in);
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return hash;
    }

    /**
     * 获得一个流文件的MD5值
     *
     * @param stream
     * @return
     */
    public String getStreamHash(InputStream stream) {
        String hash = null;
        byte[] buffer = new byte[1024];
        BufferedInputStream in = null;
        try {
            in = new BufferedInputStream(stream);
            int numRead = 0;
            while ((numRead = in.read(buffer)) > 0) {
                md5.update(buffer, 0, numRead);
            }
            in.close();
            hash = toHexString(md5.digest());
        } catch (Exception e) {
            if (in != null)
                try {
                    in.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            e.printStackTrace();
        }
        return hash;
    }

    /**
     * 将byte数据转化为MD5
     *
     * @param b
     * @return
     */
    private String toHexString(byte[] b) {
        StringBuilder sb = new StringBuilder(b.length * 2);
        for (int i = 0; i < b.length; i++) {
            sb.append(hexChar[(b[i] & 0xf0) >>> 4]);
            sb.append(hexChar[b[i] & 0x0f]);
        }
        return sb.toString();
    }

    // 转化匹配数组
    private char[] hexChar = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

}
