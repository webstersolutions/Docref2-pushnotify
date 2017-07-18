package in.docref;

        import android.app.ProgressDialog;
        import android.content.Intent;
        import android.support.design.widget.NavigationView;
        import android.support.v4.view.GravityCompat;
        import android.support.v4.widget.DrawerLayout;
        import android.support.v7.app.ActionBarDrawerToggle;
        import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;
        import android.support.v7.widget.LinearLayoutManager;
        import android.support.v7.widget.RecyclerView;
        import android.support.v7.widget.Toolbar;
        import android.view.Menu;
        import android.view.MenuItem;
        import android.view.View;
        import android.widget.AdapterView;
        import android.widget.TextView;
        import android.widget.Toast;

        import com.android.volley.RequestQueue;
        import com.android.volley.Response;
        import com.android.volley.VolleyError;
        import com.android.volley.toolbox.JsonArrayRequest;
        import com.android.volley.toolbox.Volley;

        import org.json.JSONArray;
        import org.json.JSONException;
        import org.json.JSONObject;
        import org.w3c.dom.Text;

        import java.util.ArrayList;
        import java.util.List;

/**
 * Created by alfiasorte on 13-07-2017.
 */

public class ListActivity extends AppCompatActivity implements  AdapterView.OnItemSelectedListener, NavigationView.OnNavigationItemSelectedListener{

    //Creating a List of superheroes
    private List<appointments> listAppoint;

    //Creating Views
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.Adapter adapter;

    private String user_number="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        user_number=getIntent().getStringExtra("PASS_MOBILE");
        /* user_number="8421902025"; */

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

        //Initializing Views
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        //Initializing our superheroes list
        listAppoint = new ArrayList<>();

        //Calling method to get data
        getData();
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
            Intent intent_home3 = new Intent(ListActivity.this, UserProfile.class);
            intent_home3.putExtra("PASS_MOBILE", getIntent().getStringExtra("PASS_MOBILE"));
            /* intent_home2.putExtra("PASS_MOBILE2", "8421902025"); */
            startActivity(intent_home3);
        } else if(id == R.id.nav_appointment){
            Intent intent_home2 = new Intent(ListActivity.this, FixAppointment.class);
            intent_home2.putExtra("PASS_MOBILE2", getIntent().getStringExtra("PASS_MOBILE"));
            /* intent_home2.putExtra("PASS_MOBILE2", "8421902025"); */
            startActivity(intent_home2);
        }else if(id == R.id.nav_list){
            finish();
            startActivity(getIntent());
        } else if(id == R.id.nav_logout){
            Intent intent_home2 = new Intent(ListActivity.this, MobileActivity.class);
            startActivity(intent_home2);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    /* for side menu end */

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


    //This method will get data from the web api
    private void getData(){
        //Showing a progress dialog
        final ProgressDialog loading = ProgressDialog.show(this,"Loading Data", "Please wait...",false,false);

        //Creating a json array request
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Config.DATA_URL+"?mobile_number="+user_number+"&list_type=all",
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        //Dismissing progress dialog
                        loading.dismiss();

                        //calling method to parse json array
                        parseData(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });

        //Creating request queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        //Adding request to the queue
        requestQueue.add(jsonArrayRequest);
    }

    //This method will parse json data
    private void parseData(JSONArray array){
        for(int i = 0; i<array.length(); i++) {
            appointments newAppoint = new appointments();
            JSONObject json = null;
            try {
                json = array.getJSONObject(i);

                //newAppoint.setImageUrl(json.getString(Config.TAG_IMAGE_URL));
                /*
                newAppoint.setName(json.getString(Config.TAG_NAME));
                newAppoint.setRank(json.getInt(Config.TAG_RANK));
                newAppoint.setRealName(json.getString(Config.TAG_REAL_NAME));
                newAppoint.setCreatedBy(json.getString(Config.TAG_CREATED_BY));
                newAppoint.setFirstAppearance(json.getString(Config.TAG_FIRST_APPEARANCE));
                */

                newAppoint.setDoctorId(json.getString(Config.TAG_DOC_ID));
                newAppoint.setDoctorName(json.getString(Config.TAG_DOC_NAME));
                newAppoint.setDoctorMobile(json.getString(Config.TAG_DOC_MOBILE));
                newAppoint.setImageThumbUrl(json.getString(Config.TAG_IMAGE_THUMB));

                newAppoint.setPatientName(json.getString(Config.TAG_PNM));
                newAppoint.setPatientMobile(json.getString(Config.TAG_PMOBILE));

                newAppoint.setActivityStatus(json.getString(Config.TAG_ACTIVITY_STATUS));
                newAppoint.setAppointmentID(json.getString(Config.TAG_APP_ID));
                newAppoint.setAppointmentDate(json.getString(Config.TAG_APP_DATE));
                newAppoint.setCreateTime(json.getString(Config.TAG_APP_CTIME));
                newAppoint.setAppointmentStatus(json.getString(Config.TAG_APP_STATUS));

/*
                ArrayList<String> powers = new ArrayList<String>();

                JSONArray jsonArray = json.getJSONArray(Config.TAG_POWERS);

                for(int j = 0; j<jsonArray.length(); j++){
                    powers.add(((String) jsonArray.get(j))+"\n");
                }
                newAppoint.setPowers(powers);
                */


            } catch (JSONException e) {
                e.printStackTrace();
            }
            listAppoint.add(newAppoint);
        }

        //Finally initializing our adapter
        adapter = new CardAdapter(listAppoint, this);

        //Adding adapter to recyclerview
        recyclerView.setAdapter(adapter);
    }

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
