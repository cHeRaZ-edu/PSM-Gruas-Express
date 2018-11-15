package com.psm.edu.psm_gruas_express.database.table;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.psm.edu.psm_gruas_express.database.SQLHelper;
import com.psm.edu.psm_gruas_express.models.User;

public class UserSessionDataSource extends SQLHelper {
    static final String TABLE_NAME = "user_session";
    static final String COLUMN_ID = "id";
    static final String COLUMN_NAME = "name";
    static final String COLUMN_LAST_NAME = "last_name";
    static final String COLUMN_NICKNAME = "nickname";
    static final String COLUMN_EMAIL = "email";
    static final String COLUMN_PASSWORD = "password";
    static final String COLUMN_IMG_URL = "imageURL";
    static final String COLUMN_IMG_URL_BACKGROUND = "imgBackgroundURL";
    static final String COLUMN_PROVIDER = "provider";
    static final String COLUMN_PHONE = "phone";

    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    +COLUMN_ID + " INTEGER,"  // " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    +COLUMN_NAME + " TEXT,"
                    +COLUMN_LAST_NAME + " TEXT,"
                    +COLUMN_NICKNAME + " TEXT,"
                    +COLUMN_EMAIL + " TEXT,"
                    +COLUMN_PASSWORD + " TEXT,"
                    +COLUMN_IMG_URL + " TEXT,"
                    +COLUMN_IMG_URL_BACKGROUND + " TEXT,"
                    +COLUMN_PROVIDER + " TEXT,"
                    +COLUMN_PHONE + " TEXT"
                    +")";
    public static final String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

    private static final String DELETE_TABLE = "DELETE FROM " + TABLE_NAME + ";";

    public UserSessionDataSource(Context context) {
        super(context);
    }

    private void deleteUserSession(){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(DELETE_TABLE);
        db.close();
    }

    private void insertTableUser(User user){
        ContentValues values = new ContentValues();
        values.put(COLUMN_ID,user.getId());
        values.put(COLUMN_NAME,user.getName());
        values.put(COLUMN_LAST_NAME,user.getLastName());
        values.put(COLUMN_NICKNAME,user.getNickname());
        values.put(COLUMN_EMAIL,user.getPassword());
        values.put(COLUMN_PASSWORD,user.getEmail());
        values.put(COLUMN_IMG_URL,user.getImageURL());
        values.put(COLUMN_IMG_URL_BACKGROUND,user.getImageBackgroundURL());
        values.put(COLUMN_PROVIDER,user.getProvider());
        values.put(COLUMN_PHONE,user.getPhone());
        SQLiteDatabase db = getWritableDatabase();
        long id = db.insert(TABLE_NAME,null,values);//retorna id
        db.close();
    }

    public void setUserSession(User user) {
        deleteUserSession();
        insertTableUser(user);
    }

    public User SelectTableUser() {
        User user = new User();
        user.setId(-1);
        SQLiteDatabase db = getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME,null);//Permite utilizar la sintaxis de sql completo

        while(cursor.moveToNext())//Obtiene la posicion anterior al ultimo
        {
            int id = cursor.getInt( cursor.getColumnIndex(COLUMN_ID));
            String name = cursor.getString( cursor.getColumnIndex(COLUMN_NAME));
            String last_name = cursor.getString(cursor.getColumnIndex(COLUMN_LAST_NAME));
            String nickname = cursor.getString(cursor.getColumnIndex(COLUMN_NICKNAME));
            String password = cursor.getString(cursor.getColumnIndex(COLUMN_PASSWORD));
            String email = cursor.getString(cursor.getColumnIndex(COLUMN_EMAIL));
            String imgURL = cursor.getString(cursor.getColumnIndex(COLUMN_IMG_URL));
            String imgBackgroundURL = cursor.getString(cursor.getColumnIndex(COLUMN_IMG_URL_BACKGROUND));
            String provider = cursor.getString(cursor.getColumnIndex(COLUMN_PROVIDER));
            String phone = cursor.getString(cursor.getColumnIndex(COLUMN_PHONE));

            user.setId(id);
            user.setName(name);
            user.setLastName(last_name);
            user.setNickname(nickname);
            user.setPassword(password);
            user.setEmail(email);
            user.setImageURL(imgURL);
            user.setImageBackgroundURL(imgBackgroundURL);
            user.setProvider(provider);
            user.setPhone(phone);
        }
        cursor.close();

        db.close();

        return user;
    }


}
