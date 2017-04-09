package com.jonathonfvega.bitdoc;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


import com.ibm.watson.developer_cloud.visual_recognition.v3.VisualRecognition;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.ClassifyImagesOptions;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.VisualClassification;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import static android.R.id.button2;


public class MainActivity extends Activity implements SensorEventListener{

    int TAKE_PHOTO_CODE = 0;
    public static int count = 0;
    public String dir;

    public File mfile;

    AnimationDrawable heartPulseAnimation;

    Intent intent ;



    //heart Rate Stuff
    TextView box;
    //--Button b;
    ImageView heartButton;
    int permissionCheck;
    Context thisActivity;
    SensorManager mSensorManager;


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


        //check for permissions for heart rate sensor
        box=(TextView)findViewById(R.id.hr);
        heartButton= (ImageView) findViewById(R.id.heart_pulse);
        permissionCheck= ContextCompat.checkSelfPermission(this,
                Manifest.permission.BODY_SENSORS);

        heartButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                getBeat();
            }
        });
        thisActivity=getApplicationContext();

        //check for permissions for camera
        int MY_PERMISSIONS_REQUEST_CAMERA= 1;
        if (ContextCompat.checkSelfPermission(thisActivity,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(thisActivity,
                Manifest.permission.INTERNET)
                != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(thisActivity,
                Manifest.permission.ACCESS_NETWORK_STATE)
                != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(thisActivity,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(thisActivity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED )
        {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.BODY_SENSORS)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            }
            else {
                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA},
                        MY_PERMISSIONS_REQUEST_CAMERA);

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.INTERNET},
                        MY_PERMISSIONS_REQUEST_CAMERA);
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_NETWORK_STATE},
                        MY_PERMISSIONS_REQUEST_CAMERA);
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_CAMERA);
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_CAMERA);
            }

            }


        //check for permissions
        int MY_PERMISSIONS_REQUEST_BODY_SENSORS= 1;
        if (ContextCompat.checkSelfPermission(thisActivity,
                Manifest.permission.BODY_SENSORS)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.BODY_SENSORS)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.BODY_SENSORS},
                        MY_PERMISSIONS_REQUEST_BODY_SENSORS);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }

    }


    /*heart rate methods */
    //--------------------------------------------------------------------------------------------
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_HEART_RATE) {
            String msg = "Heart Rate: " + (int) event.values[0]+" BPM";
            box.setText(msg);
            // Log.d(TAG, msg);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        //dont really need to do anything
    }

    private void getBeat() {
        mSensorManager = ((SensorManager) getSystemService(SENSOR_SERVICE));
        Sensor mHeartRateSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);

        boolean reg=mSensorManager.registerListener(this, mHeartRateSensor, SensorManager.SENSOR_DELAY_NORMAL);
        //Log.d("Sensor Status:", " Sensor registered: " + (reg ? "yes" : "no"));

        //box.setText();
        Handler mHandler = new Handler();
        mHandler.postDelayed(new Runnable() {

            @Override
            public void run() {
                //stop measure after 30 seconds
                String beat=(box.getText().toString());
                box.setText(beat);
                stopMeasure();

            }

        }, 35000L);
    }
    private void stopMeasure() {
        mSensorManager.unregisterListener(this);
    }

    /*end of heart rate methods*/
    //-------------------------------------------------------------------------------------------------------------

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

