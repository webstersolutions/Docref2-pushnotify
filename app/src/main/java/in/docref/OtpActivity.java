package in.docref;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessaging;

import in.docref.R;
import in.docref.app.Config;
import in.docref.util.NotificationUtils;

/**
 * Created by alfiasorte on 10-07-2017.
 */


public class OtpActivity extends AppCompatActivity {

    public static final String TAG = OtpActivity.class
            .getSimpleName();

    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;
    private static OtpActivity mInstance;

    // UI references.
    private EditText mOtpNumber;
    private View mProgressView;
    private View mMobileLoginFormView;

    private BroadcastReceiver mRegistrationBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);

        ImageButton mSendButton = (ImageButton) findViewById(R.id.sendButton);
        mSendButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                sendLogin();

            }
        });

        Button mReSendButton = (Button) findViewById(R.id.resendOtp);
        mReSendButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                resendOtp();

            }
        });

        mMobileLoginFormView = findViewById(R.id.mobile_otp_form);
        mProgressView = findViewById(R.id.otp_progress);
    }

     /*
    volley functions
     */

    public static synchronized OtpActivity getInstance() {
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }

    public ImageLoader getImageLoader() {
        getRequestQueue();
        if (mImageLoader == null) {
            mImageLoader = new ImageLoader(this.mRequestQueue,
                    new LruBitmapCache());
        }
        return this.mImageLoader;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }
    /*
    volley functions end
     */


    private void sendLogin() {

        // Show a progress spinner, and kick off a background task to
        // perform the user login attempt.
        showProgress(true);
        //loader
        ProgressDialog loading = null;

        loading = new ProgressDialog(this);
        loading.setCancelable(false);
        loading.setMessage("Verifying OTP");
        loading.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        loading.show();

        mOtpNumber = (EditText) findViewById(R.id.enter_otp);
        String otpNumber= mOtpNumber.getText().toString();

        final AlertDialog.Builder alertDialogBuilder123 = new AlertDialog.Builder(this);



        final String mobileNumber=getIntent().getStringExtra("PASS_MOBILE");
        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
        final String regId = pref.getString("regId", null);

        RequestQueue queue = Volley.newRequestQueue(this);  // this = context


        final String url = "http://docref.in/api/doctors/sign_on.php?mobile_number=" + mobileNumber + "&fcm_key=" + regId;

// prepare the Request
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        // display response
                        Log.d("Response", response.toString());

                        String jsonStr=response.toString();
                        if (jsonStr != null) {
                            try {
                                JSONObject jsonObj = new JSONObject(jsonStr);

                                String json_response = jsonObj.getString("response");

                                String response_status = "true";
                                if(response_status.equals(json_response)) {

                                    Intent intent_home = new Intent(OtpActivity.this, UserProfile.class);
                                    intent_home.putExtra("PASS_MOBILE", mobileNumber);
                                    startActivity(intent_home);
                                }
                                else{
                                    alertDialogBuilder123.setMessage("Something Wents Wrong");
                                    AlertDialog alertDialog = alertDialogBuilder123.create();
                                    alertDialog.show();
                                }

                            } catch (final JSONException e) {
                                Log.e(TAG, "Json parsing error: " + e.getMessage());
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getApplicationContext(),
                                                "Json parsing error: " + e.getMessage(),
                                                Toast.LENGTH_LONG)
                                                .show();
                                    }
                                });

                            }
                        }

                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Error.Response", error.toString());
                    }
                }
        );

        // add it to the RequestQueue
        if(otpNumber.equals(getIntent().getStringExtra("PASS_OTP").toString())) {
            queue.add(getRequest);
        }
        else{
            alertDialogBuilder123.setMessage("Entered OTP Not Correct");
            AlertDialog alertDialog = alertDialogBuilder123.create();
            alertDialog.show();
        }
        loading.dismiss();
        showProgress(false);
    }

    // resend otp
    private void resendOtp() {

        // Show a progress spinner, and kick off a background task to
        // perform the user login attempt.
        showProgress(true);
        //loader
        ProgressDialog loading = null;

        loading = new ProgressDialog(this);
        loading.setCancelable(false);
        loading.setMessage("Resending OTP");
        loading.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        loading.show();

        final String mobileNumber=getIntent().getStringExtra("PASS_MOBILE");
        RequestQueue queue = Volley.newRequestQueue(this);  // this = context

        final String url = "http://docref.in/api/doctors/send_otp.php?mobile_number=" + mobileNumber;

        final AlertDialog.Builder alertDialogBuilder2 = new AlertDialog.Builder(this);

        // prepare the Request
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        // display response
                        Log.d("Response", response.toString());

                        String jsonStr=response.toString();
                        if (jsonStr != null) {
                            try {
                                JSONObject jsonObj = new JSONObject(jsonStr);

                                String json_response = jsonObj.getString("response");
                                String json_mobile = jsonObj.getString("mobile");
                                String json_otp = jsonObj.getString("otp");
                                String response_status = "true";
                                if(response_status.equals(json_response)) {
                                    Intent intent_home = new Intent(OtpActivity.this, OtpActivity.class);
                                    intent_home.putExtra("PASS_MOBILE", json_mobile);
                                    intent_home.putExtra("PASS_OTP", json_otp);
                                    startActivity(intent_home);
                                }
                                else{
                                    alertDialogBuilder2.setMessage("Something Wents Wrong");
                                    AlertDialog alertDialog = alertDialogBuilder2.create();
                                    alertDialog.show();
                                }

                            } catch (final JSONException e) {
                                Log.e(TAG, "Json parsing error: " + e.getMessage());
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getApplicationContext(),
                                                "Json parsing error: " + e.getMessage(),
                                                Toast.LENGTH_LONG)
                                                .show();
                                    }
                                });

                            }
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Error.Response", error.toString());
                    }
                }
        );

        // add it to the RequestQueue
        Pattern pattern = Pattern.compile("\\d{10}");
        Matcher matcher = pattern.matcher(mobileNumber);
        if (matcher.matches()) {
            queue.add(getRequest);
        }
        else{
            alertDialogBuilder2.setMessage("Mobile Number Not Correct");
            AlertDialog alertDialog = alertDialogBuilder2.create();
            alertDialog.show();
        }

        loading.dismiss();
        showProgress(false);
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mMobileLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mMobileLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mMobileLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mMobileLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public void onBackPressed() {
        // do nothing.
        Intent intent_home2 = new Intent(OtpActivity.this, MobileActivity.class);
        startActivity(intent_home2);
    }

}