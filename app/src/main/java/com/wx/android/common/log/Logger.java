package com.wx.android.common.log;

import android.util.Log;

import com.wx.android.common.CommonCfg;
import com.wx.android.common.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Log manager
 * <p/>
 * 1.Logger.d("MSG");<p/>
 * 2.Logger.d("MSG{}MSG{}", "first", "second");<p/>
 * 3.Logger.d("TAG", "MSG{}MSG{}", "first", "second");<p/>
 * 4.Logger.d(Throwable, "TAG", "MSG{}MSG{}", "first", "second");<p/>
 *
 * @author fengwx
 */
public final class Logger {

    private static int mLevel = CommonCfg.LOG;

    private static final String mFlag = "{}";   // message中的标记，用于替换字符

    private static ConcurrentHashMap<String, String> tagMap = new ConcurrentHashMap<String, String>(10);    // 记录全局tag，即整个类的tag标签

    /**
     * 初始化全局tag，一个类中仅能初始化一次，最好在静态方法块中初始化。如果需要重新初始化，先调用clearTag()方法。
     *
     * @param tag
     */
    public static void initTag(String tag) {
        if (!StringUtils.isEmpty(tag)) {
            String fileName = Thread.currentThread().getStackTrace()[3]
                    .getFileName();  // className可能出现org.fengwx.core.Main$4这种命名，因此使用fileName
            if (tagMap.size() > 0 && tagMap.containsKey(fileName)) {
                throw new IllegalStateException("initTag() only can be invoked one time in a class, please invoke clearTag() first.");
            }
            tagMap.put(fileName, tag);
        }
    }

    /**
     * 清除全局tag
     */
    public static void clearTag() {
        String fileName = Thread.currentThread().getStackTrace()[3]
                .getFileName();  // className可能出现org.fengwx.core.Main$4这种命名，因此使用fileName
        if (tagMap.size() > 0 && tagMap.containsKey(fileName)) {
            tagMap.remove(fileName);
        }
    }

    @Deprecated
    private Logger() {
    }

    /**
     * fatal
     *
     * @param msg
     * @param args
     */
    public static void wtf(CharSequence msg, Object... args) {
        wtf(null, null, msg, args);
    }

    /**
     * fatal
     *
     * @param tag
     * @param msg
     * @param args
     */
    public static void wtf(String tag, CharSequence msg, Object... args) {
        if (!StringUtils.isEmpty(tag) && tag.contains(mFlag)) {  // tag如果包含{}，tag为msg
            if (args != null && args.length > 0) {  // 如果args不为空，将msg和args合并作为参数传入
                ArrayList<Object> list = new ArrayList<Object>(Arrays.asList(args));
                list.add(0, msg);
                wtf(tag, list.toArray());
            } else {
                wtf(tag, msg);
            }
        } else {
            wtf(null, tag, msg, args);
        }
    }

    /**
     * fatal
     *
     * @param tr
     * @param msg
     * @param args
     */
    public static void wtf(Throwable tr, String tag, CharSequence msg, Object... args) {
        if (mLevel <= Level.ASSERT) {
            println(Level.ASSERT, tr, tag, msg, args);
        }
    }


    /**
     * error
     *
     * @param msg
     * @param args
     */
    public static void e(CharSequence msg, Object... args) {
        e(null, null, msg, args);
    }

    /**
     * error
     *
     * @param tag
     * @param msg
     * @param args
     */
    public static void e(String tag, CharSequence msg, Object... args) {
        if (!StringUtils.isEmpty(tag) && tag.contains(mFlag)) {  // tag如果包含{}，tag为msg
            if (args != null && args.length > 0) {  // 如果args不为空，将msg和args合并作为参数传入
                ArrayList<Object> list = new ArrayList<Object>(Arrays.asList(args));
                list.add(0, msg);
                e(tag, list.toArray());
            } else {
                e(tag, msg);
            }
        } else {
            e(null, tag, msg, args);
        }
    }

    /**
     * error
     *
     * @param tr
     * @param msg
     * @param args
     */
    public static void e(Throwable tr, String tag, CharSequence msg, Object... args) {
        if (mLevel <= Level.ERROR) {
            println(Level.ERROR, tr, tag, msg, args);
        }
    }

    /**
     * warn
     *
     * @param msg
     * @param args
     */
    public static void w(CharSequence msg, Object... args) {
        w(null, null, msg, args);
    }

    /**
     * warn
     *
     * @param tag
     * @param msg
     * @param args
     */
    public static void w(String tag, CharSequence msg, Object... args) {
        if (!StringUtils.isEmpty(tag) && tag.contains(mFlag)) {  // tag如果包含{}，tag为msg
            if (args != null && args.length > 0) {  // 如果args不为空，将msg和args合并作为参数传入
                ArrayList<Object> list = new ArrayList<Object>(Arrays.asList(args));
                list.add(0, msg);
                w(tag, list.toArray());
            } else {
                w(tag, msg);
            }
        } else {
            w(null, tag, msg, args);
        }
    }

