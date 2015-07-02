package com.kaushiksamba.contacts;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler extends SQLiteOpenHelper
{

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "contactsManager";
    public static final String TABLE_NAME = "contacts";

    //Columns
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_IMG_URL = "imgurl";

    public DatabaseHandler(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_NAME + "(" + COLUMN_NAME + " TEXT, " + COLUMN_IMG_URL + " TEXT);";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME + ";");
        onCreate(db);
    }
    public void addContact(EachContact contact)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_NAME, contact.getName());
        contentValues.put(COLUMN_IMG_URL, contact.getImg_url());

        db.insert(TABLE_NAME, null, contentValues);
        db.close();
    }

    public void deleteContact(EachContact contact)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME,COLUMN_NAME+"=?",new String[]{String.valueOf(contact.getName())});
        db.close();
    }

    public List<EachContact> getAllContacts()
    {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT rowid,* FROM " + TABLE_NAME, null);

        List<EachContact> list = new ArrayList<EachContact>();

        if(cursor.moveToFirst())
        {
            do
            {
                EachContact contact = new EachContact();
                contact.setId(Integer.parseInt(cursor.getString(0)));
                contact.setName(cursor.getString(1));
                contact.setImg_url(cursor.getString(2));
                list.add(contact);
            }while(cursor.moveToNext());
        }
        cursor.close();
        return list;
    }
    public int getContactCount()
    {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME;
        Cursor cursor = db.rawQuery(query,null);
        int x = cursor.getCount();
        cursor.close();
        return x;
    }
    public int updateContact(String originalname, EachContact contact)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_NAME,contact.getName());
        contentValues.put(COLUMN_IMG_URL,contact.getImg_url());

        return db.update(TABLE_NAME,contentValues,COLUMN_NAME + "=?",new String[]{originalname});
    }
}
