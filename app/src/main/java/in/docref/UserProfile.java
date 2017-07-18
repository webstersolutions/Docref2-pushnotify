package in.docref;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 * Created by alfiasorte on 10-07-2017.
 */

public class UserProfile extends AppCompatActivity implements AdapterView.OnItemSelectedListener, View.OnClickListener, NavigationView.OnNavigationItemSelectedListener{

    /* Image upload */
    private Button buttonChoose;
    /* private Button buttonUpload; */

    private ImageView imageView;

    /* private EditText editTextName; */

    private Bitmap bitmap;

    private int PICK_IMAGE_REQUEST = 1;

    private String UPLOAD_URL ="http://docref.in/api/doctors/file_upload.php";

    private String KEY_IMAGE = "image";
    private String KEY_NAME = "name";

    private String ROW_URL = "";
    /* Image upload end */


    public static final String TAG = UserProfile.class
            .getSimpleName();

    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;

    String first_name="";
    String last_name="";
    String email ="";
    String mob_number ="";
    String speciality_id="";
    String speciality_name="";
    String profile_img="";
    String profile_thumb="";
    String experience="";
    String join_date="";
    String role="";

    private Button buttonApt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uprofile);

        /* for drawer menu */
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        */

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        /* for drawer menu end */

        String data_otp=getIntent().getStringExtra("PASS_MOBILE");

        //image upload
        buttonChoose = (Button) findViewById(R.id.buttonChoose);
        /* buttonUpload = (Button) findViewById(R.id.buttonUpload); */

        /* editTextName = (EditText) findViewById(R.id.editText); */

        imageView  = (ImageView) findViewById(R.id.profile_image);

        buttonChoose.setOnClickListener(this);
        /* buttonUpload.setOnClickListener(this); */

        //loader
        ProgressDialog loading = null;

        loading = new ProgressDialog(this);
        loading.setCancelable(false);
        loading.setMessage("Verifying OTP");
        loading.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        //loading.show();

        /* get data */
        final String mobileNumber=getIntent().getStringExtra("PASS_MOBILE");
        /* final String mobileNumber="8421902025"; */
        RequestQueue queue = Volley.newRequestQueue(this);  // this = context

        final String url = "http://docref.in/api/doctors/profile.php?mobile_number=" + mobileNumber;

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

                                if(jsonObj.getString("response").toString().equals("true")) {

                                    first_name = jsonObj.getString("first_name");
                                    last_name = jsonObj.getString("last_name");
                                    email = jsonObj.getString("email");
                                    mob_number = jsonObj.getString("mobile_number");
                                    speciality_id = jsonObj.getString("speciality_id");
                                    speciality_name = jsonObj.getString("speciality_name");
                                    profile_img = jsonObj.getString("profile_image");
                                    profile_thumb = jsonObj.getString("profile_thumbnail");
                                    experience = jsonObj.getString("experience");
                                    join_date = jsonObj.getString("join_date");
                                    role = jsonObj.getString("role");

                                    set_values();
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
        queue.add(getRequest);
        /* get data end */

        ImageButton mUpdateButton = (ImageButton) findViewById(R.id.updateButton);
        mUpdateButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                update_profile();
            }
        });

        /* get permission to user camera */
        askForPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,1);

    }

    /* for side menu */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_profile) {
            finish();
            startActivity(getIntent());
        } else if(id == R.id.nav_appointment){
            Intent intent_home2 = new Intent(UserProfile.this, FixAppointment.class);
            intent_home2.putExtra("PASS_MOBILE2", getIntent().getStringExtra("PASS_MOBILE"));
            /* intent_home2.putExtra("PASS_MOBILE2", "8421902025"); */
            startActivity(intent_home2);
        } else if(id == R.id.nav_list){
            Intent intent_home3 = new Intent(UserProfile.this, ListActivity.class);
            intent_home3.putExtra("PASS_MOBILE", getIntent().getStringExtra("PASS_MOBILE"));
            /* intent_home2.putExtra("PASS_MOBILE2", "8421902025"); */
            startActivity(intent_home3);
        } else if(id == R.id.nav_logout){
            Intent intent_home2 = new Intent(UserProfile.this, MobileActivity.class);
            startActivity(intent_home2);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    /* for side menu end */

    /* user permissions */
    private void askForPermission(String permission, Integer requestCode) {
        if (ContextCompat.checkSelfPermission(UserProfile.this, permission) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(UserProfile.this, permission)) {

                //This is called if user has denied the permission before
                //In this case I am just asking the permission again
                ActivityCompat.requestPermissions(UserProfile.this, new String[]{permission}, requestCode);

            } else {

                ActivityCompat.requestPermissions(UserProfile.this, new String[]{permission}, requestCode);
            }
        } else {
            /* Toast.makeText(this, "" + permission + " is already granted.", Toast.LENGTH_SHORT).show(); */
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(ActivityCompat.checkSelfPermission(this, permissions[0]) == PackageManager.PERMISSION_GRANTED){
            switch (requestCode) {
                //Write external Storage
                case 1:
                    askForPermission(Manifest.permission.READ_EXTERNAL_STORAGE,2);
                    break;
                //Read External Storage
                case 2:
                    askForPermission(Manifest.permission.CAMERA,3);
                    break;
                //Camera
                case 3:
                    break;
            }

            /* Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show(); */
        }else{
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
        }
    }
    /* get user permissions end */

    //image upload

    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    private void uploadImage(){

        //Showing the progress dialog
        final ProgressDialog loading = ProgressDialog.show(this,"Uploading...","Please wait...",false,false);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, UPLOAD_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        //Disimissing the progress dialog
                        loading.dismiss();
                        //Showing toast message of the response
                        //Toast.makeText(UserProfile.this, s , Toast.LENGTH_LONG).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        //Dismissing the progress dialog
                        loading.dismiss();

                        //Showing toast
                        Toast.makeText(UserProfile.this, volleyError.getMessage().toString(), Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //Converting Bitmap to String
                String image = getStringImage(bitmap);

                //Getting Image Name
                /* String name = editTextName.getText().toString().trim(); */

                //Creating parameters
                Map<String,String> params = new Hashtable<String, String>();

                //Adding parameters
                String row_url_nm="simple_name";
                String mobile_key="mobile_number";
                params.put(KEY_IMAGE, image);
                /* params.put(KEY_NAME, name); */
                params.put(row_url_nm , ROW_URL);
                params.put(mobile_key, mob_number);

                //returning parameters
                return params;
            }
        };

        if(!getStringImage(bitmap).equals("")) {
            //Creating a Request Queue
            RequestQueue requestQueue = Volley.newRequestQueue(this);

            //Adding request to the queue
            requestQueue.add(stringRequest);
        }


    }

    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();

            ROW_URL = filePath.toString();

            try {
                //Getting the Bitmap from Gallery
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                //Setting the Bitmap to ImageView
                imageView.setImageBitmap(bitmap);
                uploadImage();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onClick(View v) {

        if(v == buttonChoose){
            showFileChooser();
        }

/*
        if(v == buttonUpload){
            uploadImage();
        }
        */
    }
    //image upload end


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // On selecting a spinner item
        String item = parent.getItemAtPosition(position).toString();

        // Showing selected spinner item
        /* Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show(); */

    }

    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub

    }

    public void set_values(){
        if(!first_name.isEmpty()){
            EditText field_fnm = (EditText) findViewById(R.id.first_name);
            field_fnm.setText(first_name);
        }
        if(!last_name.isEmpty()){
            EditText field_lnm = (EditText) findViewById(R.id.last_name);
            field_lnm.setText(last_name);
        }
        if(!mob_number.isEmpty()){
            EditText field_lnm = (EditText) findViewById(R.id.mobile_number);
            field_lnm.setText(mob_number);
        }
        if(!email.isEmpty()){
            EditText field_lnm = (EditText) findViewById(R.id.email);
            field_lnm.setText(email);
        }
        if(!experience.isEmpty()){
            EditText field_lnm = (EditText) findViewById(R.id.experience);
            field_lnm.setText(experience);
        }
        if(!profile_img.trim().isEmpty()){
            //Getting the Bitmap from Gallery
            //try {
                /*
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.parse(profile_img.toString()));
                //Setting the Bitmap to ImageView
                imageView.setImageBitmap(bitmap);
*/
                /*
            Toast.makeText(getApplicationContext(),
                    "Image url: " + profile_img.toString(),
                    Toast.LENGTH_LONG)
                    .show();
*/

            Picasso.with(getApplicationContext()).load(profile_img.toString()).resize(200,200).into(imageView);

            //} catch (IOException e) {
            //    e.printStackTrace();
            //}


            // imageView.setImageURI(Uri.parse(profile_img));
        }


        // Spinner Drop down elements
        final List<String> special_list = new ArrayList<String>();

        RequestQueue queue2 = Volley.newRequestQueue(this);  // this = context
        final String url2 = "http://docref.in/api/doctors/specialities.php";

        // prepare the Request
        JsonObjectRequest getRequest2 = new JsonObjectRequest(Request.Method.GET, url2, null,
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

                                if(jsonObj.getString("response").toString().equals("true")) {


                                    // Getting JSON Array node
                                    JSONArray contacts = jsonObj.getJSONArray("data");

                                    // looping through All Contacts
                                    for (int i = 0; i < contacts.length(); i++) {
                                        JSONObject c = contacts.getJSONObject(i);

                                        String id = c.getString("id");
                                        String name = c.getString("name");

                                        special_list.add(name);
                                    }

                                    // Spinner element SPECIALITY
                                    Spinner speciality = (Spinner) findViewById(R.id.speciality);

                                    // Spinner click listener
                                    speciality.setOnItemSelectedListener(UserProfile.this);

                                    // Creating adapter for spinner
                                    ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(UserProfile.this, android.R.layout.simple_spinner_item, special_list);

                                    // Drop down layout style - list view with radio button
                                    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                                    // attaching data adapter to spinner
                                    speciality.setAdapter(dataAdapter);

                                    if(!speciality_id.isEmpty()){
                                        int selectionPosition= dataAdapter.getPosition(speciality_id);
                                        speciality.setSelection(selectionPosition);
                                        //speciality.setAdapter(dataAdapter);

                                    }


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
        queue2.add(getRequest2);
        /* get data end */

        // Spinner element ROLE
        Spinner role_ele = (Spinner) findViewById(R.id.role);

        // Spinner click listener
        role_ele.setOnItemSelectedListener(this);

        // Spinner Drop down elements
        List<String> role_list = new ArrayList<String>();
        role_list.add("Doctor");
        role_list.add("Lab");

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, role_list);

        // Drop down layout style - list view with radio button
        dataAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        role_ele.setAdapter(dataAdapter2);
        if(!role.isEmpty()){
            int selectionPosition2 = dataAdapter2.getPosition(role.toString());
            role_ele.setSelection(selectionPosition2);
            //role_ele.setAdapter(dataAdapter2);
        }

    }

    /*
    volley functions
     */

    /*public static synchronized OtpActivity getInstance() {
        return mInstance;
    }
    */

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

    /* update profile */
    public void update_profile(){

        String up_mobile = "";
        String up_fnm = "";
        String up_lnm = "";
        String up_email = "";
        String up_speciality = "";
        String up_exp = "";
        String up_role = "";

        EditText mMobNumber;
        mMobNumber = (EditText) findViewById(R.id.mobile_number);
        up_mobile= ""+mMobNumber.getText().toString().trim();

        EditText mFirstName;
        mFirstName = (EditText) findViewById(R.id.first_name);
        up_fnm= ""+mFirstName.getText().toString().trim();

        EditText mLastName;
        mLastName = (EditText) findViewById(R.id.last_name);
        up_lnm= ""+mLastName.getText().toString();

        EditText mEmail;
        mEmail = (EditText) findViewById(R.id.email);
        up_email= ""+mEmail.getText().toString().trim();

        EditText mExp;
        mExp = (EditText) findViewById(R.id.experience);
        up_exp= mExp.getText().toString();

        Spinner role_spinner = (Spinner)findViewById(R.id.role);
        up_role = role_spinner.getSelectedItem().toString();

        Spinner special_spinner = (Spinner)findViewById(R.id.speciality);
        String sel_name = special_spinner.getSelectedItem().toString();

        up_speciality=sel_name;

        RequestQueue up_queue = Volley.newRequestQueue(this);  // this = context

        String up_url_encode ="";
        try {

            String encodedUrl = URLEncoder.encode(up_mobile, "UTF-8");
            encodedUrl += "&first_name="+URLEncoder.encode(up_fnm, "UTF-8");
            encodedUrl += "&last_name="+URLEncoder.encode(up_lnm, "UTF-8");
            encodedUrl += "&email="+URLEncoder.encode(up_email, "UTF-8");
            encodedUrl += "&speciality="+URLEncoder.encode(up_speciality, "UTF-8");
            encodedUrl += "&experience="+URLEncoder.encode(up_exp, "UTF-8");
            encodedUrl += "&role="+URLEncoder.encode(up_role, "UTF-8");

            up_url_encode = "http://docref.in/api/doctors/general_info.php?mobile_number="+encodedUrl;

            final String up_url = up_url_encode;

            // prepare the Request
            JsonObjectRequest up_getRequest = new JsonObjectRequest(Request.Method.GET, up_url, null,
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

                                    if(jsonObj.getString("response").toString().equals("true")) {

                                        finish();
                                        startActivity(getIntent());
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
            up_queue.add(up_getRequest);

        } catch (UnsupportedEncodingException e) {

            System.err.println(e);

        }
    }
    /* update profile end */

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }

        //Intent intent_home2 = new Intent(UserProfile.this, MobileActivity.class);
        //startActivity(intent_home2);
    }
}
