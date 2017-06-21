package com.kys.kyspartners.Activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v13.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapCircleThumbnail;
import com.borax12.materialdaterangepicker.time.RadialPickerLayout;
import com.borax12.materialdaterangepicker.time.TimePickerDialog;
import com.github.bijoysingh.starter.util.PermissionManager;
import com.github.jorgecastilloprz.FABProgressCircle;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.kys.kyspartners.ApiConfig;
import com.kys.kyspartners.AppConfig;
import com.kys.kyspartners.Database.AppData;
import com.kys.kyspartners.General;
import com.kys.kyspartners.Information.Shop;
import com.kys.kyspartners.Information.User;
import com.kys.kyspartners.R;
import com.kys.kyspartners.ServerResponse;
import com.kys.kyspartners.network.GetLocationFromServer;
import com.kys.kyspartners.network.RegisterShop;

import java.io.File;
import java.util.Calendar;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class OwnerActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, com.kys.kyspartners.Callbacks.LocationCallback, TimePickerDialog.OnTimeSetListener {

    AppData data;
    General general;
    private static final int MY_PERMISSION_REQUEST_CODE = 7171;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 7172;
    private static final int RESULT_LOAD_IMG = 1960;
    private boolean mRequestingLocationUpdates = false;
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private LocationManager mLocationManager;

    private static int UPDATE_INTERVAL = 5000;
    private static int FATEST_INTERVAL = 3000;
    private static int DISPLACEMENT = 10; //METERS

    boolean isGPSEnabled = false;

    BootstrapCircleThumbnail logo;
    EditText editName, editDesc, editFull, editCity, editArea, editStrt, editNumber, editFrom, editTo;
    FABProgressCircle fabProgressCircle;
    FloatingActionButton floatingActionButton;
    BootstrapButton button;
    String imagePath = "";
    String imageUrl = "";
    String imgDecodableString = "";
    String name = "", desc = "", full_add = "", city = "", area = "", inside_area = "", phone_number = "", open = "", close = "";
    Shop myShop = null;
    User user;

    private boolean CheckFields() {
        if (name.isEmpty() || desc.isEmpty() || full_add.isEmpty() || city.isEmpty() || area.isEmpty() || inside_area.isEmpty()
                || phone_number.isEmpty() || open.isEmpty() || close.isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    private void InitializeViews() {
        logo = (BootstrapCircleThumbnail) findViewById(R.id.shopLogo);
        editName = (EditText) findViewById(R.id.shopName);
        editDesc = (EditText) findViewById(R.id.shopDesc);
        editFull = (EditText) findViewById(R.id.shopFull);
        editCity = (EditText) findViewById(R.id.shopCity);
        editArea = (EditText) findViewById(R.id.shopArea);
        editStrt = (EditText) findViewById(R.id.shopStreet);
        editNumber = (EditText) findViewById(R.id.shopNumber);
        editFrom = (EditText) findViewById(R.id.shopOpen);
        editTo = (EditText) findViewById(R.id.shopClose);
        fabProgressCircle = (FABProgressCircle) findViewById(R.id.fabProgressCircle);
        floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
        button = (BootstrapButton) findViewById(R.id.btnTimes);

    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (checkPlayServices())
                        buildGoogleApiClient();
                }
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner);

        data = new AppData(OwnerActivity.this);
        general = new General(OwnerActivity.this);
        user = data.getUser();
        InitializeViews();
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        isGPSEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!data.getShopAdded())
            LocationSettingUp();

    }

    public void SelectLogo(View view) {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            // When an Image is picked
            if (requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK
                    && null != data) {
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
                logo.setImageBitmap(BitmapFactory.decodeFile(imgDecodableString));

            } else {
                Toast.makeText(this, "You haven't picked Image",
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void DisplayTimeDialog(View view) {
        Calendar now = Calendar.getInstance();
        TimePickerDialog tpd = TimePickerDialog.newInstance(
                OwnerActivity.this,
                now.get(Calendar.HOUR_OF_DAY),
                now.get(Calendar.MINUTE),
                false
        );
        tpd.setEndTime(Calendar.HOUR_OF_DAY, Calendar.MINUTE);

        tpd.setAccentColor(getResources().getColor(R.color.colorAccent));
        tpd.setThemeDark(true);
        tpd.setTabIndicators("Open", "Close");
        tpd.setTitle("Shop Period");
        tpd.show(getFragmentManager(), "Timepickerdialog");
    }

    public void UploadShop(View view) {
        name = editName.getText().toString();
        desc = editDesc.getText().toString();
        full_add = editFull.getText().toString();
        city = editCity.getText().toString();
        area = editArea.getText().toString();
        inside_area = editStrt.getText().toString();
        phone_number = editNumber.getText().toString();
        open = editFrom.getText().toString();
        close = editTo.getText().toString();
        if (!CheckFields()) {
            general.error("Please all fields must be filled.");
            return;
        }
        if (!imgDecodableString.isEmpty()) {
            imagePath = General.CopyTo(imgDecodableString, name.replace(" ", "_"));
            imageUrl = imagePath.substring(imagePath.lastIndexOf("/") + 1);
        } else {
            imageUrl = "no_logo.jpeg";
            view.setTag("register");
        }
        myShop = new Shop(user.id, name, desc, imageUrl, full_add, city, area, inside_area, phone_number, open, close);
        String tag = view.getTag().toString();
        if (tag.contentEquals("upload")) {
            floatingActionButton.setEnabled(false);
            Upload_one(imagePath);
        } else {
            floatingActionButton.setEnabled(false);
            RegisterShop registerShop = new RegisterShop(OwnerActivity.this, myShop);
            registerShop.Register(fabProgressCircle, floatingActionButton, OwnerActivity.this);
        }

    }

    private void LocationSettingUp() {
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        isGPSEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (Build.VERSION.SDK_INT > 19) {
            RequestPermissions();
        }

        if (!isGPSEnabled) {
            new MaterialDialog.Builder(OwnerActivity.this)
                    .title("GPS")
                    .content("Please enable your GPS")
                    .cancelable(false)
                    .canceledOnTouchOutside(false)
                    .negativeText("OK")
                    .positiveText("Open Settings")
                    .onNegative(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            displayLocation();
                        }
                    })
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(intent);
                        }
                    }).show();
            return;
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, MY_PERMISSION_REQUEST_CODE);
        } else {
            if (checkPlayServices()) {
                buildGoogleApiClient();
                createLocationRequest();
                displayLocation();
            }
        }
    }

    private void displayLocation() {
        general.displayDialog("Getting your location...");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            }, MY_PERMISSION_REQUEST_CODE);
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        double latitude = 0, longitude = 0;
        if (mLastLocation != null) {
            latitude = mLastLocation.getLatitude();
            longitude = mLastLocation.getLongitude();
            data.setLatLng(String.valueOf(latitude), String.valueOf(longitude));
            GetLocationFromServer getLocationFromServer = new GetLocationFromServer(OwnerActivity.this, latitude, longitude, this);
            getLocationFromServer.Locate();
            general.dismissDialog();
            Log.e("Location Update", "LatLng = " + latitude + " / " + longitude);
            if (latitude == 0 && longitude == 0) {
                general.dismissDialog();
                TryAgain();
            }
        }
    }

    private void RequestPermissions() {
        String[] permissions = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.CALL_PHONE, Manifest.permission.READ_PHONE_STATE};
        PermissionManager permissionManager = new PermissionManager(OwnerActivity.this, permissions);
        if (!permissionManager.hasAllPermissions()) {
            permissionManager.requestPermissions(MY_PERMISSION_REQUEST_CODE);
        }
    }

    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FATEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);

    }

    private synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
        mGoogleApiClient.connect();

    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(OwnerActivity.this, "This device is not supported", Toast.LENGTH_SHORT).show();
            }
            return false;
        }
        return true;
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && android.support.v4.app.ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    private void TryAgain() {
        new MaterialDialog.Builder(OwnerActivity.this)
                .title("Location")
                .cancelable(false)
                .canceledOnTouchOutside(false)
                .content("Your location is not available, please try again.")
                .positiveText("Try Again")
                .negativeText("Cancel")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        displayLocation();
                    }
                })
                .show();
    }


    @Override
    protected void onStart() {
        super.onStart();
        if (data.getShopAdded()) {
            startActivity(new Intent(OwnerActivity.this, AddProductActivity.class));
            finish();
        } else {
            if (mGoogleApiClient != null) {
                //mGoogleApiClient.connect();
            }
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mGoogleApiClient != null) {
            //later
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_owner, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_refresh_loc) {
            LocationSettingUp();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        if (mGoogleApiClient != null) {
            if (mGoogleApiClient.isConnected()) {
                LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
                mGoogleApiClient.disconnect();
            }
        }
        super.onStop();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        displayLocation();
        if (mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        if (mGoogleApiClient != null)
            mGoogleApiClient.disconnect();///get back to u
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        displayLocation();
    }

    public void Upload_one(final String path1) {
        fabProgressCircle.show();
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
                        RegisterShop registerShop = new RegisterShop(OwnerActivity.this, myShop);
                        registerShop.Register(fabProgressCircle, floatingActionButton, OwnerActivity.this);
                        Log.e("tonSuccess", serverResponse.getMessage());
                        //Toast.makeText(context, serverResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    } else {
                        Log.e("tonFailure", serverResponse.getMessage());
                        fabProgressCircle.hide();
                        floatingActionButton.setEnabled(true);
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
                fabProgressCircle.hide();
                floatingActionButton.setEnabled(true);
                general.error("An error occurred. Check your internet connection.");
            }
        });
    }

    private void OnDismissMaterialDialog() {
        general.getMaterialDialog().setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                stopGoogle();
            }
        });
    }

    private void stopGoogle() {
        if (mGoogleApiClient != null) {
            if (mGoogleApiClient.isConnected()) {
                LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
                mGoogleApiClient.disconnect();
            }
        }
    }

    @Override
    public void setLocation(String city, String area, String inside_area, String formatted_address) {
        if (city.isEmpty()) {
            general.dismissDialog();
            TryAgain();
            return;
        }
        general.dismissDialog();
        //data.setLocation(city, area, inside_area, formatted_address);
        //data.setLocationEnter(true);
        editFull.setText(formatted_address);
        editCity.setText(city);
        editArea.setText(area);
        editStrt.setText(inside_area);
        general.dismissDialog();
        OnDismissMaterialDialog();
    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int hourOfDayEnd, int minuteEnd) {
        String am_pm_open = "AM";
        String am_pm_close = "AM";
        if (hourOfDay > 12) {
            am_pm_open = "PM";
            hourOfDay = (hourOfDay - 12);
        }
        if (hourOfDayEnd > 12) {
            am_pm_close = "PM";
            hourOfDayEnd = (hourOfDayEnd - 12);
        }
        if (hourOfDay == 12) {
            am_pm_open = "PM";
        }
        if (hourOfDayEnd == 12) {
            am_pm_close = "PM";
        }
        if (hourOfDay == 0) {
            hourOfDay = 12;
            am_pm_open = "AM";
        }
        if (hourOfDayEnd == 0) {
            hourOfDayEnd = 12;
            am_pm_close = "AM";
        }
        String hourString = hourOfDay < 10 ? "0" + hourOfDay : "" + hourOfDay;
        String minuteString = minute < 10 ? "0" + minute : "" + minute;

        String hourStringEnd = hourOfDayEnd < 10 ? "0" + hourOfDayEnd : "" + hourOfDayEnd;
        String minuteStringEnd = minuteEnd < 10 ? "0" + minuteEnd : "" + minuteEnd;

        editFrom.setText(hourString + ":" + minuteString + " " + am_pm_open);
        editTo.setText(hourStringEnd + ":" + minuteStringEnd + " " + am_pm_close);
    }
}
