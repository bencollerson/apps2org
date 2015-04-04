/*
 * Copyright (C) 2009 Apps Organizer
 *
 * This file is part of Apps Organizer
 *
 * Apps Organizer is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Apps Organizer is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have keived a copy of the GNU General Public License
 * along with Apps Organizer.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.google.code.appsorganizer;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ComponentInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;

import com.google.code.appsorganizer.db.AppCacheDao;
import com.google.code.appsorganizer.db.DatabaseHelper;
import com.google.code.appsorganizer.model.AppCache;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class ApplicationInfoManager {

	private ApplicationInfoManager() {
	}

    public static void reloadAll(Context context, DatabaseHelper dbHelper, Handler handler, boolean discardCache, String packageToReload) {
        PackageManager pm = context.getPackageManager();
		AppCacheDao appCacheDao = dbHelper.appCacheDao;
		synchronized (ApplicationInfoManager.class) {
            appCacheDao.fixDuplicateApps();
			StringBuffer installedIds = new StringBuffer("-1");
			List<ResolveInfo> installedApplications = getAllResolveInfo(pm);

			if (handler != null) {
				sendSizeMessage(handler, installedApplications.size());
			}

			for (ResolveInfo resolveInfo : installedApplications) {
				ComponentInfo a = resolveInfo.activityInfo;
				AppCache appCache = appCacheDao.queryForAppCache(a.packageName, a.name, false, !discardCache);
				String label = loadAppLabel(context, a, discardCache || a.packageName.equals(packageToReload), appCacheDao, appCache, installedIds);
				if (handler != null) {
					Message message = new Message();
					message.obj = label;
					handler.sendMessage(message);
				}
			}
			if (discardCache) {
				appCacheDao.removeUninstalledApps(installedIds);
			}
		}
	}

	private static void sendSizeMessage(Handler handler, int size) {
		Message message = new Message();
		message.arg1 = size;
		handler.sendMessage(message);
	}

	private static String loadAppLabel(Context context, ComponentInfo a, boolean discardCache,
                                       AppCacheDao appCacheDao, AppCache loadedObj,
			                           StringBuffer installedIds) {
        Resources res = context.getResources();
        PackageManager pm = context.getPackageManager();
		boolean changed = false;
		String label = null;
		byte[] image = null;
		if (loadedObj != null) {
			label = loadedObj.label;
			image = loadedObj.image;
			if (loadedObj.disabled) {
				changed = true;
			}
		}
		if (label == null || discardCache) {
			CharSequence l = a.loadLabel(pm);
			if (l != null) {
				label = l.toString();
				changed = true;
			}
		}
		if (image == null || discardCache) {
			Drawable drawable = a.loadIcon(pm);
			if (drawable instanceof BitmapDrawable) {
				Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
				int width = bitmap.getWidth();
				int height = bitmap.getHeight();
                ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
                final int ICON_SIZE = am.getLauncherLargeIconSize();
                if (width > ICON_SIZE || height > ICON_SIZE) {
                    bitmap = scaleImage(res, bitmap, ICON_SIZE, width, height);
				}
				ByteArrayOutputStream os = new ByteArrayOutputStream();
				boolean compressed = bitmap.compress(CompressFormat.PNG, 100, os);
				if (!compressed) {
					bitmap = Bitmap.createScaledBitmap(bitmap, width, height, true);
					os = new ByteArrayOutputStream();
					compressed = bitmap.compress(CompressFormat.PNG, 100, os);
				}
				image = os.toByteArray();
				changed = true;
			}
		}
		if (changed) {
			// if label is not in cache table
			if (loadedObj == null) {
				// retrieve and store label
				AppCache obj = new AppCache(a.packageName, a.name, label);
				obj.image = image;
				long insertedId = appCacheDao.insert(obj);
				installedIds.append(',').append(insertedId);
			} else {
				appCacheDao.updateLabel(a.packageName, a.name, label, image, false);
				installedIds.append(',').append(loadedObj.getId());
			}
		} else {
			installedIds.append(',').append(loadedObj.getId());
		}
		return label;
	}

	private static Bitmap scaleImage(Resources res, Bitmap bitmap, int iconSize, int width, int height) {
		int newWidth = iconSize;
		int newHeight = iconSize;
		if (width > height) {
			newHeight = iconSize * height / width;
		} else if (width < height) {
			newWidth = iconSize * width / height;
		}
		Bitmap bitmap2 = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);

		return createSquareBitmap(res, bitmap2, iconSize);
	}

	private static Bitmap createSquareBitmap(Resources res, Bitmap bitmap, int iconSize) {
		Bitmap bm = Bitmap.createBitmap(iconSize, iconSize, Config.ARGB_8888);
		Canvas c = new Canvas(bm);
		BitmapDrawable d = new BitmapDrawable(res, bitmap);
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		int left = (iconSize - width) / 2;
		int top = (iconSize - height) / 2;
		d.setBounds(left, top, left + width, top + height);
		d.draw(c);
		return bm;
	}

	private static List<ResolveInfo> getAllResolveInfo(PackageManager pm) {
		Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
		mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);

		return pm.queryIntentActivities(mainIntent, 0);
	}

	public static ArrayList<String> getAllActivityNames(PackageManager pm, String packageName) {
		List<ResolveInfo> allResolveInfo = getAllResolveInfo(pm);
		ArrayList<String> activityNames = new ArrayList<String>();
		for (ResolveInfo resolveInfo : allResolveInfo) {
			if (resolveInfo.activityInfo.packageName.equals(packageName)) {
				activityNames.add(resolveInfo.activityInfo.name);
			}
		}
		return activityNames;
	}

}
