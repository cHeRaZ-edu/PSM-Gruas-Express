package com.psm.edu.psm_gruas_express.database.table;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.psm.edu.psm_gruas_express.database.SQLHelper;
import com.psm.edu.psm_gruas_express.models.Grua;

public class GruaSessionDataSource extends SQLHelper {

    static final String TABLE_NAME = "grua_session";
    static final String COLUMN_ID = "id";
    static final String COLUMN_NAME = "name";
    static final String COLUMN_DESC = "description";
    static final String COLUMN_IMG_URL = "imageURL";

    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    +COLUMN_ID + " INTEGER,"
                    +COLUMN_NAME + " TEXT,"
                    +COLUMN_DESC + " TEXT,"
                    +COLUMN_IMG_URL + " TEXT"
                    +")";
    public static final String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

    private static final String DELETE_TABLE = "DELETE FROM " + TABLE_NAME + ";";

    public GruaSessionDataSource(Context context) {
        super(context);
    }

    private void deleteGruaSession(){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(DELETE_TABLE);
        db.close();
    }

    private void insertTableGrua(Grua grua){
        ContentValues values = new ContentValues();
        values.put(COLUMN_ID,grua.getId());
        values.put(COLUMN_NAME,grua.getName());
        values.put(COLUMN_DESC,grua.getDescription());
        values.put(COLUMN_IMG_URL,grua.getImageURL());
        SQLiteDatabase db = getWritableDatabase();
        long id = db.insert(TABLE_NAME,null,values);//retorna id
        db.close();
    }

    public void setGruaSession(Grua grua) {
        deleteGruaSession();
        insertTableGrua(grua);
    }

    public Grua SelectTableUser() {
        Grua grua = new Grua();
        grua.setId(-1);
        SQLiteDatabase db = getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME,null);//Permite utilizar la sintaxis de sql completo

        while(cursor.moveToNext())//Obtiene la posicion anterior al ultimo
        {
            int id = cursor.getInt( cursor.getColumnIndex(COLUMN_ID));
            String name = cursor.getString( cursor.getColumnIndex(COLUMN_NAME));
            String desc = cursor.getString(cursor.getColumnIndex(COLUMN_DESC));
            String imgURL = cursor.getString(cursor.getColumnIndex(COLUMN_IMG_URL));

            grua.setId(id);
            grua.setName(name);
            grua.setDescription(desc);
            grua.setImageURL(imgURL);
        }
        cursor.close();
        db.close();

        return grua;
    }


}
