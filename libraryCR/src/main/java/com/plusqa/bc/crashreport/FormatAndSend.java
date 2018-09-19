package com.plusqa.bc.crashreport;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class FormatAndSend extends AppCompatActivity
        implements ImageRecyclerViewAdapter.ItemClickListener {

    InputMethodManager imm;
    private List<Bitmap> images;
    private ImageRecyclerViewAdapter adapter;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_format_and_send);

        Toolbar mTopToolbar = findViewById(R.id.format_and_send_toolbar);
        setSupportActionBar(mTopToolbar);


        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }

        Intent intent = getIntent();
        String calledFrom = intent.getStringExtra("CalledFrom");

        images = new ArrayList<>();

        RecyclerView recyclerView = findViewById(R.id.previewScroll);

        LinearLayoutManager horizontalLayoutManager
                = new LinearLayoutManager(FormatAndSend.this, LinearLayoutManager.HORIZONTAL, false);

        recyclerView.setLayoutManager(horizontalLayoutManager);

        adapter = new ImageRecyclerViewAdapter(this, images);

        adapter.setClickListener(this);

        recyclerView.setAdapter(adapter);

        if (calledFrom.equals("SCREENSHOT")) {
            Bitmap screenShotBM = Utils.getBitMap(getApplicationContext(), "edited_image");
            images.add(screenShotBM);
        }

        adapter.notifyItemInserted(images.size());

        //Tapping outside of fields will clear focus and collapse keyboard
        ConstraintLayout constraintLayout = findViewById(R.id.base_layout);
        constraintLayout.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                deselectView(getCurrentFocus());
                return false;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.format_report_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        if (item.getItemId() == android.R.id.home) {

            this.finish();

            return true;

        } else if (item.getItemId() == R.id.action_send) {

            sendReport();

            this.finishAffinity();

            return true;
        }

        return super.onOptionsItemSelected(item);

    }

    public void deselectView(View v) {
        if (imm != null) {
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
        v.clearFocus();
    }

    @Override
    public void onItemClick(View view, int position) {

    }

    public void addPhoto(View view) {

        Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhoto , 1);

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch(requestCode) {
            case 0:
                if(resultCode == RESULT_OK){
                    Uri selectedImage = imageReturnedIntent.getData();
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                        images.add(bitmap);
                        adapter.notifyItemInserted(images.size());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                break;
            case 1:
                if(resultCode == RESULT_OK){
                    Uri selectedImage = imageReturnedIntent.getData();
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                        images.add(bitmap);
                        adapter.notifyItemInserted(images.size());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }

    private boolean sendReport() {

        //grab log from internal storage
        String logcatString = Utils.readLogFromInternalMemory(this.getApplicationContext()).toString();

        // Post to server
        try {
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            String URL = "http://172.16.1.170:3001/v1/logs.json";
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("Title", "ShakeReport_log");
            jsonBody.put("Logcat", logcatString);
            final String requestBody = jsonBody.toString();

            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.i("VOLLEY", response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("VOLLEY", error.toString());
                }
            }) {
                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }

                @Override
                public byte[] getBody() {
                    try {
                        return requestBody == null ? null : requestBody.getBytes("utf-8");
                    } catch (UnsupportedEncodingException uee) {
                        VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                        return null;
                    }
                }

                @Override
                protected Response<String> parseNetworkResponse(NetworkResponse response) {
                    String responseString = "";

                    CharSequence text;
                    Context context = getApplicationContext();
                    int duration = Toast.LENGTH_SHORT;

                    if (null != response) {
                        responseString = String.valueOf(response.statusCode);
                        if (responseString.equals("200")) {
                            text = "Report Sent!";
                        } else {
                            text = "Report did not send: " + responseString;
                        }
                    } else {
                        text = "Report did not send";
                    }
                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                    return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
                }
            };

            requestQueue.add(stringRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        CharSequence text = "Report Sent!";
        int duration = Toast.LENGTH_SHORT;
        Context context = getApplicationContext();
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();

        return true;
    }


}
