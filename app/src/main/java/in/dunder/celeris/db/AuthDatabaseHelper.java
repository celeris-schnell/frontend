package in.dunder.celeris.db;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import in.dunder.celeris.models.User;

public class AuthDatabaseHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "authdb";

    public AuthDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE users (id INTEGER PRIMARY KEY, name TEXT, balance INTEGER, email TEXT, phoneNumber TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS users");
        onCreate(db);
    }

    public static boolean checkDatabaseExists(Context context) {
        SQLiteDatabase checkDB = null;
        String DB_FULL_PATH = context.getDatabasePath("authdb").getPath();
        try {
            checkDB = SQLiteDatabase.openDatabase(DB_FULL_PATH, null, SQLiteDatabase.OPEN_READONLY);
        } catch (Exception ignored) {
        }

        if (checkDB != null) {
            checkDB.close();
        }

        return checkDB != null;
    }

    public void saveUser(int id, String name, int balance, String email, String phoneNumber) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("INSERT INTO users (id, name, balance, email, phoneNumber) VALUES (?, ?, ?, ?, ?)",
                new Object[]{id, name, balance, email, phoneNumber});
        db.close();
    }

    public boolean isUserLoggedIn() {
        SQLiteDatabase db = this.getReadableDatabase();
        boolean loggedIn = false;
        try (Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM users LIMIT 1", null)) {
            if (cursor.moveToFirst()) {
                int count = cursor.getInt(0);
                loggedIn = count > 0;
            }
        }
        db.close();
        return loggedIn;
    }

    public User getUser() {
        SQLiteDatabase db = this.getReadableDatabase();
        User user = null;
        try (Cursor cursor = db.rawQuery("SELECT * FROM users", null)) {
            if (cursor.moveToFirst()) {
                @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex("id"));
                @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex("name"));
                @SuppressLint("Range") int balance = cursor.getInt(cursor.getColumnIndex("balance"));
                @SuppressLint("Range") String email = cursor.getString(cursor.getColumnIndex("email"));
                @SuppressLint("Range") String phoneNumber = cursor.getString(cursor.getColumnIndex("phoneNumber"));
                user = new User(id, name, balance, email, phoneNumber);
            }
        }
        db.close();
        return user;
    }
}
