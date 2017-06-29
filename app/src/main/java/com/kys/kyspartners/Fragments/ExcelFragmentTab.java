package com.kys.kyspartners.Fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.afollestad.materialdialogs.folderselector.FileChooserDialog;
import com.beardedhen.androidbootstrap.BootstrapButton;
import com.kys.kyspartners.Activity.AddProductActivity;
import com.kys.kyspartners.Database.AppData;
import com.kys.kyspartners.General;
import com.kys.kyspartners.Information.Excel;
import com.kys.kyspartners.Information.Products;
import com.kys.kyspartners.Information.Shop;
import com.kys.kyspartners.MyApplication;
import com.kys.kyspartners.R;
import com.kys.kyspartners.network.RegisterProducts;
import com.nbsp.materialfilepicker.MaterialFilePicker;
import com.nbsp.materialfilepicker.ui.FilePickerActivity;
import com.wajahatkarim3.easyflipview.EasyFlipView;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Pattern;

import pl.polak.clicknumberpicker.ClickNumberPickerView;

import static android.app.Activity.RESULT_OK;

/**
 * Created by sanniAdewale on 11/05/2017.
 */

public class ExcelFragmentTab extends Fragment implements View.OnClickListener {

    private String LOG_TAG = "ReadExcelFromFile";
    private static final int FILE_PICKER_CODE = 190;
    AppData data;
    General general;
    Shop shop;
    ProgressBar progressBar1, _progressBar2;
    TextView progressText, tvStatus, _progressText2, _tvStatus2;
    BootstrapButton button, button2;
    EasyFlipView flipView;
    ImageView settings;
    CardView cardView1, cardView2;
    ImageView ivBack;
    CheckBox checkBox;
    Timer timer, timer2;

