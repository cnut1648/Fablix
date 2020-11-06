package edu.uci.ics.fabflixmobile;



import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class singlemovie extends AppCompatActivity {

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
        setContentView(R.layout.singlemovie);
        Toolbar toolbar = findViewById(R.id.toolbar_single);
        setSupportActionBar(toolbar);
        drawer_layout = findViewById(R.id.drawer_layout);
        ActionBar actionbar = getSupportActionBar();
        if (actionbar!=null){
            actionbar.setDisplayHomeAsUpEnabled(true);
            actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);
        }

        //this should be retrieved from the database and the backend server
        final ArrayList<Movie> movies = (ArrayList<Movie>)getIntent().getSerializableExtra("movielist");

        Log.d("params", "onCreate");

        TextView title = findViewById(R.id.title_name);
        title.setText(movies.get(0).getName());
        TextView year =  findViewById(R.id.Year);
        year.setText(movies.get(0).getYear() + "");
        ((TextView) findViewById(R.id.Director)).setText(movies.get(0).getDirector() + "");
        ((TextView) findViewById(R.id.rating_movie)).setText(movies.get(0).getRating() + "");
        ((TextView) findViewById(R.id.allgenre)).setText(movies.get(0).getAllGenre() + "");
        ((TextView) findViewById(R.id.allstar)).setText(movies.get(0).getAllStar() + "");
        FloatingActionButton floatButton = findViewById(R.id.floatingActionButton);
        floatButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
}