    /**
     * warn
     *
     * @param tr
     * @param msg
     * @param args
     */
    public static void w(Throwable tr, String tag, CharSequence msg, Object... args) {
        if (mLevel <= Level.WARN) {
            println(Level.WARN, tr, tag, msg, args);
        }
    }

    /**
     * info
     *
     * @param msg
     * @param args
     */
    public static void i(CharSequence msg, Object... args) {
        i(null, null, msg, args);
    }

    /**
     * info
     *
     * @param tag
     * @param msg
     * @param args
     */
    public static void i(String tag, CharSequence msg, Object... args) {
        if (!StringUtils.isEmpty(tag) && tag.contains(mFlag)) {  // tag如果包含{}，tag为msg
            if (args != null && args.length > 0) {  // 如果args不为空，将msg和args合并作为参数传入
                ArrayList<Object> list = new ArrayList<Object>(Arrays.asList(args));
                list.add(0, msg);
                i(tag, list.toArray());
            } else {
                i(tag, msg);
            }
        } else {
            i(null, tag, msg, args);
        }
    }

    /**
     * info
     *
     * @param tr
     * @param msg
     * @param args
     */
    public static void i(Throwable tr, String tag, CharSequence msg, Object... args) {
        if (mLevel <= Level.INFO) {
            println(Level.INFO, tr, tag, msg, args);
        }
    }

    /**
     * debug
     *
     * @param msg
     * @param args
     */
    public static void d(CharSequence msg, Object... args) {
        d(null, null, msg, args);
    }

    /**
     * debug
     *
     * @param tag
     * @param msg
     * @param args
     */
    public static void d(String tag, CharSequence msg, Object... args) {
        if (!StringUtils.isEmpty(tag) && tag.contains(mFlag)) {  // tag如果包含{}，tag为msg
            if (args != null && args.length > 0) {  // 如果args不为空，将msg和args合并作为参数传入
                ArrayList<Object> list = new ArrayList<Object>(Arrays.asList(args));
                list.add(0, msg);
                d(tag, list.toArray());
            } else {
                d(tag, msg);
            }
        } else {
            d(null, tag, msg, args);
        }
    }

    /**
     * debug
     *
     * @param tr
     * @param msg
     * @param args
     */
    public static void d(Throwable tr, String tag, CharSequence msg, Object... args) {
        if (mLevel <= Level.DEBUG) {
            println(Level.DEBUG, tr, tag, msg, args);
        }
    }

    /**
     * verbose
     *
     * @param msg
     * @param args
     */
    public static void v(CharSequence msg, Object... args) {
        v(null, null, msg, args);
    }

    /**
     * verbose
     *
     * @param tag
     * @param msg
     * @param args
     */
    public static void v(String tag, CharSequence msg, Object... args) {
        if (!StringUtils.isEmpty(tag) && tag.contains(mFlag)) {  // tag如果包含{}，tag为msg
            if (args != null && args.length > 0) {  // 如果args不为空，将msg和args合并作为参数传入
                ArrayList<Object> list = new ArrayList<Object>(Arrays.asList(args));
                list.add(0, msg);
                v(tag, list.toArray());
            } else {
                v(tag, msg);
            }
        } else {
            v(null, tag, msg, args);
        }
    }

    /**
     * verbose
     *
     * @param tr
     * @param msg
     * @param args
     */
    public static void v(Throwable tr, String tag, CharSequence msg, Object... args) {
        if (mLevel <= Level.VERBOSE) {
            println(Level.VERBOSE, tr, tag, msg, args);
        }
    }

    /**
     * format
     *
     * @param msg
     * @param args
     * @return
     */
    private static String format(String msg, Object... args) {
        if (!StringUtils.isEmpty(msg) && null != args && msg.contains(mFlag)) {
            StringBuilder result = new StringBuilder(msg);
            int len = args.length;
            if (len == 0) {
                return msg;
            }
            int i = 0;
            while (result.toString().contains(mFlag)) {
                if (i >= len) {
                    return result.toString();
                }
                int index = result.indexOf(mFlag);
                String value = result.toString();
                result.delete(0, result.length());
                result.append(value.substring(0, index))
                        .append(((args[i] == null) ? "" : String.valueOf(args[i]))).append(value.substring(index + mFlag.length()));
                i++;
            }
            return result.toString();
        }
        return msg;
    }

    /**
     * print log info
     *
     * @param level
     * @param tr
     * @param msg
     * @param args
     */
    private static void println(int level, Throwable tr, String tag, CharSequence msg, Object... args) {
        Log.println(level, markTag(tag),
                ((msg = format(msg.toString(), args)) == null ? "" : msg) + (tr == null ? "" : "\n" + Log
                        .getStackTraceString(tr)));
    }

    /**
     * mark tag
     *
     * @return
     */
    private static String markTag(String tag) {
        if (!StringUtils.isEmpty(tag)) { // log中有tag，以tag为准
            return tag;
        }
        String fileName = Thread.currentThread().getStackTrace()[6].getFileName();
        if (tagMap.size() > 0 && tagMap.containsKey(fileName)) { // 判断是否存在全局tag，存在以全局tag为准
            return tagMap.get(fileName);
        }
        return Thread.currentThread().getStackTrace()[6].getClassName();
    }

}
