package edu.uci.ics.fabflixmobile;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ListViewActivity extends AppCompatActivity{
    private Button prev;
    private Button next;
    private String url;
    private int page;
    private String title;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listview);
        //this should be retrieved from the database and the backend server
        final ArrayList<Movie> movies = (ArrayList<Movie>)getIntent().getSerializableExtra("movielist");
        title=getIntent().getStringExtra("title");
        page=getIntent().getIntExtra("page",1);
        MovieListViewAdapter adapter = new MovieListViewAdapter( movies.subList(0, movies.size()-1), this);

        ListView listView = findViewById(R.id.list);
        prev= findViewById(R.id.prev);
        next = findViewById(R.id.next);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ArrayList<Movie> movie_list=new ArrayList<>();
                Movie movie = movies.get(position);
                movie_list.add(movie);
                Intent listPage = new Intent(ListViewActivity.this, singlemovie.class);
                listPage.putExtra("movielist",(Serializable)movie_list);
                startActivity(listPage);
            }
        });
        url = "https://ec2-54-174-50-96.compute-1.amazonaws.com:8443/cs122B/api/";
        if (movies.size() < 19){
            next.setEnabled(false);
        }
        if (page == 1){
            prev.setEnabled(false);
        }

        //assign a listener to call a function to handle the user request when clicking a button
        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                page--;
                sendRequest(page);
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                page++;
                sendRequest(page);
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent main = new Intent(ListViewActivity.this, main.class);
        startActivity(main);
    }

    public void sendRequest(int page){
        // Use the same network queue across our application
        final RequestQueue queue = NetworkManager.sharedManager(this).queue;
        String nexturl=url+"movie"+"?"+"title="+title+"&N=20&page="+Integer.toString(page);
        final StringRequest loginRequest = new StringRequest(Request.Method.GET, nexturl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //TODO should parse the json response to redirect to appropriate functions.
                Log.d("params", title);
                ArrayList<Movie> movie_list=new ArrayList<>();

                try {
                    JSONArray jsonArray = new JSONArray(response);

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
                    e.printStackTrace();
                }


                //initialize the activity(page)/destination
                Intent listPage = new Intent(ListViewActivity.this, ListViewActivity.class);
                listPage.putExtra("movielist",(Serializable)movie_list);
                listPage.putExtra("title",title);
                listPage.putExtra("page",page);
                //without starting the activity/page, nothing would happen
                startActivity(listPage);
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("login.error", error.toString());
                    }
                }) {

        };
        // !important: queue.add is where the login request is actually sent
        queue.add(loginRequest);
    }
}