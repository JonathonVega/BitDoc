package com.jonathonfvega.bitdoc;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


import com.ibm.watson.developer_cloud.visual_recognition.v3.VisualRecognition;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.ClassifyImagesOptions;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.VisualClassification;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import static android.R.id.button2;


public class MainActivity extends AppCompatActivity {

    int TAKE_PHOTO_CODE = 0;
    public static int count = 0;
    public String dir;

    public File mfile;

    AnimationDrawable heartPulseAnimation;

    Intent intent ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        ImageView heartPulse = (ImageView)findViewById(R.id.heart_pulse);
        heartPulse.setBackgroundResource(R.drawable.heart_pulse);
        heartPulseAnimation = (AnimationDrawable) heartPulse.getBackground();


        final String dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/picFolder/";
        this.dir = dir;
        File newDir = new File(dir);
        newDir.mkdirs();
    }



    private VisualRecognition initVisualRecognition() {
        VisualRecognition service = new VisualRecognition(VisualRecognition.VERSION_DATE_2016_05_20);
        service.setApiKey("b679b786b434d320c1bec68f3173225f9a232879");

        return service;
    }

    private class WatsonTask extends AsyncTask<String, Void, String> {

        String someString;

        @Override
        protected String doInBackground(String[] params) {
            // do above Server call here


            VisualRecognition service = initVisualRecognition();
            System.out.println("Classify an image");
            ClassifyImagesOptions options = new ClassifyImagesOptions.Builder()
                    .images(mfile)
                    .build();
            VisualClassification result = service.classify(options).execute();

            System.out.print(result);
            Double x = result.getImages().get(0).getClassifiers().get(0).getClasses().get(0).getScore();
            someString += "";

            return someString;
        }



        @Override
        protected void onPostExecute(String message) {
            //process message
            //TextView viewk = (TextView) findViewById(R.id.textView);
            //viewk.setText(someString);



            //debuggin purposes vega
            System.out.println("testing -- " + someString);
        }
    }



   /*  tester touch event for animation

   public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            heartPulseAnimation.start();
            return true;
        }
        return super.onTouchEvent(event);
    }*/



    public void btnClick(View v) {

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        startActivityForResult(cameraIntent, TAKE_PHOTO_CODE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == TAKE_PHOTO_CODE && resultCode == RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");



            // CALL THIS METHOD TO GET THE URI FROM THE BITMAP
            Uri tempUri = getImageUri(getApplicationContext(), photo);

            // CALL THIS METHOD TO GET THE ACTUAL PATH
            File finalFile = new File(getRealPathFromURI(tempUri));

            mfile = finalFile;

            WatsonTask task = new WatsonTask();
            task.execute("This should work");


            heartPulseAnimation.start();
        }


    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }


    public void showData(String someString){
        new Intent(getApplicationContext(), dataDisplay.class);
        intent.putExtra("VALUE1",someString);
        startActivity(intent);
    }

}

