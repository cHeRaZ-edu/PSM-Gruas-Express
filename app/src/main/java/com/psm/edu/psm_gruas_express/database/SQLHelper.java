package com.psm.edu.psm_gruas_express.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.psm.edu.psm_gruas_express.database.table.GruaSessionDataSource;
import com.psm.edu.psm_gruas_express.database.table.UserSessionDataSource;

public class SQLHelper extends SQLiteOpenHelper {
    private static final String dbName = "gruasExpress.db";
    private  static  final int version = 1;

    public SQLHelper(Context context) {
        super(context, SQLHelper.dbName, null, SQLHelper.version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(UserSessionDataSource.CREATE_TABLE);
        db.execSQL(GruaSessionDataSource.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(UserSessionDataSource.DROP_TABLE);
        db.execSQL(GruaSessionDataSource.DROP_TABLE);
        onCreate(db);
    }
}
