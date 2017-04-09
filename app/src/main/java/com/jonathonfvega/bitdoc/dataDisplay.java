package com.jonathonfvega.bitdoc;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class dataDisplay extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_display);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String value = extras.getString("VALUE1");

            TextView viewk = (TextView) findViewById(R.id.textView4);
            viewk.setText(value);
        }
    }
}
