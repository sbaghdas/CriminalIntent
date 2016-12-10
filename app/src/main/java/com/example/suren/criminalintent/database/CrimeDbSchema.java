package com.example.suren.criminalintent.database;

/**
 * Created by Suren on 12/10/16.
 */

public class CrimeDbSchema {
    public static final class CrimeTable {
        public static final String NAME = "crimes";
    }
    public static final class Column {
        public static final String UUID = "uuid";
        public static final String TITLE = "title";
        public static final String DATE = "date";
        public static final String SOLVED = "solved";
    }
}
