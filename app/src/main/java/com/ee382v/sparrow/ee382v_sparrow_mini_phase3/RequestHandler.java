package com.ee382v.sparrow.ee382v_sparrow_mini_phase3;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.ee382v.sparrow.ee382v_sparrow_mini_phase3.ViewOneStream.SELECTED_IMAGE;

/**
 * Created by kyle on 10/24/17.
 */

public class RequestHandler {

    public static int handleImageResponse(final Context mContext,
                                   final int resource,
                                   final int target,
                                   final Class intentDest,
                                   final String intentTag,
                                   final JSONObject response
                                   ) throws JSONException{
        ArrayList<GridItem> items = new ArrayList<>();
        JSONArray contents = response.getJSONArray("content");
        int start = response.getInt("start");
        for (int i = 0; i < contents.length(); i++) {
            JSONObject obj = contents.getJSONObject(i);
            String title = obj.getString("title");
            String link = MainActivity.getEndpoint() + "/view_image?img_id="
                    + obj.getString("key");
            String streamName = obj.getString("stream_name");
            items.add(new GridItem(title, link, streamName));
        }
        GridViewAdapter adapter = new GridViewAdapter(mContext, resource, items);
        GridView gv = ((Activity)mContext).findViewById(target);
        gv.setAdapter(adapter);

        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(mContext, intentDest);
                ImageView imageView = view.findViewById(R.id.grid_item_image);
                TextView textView = view.findViewById(R.id.grid_item_title);
                intent.putExtra(intentTag, new String[] {(String) imageView.getTag(),
                        textView.getText().toString(), (String) textView.getTag()});
                mContext.startActivity(intent);
            }
        });
        return start;
    }
}
