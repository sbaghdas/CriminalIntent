package com.example.suren.criminalintent;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class CrimeListFragment extends Fragment {
    private static final String SAVED_SUBTITLE_VISIBLE = "subtitle";
    private RecyclerView mCrimeRecyclerView;
    private CrimeAdapter mCrimeAdapter;
    private int mSelectedPosition = -1;
    private boolean mSubtitleVisible = false;

    private class CrimeHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        private CheckBox mCrimeSolvedCheckBox;
        private TextView mCrimeTitleTextView;
        private TextView mCrimeDateTextView;
        private Crime mCrime = null;
        private int mPosition;

        public CrimeHolder(View itemView) {
            super(itemView);
            mCrimeSolvedCheckBox = (CheckBox)itemView.findViewById(R.id.list_item_crime_solved_check_box);
            mCrimeTitleTextView = (TextView)itemView.findViewById(R.id.list_item_title_text_view);
            mCrimeDateTextView = (TextView)itemView.findViewById(R.id.list_item_date_text_view);
            itemView.setOnClickListener(this);
        }

        public void bindCrime(Crime crime, int position) {
            mCrime = crime;
            mPosition = position;
            mCrimeSolvedCheckBox.setChecked(crime.isSolved());
            mCrimeTitleTextView.setText(crime.getTitle());
            mCrimeDateTextView.setText(crime.getDate().toString());
        }

        @Override
        public void onClick(View view) {
            startCrimeDetailsActivity(mPosition);
        }
    }

    private void startCrimeDetailsActivity(int position) {
        mSelectedPosition = position;
        startActivity(CrimePagerActivity.newIntent(getContext(), position));
    }

    private class CrimeAdapter extends RecyclerView.Adapter<CrimeHolder> {
        private List<Crime> mCrimes;

        public CrimeAdapter(List<Crime> crimes) {
            mCrimes = crimes;
        }

        @Override
        public CrimeHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View v = layoutInflater.inflate(R.layout.crime_list_item, parent, false);
            return new CrimeHolder(v);
        }

        @Override
        public void onBindViewHolder(CrimeHolder holder, int position) {
            holder.bindCrime(mCrimes.get(position), position);
        }

        @Override
        public int getItemCount() {
            return mCrimes.size();
        }
    }

    public CrimeListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mSubtitleVisible = (savedInstanceState != null) &&
                savedInstanceState.getBoolean(SAVED_SUBTITLE_VISIBLE, false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_crime_list, container, false);

        CrimeLab crimeLab = CrimeLab.getInstance(getActivity());
        mCrimeAdapter = new CrimeAdapter(crimeLab.getCrimes());

        mCrimeRecyclerView = (RecyclerView)v.findViewById(R.id.crime_view_recycler);
        mCrimeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mCrimeRecyclerView.setAdapter(mCrimeAdapter);

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mSelectedPosition != -1) {
            mCrimeAdapter.notifyDataSetChanged();
        }
        updateSubtitle();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(SAVED_SUBTITLE_VISIBLE, mSubtitleVisible);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case (R.id.menu_item_new_crime):
                Crime crime = new Crime();
                int position = CrimeLab.getInstance(getContext()).addCrime(crime);
                startCrimeDetailsActivity(position);
                return true;
            case (R.id.menu_item_show_subtitle):
                mSubtitleVisible = !mSubtitleVisible;
                getActivity().invalidateOptionsMenu();
                updateSubtitle();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateSubtitle() {
        int crimeCount = CrimeLab.getInstance(getContext()).getCrimes().size();
        String subtitle;
        if (mSubtitleVisible) {
            if (crimeCount > 0) {
                subtitle = getResources().getQuantityString(R.plurals.subtitle_plural,
                        crimeCount, crimeCount);
            } else {
                subtitle = getString(R.string.no_crimes);
            }
        }
        else {
            subtitle = null;
        }
        AppCompatActivity activity = (AppCompatActivity)getActivity();
        activity.getSupportActionBar().setSubtitle(subtitle);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime_list, menu);
        MenuItem subtitleMenu = menu.findItem(R.id.menu_item_show_subtitle);
        if (mSubtitleVisible) {
            subtitleMenu.setTitle(R.string.hide_subtitle);
        }
        else {
            subtitleMenu.setTitle(R.string.show_subtitle);
        }
    }
}