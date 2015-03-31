package com.example.skom.thebarcodescanner.barcodescanner;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.example.skom.thebarcodescanner.R;

/**
 * Created by Zach on 3/27/2015.
 */
public class LoadScreen extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.load_screen);
        Thread timer = new Thread() {
            public void run() {
                try {
                    sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    Class ourClass = null;
                    try {
                        ourClass = Class.forName("com.example.skom.thebarcodescanner.barcodescanner.MainActivity");
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    Intent i = new Intent(LoadScreen.this, ourClass);
                    startActivity(i);
                }
            }
        };
        timer.start();
    }
}
