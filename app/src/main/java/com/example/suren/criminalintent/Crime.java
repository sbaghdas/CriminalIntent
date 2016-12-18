package com.example.suren.criminalintent;

import java.util.Date;
import java.util.UUID;

/**
 * Created by Suren on 11/25/16.
 */

public class Crime {
    private UUID mId;
    private String mTitle;
    private Date mDate;
    private boolean mSolved;
    private String mSuspect;

    public Crime() {
        this.mId = UUID.randomUUID();
        mDate = new Date();
    }

    public Crime(String uuid) {
        this.mId = UUID.fromString(uuid);
        mDate = new Date();
    }

    public Crime setTitle(String title) {
        mTitle = title;
        return this;
    }

    public Crime setDate(Date date) {
        mDate = date;
        return this;
    }

    public Crime setSolved(boolean solved) {
        mSolved = solved;
        return this;
    }

    public Crime setSuspect(String suspect) {
        mSuspect = suspect;
        return this;
    }

    public UUID getId() {

        return mId;
    }

    public String getTitle() {
        return mTitle;
    }
    public Date getDate() {

        return mDate;
    }

    public boolean isSolved() {
        return mSolved;
    }

    public String getSuspect() {
        return mSuspect;
    }

    public String getPhotoFileName() {
        return "IMG_" + getId() + ".jpg";
    }
}
