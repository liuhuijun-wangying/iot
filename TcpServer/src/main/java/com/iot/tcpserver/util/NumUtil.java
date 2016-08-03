package com.iot.tcpserver.util;

public class NumUtil {

    public static byte[] short2Bytes(short shortData){
        byte[] result = new byte[2];
        result[0] = (byte)((shortData & 0xff00) >> 8);
        result[1] = (byte)(shortData & 0xff);
        return result;
    }

    public static short bytes2Short(byte[] byteData){
        return (short) ((byteData[0] << 8) | byteData[1] & 0xff);
    }

    public static int bytes2Int(byte[] byteData){
        return ((byteData[0] &0xff)<< 24) | ((byteData[1] &0xff)<< 16) |
                ((byteData[2] &0xff)<< 8) | (byteData[3] &0xff);
    }

    public static byte[] int2Bytes(int num) {
        byte[] result = new byte[4];
        result[0] = (byte)(num >>> 24);
        result[1] = (byte)(num >>> 16);
        result[2] = (byte)(num >>> 8);
        result[3] = (byte)(num );
        return result;
    }

    public static byte[] long2Bytes(long num) {
        byte[] byteNum = new byte[8];
        for (int ix = 0; ix < 8; ++ix) {
            int offset = 64 - (ix + 1) * 8;
            byteNum[ix] = (byte) ((num >> offset) & 0xff);
        }
        return byteNum;
    }

    public static long bytes2Long(byte[] byteNum) {
        long num = 0;
        for (int ix = 0; ix < 8; ++ix) {
            num <<= 8;
            num |= (byteNum[ix] & 0xff);
        }
        return num;
    }
}
