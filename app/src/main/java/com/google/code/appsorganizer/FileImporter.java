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
package com.google.code.appsorganizer;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.code.appsorganizer.db.DbImportExport;
import com.google.code.appsorganizer.dialogs.ActivityWithDialog;
import com.google.code.appsorganizer.dialogs.OnOkClickListener;
import com.google.code.appsorganizer.dialogs.SimpleDialog;

/**
 * @author fabio
 */
public class FileImporter extends ActivityWithDialog {

    private static final int IMPORT_REQUEST_CODE = 1;

    private SimpleDialog importErrorDialog;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(getString(R.string.import_menu));
        getActionBar().setDisplayHomeAsUpEnabled(true);

        OnOkClickListener onOkClickListener = new OnOkClickListener() {
            private static final long serialVersionUID = 1L;
            public void onClick(CharSequence t, DialogInterface dialog, int which) {
                finish();
            }
        };
        importErrorDialog = new SimpleDialog(getGenericDialogManager(), getString(R.string.import_error), onOkClickListener);
        importErrorDialog.setShowNegativeButton(false);

        //Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        //intent.setType("text/plain");
        //intent.setType("text/*");
        intent.setType("*/*");
        startActivityForResult(intent, IMPORT_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        if (requestCode == IMPORT_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (resultData != null) {
                try {
                    Uri uri = resultData.getData();
                    DbImportExport.importData(this, uri);
                    finish();
                } catch (Throwable e) {
                    e.printStackTrace();
                    importErrorDialog.setTitle(getString(R.string.import_error) + ": " + e.getMessage());
                    getGenericDialogManager().showDialog(importErrorDialog);
                }
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                return false;
        }
        return super.onOptionsItemSelected(item);
    }
}
