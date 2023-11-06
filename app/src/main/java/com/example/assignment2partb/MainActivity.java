package com.example.assignment2partb;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Switch;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.util.concurrent.AbstractScheduledService;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.core.SingleObserver;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    Switch colView;
    EditText searchImage;
    Button searchBtn;
    FrameLayout singleView, doubleView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseApp.initializeApp(this);

        colView = findViewById(R.id.colView);
        searchImage = findViewById(R.id.searchImageTxt);
        searchBtn = findViewById(R.id.searchBtn);
        singleView = findViewById(R.id.singleColSelector);
        doubleView = findViewById(R.id.doubleColSelector);

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchImage();
            }
        });

    }

    public void searchImage() {
        Toast.makeText(MainActivity.this, "Searching starts", Toast.LENGTH_SHORT).show();
        SearchImageTask sit = new SearchImageTask(MainActivity.this);
        sit.setSearchStr(searchImage.getText().toString());
        Log.d("MainActivity: ", "searchImage: " + searchImage.getText().toString());
        Single<String> searchObs = Single.fromCallable(sit);
        searchObs = searchObs.subscribeOn(Schedulers.io());
        searchObs = searchObs.observeOn(AndroidSchedulers.mainThread());
        searchObs.subscribe(new SingleObserver<String>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onSuccess(@NonNull String s) {
                Toast.makeText(MainActivity.this, "Searching ends", Toast.LENGTH_SHORT).show();
                loadImages(s);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                Toast.makeText(MainActivity.this, "error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void loadImages(String response)
    {
        RetrieveImageTask rit = new RetrieveImageTask(MainActivity.this);
        rit.setData(response);
        Toast.makeText(MainActivity.this, "Image loading starts", Toast.LENGTH_SHORT).show();
        Single<ImageData> searchObs = Single.fromCallable(rit);
        searchObs = searchObs.subscribeOn(Schedulers.io());
        searchObs = searchObs.observeOn(AndroidSchedulers.mainThread());
        searchObs.subscribe(new SingleObserver<ImageData>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onSuccess(@NonNull ImageData bitmaps) {
                Toast.makeText(MainActivity.this, "Image loading ends", Toast.LENGTH_SHORT).show();

                FragmentManager fm = getSupportFragmentManager();
                Bundle bundle = new Bundle();
                bundle.putParcelable("data", bitmaps);

                Log.d("main activity: ", "image: " + bitmaps.getImageURLs().size() + " " + bitmaps.getImageBitmaps().size());
                //default view is single column view
                doubleView.setVisibility(View.INVISIBLE);
                singleView.setVisibility(View.VISIBLE);
                SingleColFragment scf = (SingleColFragment) fm.findFragmentById(R.id.singleColSelector);
                if(scf == null)
                {
                    scf = new SingleColFragment();
                    scf.setArguments(bundle);
                    fm.beginTransaction().add(R.id.singleColSelector, scf).commit();
                }

                Toast.makeText(getApplicationContext(), colView.getTextOff().toString(), Toast.LENGTH_SHORT).show();

                //turn on switch to get double column view, turn off to get back single column view
                colView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(colView.isChecked()) //if its true, meaning if its on
                        {
                            doubleView.setVisibility(View.VISIBLE);
                            singleView.setVisibility(View.INVISIBLE);
                            DoubleColFragment dcf = (DoubleColFragment) fm.findFragmentById(R.id.doubleColSelector);
                            if(dcf == null)
                            {
                                dcf = new DoubleColFragment();
                                dcf.setArguments(bundle);
                                fm.beginTransaction().add(R.id.doubleColSelector, dcf).commit();
                            }
                            Toast.makeText(getApplicationContext(), colView.getTextOn().toString(), Toast.LENGTH_SHORT).show();
                        }
                        else //if false, go back to single column view
                        {
                            doubleView.setVisibility(View.INVISIBLE);
                            singleView.setVisibility(View.VISIBLE);
                            SingleColFragment scf = (SingleColFragment) fm.findFragmentById(R.id.singleColSelector);
                            if(scf == null)
                            {
                                scf = new SingleColFragment();
                                fm.beginTransaction().add(R.id.singleColSelector, scf).commit();
                            }

                            Toast.makeText(getApplicationContext(), colView.getTextOff().toString(), Toast.LENGTH_SHORT).show();

                        }
                    }
                });

            }

            @Override
            public void onError(@NonNull Throwable e) {
                Toast.makeText(MainActivity.this, "error, please search again", Toast.LENGTH_SHORT).show();

            }
        });
    }
}

