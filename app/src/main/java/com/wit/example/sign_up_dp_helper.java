package com.wit.example;

import static com.wit.example.my_database_helper.TABLE_NAME;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class sign_up_dp_helper extends SQLiteOpenHelper {

    private final Context context_s;

    public static final String DATABASE_NAME_SIGN_UP = "logins.db";
    public static final int DATABASE_VERSION = 1;
    public static final String TABLE_NAME_S = "login_info";
    public static final String COLUMN_ID_S = "_id";
    public static final String COLUMN_NAME_S = "NAME";
    public static final String COLUMN_SURNAME_S = "SURNAME";
    public static final String COLUMN_MIDDLENAME_S = "MIDDLE";
    public static final String COLUMN_DATE_OF_BIRTH_S = "BIRTHDAY";
    public static final String COLUMN_GENDER_S = "GENDER";
    public static final String COLUMN_USERNAME_S = "USERNAME";
    public static final String COLUMN_PASSWORD_S = "PASSWORD";

    public sign_up_dp_helper(@Nullable Context context){
        super(context, DATABASE_NAME_SIGN_UP, null, DATABASE_VERSION);
        this.context_s = context;

    }

    @Override
    public void onCreate(SQLiteDatabase sign_db) {
        String query = "CREATE TABLE " + TABLE_NAME_S +
                " (" + COLUMN_ID_S + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NAME_S + " TEXT, " +
                COLUMN_SURNAME_S + " TEXT, " +
                COLUMN_MIDDLENAME_S + " TEXT, " +
                COLUMN_DATE_OF_BIRTH_S + " TEXT, " +
                COLUMN_GENDER_S + " TEXT, " +
                COLUMN_USERNAME_S + " TEXT, " +
                COLUMN_PASSWORD_S + " TEXT);";
        sign_db.execSQL(query) ;
    }

    @Override
    public void onUpgrade(SQLiteDatabase sign_db, int i, int i1) {
        sign_db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_S);
        onCreate(sign_db);
    }

    void addValues(String surname, String name, String middle, String date_of_birth, String gender, String username, String password){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv_s = new ContentValues();

        cv_s.put(COLUMN_SURNAME_S, surname);
        cv_s.put(COLUMN_NAME_S, name);
        cv_s.put(COLUMN_MIDDLENAME_S, middle);
        cv_s.put(COLUMN_DATE_OF_BIRTH_S, date_of_birth);
        cv_s.put(COLUMN_GENDER_S, gender);
        cv_s.put(COLUMN_USERNAME_S, username);
        cv_s.put(COLUMN_PASSWORD_S, password);

        long result = db.insert(TABLE_NAME_S, null, cv_s);

        if(result == -1){
            Toast.makeText(context_s, "Xatolik", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(context_s, "Ro'yhatdan o'tildi✅", Toast.LENGTH_SHORT).show();
        }
    }

    public boolean isUsernameExist(String username) {
        boolean isExist = false;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        try {
            cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME_S + " WHERE " + COLUMN_USERNAME_S + "=?", new String[]{username});
            if (cursor.getCount() > 0) {
                isExist = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
        return isExist;
    }

    public List<String> getUsernames(String username) {
        List<String> usernames = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME_S + " WHERE " + COLUMN_USERNAME_S + " = '" + username + "'";

        // Sorguyu çalıştırın ve sonucu elde edin
        Cursor cursor = db.rawQuery(query, null);

        // Sonuç kümesindeki tüm satırları döngüye alın ve isimlerini ArrayList'e ekleyin
        if (cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_S));
                usernames.add(name);
            } while (cursor.moveToNext());
        }

        // Kaynakları temizleyin ve sonuçları döndürün
        cursor.close();
        db.close();
        return usernames;
    }


}