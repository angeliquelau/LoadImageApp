package com.example.assignment2partb;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

public class UploadActivity extends AppCompatActivity {

    ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        Intent intent = getIntent();
        Bitmap uploadImage = BitmapFactory.decodeByteArray(intent.getByteArrayExtra("imageByte"), 0, getIntent().getByteArrayExtra("imageByte").length);
        Log.d("uploadActivity: ", "uploadImage: " + uploadImage.toString());
        image = findViewById(R.id.testImage);
        image.setImageBitmap(uploadImage);
    }
}