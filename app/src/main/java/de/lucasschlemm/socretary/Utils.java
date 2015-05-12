package de.lucasschlemm.socretary;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.util.Date;

public class Utils {

    /**
     * Methode für das Einfügen eines Zeitstempels in der Datenbank
     * @return aktuelle Zeit in ms
     */
    public static long getCurrentTime(){

        return System.currentTimeMillis();
    }

    /**
     * converts bitmap to BLOB for storing in DB
     * @param picture Bitmap
     * @return BLOB of picture
     */
    public static byte[] blobify(Bitmap picture) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        picture.compress(Bitmap.CompressFormat.PNG, 100, bos);
        byte[] bArray = bos.toByteArray();
        return bArray;
    }

    /**
     * converts BLOB to bitmap for retrieving from DB
     * @param blob byte[]
     * @return Bitmap of the image
     */
    public static Bitmap bitmapify(byte[] blob){
        Bitmap bm = BitmapFactory.decodeByteArray(blob, 0, blob.length);
        return bm;
    }



}
