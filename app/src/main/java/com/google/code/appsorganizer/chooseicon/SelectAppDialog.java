/**
 *
 */
package com.google.code.appsorganizer.chooseicon;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore.Images.Media;

import com.google.code.appsorganizer.R;
import com.google.code.appsorganizer.appwidget.AppsOrganizerAppWidgetProvider;
import com.google.code.appsorganizer.db.DatabaseHelper;
import com.google.code.appsorganizer.dialogs.GenericDialogManager;
import com.google.code.appsorganizer.dialogs.SimpleDialog;
import com.google.code.appsorganizer.dialogs.SingleSelectDialog;
import com.google.code.appsorganizer.model.Label;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public final class SelectAppDialog extends SingleSelectDialog {

    private static final String ITEM_ID = "ITEM_ID";

    private static final long serialVersionUID = 1L;

    private static final int DEFAULT_ICONS = 2;

    public static final int IMAGE_GALLERY = 10;

    public static final int ICON_PACK = 20;

//FIXME    public static final int AND_EXPLORER = 30;

//FIXME    private final SimpleDialog applicationNotFoundDialog;

    private final SimpleDialog selectImageDialog;

    private long itemId;

    private final DatabaseHelper dbHelper;

    public SelectAppDialog(GenericDialogManager dialogManager, DatabaseHelper dbHelper) {
//FIXME        super(dialogManager, dialogManager.getString(R.string.choose_app), dialogManager.getString(R.string.alert_dialog_ok), new CharSequence[]{
//                "Default icons", dialogManager.getString(R.string.Android_Image_Gallery), "AndExplorer", "Icon packs"}, 0);
//        applicationNotFoundDialog = createApplicationNotFoundDialog(dialogManager);
        super(dialogManager, dialogManager.getString(R.string.choose_app), dialogManager.getString(R.string.alert_dialog_ok), new CharSequence[]{
                "Default icons", dialogManager.getString(R.string.Android_Image_Gallery), "Icon packs"}, 0);

        selectImageDialog = new SimpleDialog(dialogManager, dialogManager.getString(R.string.select_jpg_bmp_title), dialogManager
                .getString(R.string.select_jpg_bmp));
        selectImageDialog.setShowNegativeButton(false);
        this.dbHelper = dbHelper;
    }

    @Override
    protected void onOkClick(DialogInterface dialog, int selectedItem) {
        if (selectedItem == 0) {
            Intent intent = new Intent(owner, ChooseIconActivity.class);
            intent.putExtra("groupId", itemId);
            owner.startActivityForResult(intent, DEFAULT_ICONS);
        } else if (selectedItem == 1) {
            //Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.setType("image/*");
            owner.startActivityForResult(intent, IMAGE_GALLERY);
//FIXME        } else if (selectedItem == 2) {
//            try {
//                openAndExplorerFileDialog();
//            } catch (ActivityNotFoundException e) {
//                applicationNotFoundDialog.showDialog();
//            }
        } else {
            owner.startActivityForResult(new Intent(owner, IconPackActivity.class), ICON_PACK);
        }
    }

//FIXME    private SimpleDialog createApplicationNotFoundDialog(GenericDialogManager dialogManager) {
//        String title = owner.getString(R.string.Application_not_found);
//        SimpleDialog applicationNotFoundDialog = new SimpleDialog(dialogManager, title, owner.getString(R.string.Application_not_found_message),
//                new OnOkClickListener() {
//                    private static final long serialVersionUID = 1L;
//
//                    public void onClick(CharSequence charSequence, DialogInterface dialog, int which) {
//                        Intent emailIntent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse("market://search?q=pname:lysesoft.andexplorer"));
//                        owner.startActivity(emailIntent);
//                    }
//                });
//
//        applicationNotFoundDialog.setOkMessageText(owner.getString(R.string.Open_market));
//        applicationNotFoundDialog.setShowNegativeButton(true);
//        return applicationNotFoundDialog;
//    }

//FIXME    private void openAndExplorerFileDialog() {
//        Intent intent = new Intent();
//        intent.setAction(Intent.ACTION_PICK);
//        Uri startDir = Uri.fromFile(new File("/sdcard"));
//        intent.setDataAndType(startDir, "vnd.android.cursor.dir/lysesoft.andexplorer.file");
//        // Title
//        intent.putExtra("explorer_title", "Select a file");
//        // Optional colors
//        intent.putExtra("browser_title_background_color", "440000AA");
//        intent.putExtra("browser_title_foreground_color", "FFFFFFFF");
//        intent.putExtra("browser_list_background_color", "66000000");
//
//        intent.putExtra("browser_filter_extension_whitelist", "*.jpg,*.jpeg,*.png");
//        // Optional font scale
//        intent.putExtra("browser_list_fontscale", "140%");
//        // Optional 0=simple list, 1 = list with filename and size, 2 =
//        // list with filename, size and date.
//        intent.putExtra("browser_list_layout", "2");
//        owner.startActivityForResult(intent, AND_EXPLORER);
//    }

    public void showDialog(long itemId) {
        this.itemId = itemId;
        super.showDialog();
    }

    public long getItemId() {
        return itemId;
    }

    public boolean onActivityResult(int requestCode, int resultCode, Intent intent) {
        boolean ret = false;
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case DEFAULT_ICONS:
                    int icon = intent.getIntExtra("icon", -1);
                    dbHelper.labelDao.updateIcon(itemId, Label.convertToIconDb(icon), null);
                    ret = true;
                    break;
                case IMAGE_GALLERY:
                    Uri uriImage = intent.getData();
                    if (uriImage != null) {
                        try {
                            byte[] byteArray = convertToByteArray(this.owner, uriImage);
                            dbHelper.labelDao.updateIcon(itemId, null, byteArray);
                            ret = true;
                        } catch (IOException e) {
                            selectImageDialog.showDialog();
                        }
                    }
                    break;
//FIXME                case AND_EXPLORER:
//                    Uri uri = intent.getData();
//                    if (uri != null) {
//                        String path = uri.toString().toLowerCase();
//                        String type = intent.getType();
//                        if (type == null || (!type.equals("image/jpeg") && !type.equals("image/png"))) {
//                            selectImageDialog.showDialog();
//                        } else if (path.startsWith("file://")) {
//                            File file = new File(URI.create(path));
//                            Bitmap bitmap = getScaledImage(file);
//                            ByteArrayOutputStream os = new ByteArrayOutputStream();
//                            bitmap.compress(CompressFormat.PNG, 100, os);
//
//                            dbHelper.labelDao.updateIcon(itemId, null, os.toByteArray());
//                            ret = true;
//                        }
//                    }
//                    break;
                case ICON_PACK:
                    byte[] image = intent.getByteArrayExtra("image");
                    dbHelper.labelDao.updateIcon(itemId, null, image);
                    ret = true;
                    break;
            }
            if (ret) {
                AppsOrganizerAppWidgetProvider.updateAppWidget(getOwner(), dbHelper.labelDao.queryById(itemId));
            }
        }
        return ret;
    }

    private byte[] convertToByteArray(Context context, Uri uri) throws FileNotFoundException, IOException {
        Bitmap bm = Media.getBitmap(owner.getContentResolver(), uri);
        return convertToByteArray(context, bm);
    }

    public static byte[] convertToByteArray(Context context, Bitmap bm) {
        Bitmap bitmap = getScaledImage(context, bm);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        bitmap.compress(CompressFormat.PNG, 100, os);
        return os.toByteArray();
    }

    //private static Bitmap getScaledImage(Context context, File file) {
    //    return getScaledImage(context, BitmapFactory.decodeFile(file.getAbsolutePath()));
    //}

    private static Bitmap getScaledImage(Context context, Bitmap bitmapOrg) {
        int width = bitmapOrg.getWidth();
        int height = bitmapOrg.getHeight();

        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        final int ICON_SIZE = am.getLauncherLargeIconSize();
        int newWidth = ICON_SIZE;
        int newHeight = ICON_SIZE;

        // calculate the scale - in this case = 0.4f
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;

        // createa matrix for the manipulation
        Matrix matrix = new Matrix();
        // resize the bit map
        matrix.postScale(scaleWidth, scaleHeight);

        // recreate the new Bitmap
        return Bitmap.createBitmap(bitmapOrg, 0, 0, width, height, matrix, true);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(ITEM_ID, itemId);
    }

    @Override
    public void onRestoreInstanceState(Bundle state) {
        super.onRestoreInstanceState(state);
        itemId = state.getLong(ITEM_ID, -1);
    }
}