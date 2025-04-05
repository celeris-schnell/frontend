package in.dunder.celeris.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

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

    }

    public static boolean checkDatabaseExists(Context context) {
        SQLiteDatabase checkDB = null;
        String DB_FULL_PATH = context.getDatabasePath("authdb").getPath();
        try {
            checkDB = SQLiteDatabase.openDatabase(DB_FULL_PATH, null, SQLiteDatabase.OPEN_READONLY);
        } catch (Exception e) {
        }

        if (checkDB != null) {
            checkDB.close();
        }

        return checkDB != null;
    }

    public void insertUser(int id, String name, int balance, String email, String phoneNumber) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("INSERT INTO users (id, name, balance, email, phoneNumber) VALUES (?, ?, ?, ?, ?)",
                new Object[]{id, name, balance, email, phoneNumber});
        db.close();
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
        try (Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM users", null)) {
            if (cursor.moveToFirst()) {
                int count = cursor.getInt(0);
                loggedIn = count > 0;
            }
        }
        db.close();
        return loggedIn;
    }
}
