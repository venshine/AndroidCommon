package com.wx.android.common.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Byte
 *
 * @author fengwx
 */
public class ByteUtils {

    /**
     * Convert byte array to object
     *
     * @param bytes
     * @return
     */
    public static Object byteToObject(byte[] bytes) throws IOException, ClassNotFoundException {
        ObjectInputStream ois = null;
        try {
            ois = new ObjectInputStream(new ByteArrayInputStream(bytes));
            return ois.readObject();
        } finally {
            IOUtils.close(ois);
        }
    }

    /**
     * Convert object to byte array
     *
     * @param obj
     * @return
     */
    public static byte[] objectToByte(Object obj) {
        ObjectOutputStream oos = null;
        ByteArrayOutputStream bos = null;
        try {
            bos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(bos);
            oos.writeObject(obj);
            return bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.close(oos);
            IOUtils.close(bos);
        }
        return null;
    }

}
