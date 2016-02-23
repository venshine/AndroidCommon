package com.wx.android.common.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * Url
 *
 * @author fengwx
 */
public class UrlUtils {

    private static final String CHARSET_NAME = "UTF-8";

    /**
     * Encode url
     *
     * @param url
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String encode(String url) throws UnsupportedEncodingException {
        return URLEncoder.encode(url, CHARSET_NAME);
    }

    /**
     * Decode url
     *
     * @param url
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String decode(String url) throws UnsupportedEncodingException {
        return URLDecoder.decode(url, CHARSET_NAME);
    }

}
