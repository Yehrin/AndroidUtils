package utils;


public class ByteUtils {
    private static final String TAG = "ByteUtils";

    /*
     * byte[] 与 String 转化的情形：
     * 1、输入"abc"，返回 {0x61, 0x62, 0x63}
     * 2、输入{0x61, 0x62, 0x63}，返回"abc"
     * 3、输入"01A2"，返回 {0x01, 0xA2}
     * 4、输入{0x01, 0xA2}，转化为："01A2"
     */

    /**
     * @ Title: getStringBytes
     * @ Description: 获得字符串每个字符对应的Byte所构成的数组
     * @ param:
     * @ return:
     * @ author: Ye
     * @ date:
     * @ e.g: 输入"abc"，返回{0x61, 0x62, 0x63}
     */
    public static byte[] getStringBytes(String str) {
        return str.getBytes();
    }

    /**
     * @ Title: getBytesString
     * @ Description: 获得bytes数组每个byte对应的字符所构成的字符串
     * @ param:
     * @ return:
     * @ author: Ye
     * @ date:
     * @ e.g: 输入{0x61, 0x62, 0x63}，返回"abc"
     */
    public static String getBytesString(byte[] bytes) {
        return new String(bytes);
    }



    /**
     * @ Title: hexStringToBytes
     * @ Description: hex字符串转byte数组，2个hex转为一个byte
     * @ param:
     * @ return:
     * @ author: Ye
     * @ date:
     * @ e.g: "01A2"得到{0x01, 0xA2}
     */
    public static byte[] hexStringToBytes(String src) {
        byte[] res = new byte[src.length() / 2];
        char[] chs = src.toCharArray();
        for (int i = 0, c = 0; i < chs.length; i += 2, c++) {
            res[c] = (byte) (Integer.parseInt(new String(chs, i, 2), 16));
        }
        return res;
    }

    /**
     * @ Title: bytesToHexString
     * @ Description: 把字节数组转换成16进制字符串
     * @ param:
     * @ return:
     * @ author: Ye
     * @ date:
     * @ e.g: {0x01, 0xA2} 转化为："01A2"
     */
    public static String bytesToHexString(byte[] byteArray) {
        StringBuilder sb = new StringBuilder(byteArray.length);
        String sTemp;
        for (byte mByte : byteArray) {
            sTemp = Integer.toHexString(mByte & 0xFF);
            if (sTemp.length() < 2)
                sb.append('0');
            sb.append(sTemp.toUpperCase());
        }
        return sb.toString();
    }


    /*
     * byte[] 与 int32、int16、float 转化的情形：
     * 1、
     * 2、
     * 3、
     * 4、
     */

    /**
     * @ Title: byteArrayToInt32
     * @ Description: byte[]与int转换，常见于字节流里提取int32
     * @ param:
     * @ return:
     * @ author: Ye
     * @ date:
     */
    public static int byteArrayToInt32(byte[] b) {
        return   b[3] & 0xFF |
                (b[2] & 0xFF) << 8 |
                (b[1] & 0xFF) << 16 |
                (b[0] & 0xFF) << 24;
    }

    public static int byteArrayToInt16(byte[] b) {
        return byteToUnsigned(b[1]) |
                byteToUnsigned(b[0]) << 8;
    }

    public static byte[] intToByteArray(int a) {
        return new byte[] {
                (byte) ((a >> 24) & 0xFF),
                (byte) ((a >> 16) & 0xFF),
                (byte) ((a >> 8) & 0xFF),
                (byte) (a & 0xFF)
        };
    }

    /**
     * @ Title: floatToHexBytes
     * @ Description: 获得float数据对应的十六进制数
     * @ param:
     * @ return:
     * @ author: Ye
     * @ date:
     * @ e.g:
     */
    public static byte[] floatToHexBytes(float data) {
        String hexStr = Integer.toHexString(Float.floatToIntBits(data));
        return hexStringToBytes(hexStr);
    }



    /**
     * @ Title: byteToUnsigned
     * @ Description: 获得byte对应的无符号数值，示例：0x12返回18
     * @ param:
     * @ return:
     * @ author: Ye
     * @ date:
     */
    public static int byteToUnsigned(byte data) {
        return ((int)data) & 0xFF;
    }


    /*
     * byte[] 合并的情形：
     */

    /**
     * @ Title: addByteAndBytes
     * @ Description: 1个byte与1个byte数组拼接
     * @ param: data1
     * @ param: data2
     * @ return:
     * @ author: Ye
     * @ date:
     */
    public static byte[] addByteAndBytes(byte data1, byte[] data2) {
        return bytesMerger(new byte[]{data1}, data2);
    }

    /**
     * @ Title: bytesMerger
     * @ Description: 两个byte数组拼接
     * @ param:
     * @ return:
     * @ author: Ye
     * @ date:
     * @ e.g: data1: {0x01, 0x02}   data2：{0x03, 0x04}. 返回 {0x01, 0x02, 0x03, 0x04}
     */
    public static byte[] bytesMerger(byte[] data1, byte[] data2) {
        int len = 0;
        if (data1 != null) {
            len += data1.length;
        }
        if (data2 != null) {
            len += data2.length;
        }
        if (len == 0) {
            return null;
        }
        byte[] data3 = new byte[len];
        if (data1 != null) {
            System.arraycopy(data1, 0, data3, 0, data1.length);
        }
        if (data2 != null) {
            if (data1 == null) {
                System.arraycopy(data2, 0, data3, 0, data2.length);
            } else {
                System.arraycopy(data2, 0, data3, data1.length, data2.length);
            }
        }
        return data3;
    }
}
