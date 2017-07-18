package in.docref;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.messaging.FirebaseMessaging;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.Map;


/**
 * Created by alfiasorte on 10-07-2017.
 */


public class FixAppointment extends AppCompatActivity implements AdapterView.OnItemClickListener, AdapterView.OnItemSelectedListener, NavigationView.OnNavigationItemSelectedListener {

    public static final String TAG = FixAppointment.class.getSimpleName();
    // Auto complete
    AutoCompleteTextView textView=null;
    private ArrayAdapter<String> adapter;

    // Store contacts values in these arraylist
    public static ArrayList<String> phoneValueArr = new ArrayList<String>();
    public static ArrayList<String> nameValueArr = new ArrayList<String>();
    public static ArrayList<String> nameDispValueArr = new ArrayList<String>();

    EditText toNumber=null;
    String toNumberValue="";

    static final Integer CONTACT = 0x1;
    /* --------- auto complete end ----------- */

    /* check mobile exists in system */
    private String UPLOAD_URL ="http://docref.in/api/doctors/sign_on.php";
    private String KEY_MOB = "mobile_number";
    private String C_MOBILE_NUMBER = "";

    //other variables
    ProgressDialog loading = null;

    //cp details
    private View cp_section;
    private ImageView cp_image;
    private TextView cp_name;
    private TextView cp_email;
    private TextView cp_specility;

    /* date and time picker */
    private int mYear, mMonth, mDay, mHour, mMinute;

    EditText et_show_date_time;
    Button btn_set_date_time;
    String date_time = "";

    private String mobileNumber;

