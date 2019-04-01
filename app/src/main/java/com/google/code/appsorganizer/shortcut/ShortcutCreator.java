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
 * You should have received a copy of the GNU General Public License
 * along with Apps Organizer.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.google.code.appsorganizer.shortcut;

import android.app.Activity;
import android.app.ListActivity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.pm.ShortcutInfoCompat;
import android.support.v4.content.pm.ShortcutManagerCompat;
import android.support.v4.graphics.drawable.IconCompat;
import android.view.View;
import android.widget.AdapterView;

import com.google.code.appsorganizer.R;
import com.google.code.appsorganizer.db.DatabaseHelper;
import com.google.code.appsorganizer.model.Label;
import com.google.code.appsorganizer.utils.ArrayAdapterSmallRow;

import java.util.List;

/**
 * @author fabio
 * 
 */
//public class ShortcutCreator extends AppCompatActivity {
public class ShortcutCreator extends ListActivity {

	//private ListView listView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//listView = findViewById(R.id.labelList);
		showCreateShortcutView();
	}

	public void showCreateShortcutView() {
		final List<Label> labels = DatabaseHelper.initOrSingleton(this).labelDao.getLabels();
		labels.add(0, new Label(LabelShortcut.ALL_LABELS_ID, getString(R.string.all_labels), R.drawable.icon));
		labels.add(1, new Label(LabelShortcut.ALL_STARRED_ID, getString(R.string.Starred_apps), R.drawable.favorites));
		labels.add(2, new Label(LabelShortcut.OTHER_APPS, getString(R.string.other_label), 0));
		setTitle(R.string.choose_labels_for_shortcut);
		//listView.setAdapter(...
		setListAdapter(new ArrayAdapterSmallRow<Label>(this, android.R.layout.simple_list_item_1, labels));

		//listView.setOnItemClickListener(...
		getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View v, int pos, long arg3) {
				Label labelObject = labels.get(pos);
				setupShortcut(labelObject.getId(), labelObject.getName(), labelObject.getImageBytes(), labelObject.getIcon());
				finish();
			}
		});
	}

	private void setupShortcut(Long id, String name, byte[] imageBytes, int icon) {
		ShortcutManagerCompat shortcutManager = getSystemService(ShortcutManagerCompat.class);
		if (shortcutManager.isRequestPinShortcutSupported(this)) {
			Intent shortcutIntent = createOpenLabelIntent(this, id);
			ShortcutInfoCompat shortcutInfo = new ShortcutInfoCompat.Builder(this, Long.toString(id))
					.setShortLabel(name)
					.setIcon(imageBytes != null ? IconCompat.createWithData(imageBytes, 0, imageBytes.length) :
												  IconCompat.createWithResource(this, icon))
					.setIntent(shortcutIntent)
					.build();
			Intent pinnedShortcutCallbackIntent = shortcutManager.createShortcutResultIntent(this, shortcutInfo);

			setResult(LabelShortcut.RESULT_OK, pinnedShortcutCallbackIntent);
		}
	}

	public static void requestPinShortcut(Activity a, Long id, String name, byte[] imageBytes, int icon) {
		ShortcutManagerCompat shortcutManager = a.getSystemService(ShortcutManagerCompat.class);
		if (shortcutManager.isRequestPinShortcutSupported(a)) {
			Intent shortcutIntent = createOpenLabelIntent(a, id);
			ShortcutInfoCompat shortcutInfo = new ShortcutInfoCompat.Builder(a, Long.toString(id))
					.setShortLabel(name)
					.setIcon(imageBytes != null ? IconCompat.createWithData(imageBytes, 0, imageBytes.length) :
												  IconCompat.createWithResource(a, icon))
					.setIntent(shortcutIntent)
					.build();
			Intent pinnedShortcutCallbackIntent = shortcutManager.createShortcutResultIntent(a, shortcutInfo);

			PendingIntent successCallback = PendingIntent.getBroadcast(a, /* request code */ 0, pinnedShortcutCallbackIntent, /* flags */ 0);
			shortcutManager.requestPinShortcut(a, shortcutInfo, successCallback.getIntentSender());
		}
	}

	public static Intent createOpenLabelIntent(Context a, Long id) {
		Intent shortcutIntent = new Intent(Intent.ACTION_MAIN);
		shortcutIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		shortcutIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		shortcutIntent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
		shortcutIntent.setClassName(a, LabelShortcut.class.getName());
		shortcutIntent.putExtra(LabelShortcut.LABEL_ID, id);
		return shortcutIntent;
	}
}
