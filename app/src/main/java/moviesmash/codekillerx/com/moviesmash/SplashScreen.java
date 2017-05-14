package moviesmash.codekillerx.com.moviesmash;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class SplashScreen extends Activity{

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        Runnable r = new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                }catch (InterruptedException e){
                    e.printStackTrace();
                }finally {
                    Intent intent = new Intent(SplashScreen.this,HomeScreen.class);
                    startActivity(intent);
                }
            }
        };
        Thread t = new Thread(r);
        t.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}
