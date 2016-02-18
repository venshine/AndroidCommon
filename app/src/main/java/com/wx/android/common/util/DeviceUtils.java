package com.wx.android.common.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.view.Window;

/**
 * Device Information, such as the parameters of screen.
 * 
 * @author fengwx
 */
public class DeviceUtils {

	/**
	 * Get screen width, in pixels
	 * 
	 * @param context
	 * @return
	 */
	public static int getScreenWidth(Context context) {
		DisplayMetrics dm = context.getResources().getDisplayMetrics();
		return dm.widthPixels;
	}

	/**
	 * Get screen height, in pixels
	 * 
	 * @param context
	 * @return
	 */
	public static int getScreenHeight(Context context) {
		DisplayMetrics dm = context.getResources().getDisplayMetrics();
		return dm.heightPixels;
	}

	/**
	 * Get screen density, the logical density of the display
	 * 
	 * @param context
	 * @return
	 */
	public static float getScreenDensity(Context context) {
		DisplayMetrics dm = context.getResources().getDisplayMetrics();
		return dm.density;
	}

	/**
	 * Get screen density dpi, the screen density expressed as dots-per-inch
	 * 
	 * @param context
	 * @return
	 */
	public static int getScreenDensityDPI(Context context) {
		DisplayMetrics dm = context.getResources().getDisplayMetrics();
		return dm.densityDpi;
	}

	/**
	 * Get titlebar height, this method cannot be used in onCreate(),onStart(),onResume(), unless it is called in the
	 * post(Runnable).
	 * 
	 * @param activity
	 * @return
	 */
	public static int getTitleBarHeight(Activity activity) {
		int statusBarHeight = getStatusBarHeight(activity);
		int contentViewTop = activity.getWindow().findViewById(Window.ID_ANDROID_CONTENT).getTop();
		int titleBarHeight = contentViewTop - statusBarHeight;
		return titleBarHeight < 0 ? 0 : titleBarHeight;
	}

	/**
	 * Get statusbar height, this method cannot be used in onCreate(),onStart(),onResume(), unless it is called in the
	 * post(Runnable).
	 * 
	 * @param activity
	 * @return
	 */
	public static int getStatusBarHeight(Activity activity) {
		Rect rect = new Rect();
		activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
		return rect.top;
	}

	/**
	 * Get statusbar height
	 * 
	 * @param activity
	 * @return
	 */
	public static int getStatusBarHeight2(Activity activity) {
		int statusBarHeight = getStatusBarHeight(activity);
		if (0 == statusBarHeight) {
			Class<?> localClass;
			try {
				localClass = Class.forName("com.android.internal.R$dimen");
				Object localObject = localClass.newInstance();
				int id = Integer.parseInt(localClass.getField("status_bar_height").get(localObject).toString());
				statusBarHeight = activity.getResources().getDimensionPixelSize(id);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (NoSuchFieldException e) {
				e.printStackTrace();
			}
		}
		return statusBarHeight;
	}

}
