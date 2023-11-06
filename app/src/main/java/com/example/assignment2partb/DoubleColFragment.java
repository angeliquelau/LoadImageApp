package com.example.assignment2partb;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class DoubleColFragment extends Fragment {
    private ImageView selectImage;
    private RecyclerView rv;
    private RecyclerView.Adapter doubleAdapter;
    private ImageData imageBitmaps;

    public DoubleColFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imageBitmaps = getArguments().getParcelable("data");
        Log.d("double column", "imageBitmaps: " + imageBitmaps.getImageBitmaps().size());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_double_col, container, false);
        rv = (RecyclerView) view.findViewById(R.id.doubleColRV);
        rv.setLayoutManager(new GridLayoutManager(getActivity(), 2, LinearLayoutManager.VERTICAL, false));
        doubleAdapter = new DoubleAdapter();
        rv.setAdapter(doubleAdapter);
        return view;
    }

    public class DoubleViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        private ImageBitmap bitmap;
        private Image image;

        public DoubleViewHolder(LayoutInflater li, ViewGroup view) {
            super(li.inflate(R.layout.double_col_image, view, false));
            selectImage = itemView.findViewById(R.id.doubleColImg);
            itemView.setOnClickListener(this);
        }

        public void bind(ImageBitmap bitmap, Image image)
        {
            this.bitmap = bitmap;
            this.image = image;
            selectImage.setImageBitmap(bitmap.getImageBitmap());
        }

        @Override
        public void onClick(View view) { //do the upload part
            //referred to: https://www.geeksforgeeks.org/how-to-fix-android-os-networkonmainthreadexception/
            if(Build.VERSION.SDK_INT > 9)
            {
                StrictMode.ThreadPolicy dlPolicy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(dlPolicy);
            }

            //referred to: https://stackoverflow.com/questions/18210700/best-method-to-download-image-from-url-in-android
            File filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES + "/Ass2PtB"); //create folder

            if(!filePath.exists()){
                filePath.mkdirs();
            }

            File imageFile = new File(filePath, String.valueOf(System.currentTimeMillis()) + ".jpeg");
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(imageFile);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            try {
                bitmap.getImageBitmap().compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.flush();
                fos.close();
                MediaScannerConnection.scanFile(getActivity(), new String[]{imageFile.getAbsolutePath()}, null, new MediaScannerConnection.OnScanCompletedListener() {
                    @Override
                    public void onScanCompleted(String s, Uri uri) {
                        Log.i("D-ExternalStorage ", "scan completed");
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }

            //referred to: https://firebase.google.com/docs/storage/android/upload-files?hl=en
            //upload from a local file part from firebase documentation
            Log.d("double column:", "imageFile name: " + imageFile.getName());
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageReference = storage.getReference();

            Log.d("double column:", "imageFile path: " + imageFile.getPath());
            Uri uploadFile = Uri.fromFile(new File(imageFile.getPath()));
            StorageReference imageFolderRef = storageReference.child("Ass2PtB/" + uploadFile.getLastPathSegment());
            UploadTask ut = imageFolderRef.putFile(uploadFile);
            ut.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(), "Failed to upload.", Toast.LENGTH_SHORT).show();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(getContext(), "Upload success.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public class DoubleAdapter extends RecyclerView.Adapter<DoubleViewHolder> {
        @NonNull
        @Override
        public DoubleViewHolder onCreateViewHolder(@NonNull ViewGroup view, int viewType) {
            LayoutInflater li = LayoutInflater.from(getActivity());
            return new DoubleViewHolder(li, view);
        }

        @Override
        public void onBindViewHolder(@NonNull DoubleViewHolder holder, int position) {
            holder.bind(imageBitmaps.getImageBitmaps().get(position), imageBitmaps.getImageURLs().get(position));
        }

        @Override
        public int getItemCount() {
            return imageBitmaps.getImageBitmaps().size();
        }
    }
}