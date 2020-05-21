package com.wgx.app;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "12345677777778899";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final TextView dd=null;
//        getWindow().getDecorView().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                dd.setVisibility(View.GONE);
//            }
//        },2000);
//        getWindow().getDecorView().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                TextView tv=(TextView)findViewById(R.id.ddd);
//                tv.setText("123456");
//            }
//        },3000);

//
        dd.setVisibility(View.GONE);
        TextView tv=(TextView)findViewById(R.id.ddd);
        tv.setText("123456");



//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                int val=0;
//                while (true){
//                    Log.d(TAG,"-----------------------------"+val);
//                    try{
//                        Thread.sleep(1000);
//                    }catch (Exception e){
//
//                    }
//                    val+=1;
//                }
//            }
//        }).start();
    }
}
