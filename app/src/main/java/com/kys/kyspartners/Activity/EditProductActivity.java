package com.kys.kyspartners.Activity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.beardedhen.androidbootstrap.BootstrapCircleThumbnail;
import com.bumptech.glide.Glide;
import com.dd.processbutton.iml.ActionProcessButton;
import com.kys.kyspartners.ApiConfig;
import com.kys.kyspartners.AppConfig;
import com.kys.kyspartners.Callbacks.LogTypeCallback;
import com.kys.kyspartners.Database.AppData;
import com.kys.kyspartners.General;
import com.kys.kyspartners.Information.Products;
import com.kys.kyspartners.Information.Shop;
import com.kys.kyspartners.Information.User;
import com.kys.kyspartners.R;
import com.kys.kyspartners.ServerResponse;
import com.kys.kyspartners.Utility.Separation;
import com.kys.kyspartners.network.DeleteProduct;
import com.kys.kyspartners.network.GetLogType;
import com.kys.kyspartners.network.RegisterProducts;
import com.kys.kyspartners.network.UpdateProductById;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import pl.polak.clicknumberpicker.ClickNumberPickerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class EditProductActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener, LogTypeCallback {

    private static final int RESULT_LOAD_IMG = 1960;
    BootstrapCircleThumbnail logo;
    EditText editName, editDesc, editPrice;
    AutoCompleteTextView editCategory;
    ClickNumberPickerView stepperTouch;
    ActionProcessButton actionProcessButton;
    ImageView imageView;
    AppData data;
    General general;
    Products myProduct;
    String shop_name = "", product_category = "", product_name = "", product_price = "", product_description = "", inStock = "", type = "";
    String imagePath = "";
    String imageUrl = "";
    String imgDecodableString = "";
    String initial_load = "";
    int id = 0;
    int unique_value = 0;
    String old_product_name, old_category_name;
    String[] values = new String[3];
    String[] c_values = new String[3];
    User user;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        data = new AppData(EditProductActivity.this);
        general = new General(EditProductActivity.this);
        Shop shop = data.getShop();
        shop_name = shop.name;
        user = data.getUser();

        imageView = (ImageView) findViewById(R.id.btnRefresh);
        logo = (BootstrapCircleThumbnail) findViewById(R.id.productLogo);
        editName = (EditText) findViewById(R.id.productName);
        editDesc = (EditText) findViewById(R.id.productDesc);
        editCategory = (AutoCompleteTextView) findViewById(R.id.productCategory);
        editPrice = (EditText) findViewById(R.id.productPrice);
        stepperTouch = (ClickNumberPickerView) findViewById(R.id.productStock);
        actionProcessButton = (ActionProcessButton) findViewById(R.id.btnUpload);

        actionProcessButton.setMode(ActionProcessButton.Mode.ENDLESS);
        actionProcessButton.setOnClickListener(this);
        logo.setOnClickListener(this);
        stepperTouch.setOnLongClickListener(this);
        editCategory.setThreshold(1);

        LoadLayouts();
    }

    private void LoadLayouts() {
        Bundle bundle = getIntent().getExtras();
        id = bundle.getInt("id");
        String _logo = bundle.getString("logo");
        imageUrl = _logo;
        String nm = bundle.getString("name");
        String ds = bundle.getString("desc");
        String cat = bundle.getString("cat");
        String pr = bundle.getString("price");
        String st = bundle.getString("stock");
        type = bundle.getString("type");
        unique_value = bundle.getInt("unique_value");
        initial_load = bundle.getString("load");
        String url = AppConfig.WEB_URL2 + "images/" + _logo;
        String web = url.replace(" ", "%20");
        Glide.with(EditProductActivity.this).load(web).fitCenter().centerCrop().placeholder(R.drawable.no_logo).into(logo);
        editName.setText(nm);
        editDesc.setText(ds);
        editCategory.setText(cat);
        editPrice.setText(pr);
        stepperTouch.setPickerValue(Float.parseFloat(st));
        old_product_name = nm;
        old_category_name = cat;

        GetTypeForChart();
    }

    private void UploadProduct() {
        product_category = editCategory.getText().toString();
        product_name = editName.getText().toString();
        product_description = editDesc.getText().toString();
        product_price = editPrice.getText().toString();
        inStock = String.valueOf(stepperTouch.getValue());
        values[0] = product_name;
        values[1] = old_product_name;
        values[2] = user.id + "";

        c_values[0] = product_category;
        c_values[1] = old_category_name;
        c_values[2] = user.id + "";


        if (!CheckFields()) {
            general.error("Please all fields must be filled.");
            return;
        }
        if (!imgDecodableString.isEmpty()) {
            imagePath = General.CopyTo(imgDecodableString, product_name.replace(" ", "_"));
            imageUrl = imagePath.substring(imagePath.lastIndexOf("/") + 1);
        } else {
            //imageUrl = "no_logo.jpeg";
            actionProcessButton.setTag("register");
        }
        myProduct = new Products(id, shop_name, product_category, product_name, product_price, product_description, imageUrl, inStock, type, unique_value);
        String tag = actionProcessButton.getTag().toString();
        if (tag.contentEquals("upload")) {
            actionProcessButton.setEnabled(false);
            Upload_one(imagePath);
        } else {
            actionProcessButton.setEnabled(false);
            UpdateProduct();
        }
    }

    private boolean CheckFields() {
        if (product_category.isEmpty() || product_name.isEmpty() || product_description.isEmpty() || product_price.isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    private void GetLogo() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            // When an Image is picked
            if (requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK && null != data) {
                // Get the Image from data

                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};

                // Get the cursor
                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                // Move to first row
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                imgDecodableString = cursor.getString(columnIndex);
                cursor.close();
                Log.e("result upload", imgDecodableString);
                logo.setImageBitmap(BitmapFactory.decodeFile(imgDecodableString));

            } else {
                Toast.makeText(EditProductActivity.this, "You haven't picked Image", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Log.e("result exception", e.toString());
        }
    }

    public void Upload_one(final String path1) {
        actionProcessButton.setProgress(1);
        File file = new File(path1);

        // Parsing any Media type file
        RequestBody requestBody1 = RequestBody.create(MediaType.parse("*/*"), file);

        MultipartBody.Part fileToUpload1 = MultipartBody.Part.createFormData("file1", file.getName(), requestBody1);

        ApiConfig getResponse = AppConfig.getRetrofit().create(ApiConfig.class);
        Call<ServerResponse> call = getResponse.uploadDisplayImage(fileToUpload1);
        call.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                ServerResponse serverResponse = response.body();
                if (serverResponse != null) {
                    if (serverResponse.getSuccess()) {
                        UpdateProduct();
                        Log.e("tonSuccess", serverResponse.getMessage());
                        //Toast.makeText(context, serverResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    } else {
                        Log.e("tonFailure", serverResponse.getMessage());
                        actionProcessButton.setProgress(0);
                        actionProcessButton.setEnabled(true);
                        general.error("An error occurred. Check your internet connection.");
                    }
                } else {
                    assert serverResponse != null;
                    Log.e("Response", serverResponse.toString());
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                Log.e("onFailure", t.getMessage());
                actionProcessButton.setProgress(0);
                actionProcessButton.setEnabled(true);
                general.error("An error occurred. Check your internet connection.");
            }
        });
    }

    private void UpdateProduct() {
        UpdateProductById updateProductById = new UpdateProductById(EditProductActivity.this, myProduct);
        updateProductById.UpdateById(actionProcessButton, EditProductActivity.this, c_values, values);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btnUpload) {
            UploadProduct();
        }
        if (id == R.id.productLogo) {
            GetLogo();
        }
    }

    int dialogValue = 1;

    @Override
    public boolean onLongClick(View v) {
        new MaterialDialog.Builder(EditProductActivity.this)
                .title("Stock Value")
                .inputType(InputType.TYPE_CLASS_NUMBER)
                .input("Enter stock value for product", "", false, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                        dialogValue = Integer.parseInt(String.valueOf(input));
                    }
                })
                .negativeText("Cancel")
                .positiveText("Ok")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        if (dialogValue == 0) {
                            general.error("Stock value cannot be 0.");
                            //dialog.dismiss();
                        } else {
                            stepperTouch.setPickerValue(dialogValue);
                            dialog.dismiss();
                        }
                    }
                })
                .show();
        return true;
    }

    private void DeleteProduct() {
        product_name = editName.getText().toString();
        new MaterialDialog.Builder(this)
                .title("Delete Product")
                .content("Delete product " + product_name + " permanently?")
                .negativeText("Cancel")
                .positiveText("Yes")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                        DeleteProduct deleteProduct = new DeleteProduct(EditProductActivity.this);
                        deleteProduct.Delete(unique_value, EditProductActivity.this);
                    }
                }).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_product, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        if (id == R.id.action_delete) {
            DeleteProduct();
        }
        return super.onOptionsItemSelected(item);
    }

    private void GetTypeForChart() {
        GetLogType getLogType = new GetLogType(EditProductActivity.this, this);
        getLogType.getShopLogByType("Category", null);
    }

    @Override
    public void onLogType(ArrayList<String> values) {
        if (!values.isEmpty()) {
            ArrayList<String> cats = new ArrayList<>();
            Map<String, Integer> _value = Separation.ReturnedData(values);
            Set<String> keys = _value.keySet();
            for (String st : keys) {
                cats.add(st);
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<>(EditProductActivity.this, android.R.layout.simple_list_item_1, cats);
            editCategory.setAdapter(adapter);
            Toast.makeText(EditProductActivity.this, "Categories loaded", Toast.LENGTH_SHORT).show();
        }

    }

    public void RefreshCategory(View view) {
        imageView.setVisibility(View.GONE);
        GetTypeForChart();
    }
}
