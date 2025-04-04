package in.dunder.celeris.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "qrcode.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "merchant_qr";
    private static final String COLUMN_MERCHANT_ID = "merchant_id";
    private static final String COLUMN_QR_CODE = "qr_code";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "("
                + COLUMN_MERCHANT_ID + " TEXT PRIMARY KEY,"
                + COLUMN_QR_CODE + " BLOB)";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void saveQRCode(String merchantId, Bitmap qrCode) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_MERCHANT_ID, merchantId);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        qrCode.compress(Bitmap.CompressFormat.PNG, 100, stream);
        values.put(COLUMN_QR_CODE, stream.toByteArray());
        db.replace(TABLE_NAME, null, values);
        db.close();
    }

    public Bitmap getQRCode(String merchantId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, new String[]{COLUMN_QR_CODE},
                COLUMN_MERCHANT_ID + "=?", new String[]{merchantId},
                null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            byte[] qrCodeBytes = cursor.getBlob(0);
            cursor.close();
            return BitmapFactory.decodeByteArray(qrCodeBytes, 0, qrCodeBytes.length);
        }
        return null;
    }
}