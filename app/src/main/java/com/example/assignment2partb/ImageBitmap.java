package com.example.assignment2partb;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.ByteArrayOutputStream;

public class ImageBitmap implements Parcelable {
    private Bitmap imageBitmap;

    public ImageBitmap(Bitmap imageBitmap)
    {
        this.imageBitmap = imageBitmap;
    }

    protected ImageBitmap(Parcel in) {
        imageBitmap = in.readParcelable(Bitmap.class.getClassLoader());
    }

    public static final Creator<ImageBitmap> CREATOR = new Creator<ImageBitmap>() {
        @Override
        public ImageBitmap createFromParcel(Parcel in) {
            return new ImageBitmap(in);
        }

        @Override
        public ImageBitmap[] newArray(int size) {
            return new ImageBitmap[size];
        }
    };

    public Bitmap getImageBitmap() {
        return imageBitmap;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 10, baos);
        parcel.writeByteArray(baos.toByteArray());
    }
}
