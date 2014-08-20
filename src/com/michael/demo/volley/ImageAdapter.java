package com.michael.demo.volley;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.android.volley.toolbox.Volley;

import java.util.List;

/**
 * Created by michael on 2013/8/20.
 */
public class ImageAdapter extends ArrayAdapter<String> {

    private RequestQueue mQueue;
    private ImageLoader mImageLoader;

    public ImageAdapter(Context context, List<String> objects) {
        super(context, 0, objects);
        mQueue = Volley.newRequestQueue(getContext());
        mImageLoader = new ImageLoader(mQueue, new BitmapCache());
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String url = getItem(position);
        ImageView imageView;
        if (convertView == null) {
            imageView = new ImageView(getContext());
        } else {
            imageView = (ImageView) convertView;
        }
        // 图像采集过程
        ImageListener listener = ImageLoader.getImageListener(imageView, android.R.drawable.ic_menu_rotate, android.R.drawable.ic_delete);
        mImageLoader.get(url, listener);
        return imageView;
    }

}
