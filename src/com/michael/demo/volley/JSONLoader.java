package com.michael.demo.volley;

import android.content.Context;
import android.net.ParseException;
import android.support.v4.content.AsyncTaskLoader;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by michael on 2013/8/20.
 */
public class JSONLoader extends AsyncTaskLoader<JSONObject> {
    public JSONLoader(Context context) {
        super(context);
        forceLoad();
    }

    @Override
    public JSONObject loadInBackground() {
        //TODO michael 亲记得翻墙喔
        String url = "http://www.google.com/uds/GnewsSearch?q=michael&v=1.0";
        HttpClient httpClient = new DefaultHttpClient();
        HttpGet get = new HttpGet(url);
        HttpResponse res;
        try {
            res = httpClient.execute(get);
            HttpEntity entity = res.getEntity();
            String body = EntityUtils.toString(entity);
            return new JSONObject(body);
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}