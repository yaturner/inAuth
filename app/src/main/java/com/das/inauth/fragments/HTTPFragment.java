package com.das.inauth.fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.das.inauth.MainActivity;
import com.das.inauth.R;
import com.das.inauth.constants.Constants;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by yaturner on 5/10/2017.
 */

public class HTTPFragment extends Fragment {
  private final static String TAG = HTTPFragment.class.getSimpleName();

  private HTTPFragment fragment = null;

  private MainActivity mainActivity = MainActivity.getInstance();

  private Bundle bundle = null;

  private View view = null;
  private TextView jsonTextView = null;

  public static HTTPFragment newInstance(Bundle bundle) {
    HTTPFragment fragment = new HTTPFragment();
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
    view = inflater.inflate(R.layout.http_fragment, container, false);
    jsonTextView = (TextView) view.findViewById(R.id.json_textview);

    mainActivity.showProgressDialog(getString(R.string.loading_data));

    String uri = Constants.TEST_URI.replace("%s", Constants.ACCOUNT_API_KEY);
    Ion.with(getActivity())
            .load(uri)
            .setLogging(TAG, Log.DEBUG)
            .asString()
            .setCallback(new FutureCallback<String>() {
              @Override
              public void onCompleted(Exception e, String result) {
                Log.d(TAG, "result = " + result);
                if (result != null && !result.isEmpty()) {
                  mainActivity.dismissProgressDialog();
                  try {
                    String str = new JSONObject(result).toString(2);
                    jsonTextView.setText(str);
                  } catch (JSONException e1) {
                    e1.printStackTrace();
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle(R.string.alert)
                            .setMessage(R.string.http_error + "\n" + e.getLocalizedMessage())
                            .setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
                              @Override
                              public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                MainActivity.getInstance().onBackPressed();
                              }
                            }).show();
                  }

                }
              }
            });

    return view;
  }
}
