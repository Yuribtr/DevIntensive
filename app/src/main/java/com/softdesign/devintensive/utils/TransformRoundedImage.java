package com.softdesign.devintensive.utils;

import android.graphics.Bitmap;
import com.squareup.picasso.Transformation;

public class TransformRoundedImage implements Transformation {

    @Override
    public Bitmap transform(Bitmap source) {
        return RoundedAvatarDrawable.getRoundedBitmap(source);
    }

    @Override
    public String key() {
        return "TransformRoundedImage";
    }
}
