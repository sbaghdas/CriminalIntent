package com.example.suren.criminalintent;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.suren.criminalintent.database.CrimeBaseHelper;
import com.example.suren.criminalintent.database.CrimeDbSchema;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by Suren on 11/27/16.
 */

public class CrimeLab {
    private static CrimeLab sCrimeLab;
    private Context mContext;
    private CrimeBaseHelper mDbHelper;
    private SQLiteDatabase mDb;


    private CrimeLab(Context context) {
        // save application context because by storing activity context in
        // a static object will lead to that activity never being destroyed
        mContext = context.getApplicationContext();
        mDbHelper = new CrimeBaseHelper(context);
        mDb = mDbHelper.getWritableDatabase();
    }

    public static CrimeLab getInstance(Context context) {
        if (sCrimeLab == null) {
            sCrimeLab = new CrimeLab(context);
        }
        return sCrimeLab;
    }

    private ContentValues getContentValues(Crime crime) {
        ContentValues res = new ContentValues();
        res.put(CrimeDbSchema.Column.UUID, crime.getId().toString());
        res.put(CrimeDbSchema.Column.TITLE, crime.getTitle());
        res.put(CrimeDbSchema.Column.DATE, crime.getDate().getTime());
        res.put(CrimeDbSchema.Column.SOLVED, crime.isSolved() ? 1 : 0);
        res.put(CrimeDbSchema.Column.SUSPECT, crime.getSuspect());
        return res;
    }

    private Crime createCrime(Cursor cursor) {
        return new Crime(cursor.getString(cursor.getColumnIndex(CrimeDbSchema.Column.UUID))).
                setTitle(cursor.getString(cursor.getColumnIndex(CrimeDbSchema.Column.TITLE))).
                setDate(new Date(cursor.getInt(cursor.getColumnIndex(CrimeDbSchema.Column.DATE)))).
                setSolved(cursor.getInt(cursor.getColumnIndex(CrimeDbSchema.Column.SOLVED)) != 0).
                setSuspect(cursor.getString(cursor.getColumnIndex(CrimeDbSchema.Column.SUSPECT)));
    }

    public List<Crime> getCrimes() {
        Cursor c = mDb.query(CrimeDbSchema.CrimeTable.NAME,
                null, null, null, null, null, null);
        List<Crime> list = new ArrayList<>(c.getCount());
        c.moveToFirst();
        while (!c.isAfterLast()) {
            list.add(createCrime(c));
            c.moveToNext();
        }
        c.close();
        return list;
    }

    public Crime getCrime(UUID id) {
        Cursor c = mDb.query(CrimeDbSchema.CrimeTable.NAME,
                null, CrimeDbSchema.Column.UUID + " = ?",
                new String[]{ id.toString() },
                null, null, null);
        Crime crime = null;
        if (c.moveToFirst()) {
            crime = createCrime(c);
        }
        c.close();
        return crime;
    }

    public void addCrime(Crime crime) {
        ContentValues values = getContentValues(crime);
        mDb.insert(CrimeDbSchema.CrimeTable.NAME, null, values);
    }

    public boolean updateCrime(Crime crime) {
        ContentValues values = getContentValues(crime);
        int rowsUpdated = mDb.update(CrimeDbSchema.CrimeTable.NAME, values,
                CrimeDbSchema.Column.UUID + " = ?",
                new String[] { crime.getId().toString() });
        return (rowsUpdated > 0);
    }

    public boolean deleteCrime(Crime crime) {
        int rowsDeleted = mDb.delete(CrimeDbSchema.CrimeTable.NAME,
                CrimeDbSchema.Column.UUID + " = ?",
                new String[] { crime.getId().toString() } );
        return (rowsDeleted > 0);
    }
}