    EditText myEditText=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fix_appointment);

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

       // mobileNumber=getIntent().getStringExtra("PASS_MOBILE2");
         mobileNumber ="8421902025";

        //loader
        loading = new ProgressDialog(this);
        loading.setCancelable(false);
        loading.setMessage("Verifying Details");
        loading.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        /* auto complete */

        int permissionCheck = ContextCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.READ_CONTACTS);
        final Button Send = (Button) findViewById(R.id.Send);

        // Initialize AutoCompleteTextView values
        textView = (AutoCompleteTextView) findViewById(R.id.toNumber);

        //Create adapter
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, new ArrayList<String>());
        textView.setThreshold(1);

        //Set adapter to AutoCompleteTextView
        textView.setAdapter(adapter);
        textView.setOnItemSelectedListener(this);
        textView.setOnItemClickListener(this);

        // Read contact data and add data to ArrayAdapter
        // ArrayAdapter used by AutoCompleteTextView
        //readContactData();

        /********** Button Click pass textView object ***********/
        Send.setOnClickListener(BtnAction(textView));

        /* auto complete init end */
        askForPermission(Manifest.permission.READ_CONTACTS, 1);

        /* date and time picker */
        et_show_date_time = (EditText) findViewById(R.id.date);
        btn_set_date_time = (Button) findViewById(R.id.btn_datetime);

        btn_set_date_time.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                datePicker();

            }
        });

        ImageButton mUpdateButton = (ImageButton) findViewById(R.id.appointmentButton);
        mUpdateButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                place_ap();
            }
        });

        myEditText = (EditText) findViewById(R.id.date);

        myEditText.setInputType(InputType.TYPE_NULL);
        myEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // showMyDialog();
                datePicker();
            }
        });
        myEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    // showMyDialog();

                }
            }
        });
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
            Intent intent_home2 = new Intent(FixAppointment.this, UserProfile.class);
            intent_home2.putExtra("PASS_MOBILE", getIntent().getStringExtra("PASS_MOBILE2"));
            /* intent_home2.putExtra("PASS_MOBILE", "8421902025"); */
            startActivity(intent_home2);
        } else if(id == R.id.nav_appointment){
            finish();
            startActivity(getIntent());
        }else if(id == R.id.nav_list){
            Intent intent_home3 = new Intent(FixAppointment.this, ListActivity.class);
            intent_home3.putExtra("PASS_MOBILE", getIntent().getStringExtra("PASS_MOBILE2"));
            /* intent_home2.putExtra("PASS_MOBILE2", "8421902025"); */
            startActivity(intent_home3);
        } else if(id == R.id.nav_logout){
            Intent intent_home3 = new Intent(FixAppointment.this, MobileActivity.class);
            startActivity(intent_home3);
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    /* for side menu end */

    private void datePicker(){

        // Get Current Date
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                        date_time = dayOfMonth + "-" + (monthOfYear + 1) + "-" + year;
                        //*************Call Time Picker Here ********************
                        tiemPicker();
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }
    private void tiemPicker(){
        // Get Current Time
        final Calendar c = Calendar.getInstance();
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);

        // Launch Time Picker Dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                        mHour = hourOfDay;
                        mMinute = minute;

                        et_show_date_time.setText(date_time+" "+hourOfDay + ":" + minute);
                    }
                }, mHour, mMinute, false);
        timePickerDialog.show();
    }

    private void askForPermission(String permission, Integer requestCode) {
        if (ContextCompat.checkSelfPermission(FixAppointment.this, permission) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(FixAppointment.this, permission)) {

                //This is called if user has denied the permission before
                //In this case I am just asking the permission again
                ActivityCompat.requestPermissions(FixAppointment.this, new String[]{permission}, requestCode);

            } else {

                ActivityCompat.requestPermissions(FixAppointment.this, new String[]{permission}, requestCode);
            }
        } else {
           /* Toast.makeText(this, "" + permission + " is already granted.", Toast.LENGTH_SHORT).show(); */
            readContactData();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(ActivityCompat.checkSelfPermission(this, permissions[0]) == PackageManager.PERMISSION_GRANTED){
            switch (requestCode) {
                //Location
                case 1:
                    readContactData();
                    break;
            }

            /* Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show(); */
        }else{
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
        }
    }

    /* auto complete */
    private View.OnClickListener BtnAction(final AutoCompleteTextView toNumber) {
        return new View.OnClickListener() {

            public void onClick(View v) {

                String NameSel = "";
                NameSel = toNumber.getText().toString();


                final String ToNumber = toNumberValue;


                if (ToNumber.length() == 0 ) {
                    Toast.makeText(getBaseContext(), "Please fill phone number",
                            Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(getBaseContext(), NameSel+" : "+toNumberValue,
                            Toast.LENGTH_LONG).show();
                }

            }
        };
    }


    // Read phone contact name and phone numbers

    private void readContactData() {

        try {

            /*********** Reading Contacts Name And Number **********/

            String phoneNumber = "";
            ContentResolver cr = getBaseContext().getContentResolver();

            String[] projection    = new String[] {ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                    ContactsContract.CommonDataKinds.Phone.NUMBER};

            //Query to get contact name

            Cursor cur = cr
                    .query(ContactsContract.Contacts.CONTENT_URI,
                            new String[]{ContactsContract.Contacts._ID,ContactsContract.Contacts.HAS_PHONE_NUMBER,ContactsContract.Contacts.DISPLAY_NAME},
                            null,
                            null,
                            null);

            // If data data found in contacts
            if (cur.getCount() > 0) {

                Log.i("AutocompleteContacts", "Reading   contacts........");
                int k=0;
                String name = "";

                while (cur.moveToNext())
                {

                    String id = cur
                            .getString(cur
                                    .getColumnIndex(ContactsContract.Contacts._ID));
                    name = cur
                            .getString(cur
                                    .getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                    //Check contact have phone number
                    if (Integer
                            .parseInt(cur
                                    .getString(cur
                                            .getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0)
                    {

                        //Create query to get phone number by contact id
                        Cursor pCur = cr
                                .query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                        null,
                                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID
                                                + " = ?",
                                        new String[] { id },
                                        null);
                        int j=0;

                        while (pCur
                                .moveToNext())
                        {
                            // Sometimes get multiple data
                            if(j==0)
                            {
                                // Get Phone number
                                phoneNumber =""+pCur.getString(pCur
                                        .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                                // Add contacts names to adapter
                                adapter.add(name);
                                // Add ArrayList names to adapter
                                phoneValueArr.add(phoneNumber.toString());
                                nameValueArr.add(name.toString());
                                nameDispValueArr.add(name.toString());

                                adapter.add(phoneNumber);

                                // Add ArrayList names to adapter
                                phoneValueArr.add(phoneNumber.toString());
                                nameDispValueArr.add(name.toString());
                                nameValueArr.add(phoneNumber);

                                j++;
                                k++;
                            }
                        }  // End while loop
                        pCur.close();
                    } // End if

                }  // End while loop

            } // End Cursor value check
            cur.close();

        } catch (Exception e) {
            Log.i("AutocompleteContacts","Exception : "+ e);
        }


    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        // TODO Auto-generated method stub

        // Get Array index value for selected name
        //int i = nameValueArr.indexOf(""+arg0.getItemAtPosition(arg2));

        int i = nameValueArr.indexOf(""+arg0.getItemAtPosition(arg2));

        cp_section = (View) findViewById(R.id.contact_person);
        cp_image  = (ImageView) findViewById(R.id.contact_img);
        cp_name = (TextView)findViewById(R.id.contact_name);
        cp_email = (TextView)findViewById(R.id.contact_email);
        cp_specility = (TextView)findViewById(R.id.contact_specility);


        // If name exist in name ArrayList
        if (i >= 0) {

            // Get Phone Number
            toNumberValue = phoneValueArr.get(i);

            //Get name
            String toNameValue = nameDispValueArr.get(i);

            InputMethodManager imm = (InputMethodManager) getSystemService(
                    INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
/*
            // Show Alert
            Toast.makeText(getBaseContext(),
                    "Position:"+arg2+" Name:"+arg0.getItemAtPosition(arg2)+" Number:"+toNumberValue,
                    Toast.LENGTH_LONG).show();

            Log.d("AutocompleteContacts",
                    "Position:"+arg2+" Name:"+arg0.getItemAtPosition(arg2)+" Number:"+toNumberValue);
                    */

            // Show Alert
            Toast.makeText(getBaseContext(),
                    "Position:"+arg2+" Name:"+toNameValue+" Number:"+toNumberValue,
                    Toast.LENGTH_LONG).show();

            Log.d("AutocompleteContacts",
                    "Position:"+arg2+" Name:"+toNameValue+" Number:"+toNumberValue);

            /* Check for mobile number existance */
            final ProgressDialog loading = ProgressDialog.show(this,"Uploading...","Please wait...",false,false);

            StringRequest stringRequest = new StringRequest(Request.Method.POST, UPLOAD_URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String s) {
                            //Disimissing the progress dialog
                            loading.dismiss();
                            //Showing toast message of the response
                            //Toast.makeText(FixAppointment.this, s , Toast.LENGTH_LONG).show();

                            String jsonStr=s.toString();
                            if (jsonStr != null) {
                                try {
                                    JSONObject jsonObj = new JSONObject(jsonStr);

                                    if(jsonObj.getBoolean("response")){

                                        String str_status = jsonObj.getString("user_status");

                                        C_MOBILE_NUMBER = jsonObj.getString("mobile_number");

                                        if(str_status.equals("Existing")){
                                            cp_name.setText( "Name: "+jsonObj.getString("first_name") + " " +jsonObj.getString("last_name") );
                                            cp_email.setText("Email: "+jsonObj.getString("email"));
                                            cp_specility.setText("Speciality: "+jsonObj.getString("speciality"));

                                            Picasso.with(getApplicationContext()).load(jsonObj.getString("profile_image")).into(cp_image);
                                        }

                                        if(str_status.equals("New")){
                                            cp_name.setText( "Name: -" );
                                            cp_email.setText("Email: -");
                                            cp_specility.setText("Speciality: -");
                                        }

                                        cp_section.setVisibility(View.VISIBLE);
                                    }
                                    /*
                                    else{
                                        String error_msg = jsonObj.getString("message");
                                        Toast.makeText(getApplicationContext(),
                                                "Error: " + error_msg,
                                                Toast.LENGTH_LONG)
                                                .show();
                                    }
                                    */


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
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            //Dismissing the progress dialog
                            loading.dismiss();

                            //Showing toast
                            Toast.makeText(FixAppointment.this, volleyError.getMessage().toString(), Toast.LENGTH_LONG).show();
                        }
                    }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {

                    //Creating parameters
                    Map<String,String> params = new Hashtable<String, String>();

                    //Adding parameters
                    String new_mob = toNumberValue;
                    new_mob = new_mob.replaceAll("\\s+","");
                    params.put(KEY_MOB, new_mob);

                    //returning parameters
                    return params;
                }
            };

            //Creating a Request Queue
            RequestQueue requestQueue = Volley.newRequestQueue(this);

            //Adding request to the queue
            requestQueue.add(stringRequest);

            /* checking end */

        }

    }

    protected void onResume() {
        super.onResume();
    }

    protected void onDestroy() {
        super.onDestroy();
    }

    /* end auto complete */

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

    /* create an appointment  */
    public void place_ap() {

        loading = new ProgressDialog(this);
        loading.setCancelable(false);
        loading.setMessage("Sending Appointment Request");
        loading.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        loading.show();

        String add_from_mobile = "";
        String add_to_mobile = "";
        String add_app_date="";
        String add_patient_name = "";
        String add_patient_mobile = "";
        String add_app_desc = "";

        add_from_mobile = "" + mobileNumber.trim();
        add_to_mobile = C_MOBILE_NUMBER;


        EditText mDate;
        mDate = (EditText) findViewById(R.id.date);
        add_app_date = "" + mDate.getText().toString().trim();

        EditText mPatientName;
        mPatientName = (EditText) findViewById(R.id.patient_name);
        add_patient_name = "" + mPatientName.getText().toString().trim();

        EditText mPatientMpobile;
        mPatientMpobile = (EditText) findViewById(R.id.patient_mobile_number);
        add_patient_mobile = "" + mPatientMpobile.getText().toString();

        EditText mDesc;
        mDesc = (EditText) findViewById(R.id.description);
        add_app_desc = "" + mDesc.getText().toString().trim();

        RequestQueue up_queue = Volley.newRequestQueue(this);  // this = context

        String up_url_encode = "";

        if(!add_to_mobile.equals("")){
            try {

                String encodedUrl = "?from_mobile=" + URLEncoder.encode(add_from_mobile, "UTF-8");
                encodedUrl += "&to_mobile=" + URLEncoder.encode(add_to_mobile, "UTF-8");
                encodedUrl += "&app_date=" + URLEncoder.encode(add_app_date, "UTF-8");
                encodedUrl += "&patient_name=" + URLEncoder.encode(add_patient_name, "UTF-8");
                encodedUrl += "&patient_mobile=" + URLEncoder.encode(add_patient_mobile, "UTF-8");
                encodedUrl += "&app_desc=" + URLEncoder.encode(add_app_desc, "UTF-8");

                up_url_encode = "http://docref.in/api/doctors/add_appointment.php" + encodedUrl;

                final String up_url = up_url_encode;

                // prepare the Request
                JsonObjectRequest up_getRequest = new JsonObjectRequest(Request.Method.GET, up_url, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                // display response
                                Log.d("Response", response.toString());

                                String jsonStr = response.toString();

                                if (jsonStr != null) {
                                    try {
                                        JSONObject jsonObj = new JSONObject(jsonStr);

                                        if (jsonObj.getString("response").toString().equals("true")) {

                                            loading.dismiss();
                                            Toast.makeText(getApplicationContext(),
                                                    "Appointment ID: " + jsonObj.getString("app_id").toString(),
                                                    Toast.LENGTH_LONG)
                                                    .show();

                                            Intent intent_home3 = new Intent(FixAppointment.this, ListActivity.class);
                                            intent_home3.putExtra("PASS_MOBILE", getIntent().getStringExtra("PASS_MOBILE2"));
                                            startActivity(intent_home3);
                                        }


                                    } catch (final JSONException e) {
                                        Log.e(TAG, "Json parsing error: " + e.getMessage());
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                loading.dismiss();
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
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                loading.dismiss();
                                Log.d("Error.Response", error.toString());
                            }
                        }
                );
                up_queue.add(up_getRequest);

            } catch (UnsupportedEncodingException e) {

                loading.dismiss();
                System.err.println(e);

            }

        }else{
            loading.dismiss();
            Toast.makeText(getApplicationContext(),
                    "Invalid Input",
                    Toast.LENGTH_LONG)
                    .show();
        }
    }
    /* create an appointment end */

    @Override
    public void onBackPressed() {
        // do nothing.
        Intent intent_home2 = new Intent(FixAppointment.this, UserProfile.class);
        intent_home2.putExtra("PASS_MOBILE", getIntent().getStringExtra("PASS_MOBILE2"));
        startActivity(intent_home2);
    }

}
