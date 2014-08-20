package com.michael.demo.volley;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.GridView;

import java.util.ArrayList;
/**
 * Created by michael on 2013/8/20.
 */
public class ImageLoadingActivity extends FragmentActivity {
    
    private GridView mGridView;
    
    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        mGridView = new GridView(this);
        mGridView.setNumColumns(2);
        setContentView(mGridView);
        
        ArrayList<String> list = new ArrayList<String>();
        for (int i = 0; i < 200; i++) {
            //TODO michael  一个是https一个是http
            String url = (i % 2 == 0) ? "https://github.com/fluidicon.png" : "http://www.baidu.com/img/bdlogo.png";
            list.add(url);
        }
        ImageAdapter adapter = new ImageAdapter(this, list);
        mGridView.setAdapter(adapter);
    }

}
