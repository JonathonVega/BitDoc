package com.jonathonfvega.bitdoc;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.ibm.watson.developer_cloud.visual_recognition.v3.VisualRecognition;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.ClassifyImagesOptions;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.VisualClassification;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private static final int SELECTED_PIC=1;

    public String filePath;

    private VisualRecognition initVisualRecognition() {
        VisualRecognition service = new VisualRecognition(VisualRecognition.VERSION_DATE_2016_05_20);
        service.setApiKey("b679b786b434d320c1bec68f3173225f9a232879");

        return service;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        /*WatsonTask task = new WatsonTask();
        task.execute(new String[]{});

        {
            "url": "https://gateway-a.watsonplatform.net/visual-recognition/api",
                "note": "This is your previous free key. If you want a different one, please wait 24 hours after unbinding the key and try again.",
                "api_key": "b679b786b434d320c1bec68f3173225f9a232879"
        }

        System.out.println("Classify an image");
        ClassifyImagesOptions options = new ClassifyImagesOptions.Builder()
                .images(new File("src/test/resources/visual_recognition/car.png"))
                .build();
        VisualClassification result = service.classify(options).execute();
        System.out.println(result);
        */




    }

    private class WatsonTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String[] params) {
            // do above Server call here



            VisualRecognition service = initVisualRecognition();
            System.out.println("Classify an image");
            ClassifyImagesOptions options = new ClassifyImagesOptions.Builder()
                    .images(new File(filePath))
                    .build();
            VisualClassification result = service.classify(options).execute();
            System.out.println(result);
            return "some message";
        }



        @Override
        protected void onPostExecute(String message) {
            //process message
        }
    }




    public void btnClick(View v) {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, SELECTED_PIC);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SELECTED_PIC:
                if (resultCode==RESULT_OK) {
                    Uri uri = data.getData();
                    String[] projection = {MediaStore.Images.Media.DATA};
                    Cursor cursor = getContentResolver().query(uri,projection,null,null,null);
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(projection[0]);
                    String filepath=cursor.getString(columnIndex);
                    this.filePath = filepath;
                    Log.d("Hello", "This is here!!!!" + filepath);

                    cursor.close();

                    WatsonTask task = new WatsonTask();
                    task.execute("This should work");
                }
                break;
            default:
                break;
        }


    }

}
