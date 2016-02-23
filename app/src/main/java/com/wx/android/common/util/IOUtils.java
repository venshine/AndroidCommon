package com.wx.android.common.util;

import com.wx.android.common.log.Logger;

import java.io.Closeable;
import java.io.IOException;

/**
 * IO operation
 *
 * @author fengwx
 */
public class IOUtils {

    /**
     * Close closeable object
     *
     * @param closeable
     */
    public static void close(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                Logger.e(e, "", e.toString());
            }
        }
    }

}