    ClickNumberPickerView stepperTouch1, stepperTouch2, stepperTouch3, stepperTouch4, stepperTouch5, stepperTouch6, stepperTouch7;
    ArrayList<Products> customData = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_excel, container, false);

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        data = new AppData(getActivity());
        general = new General(getActivity());
        shop = data.getShop();
        progressBar1 = (ProgressBar) view.findViewById(R.id.progressBar);
        _progressBar2 = (ProgressBar) view.findViewById(R.id.progressBar2);
        progressText = (TextView) view.findViewById(R.id.progress_circle_text);
        tvStatus = (TextView) view.findViewById(R.id.status);
        _progressText2 = (TextView) view.findViewById(R.id.progress_circle_text2);
        _tvStatus2 = (TextView) view.findViewById(R.id.status2);
        button = (BootstrapButton) view.findViewById(R.id.btnFile);
        button2 = (BootstrapButton) view.findViewById(R.id.btnDone);
        flipView = (EasyFlipView) view.findViewById(R.id.flipView);
        settings = (ImageView) view.findViewById(R.id.ivSettings);
        ivBack = (ImageView) view.findViewById(R.id.action_back);
        cardView1 = (CardView) view.findViewById(R.id.card1);
        cardView2 = (CardView) view.findViewById(R.id.card2);
        stepperTouch1 = (ClickNumberPickerView) view.findViewById(R.id.st_rows_before);
        stepperTouch2 = (ClickNumberPickerView) view.findViewById(R.id.st_product_category);
        stepperTouch3 = (ClickNumberPickerView) view.findViewById(R.id.st_product_price);
        stepperTouch4 = (ClickNumberPickerView) view.findViewById(R.id.st_product_desc);
        stepperTouch5 = (ClickNumberPickerView) view.findViewById(R.id.st_product_stock);
        stepperTouch6 = (ClickNumberPickerView) view.findViewById(R.id.st_product_name);
        stepperTouch7 = (ClickNumberPickerView) view.findViewById(R.id.st_product_logo);
        checkBox = (CheckBox) view.findViewById(R.id.checkbox);
        settings.setOnClickListener(this);
        button.setOnClickListener(this);
        button2.setOnClickListener(this);
        ivBack.setOnClickListener(this);
    }

    private void GetExcelFile() {
        if (!data.getExcelSettingsAdded()) {
            general.error("Please click on the settings icon to configure excel arrangement.");
            return;
        }
//        new FileChooserDialog.Builder()
//                .mimeType("file/*") // Optional MIME type filter
//                .extensionsFilter(".xls", ".xlsx") // Optional extension filter, will override mimeType()
//                .tag("excel")
//                .goUpLabel("Up") // custom go up label, default label is "..."
//                .show();
        new MaterialFilePicker()
                .withFragment(this)
                .withRequestCode(FILE_PICKER_CODE)
                // .withFilter(Pattern.compile(".*\\.xls$")) // Filtering files and directories by file name using regexp
                .withFilter(Pattern.compile(".*\\.xls$"))
                .withFilterDirectories(false) // Set directories filterable (false by default)
                .withHiddenFiles(true) // Show hidden files and folders
                .start();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            Log.e("activity", "i am here_" + requestCode + "_" + resultCode);
            if (requestCode == FILE_PICKER_CODE && resultCode == RESULT_OK && data != null) {
                String filePath = data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH);
                File file = new File(filePath);
                Log.e("file", filePath);
                CallThread(file);
            }
        } catch (Exception e) {
            Log.e("onactivityex", e.toString());
        }

    }

    public void CallThread(@Nullable final File file) {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (file != null)
                            GetExcelFile2(file);
                    }
                });

            }
        }, 2000);
    }

    public void CallThread2() {
        timer2 = new Timer();
        timer2.schedule(new TimerTask() {
            @Override
            public void run() {
            }
        }, 100);
    }

    private void GetExcelFile2(File file) {
        try {
            //timer.cancel();
            tvStatus.setText("Reading excel file...");
            Excel excel = data.getExcelSettings();
            int totalCount = 0;
            int count = 0;
            InputStream inputStream = new FileInputStream(file);
            //POIFSFileSystem myFileSystem = new POIFSFileSystem(inputStream);
            // Create a workbook using the Input Stream
            HSSFWorkbook myWorkBook = new HSSFWorkbook(inputStream);

            //XSSFWorkbook

            // Get the first sheet from workbook
            HSSFSheet mySheet = myWorkBook.getSheetAt(0);

            // We now need something to iterate through the cells
            Iterator<Row> _rowIter = mySheet.rowIterator();


            while (_rowIter.hasNext()) {
                HSSFRow myRow = (HSSFRow) _rowIter.next();
                if (myRow.getRowNum() < excel.row_count) {
                    continue;
                }
                totalCount++;
            }
            Log.e("row count", totalCount + "");

            Iterator<Row> rowIter = mySheet.rowIterator();
            while (rowIter.hasNext()) {

                HSSFRow myRow = (HSSFRow) rowIter.next();
//                // Skip the first n rows
                if (myRow.getRowNum() < excel.row_count) {
                    continue;
                }
                count++;
                Log.e("n row count", count + "");

                Products shoppingCart = new Products();

                Iterator<Cell> cellIter = myRow.cellIterator();
                while (cellIter.hasNext()) {

                    HSSFCell myCell = (HSSFCell) cellIter.next();
                    String cellValue = "";

                    // Check for cell Type
                    if (myCell.getCellType() == HSSFCell.CELL_TYPE_STRING) {
                        cellValue = myCell.getStringCellValue();
                    } else {
                        cellValue = String.valueOf(myCell.getNumericCellValue());
                    }

                    // Just some log information
                    Log.e(LOG_TAG, cellValue);

                    // Push the parsed data in the Java Object
                    // Check for cell index
                    if (myCell.getColumnIndex() == excel.product_name) {
                        shoppingCart.product_name = cellValue;
                    } else if (myCell.getColumnIndex() == excel.product_category) {
                        shoppingCart.product_category = cellValue;
                    } else if (myCell.getColumnIndex() == excel.product_price) {
                        shoppingCart.product_price = cellValue;
                    } else if (myCell.getColumnIndex() == excel.product_desc) {
                        shoppingCart.product_description = cellValue;
                    } else if (myCell.getColumnIndex() == excel.product_logo) {
                        shoppingCart.product_logo = cellValue;
                    } else if (myCell.getColumnIndex() == excel.stock) {
                        shoppingCart.inStock = cellValue;
                    }


                }
                shoppingCart.id = 0;
                shoppingCart.shop_name = shop.name;
                shoppingCart.type = "excel";
                int value = new Random().nextInt(9999999);
                shoppingCart.unique_value = value - General.AddNumber();
                // Add object to list
                customData.add(shoppingCart);
                int percent = (count / totalCount) * 100;
                CallThread2();
                progressBar1.setProgress(percent);
                progressText.setText(percent + "%");


            }
        } catch (Exception e) {
            Log.e("onactivityex2", e.toString());
        }
        tvStatus.setText("File reading done.");
        //MyApplication.getWritableDatabase().insertMyProducts(customData, false);
        CallThread2();
        //timer2.cancel();
        flipView.flipTheView(true);
        flipView.setFlipDuration(1000);
        if (!checkBox.isChecked()) {
            UploadAllProducts();
        } else {
            //UpdateAllProducts(); ///add comment to rating
        }

    }

    private void SaveSettings() {
        int row_before = (int) stepperTouch1.getValue();
        int p_name = (int) stepperTouch6.getValue();
        int p_category = (int) stepperTouch2.getValue();
        int p_price = (int) stepperTouch3.getValue();
        int p_desc = (int) stepperTouch4.getValue();
        int p_logo = (int) stepperTouch7.getValue();
        int p_stock = (int) stepperTouch5.getValue();
        Excel excel = new Excel(row_before, p_name, p_category, p_price, p_desc, p_logo, p_stock);
        data.setExcelSettingsAdded(true);
        data.setExcelSettings(excel);
        cardView2.setVisibility(View.GONE);
        cardView1.setVisibility(View.VISIBLE);
    }

    private void PreviewSettings() {
        Excel excel = data.getExcelSettings();
        stepperTouch1.setPickerValue(excel.row_count);
        stepperTouch2.setPickerValue(excel.product_category);
        stepperTouch3.setPickerValue(excel.product_price);
        stepperTouch4.setPickerValue(excel.product_desc);
        stepperTouch5.setPickerValue(excel.stock);
        stepperTouch6.setPickerValue(excel.product_name);
        stepperTouch7.setPickerValue(excel.product_logo);
    }

    private void UpdateAllProducts() {
        _progressBar2.setIndeterminate(false);
        int total = customData.size();
        int count = 0;

        while (count < total) {

            RegisterProducts registerProducts = new RegisterProducts(getActivity(), customData.get(count));
            //registerProducts.RegisterFromExcel();
            count++;
            _progressBar2.setIndeterminate(false);
            int percent = (count / total) * 100;
            _progressBar2.setProgress(percent);
            _progressText2.setText(percent + "%");
            //CallThread();
        }
    }

    int count = 0;

    private void UploadAllProducts() {
        _progressBar2.setIndeterminate(false);
        int total = customData.size();


        while (count < total) {

            RegisterProducts registerProducts = new RegisterProducts(getActivity(), customData.get(count));
            registerProducts.RegisterFromExcel(_progressBar2, _progressText2, count);
            count++;
            _progressBar2.setIndeterminate(false);
            int percent = (count / total) * 100;
            _progressBar2.setProgress(percent);
            _progressText2.setText(percent + "%");
        }
        tvStatus.setText("Data uploading done.");
    }

    private void UploadAllProducts2() {
        int total = customData.size();


        while (count < total) {
            new UploadProductsAsync().execute();
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btnFile) {
            GetExcelFile();
        }
        if (id == R.id.ivSettings) {
            cardView1.setVisibility(View.GONE);
            cardView2.setVisibility(View.VISIBLE);
            PreviewSettings();
        }
        if (id == R.id.btnDone) {
            SaveSettings();
        }
        if (id == R.id.action_back) {
            flipView.flipTheView(true);
            flipView.setFlipDuration(1000);
        }
    }

    private class UploadProductsAsync extends AsyncTask<Void, Void, Void> {
        int total = customData.size();

        @Override
        protected Void doInBackground(Void... params) {
            RegisterProducts registerProducts = new RegisterProducts(getActivity(), customData.get(count));
            registerProducts.RegisterFromExcel(_progressBar2, _progressText2, count);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    count++;
                    _progressBar2.setIndeterminate(false);
                    int percent = (count / total) * 100;
                    _progressBar2.setProgress(percent);
                    _progressText2.setText(percent + "%");
                }
            });
        }
    }
}
