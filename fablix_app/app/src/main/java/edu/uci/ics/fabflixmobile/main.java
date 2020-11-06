package edu.uci.ics.fabflixmobile;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.*;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import com.google.android.material.textfield.TextInputLayout;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class main extends AppCompatActivity {

    private EditText content;
    private Button Search;
    private String url;
    DrawerLayout drawer_layout;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                drawer_layout.openDrawer(GravityCompat.START);
                break;
            case R.id.exit:
                Toast.makeText(this, "ByeBye", Toast.LENGTH_SHORT).show();
                finishAffinity();
                break;
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // upon creation, inflate and initialize the layout
        setContentView(R.layout.main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer_layout = findViewById(R.id.drawer_layout);
        ActionBar actionbar = getSupportActionBar();
        if (actionbar!=null){
            actionbar.setDisplayHomeAsUpEnabled(true);
            actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);
        }
        content = findViewById(R.id.content);

        Search = findViewById(R.id.Search);
        /**
         * In Android, localhost is the address of the device or the emulator.
         * To connect to your machine, you need to use the below IP address
         * **/
        url = "https://ec2-54-174-50-96.compute-1.amazonaws.com:8443/cs122B/api/";

        //assign a listener to call a function to handle the user request when clicking a button
        Search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                search();
            }
        });
        content.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    Search.performClick();
                    return true;
                }
                return false;
            }
        });
    }


    public void search() {
        // Use the same network queue across our application
        final RequestQueue queue = NetworkManager.sharedManager(this).queue;
        //request type is POST
        String searchurl = this.url+"movie"+"?"+"title="+ content.getText().toString()+"&N=20&page=1";
        final StringRequest loginRequest = new StringRequest(Request.Method.GET, searchurl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                ArrayList<Movie> movie_list=new ArrayList<>();
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    Log.d("params", String.valueOf(jsonArray.length()));
                    if (jsonArray.length() == 0){
                        Toast.makeText(main.this, "No Result", Toast.LENGTH_LONG).show();
                        return;
                    }

                    for(int i = 0; i < jsonArray.length(); i++){
                        JSONObject jresponse = null;
                        jresponse = jsonArray.getJSONObject(i);
                        String moviename = jresponse.getString("movie_name");
                        Short year=Short.valueOf(jresponse.getString("movie_dob"));
                        String genre= jresponse.getString("genre_list");
                        String star= jresponse.getString("star_list");
                        String director= jresponse.getString("movie_director");
                        String rating = jresponse.getString("movie_rate");
                        movie_list.add(new Movie(moviename,year,genre,star,director, rating));
                        Log.d("test title", moviename);
                    }
                } catch (JSONException e) {
                    Log.d("params", "catch" + response);
                    e.printStackTrace();
                }


                //initialize the activity(page)/destination
                Intent listPage = new Intent(main.this, ListViewActivity.class);
                listPage.putExtra("movielist",(Serializable)movie_list);
                listPage.putExtra("title",content.getText().toString());
                //without starting the activity/page, nothing would happen
                startActivity(listPage);
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("params", error.toString());

                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                // Post request form data

                final Map<String, String> params = new HashMap<>();
                params.put("title", content.getText().toString());
                params.put("N", "20");
                params.put("page","1") ;
                params.put("first","2") ;
                params.put("second","1") ;
                return params;
            }
        };

        // !important: queue.add is where the login request is actually sent
        queue.add(loginRequest);
    }
}