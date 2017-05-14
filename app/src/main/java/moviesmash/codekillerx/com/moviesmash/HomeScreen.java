package moviesmash.codekillerx.com.moviesmash;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class HomeScreen extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = HomeScreen.class.getSimpleName();
    private GridView mGridView;
    private ProgressBar mProgressBar;
    private GridViewAdapter mGridViewAdapter;
    private ArrayList<GridItem> mGridData;
    public int pageCount = 1;
    public int count;
    public int pageLimit;
    private String QUERY_URL = "http://api.themoviedb.org/3/search/movie?query=";
    private String FEED_URL = "http://api.themoviedb.org/3/movie/popular?api_key=YOUR_API_KEY&page=";
    public static final String BASE_URL = "http://image.tmdb.org/t/p/w500";
    public FloatingActionButton fab_plus,fab_up,fab_pop,fab_tr;
    public Animation FabOpen,FabClose,FabRClockwise,FabRanticlockwise;
    boolean isOpen = false;
    public  NotificationManager manager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FabOpen = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fab_open);
        FabClose = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fab_close);
        FabRanticlockwise = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_anticlockwise);
        FabRClockwise = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_clockwise);

        fab_plus = (FloatingActionButton) findViewById(R.id.fab_plus);
        fab_up = (FloatingActionButton) findViewById(R.id.fab_upcoming);
        fab_pop = (FloatingActionButton) findViewById(R.id.fab_popular);
        fab_tr = (FloatingActionButton) findViewById(R.id.fab_toprated);

        fab_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isOpen)
                {
                    fab_up.startAnimation(FabClose);
                    fab_pop.startAnimation(FabClose);
                    fab_tr.startAnimation(FabClose);
                    fab_plus.startAnimation(FabRanticlockwise);
                    fab_up.setClickable(false);
                    fab_pop.setClickable(false);
                    fab_tr.setClickable(false);
                    isOpen = false;
                }
                else
                {
                    fab_up.startAnimation(FabOpen);
                    fab_pop.startAnimation(FabOpen);
                    fab_tr.startAnimation(FabOpen);
                    fab_plus.startAnimation(FabRClockwise);
                    fab_up.setClickable(true);
                    fab_pop.setClickable(true);
                    fab_tr.setClickable(true);
                    isOpen = true;
                }
            }
        });

        fab_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mGridData.clear();
                mGridViewAdapter.clear();
                mProgressBar.setVisibility(View.VISIBLE);
                pageCount = 1;
                FEED_URL = "http://api.themoviedb.org/3/movie/upcoming?api_key=YOUR_API_KEY&page=";
                new MovieData().execute(FEED_URL + "" + pageCount);

            }
        });


        fab_pop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mGridData.clear();
                mGridViewAdapter.clear();
                mProgressBar.setVisibility(View.VISIBLE);
                pageCount = 1;
                FEED_URL = "http://api.themoviedb.org/3/movie/popular?api_key=YOUR_API_KEY&page=";
                new MovieData().execute(FEED_URL + "" + pageCount);
            }
        });


        fab_tr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mGridData.clear();
                mGridViewAdapter.clear();
                mProgressBar.setVisibility(View.VISIBLE);
                pageCount = 1;
                FEED_URL = "http://api.themoviedb.org/3/movie/top_rated?api_key=YOUR_API_KEY&page=";
                new MovieData().execute(FEED_URL + "" + pageCount);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mGridView = (GridView) findViewById(R.id.gridView);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        mGridData = new ArrayList<>();
        mGridViewAdapter = new GridViewAdapter(this,R.layout.grid_item_layout,mGridData);
        mGridView.setAdapter(mGridViewAdapter);
        mProgressBar.setVisibility(View.VISIBLE);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                GridItem g = mGridData.get((int) id);
                Intent i = new Intent(HomeScreen.this,MovieDetailsPage.class);
                i.putExtra("ID",g.getId());
                startActivity(i);
            }
        });
        mGridView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if(firstVisibleItem + visibleItemCount >= totalItemCount-2){
                    Log.i("CountShit", String.valueOf(firstVisibleItem) + " + "
                            + String.valueOf(visibleItemCount) + " >= "
                            + String.valueOf(totalItemCount));
                    Log.i("Page Count: ",""+pageCount);
                    Log.i("Page Limit: ",""+pageLimit);
                    if(pageCount < pageLimit){
                        new MovieData().execute(FEED_URL+""+pageCount);
                    }
                }
            }
        });
        manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        addNotification();
        new MovieData().execute(FEED_URL+""+pageCount);

    }

    private void addNotification() {
        // The PendingIntent to launch our activity if the user selects this notification
        Intent mintent = new Intent(this,HomeScreen.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, mintent, 0);
        // Set the info for the views that show in the notification panel.
        Notification notification = new Notification.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)  // the status icon
                .setTicker("Running")  // the status text
                .setWhen(System.currentTimeMillis())  // the time stamp
                .setContentTitle(("Movies Mash"))  // the label of the entry
                .setContentText("Check out new movies")  // the contents of the entry
                .setContentIntent(contentIntent)  // The intent to send when the entry is clicked
                .setVisibility(-1)
                .setOngoing(true)
                .build();

        // Send the notification.
        //Toast.makeText(this,"Building notification",Toast.LENGTH_SHORT).show();
        manager.notify(5, notification);
    }

    public class MovieData extends AsyncTask<String,Void,Void>{

        private final String TAG = MovieData.class.getSimpleName();

        String jsonStr = "";
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        @Override
        protected Void doInBackground(String... params) {
            if(params.length==0)
                return null;
            String movies_url = params[0];
            pageCount += 1;

            try {
                URL url = new URL(movies_url);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();

                if(inputStream==null)
                    return null;

                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line=reader.readLine())!=null)
                    buffer.append(line + "\n");

                if (buffer.length()==0)
                    return null;
                jsonStr = buffer.toString();
                Log.i(TAG, jsonStr);
                //if(!jsonStr.equals(""))
                parseResult(jsonStr);

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            /*if(urlConnection!=null)
                urlConnection.disconnect();
            try {
                if(reader!=null)
                    reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }*/
            if(jsonStr.equals(""))
                Toast.makeText(HomeScreen.this,"Failed to connect the Internet",Toast.LENGTH_SHORT).show();
            else{
                mProgressBar.setVisibility(View.GONE);
                mGridViewAdapter.setGridData(mGridData);
            }

        }
    }

    private void parseResult(String result){
        try{
            Log.i(TAG,"IN PARSE RESULT");
            JSONObject response = new JSONObject(result);
            String pl = response.getString("total_pages");
            pageLimit = Integer.parseInt(pl);
            JSONArray results = response.getJSONArray("results");
            count = results.length();
            GridItem item;
            for(int i = 0 ; i < results.length(); i++){
                JSONObject object = results.getJSONObject(i);
                String title = object.getString("original_title");
                String image = object.getString("poster_path");
                String uid = object.getString("id");
                item = new GridItem();
                item.setTitle(title);
                item.setImage(BASE_URL + image);
                item.setId(uid);
                mGridData.add(item);
            }
        }catch (JSONException j){
            j.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
           // super.onBackPressed();
            AlertDialog.Builder alertdialogbuilder = new AlertDialog.Builder(HomeScreen.this);
            alertdialogbuilder.setMessage("Are you sure you wanna quit?")
                                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        finish();

                                    }
                                })
                                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                });
            alertdialogbuilder.create();
            alertdialogbuilder.show();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_screen, menu);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                query = query.replace(' ','+');
                pageCount = 1;
                mGridData.clear();
                mGridViewAdapter.clear();
                mProgressBar.setVisibility(View.VISIBLE);
                new MovieData().execute(QUERY_URL + query + "&api_key=YOUR_API_KEY");
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        pageCount = 1;
        mGridData.clear();
        mGridViewAdapter.clear();
        mProgressBar.setVisibility(View.VISIBLE);

        if (id == R.id.pop_movie) {
            FEED_URL = "http://api.themoviedb.org/3/movie/popular?api_key=YOUR_API_KEY&page=";
        } else if (id == R.id.top_movie) {
            FEED_URL = "http://api.themoviedb.org/3/movie/top_rated?api_key=YOUR_API_KEY&page=";
        } else if (id == R.id.up_movie) {
            FEED_URL = "http://api.themoviedb.org/3/movie/upcoming?api_key=YOUR_API_KEY&page=";
        }
        new MovieData().execute(FEED_URL + "" + pageCount);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        manager.cancel(5);
    }
}
