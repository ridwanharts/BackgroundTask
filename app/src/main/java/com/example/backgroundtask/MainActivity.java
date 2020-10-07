package com.example.backgroundtask;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

public class MainActivity extends AppCompatActivity {

    Button btnShowProgress;
    ImageView my_image;
    TextView tvUpdateDownload;
    Context context;
    ProgressDialog progressDialog;
    public static final int progress_nar_type = 0;
    public static String URL = "https://api.androidhive.info/progressdialog/hive.jpg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnShowProgress = findViewById(R.id.btnProgressBar);
        my_image = findViewById(R.id.my_image);
        tvUpdateDownload = findViewById(R.id.tvupdateDownload);
        tvUpdateDownload.setText("0%");
        context = this;
        progressDialog = new ProgressDialog(this);

        btnShowProgress.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Dexter.withContext(getApplication())
                        .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .withListener(new PermissionListener() {
                            @Override public void onPermissionGranted(PermissionGrantedResponse response) {
                                new Downloadlibrary().execute(URL);
                            }
                            @Override public void onPermissionDenied(PermissionDeniedResponse response) {

                            }
                            @Override public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

                            }
                        }).check();
            }
        });
    }

    class Downloadlibrary extends AsyncTask<String, String, String>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.e("Start", "PreExecute");
            progressDialog.setTitle("Downloading");
            progressDialog.setMessage("Wait while downloading...");
            progressDialog.setCancelable(true); // disable dismiss by tapping outside of the dialog
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {

            int count;
            try{
                URL url = new URL(strings[0]);
                URLConnection connection = url.openConnection();
                connection.connect();

                int lengthOfFile = connection.getContentLength();
                InputStream inputStream = new BufferedInputStream(url.openStream(), 8192);
                OutputStream outputStream = new FileOutputStream(Environment.getExternalStorageDirectory().getPath()+"/downloadfile.jpg");

                byte data[] = new byte[1024];
                long total = 0;
                while((count = inputStream.read(data)) != -1){
                    total += count;
                    publishProgress(""+ (int)(total*100/lengthOfFile));
                    outputStream.write(data, 0, count);
                }

                outputStream.flush();
                outputStream.close();
                inputStream.close();

            }catch (Exception e){
                Log.e("error", e.getMessage());
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            tvUpdateDownload.setText(values[0]);

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressDialog.dismiss();
            String imagePath = Environment.getExternalStorageDirectory().getPath()+"/downloadfile.jpg";
            my_image.setImageDrawable(Drawable.createFromPath(imagePath));
        }
    }
}