package com.example.suren.criminalintent;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;

import java.util.Iterator;
import java.util.List;

public class CrimeListActivity extends SimpleFragmentActivity
    implements CrimeListFragment.Callbacks, CrimeFragment.Callbacks {

    @Override
    protected Fragment createFragment() {
        return new CrimeListFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_masterdetail;
    }

    @Override
    public void onCrimeSelected(Crime crime) {
        View view = findViewById(R.id.details_container);
        if (view == null) {
            startCrimeDetailsActivity(crime);
        } else {
            CrimeFragment detailsFragment = CrimeFragment.newInstance(crime.getId());
            getSupportFragmentManager().beginTransaction().
                    replace(R.id.details_container, detailsFragment).
                    commit();
        }
    }

    private void startCrimeDetailsActivity(Crime crime) {
        int position = 0;
        List<Crime> crimes = CrimeLab.getInstance(this).getCrimes();
        Iterator<Crime> iter = crimes.iterator();
        while (iter.hasNext()) {
            if (iter.next().getId().equals(crime.getId())) {
                break;
            }
            position++;
        }
        startActivity(CrimePagerActivity.newIntent(this, position));
    }

    @Override
    public void onCrimeChanged(Crime crime) {
        CrimeListFragment listFragment = (CrimeListFragment)getSupportFragmentManager().
                findFragmentById(R.id.fragment_container);
        listFragment.updateUI();
    }
}
