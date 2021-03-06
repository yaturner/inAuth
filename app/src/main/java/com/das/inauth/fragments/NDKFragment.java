package com.das.inauth.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.UserHandle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.das.inauth.MainActivity;
import com.das.inauth.R;

import java.lang.reflect.Method;
import java.util.ArrayList;

/**
 * Created by yaturner on 5/10/2017.
 */

public class NDKFragment extends Fragment
        implements AdapterView.OnItemClickListener {
  private final String TAG = NDKFragment.class.getSimpleName();

  private NDKFragment fragment = null;

  private Bundle bundle = null;
  private MainActivity mainActivity = MainActivity.getInstance();
  private PackageManager packageManager = null;

  private View view = null;
  private TextView textView = null;

  private ArrayList<ResolveInfo> pkgAppsList = null;
  private ArrayList<String> pkgStatsList = null;
  private ArrayList<String> appNameList = null;
  private StringBuilder ssb = null;

  private double cachesize;
  private double datasize;
  private double codesize;
  private double totalsize;

  private String result = null;

  public static NDKFragment newInstance(Bundle bundle) {
    NDKFragment fragment = new NDKFragment();
    fragment.fragment = fragment;
    fragment.setArguments(bundle);
    return fragment;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    bundle = getArguments();
    packageManager = getActivity().getPackageManager();
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    view = inflater.inflate(R.layout.ndk_fragment, container, false);
    textView = (TextView) view.findViewById(R.id.textview);

    final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
    mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);

    pkgAppsList = (ArrayList<ResolveInfo>) getActivity().getPackageManager().queryIntentActivities(mainIntent, 0);
    pkgStatsList = new ArrayList<>(pkgAppsList.size());
    appNameList = new ArrayList<>();
    ssb = new StringBuilder();

    Log.d(TAG, "pkgAppsList size = " + pkgAppsList.size());

    int index = 0;
    for (ResolveInfo resolveInfo : pkgAppsList) {
      ApplicationInfo ai = resolveInfo.activityInfo.applicationInfo;
      String applicationName = packageManager.getApplicationLabel(ai).toString();
      appNameList.add(applicationName);
      String packageName = pkgAppsList.get(index).activityInfo.packageName;
      PackageStats packageStats = new PackageStats(packageName);
      if (!getPackageSizeInfo(packageName)) {
        break;
      }
      index++;
    }




    return view;
  }

  private boolean getPackageSizeInfo(final String packageName) {
    try {

      Class<?> clz = packageManager.getClass();
      if (Build.VERSION.SDK_INT > 16) {
        Method myUserId = UserHandle.class.getDeclaredMethod("myUserId");//ignore check this when u set ur min SDK < 17
        int userID = (Integer) myUserId.invoke(packageManager);
        Method getPackageSizeInfo = clz.getDeclaredMethod(
                "getPackageSizeInfo", String.class, int.class,
                IPackageStatsObserver.class);//remember add int.class into the params
        getPackageSizeInfo.invoke(packageManager, packageName, userID, new PkgSizeObserver());
      } else {//for old API
        Method getPackageSizeInfo = clz.getDeclaredMethod(
                "getPackageSizeInfo", String.class,
                IPackageStatsObserver.class);
        getPackageSizeInfo.invoke(packageManager, packageName, new PkgSizeObserver());
      }
    } catch (Exception ex) {
      Log.e(TAG, "NoSuchMethodException");
      ex.printStackTrace();
      AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
      builder.setTitle(R.string.alert)
              .setMessage(R.string.not_available)
              .setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                  dialogInterface.dismiss();
                  mainActivity.onBackPressed();
                }
              }).show();
      return false;
    }
    return true;
  }

  @Override
  public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

  }

  private class PkgSizeObserver extends IPackageStatsObserver.Stub {
    /***
     * @param pStats
     * @param succeeded
     */
    @Override
    public void onGetStatsCompleted(PackageStats pStats, boolean succeeded)
            throws RemoteException {
      if (succeeded) {
        cachesize = pStats.cacheSize;//remember to declare these fields
        datasize = pStats.dataSize;
        codesize = pStats.codeSize;
        totalsize = cachesize + datasize + codesize;
        Log.i(TAG, "cachesize--->" + cachesize + " datasize---->"
                + datasize + " codeSize---->" + codesize);
        ssb = new StringBuilder();
        ssb.append(",");
        ssb.append("" + pStats.packageName);
        ssb.append(",");
        ssb.append("" + codesize);
        ssb.append(",");
        ssb.append("" + datasize);
        ssb.append(",");
        ssb.append("" + cachesize);
        ssb.append("\n");
        pkgStatsList.add(ssb.toString());

        if (pkgStatsList.size() == appNameList.size()) {
          result = mainActivity.processString(appNameList, pkgStatsList);
          mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
              textView.setText(result);
              view.invalidate();
            }
          });
        }
      }
    }
  }
}
