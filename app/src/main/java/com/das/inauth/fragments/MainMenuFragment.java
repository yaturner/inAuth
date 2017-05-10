package com.das.inauth.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.das.inauth.R;

/**
 * Created by yaturner on 5/10/2017.
 */

public class MainMenuFragment extends Fragment
        implements View.OnClickListener {
  private final static String TAG = MainMenuFragment.class.getSimpleName();

  private MainMenuFragment fragment = null;
  private FragmentManager fragmentManager = null;

  private Bundle bundle = null;

  private View view = null;
  private Button button1 = null;
  private Button button2 = null;
  private Button button3 = null;


  public static MainMenuFragment newInstance(Bundle bundle) {
    MainMenuFragment fragment = new MainMenuFragment();
    fragment.fragment = fragment;
    fragment.setArguments(bundle);
    return fragment;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    bundle = getArguments();
    fragmentManager = getFragmentManager();
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    view = inflater.inflate(R.layout.mainmenu_fragment, container, false);

    button1 = (Button) view.findViewById(R.id.button1);
    button2 = (Button) view.findViewById(R.id.button2);
    button3 = (Button) view.findViewById(R.id.button3);

    button1.setOnClickListener(this);
    button2.setOnClickListener(this);
    button3.setOnClickListener(this);

    return view;
  }

  @Override
  public void onClick(View view) {
    int vId = view.getId();
    Bundle bundle = new Bundle();
    if (vId == R.id.button1) {
      HTTPFragment httpFragment = HTTPFragment.newInstance(bundle);
      fragmentManager.beginTransaction()
              .replace(R.id.fragment_container, httpFragment, HTTPFragment.class.getSimpleName())
              .addToBackStack(HTTPFragment.class.getSimpleName())
              .commit();
    } else if (vId == R.id.button2) {
      GPSFragment gpsFragment = GPSFragment.newInstance(bundle);
      fragmentManager.beginTransaction()
              .replace(R.id.fragment_container, gpsFragment, GPSFragment.class.getSimpleName())
              .addToBackStack(GPSFragment.class.getSimpleName())
              .commit();
    }

  }
}