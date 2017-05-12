package com.das.inauth;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import com.das.inauth.constants.Constants;
import com.das.inauth.fragments.MainMenuFragment;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import im.delight.android.location.SimpleLocation;

public class MainActivity extends AppCompatActivity {

  private static MainActivity _instance = null;

  // Used to load the 'native-lib' library on application startup.
  static {
    System.loadLibrary("native-lib");
  }

  private FragmentManager fragmentManager = null;
  private Bundle bundle = null;
  private boolean locationEnabled = false;
  private LatLng currentLocation = null;
  private SimpleLocation location;

  private ProgressDialog progressDialog = null;

  public static MainActivity getInstance() {
    return _instance;
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    _instance = this;
    setContentView(R.layout.activity_main);

    if (!isNetworkAvailable()) {
      AlertDialog.Builder builder = new AlertDialog.Builder(this);
      builder.setMessage(getString(R.string.no_network))
              .setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                  dialog.dismiss();
                  MainActivity.this.finish();
                }
              })
              .show();
      return;
    }

    progressDialog = new ProgressDialog(this);

    // Application permissioms
    if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
      // Should we show an explanation?
      if (ActivityCompat.shouldShowRequestPermissionRationale(this,
              android.Manifest.permission.ACCESS_FINE_LOCATION)) {
      } else {
        ActivityCompat.requestPermissions(this,
                new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                Constants.MY_PERMISSIONS_ACCESS_FINE_LOCATION);
      }
    } else {
      locationEnabled = true;
    }
    // construct a new instance of SimpleLocation
    location = new SimpleLocation(this);

    // if we can't access the location yet
    if (!location.hasLocationEnabled()) {
      // ask the user to enable location access
      SimpleLocation.openSettings(this);
    }

    fragmentManager = getSupportFragmentManager();
    bundle = new Bundle();
    MainMenuFragment mainMenuFragment = MainMenuFragment.newInstance(bundle);
    fragmentManager.beginTransaction()
            .replace(R.id.fragment_container, mainMenuFragment, MainMenuFragment.class.getSimpleName())
            .addToBackStack(MainMenuFragment.class.getSimpleName())
            .commit();


  }

  @Override
  public void onRequestPermissionsResult(int requestCode,
                                         String permissions[], int[] grantResults) {
    switch (requestCode) {
      case Constants.MY_PERMISSIONS_ACCESS_COURSE_LOCATION:
      case Constants.MY_PERMISSIONS_ACCESS_FINE_LOCATION: {
        // If request is cancelled, the result arrays are empty.
        if (grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
          locationEnabled = true;

        } else {
          locationEnabled = false;
        }
        return;
      }
    }
  }

  /**
   * A native method that is implemented by the 'native-lib' native library,
   * which is packaged with this application.
   */
  public native String stringFromJNI(String target);

  public String processString(ArrayList<String> appNameList, ArrayList<String> pkgStatsList) {
    if (appNameList.size() != pkgStatsList.size()) {
      return null;
    }
    StringBuilder ssb = new StringBuilder();
    for (int index = 0; index < appNameList.size(); index++) {
      ssb.append(appNameList.get(index));
      ssb.append(pkgStatsList.get(index));
    }
    return stringFromJNI(ssb.toString());
  }

  public LatLng getCurrentLocation() {
    if (locationEnabled) {
      return new LatLng(location.getLatitude(), location.getLongitude());
    } else {
      return null;
    }
  }

  public boolean isLocationEnabled() {
    return locationEnabled;
  }

  public boolean isNetworkAvailable() {
    ConnectivityManager connectivityManager
            = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();


    return activeNetworkInfo != null && activeNetworkInfo.isConnected();
  }

  public void showProgressDialog(final String str) {
    if (!progressDialog.isShowing()) {
      progressDialog.setMessage(str);
      progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
      progressDialog.setIndeterminate(true);
      progressDialog.setCancelable(false);
      progressDialog.setCanceledOnTouchOutside(false);
      progressDialog.show();
    }
  }

  public void dismissProgressDialog() {
    if (progressDialog.isShowing()) {
      progressDialog.dismiss();
    }
  }

}
