package com.kys.kyspartners.Fragments;

import android.app.Fragment;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.beardedhen.androidbootstrap.BootstrapCircleThumbnail;
import com.dd.processbutton.iml.ActionProcessButton;
import com.kys.kyspartners.Activity.AddProductActivity;
import com.kys.kyspartners.Activity.EditProductActivity;
import com.kys.kyspartners.Activity.OwnerActivity;
import com.kys.kyspartners.ApiConfig;
import com.kys.kyspartners.AppConfig;
import com.kys.kyspartners.Callbacks.LogTypeCallback;
import com.kys.kyspartners.Database.AppData;
import com.kys.kyspartners.General;
import com.kys.kyspartners.Information.Products;
import com.kys.kyspartners.Information.Shop;
import com.kys.kyspartners.R;
import com.kys.kyspartners.ServerResponse;
import com.kys.kyspartners.Utility.Separation;
import com.kys.kyspartners.network.GetLogType;
import com.kys.kyspartners.network.RegisterProducts;
import com.kys.kyspartners.network.RegisterShop;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import nl.dionsegijn.steppertouch.StepperTouch;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import pl.polak.clicknumberpicker.ClickNumberPickerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;

/**
 * Created by sanniAdewale on 11/05/2017.
 */

public class ManualFragmentTab extends Fragment implements View.OnClickListener, View.OnLongClickListener, LogTypeCallback {

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
    String shop_name = "", product_category = "", product_name = "", product_price = "", product_description = "", inStock = "", type = "manual";
    String imagePath = "";
    String imageUrl = "";
    String imgDecodableString = "";
    int unique_value = 0;
    EditText[] editTexts;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_manual, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        data = new AppData(getActivity());
        general = new General(getActivity());
        Shop shop = data.getShop();
        shop_name = shop.name;

        imageView = (ImageView) view.findViewById(R.id.btnRefresh);
        logo = (BootstrapCircleThumbnail) view.findViewById(R.id.productLogo);
        editName = (EditText) view.findViewById(R.id.productName);
        editDesc = (EditText) view.findViewById(R.id.productDesc);
        editCategory = (AutoCompleteTextView) view.findViewById(R.id.productCategory);
        editPrice = (EditText) view.findViewById(R.id.productPrice);
        stepperTouch = (ClickNumberPickerView) view.findViewById(R.id.productStock);
        actionProcessButton = (ActionProcessButton) view.findViewById(R.id.btnUpload);

        actionProcessButton.setMode(ActionProcessButton.Mode.ENDLESS);
        actionProcessButton.setOnClickListener(this);
        logo.setOnClickListener(this);
        stepperTouch.setOnLongClickListener(this);
        imageView.setOnClickListener(this);
        editCategory.setThreshold(1);
        editTexts = new EditText[]{editName, editDesc, editPrice};
        GetTypeForChart();
    }

    private void UploadProduct() {
        product_category = editCategory.getText().toString();
        product_name = editName.getText().toString();
        product_description = editDesc.getText().toString();
        product_price = editPrice.getText().toString();
        inStock = String.valueOf(stepperTouch.getValue());

        int value = new Random().nextInt(9999999);
        unique_value = value + General.AddNumber();

        if (!CheckFields()) {
            general.error("Please all fields must be filled.");
            return;
        }
        if (!imgDecodableString.isEmpty()) {
            imagePath = General.CopyTo(imgDecodableString, product_name.replace(" ", "_"));
            imageUrl = imagePath.substring(imagePath.lastIndexOf("/") + 1);
        } else {
            imageUrl = "no_logo.jpeg";
            actionProcessButton.setTag("register");
        }
        myProduct = new Products(0, shop_name, product_category, product_name, product_price, product_description, imageUrl, inStock, type, unique_value);
        String tag = actionProcessButton.getTag().toString();
        if (tag.contentEquals("upload")) {
            actionProcessButton.setEnabled(false);
            Upload_one(imagePath);
        } else {
            actionProcessButton.setEnabled(false);
            RegisterProducts registerProducts = new RegisterProducts(getActivity(), myProduct);
            registerProducts.Register(actionProcessButton, editTexts, editCategory);
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            // When an Image is picked
            if (requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK && null != data) {
                // Get the Image from data

                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};

                // Get the cursor
                Cursor cursor = getActivity().getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                // Move to first row
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                imgDecodableString = cursor.getString(columnIndex);
                cursor.close();
                logo.setImageBitmap(BitmapFactory.decodeFile(imgDecodableString));

            } else {
                Toast.makeText(getActivity(), "You haven't picked Image", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
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
                        RegisterProducts registerProducts = new RegisterProducts(getActivity(), myProduct);
                        registerProducts.Register(actionProcessButton, editTexts, editCategory);
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

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btnUpload) {
            UploadProduct();
        }
        if (id == R.id.productLogo) {
            GetLogo();
        }
        if (id == R.id.btnRefresh) {
            imageView.setVisibility(View.GONE);
            GetTypeForChart();
        }
    }

    int dialogValue = 1;

    @Override
    public boolean onLongClick(View v) {

        new MaterialDialog.Builder(getActivity())
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

    private void GetTypeForChart() {
        GetLogType getLogType = new GetLogType(getActivity(), this);
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
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, cats);
            editCategory.setAdapter(adapter);
            Toast.makeText(getActivity(), "Categories loaded", Toast.LENGTH_SHORT).show();
        }
    }
}
