package com.example.suren.criminalintent;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;

public class CrimeListActivity extends SimpleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new CrimeListFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_masterdetail;
    }
}
