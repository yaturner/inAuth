package com.das.inauth.fragments;

import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.das.inauth.MainActivity;
import com.das.inauth.R;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by yaturner on 5/10/2017.
 */

public class GPSFragment extends Fragment {
  private GPSFragment fragment = null;

  private Bundle bundle = null;
  private MainActivity mainActivity = MainActivity.getInstance();

  private View view = null;
  private TextView textView = null;

  public static GPSFragment newInstance(Bundle bundle) {
    GPSFragment fragment = new GPSFragment();
    fragment.fragment = fragment;
    fragment.setArguments(bundle);
    return fragment;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    bundle = getArguments();
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    view = inflater.inflate(R.layout.gps_fragment, container, false);
    textView = (TextView) view.findViewById(R.id.text);
    LatLng goldengateLoc = new LatLng(37.8199, -122.4783);
    LatLng here = mainActivity.getCurrentLocation();

    float results[] = new float[3];
    Location.distanceBetween((double) here.latitude, (double) here.longitude,
            (double) goldengateLoc.latitude, (double) goldengateLoc.longitude, results);
    float dist = (results[0] / 1000) * .621371f;

    SpannableStringBuilder ssb = new SpannableStringBuilder();
    int len = 0;

    ssb.append(getString(R.string.gps_loc1));
    len = ssb.length();
    ssb.append(" " + String.valueOf(here.latitude));
    ssb.setSpan(new StyleSpan(android.graphics.Typeface.ITALIC), len, ssb.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    ssb.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), len, ssb.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    ssb.append(" " + getString(R.string.gps_loc2));
    len = ssb.length();
    ssb.append(" " + String.valueOf(here.longitude));
    ssb.setSpan(new StyleSpan(android.graphics.Typeface.ITALIC), len, ssb.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    ssb.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), len, ssb.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    ssb.append("\n\n");
    ssb.append(getText(R.string.gps_part1) + " ");
    len = ssb.length();
    ssb.append(String.valueOf(dist));
    ssb.setSpan(new ForegroundColorSpan(Color.WHITE), len, ssb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    ssb.append(" " + getText(R.string.gps_part2));
    len = ssb.length();
    ssb.append(" " + getString(R.string.gps_part3));
    ssb.setSpan(new ForegroundColorSpan(Color.rgb(255, 215, 0)), len, ssb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    ssb.append(" " + getText(R.string.gps_part4));

    textView.setText(ssb);


    return view;
  }
}
