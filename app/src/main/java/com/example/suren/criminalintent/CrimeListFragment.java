package com.example.suren.criminalintent;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
    private RecyclerView mCrimeRecyclerView;
    private CrimeAdapter mCrimeAdapter;
    private int mSelectedPosition = -1;

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
            /*
            Toast toast = Toast.makeText(view.getContext(), "Crime " +
                    mCrime.getTitle() + " is clicked!", Toast.LENGTH_SHORT);
            toast.show();
            */
            /*
            mSelectedPosition = mPosition;
            startActivity(CrimeActivity.newIntent(getContext(), mCrime.getId()));
            */
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
        /*
        if (mSelectedPosition != -1) {
            mCrimeAdapter.notifyItemChanged(mSelectedPosition);
            mSelectedPosition = -1;
        }
        */
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_item_new_crime) {
            Crime crime = new Crime();
            int position = CrimeLab.getInstance(getContext()).addCrime(crime);
            startCrimeDetailsActivity(position);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime_list, menu);
    }
}