package com.example.alber.navegadorweb;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by alber on 16/10/2016.
 */

public class SQLHelper extends SQLiteOpenHelper {

    //Sentencia para crear la tabla
    String sqlCreate = "CREATE TABLE info (url TEXT, fecha TEXT, hora TEXT)";
    //String sqlCreate = "CREATE TABLE info (url TEXT);

    public SQLHelper (Context context, String nombredb, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, nombredb, factory, version);
    }

    @Override
    public void onCreate (SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(sqlCreate);
    }

    @Override
    public void onUpgrade (SQLiteDatabase sqLiteDatabase, int i, int i2) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS info");
        sqLiteDatabase.execSQL(sqlCreate);
    }
}
