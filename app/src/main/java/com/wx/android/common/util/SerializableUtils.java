package com.wx.android.common.util;

import android.content.Context;

import java.io.*;

/**
 * Serialize and deserialize data
 *
 * @author fengwx
 */
public class SerializableUtils {

    /**
     * Serializable datas
     *
     * @param context
     * @param fileName
     * @param obj
     * @throws IOException
     */
    public static void serializeData(Context context, String fileName, Object obj) throws IOException {
        if (!(obj instanceof Serializable) && !(obj instanceof Externalizable)) {
            throw new InvalidClassException("Object must be serialized!");
        }
        FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
        ObjectOutputStream ostream = new ObjectOutputStream(fos);
        ostream.writeObject(obj);
        ostream.flush();
        ostream.close();
        fos.close();
    }

    /**
     * Deserializable datas
     *
     * @param context
     * @param fileName
     * @return
     * @throws ClassNotFoundException
     * @throws IOException
     */
    public static Object deserializeData(Context context, String fileName) throws ClassNotFoundException, IOException {
        FileInputStream fis = context.openFileInput(fileName);
        ObjectInputStream s = new ObjectInputStream(fis);
        Object obj = s.readObject();
        s.close();
        fis.close();
        return obj;
    }
}