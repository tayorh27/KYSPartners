package com.kys.kyspartners;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import com.afollestad.materialdialogs.MaterialDialog;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Random;

/**
 * Created by sanniAdewale on 11/05/2017.
 */

public class General {
    Context context;
    MaterialDialog materialDialog;

    public General(Context context) {
        this.context = context;
    }

    public void displayDialog(String text) {
        materialDialog = new MaterialDialog.Builder(context)
                .content(text)
                .canceledOnTouchOutside(true)
                .cancelable(true)
                .progress(true, 0)
                .show();
    }

    public void dismissDialog() {
        materialDialog.dismiss();
    }

    public MaterialDialog getMaterialDialog() {
        return materialDialog;
    }

    public void error(String text) {
        new MaterialDialog.Builder(context)
                .title("Error")
                .content(text)
                .positiveText("OK")
                .show();
    }

    public void success(String text) {
        new MaterialDialog.Builder(context)
                .title("Success")
                .titleColor(context.getResources().getColor(R.color.bg_screen3))
                .content(text)
                .contentColor(context.getResources().getColor(R.color.bg_screen3))
                .positiveText("OK")
                .positiveColor(context.getResources().getColor(R.color.bg_screen3))
                .show();
    }

    public static String CopyTo(String file, String username) {
        String new_file_path = "";
        FileOutputStream fileOutputStream = null;
        try {
            String root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();
            File myDir1 = new File(root + "/KYS/");
            myDir1.mkdirs();
            Calendar calendar = Calendar.getInstance();
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int month = calendar.get(Calendar.MONTH);
            int year = calendar.get(Calendar.YEAR);
            int milli = calendar.get(Calendar.MILLISECOND);
            File new_image_file = new File(myDir1.toString() + "/logo_" + username + "_" + (day + milli) + "" + (month + 1) + "" + year + "" + milli + "_" + new Random().nextInt(93564) + ".png");
            Log.e("dir", myDir1.toString() + "\n" + new_image_file.toString());

            String dest = new_image_file.getPath();
            new_file_path = dest;

            fileOutputStream = new FileOutputStream(dest);
            Bitmap bitmap = BitmapFactory.decodeFile(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);

        } catch (FileNotFoundException e) {
            Log.e("file exception", e.toString());
        } catch (Exception ex) {
            Log.e("normal exception", ex.toString());
        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    Log.e("io exception", e.toString());
                }
            }
        }
        return new_file_path;
    }

    public static int AddNumber() {
        Calendar calendar = Calendar.getInstance();
        int milli = calendar.get(Calendar.MILLISECOND);
        int sec = calendar.get(Calendar.SECOND);
        int min = calendar.get(Calendar.MINUTE);
        int hr = calendar.get(Calendar.HOUR);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int mt = calendar.get(Calendar.MONTH);
        int yr = calendar.get(Calendar.YEAR);

        int total = (yr + mt - day * hr + min - sec + milli);
        return total;
    }

}
