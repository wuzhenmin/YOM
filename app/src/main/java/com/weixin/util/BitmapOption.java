package com.weixin.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;

/**
 * Created by zhenmin on 2016/1/21.
 */
public class BitmapOption  {

    public static Bitmap getImageThumbnail(String uri,int width,int height){
        Bitmap bitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        bitmap = BitmapFactory.decodeFile(uri,options);
        options.inJustDecodeBounds = false;
        int beWidth = options.outWidth/width;
        int beHeight = options.outHeight/height;
        int be = 1;
        if (beWidth<beHeight){
            be = beWidth;
        }else{
            be = beHeight;
        }
        if (be<0){
            be = 1 ;
        }
        options.inSampleSize = be;
        bitmap  = BitmapFactory.decodeFile(uri,options);
        bitmap  = ThumbnailUtils.extractThumbnail(bitmap,width,height,ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        return bitmap;
    }

}
