package moviesmash.codekillerx.com.moviesmash;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by root on 22/9/16.
 */
public class MovieDetailsPage extends Activity {

    private ImageView iv;
    private TextView tv,ov,gen;
    private TextView ovt,gent,ratt;
    private RatingBar ratingBar;
    private ProgressBar pgb;
    private String BASE_URL = "http://image.tmdb.org/t/p/w500";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_details_layout);
        String id;
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                id= null;
            } else {
                id = extras.getString("ID");
            }
        }
        else
            id = (String) savedInstanceState.getSerializable("ID");
        if(id==null) {
            Toast.makeText(this,"Data Not Found",Toast.LENGTH_SHORT).show();
            finish();
        }
        String url = "http://api.themoviedb.org/3/movie/"+ id +"?api_key=YOUR_API_KEY";
        iv = (ImageView) findViewById(R.id.imageView);
        tv = (TextView) findViewById(R.id.tv);
        ov = (TextView) findViewById(R.id.overV);
        gen = (TextView) findViewById(R.id.Genres);
        ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        ovt = (TextView) findViewById(R.id.ovt);
        ratt = (TextView) findViewById(R.id.ratit);
        gent = (TextView) findViewById(R.id.gent);
        pgb = (ProgressBar) findViewById(R.id.pgb);

        ratingBar.setIsIndicator(true);
        ratingBar.setNumStars(5);
        ratingBar.setStepSize(0.5f);

        ovt.setVisibility(View.GONE);
        ratt.setVisibility(View.GONE);
        gent.setVisibility(View.GONE);
        iv.setVisibility(View.GONE);
        tv.setVisibility(View.GONE);
        ov.setVisibility(View.GONE);
        gen.setVisibility(View.GONE);
        ratingBar.setVisibility(View.GONE);
        pgb.setVisibility(View.VISIBLE);
        new MovieData().execute(url);
    }

    public class MovieData extends AsyncTask<String,Void,Void> {

        private final String TAG = MovieData.class.getSimpleName();

        String jsonStr = "";
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        @Override
        protected Void doInBackground(String... params) {
            if(params.length==0)
                return null;
            String movies_url = params[0];

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
                Toast.makeText(MovieDetailsPage.this,"Failed to connect the Internet",Toast.LENGTH_SHORT).show();
            else{
                ovt.setVisibility(View.VISIBLE);
                ratt.setVisibility(View.VISIBLE);
                gent.setVisibility(View.VISIBLE);
                iv.setVisibility(View.VISIBLE);
                tv.setVisibility(View.VISIBLE);
                ov.setVisibility(View.VISIBLE);
                gen.setVisibility(View.VISIBLE);
                ratingBar.setVisibility(View.VISIBLE);
                pgb.setVisibility(View.GONE);
                parseResult(jsonStr);
            }

        }
    }

    private void parseResult(String result){
        try{
            Log.i("MDA: ","IN PARSE RESULT");
            JSONObject response = new JSONObject(result);
            String adult = response.getString("adult");
            String title = response.getString("original_title");
            String overview = response.getString("overview");
            String rate = response.getString("vote_average");
            String poster = response.getString("poster_path");
            JSONArray results = response.getJSONArray("genres");
            int count = results.length();
            String[] genres = new String[count];
            String cmn = "";
            for(int i = 0 ; i < results.length(); i++){
                JSONObject object = results.getJSONObject(i);
                genres[i] = object.getString("name");
                if(i!=results.length()-1)
                    cmn += genres[i] + ", ";
                else
                    cmn += genres[i];
            }
            //Picasso.with(getParent()).load(BASE_URL+poster).placeholder(R.drawable.loadimg).fit().into(iv);
            gen.setText(cmn);
            tv.setText(title);
            URL url = new URL(BASE_URL+poster);
            Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            iv.setImageBitmap(bmp);
            ov.setText(overview);

            Float f = Float.parseFloat(rate);
            ratingBar.setRating(f/2);
        }catch (JSONException j){
            j.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}
