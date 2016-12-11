package com.example.suren.criminalintent;

import android.content.ContentProvider;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import java.util.Date;
import java.util.UUID;

import static com.example.suren.criminalintent.DatePickerFragment.RESULT_DATE;

public class CrimeFragment extends Fragment {
    private static final String ARG_CRIME_ID = "crime_id";
    public static final int REQUEST_DATE = 0;
    public static final int PICK_REQUEST = 1;


    private Crime mCrime;
    private EditText mTitleField;
    private Button mDateButton;
    private CheckBox mSolved;
    private Button mChooseSuspectButton;
    private Button mSendReportButton;
    private Button mCallSuspectButton;

    public CrimeFragment() {
    }

    public static CrimeFragment newInstance(UUID id) {
        CrimeFragment fragment = new CrimeFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_CRIME_ID, id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        CrimeLab crimeLab = CrimeLab.getInstance(getContext());
        UUID id = (UUID)getArguments().getSerializable(ARG_CRIME_ID);
        if (id != null) {
            mCrime = crimeLab.getCrime(id);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_crime, container, false);
        mTitleField = (EditText)v.findViewById(R.id.crime_title);
        mTitleField.setText(mCrime.getTitle());
        mTitleField.addTextChangedListener(new TextWatcher() {
           @Override
           public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

           }

           @Override
           public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
               mCrime.setTitle(charSequence.toString());
           }

           @Override
           public void afterTextChanged(Editable editable) {

           }
        }
        );
        mDateButton = (Button)v.findViewById(R.id.crime_date);
        updateDate();
        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerFragment dateDialog = DatePickerFragment.newInstance(mCrime.getDate());
                dateDialog.setTargetFragment(CrimeFragment.this, REQUEST_DATE);
                dateDialog.show(getFragmentManager(), DatePickerFragment.DLG_TAG);
            }
        });
        mSolved = (CheckBox)v.findViewById(R.id.crime_solved);
        mSolved.setChecked(mCrime.isSolved());
        mSolved.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                mCrime.setSolved(isChecked);
            }
        });

        mChooseSuspectButton = (Button)v.findViewById(R.id.choose_suspect);
        mChooseSuspectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i= new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(i, PICK_REQUEST);
            }
        });
        updateSuspectButtonTitle();

        mSendReportButton = (Button)v.findViewById(R.id.send_crime_report);
        mCallSuspectButton = (Button)v.findViewById(R.id.call_suspect);

        return v;
    }

    @Override
    public void onPause() {
        super.onPause();
        CrimeLab.getInstance(getContext()).updateCrime(mCrime);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case (REQUEST_DATE):
                mCrime.setDate((Date)data.getSerializableExtra(DatePickerFragment.RESULT_DATE));
                updateDate();
                break;
            case (PICK_REQUEST):
                if (data != null) {
                    Uri uri = data.getData();
                    Cursor cursor = getActivity().getContentResolver().query(uri,
                            new String[] { ContactsContract.Contacts.DISPLAY_NAME },
                            null, null, null);
                    try {
                        if (cursor.moveToFirst()) {
                            mCrime.setSuspect(cursor.getString(0));
                        } else {
                            mCrime.setSuspect(null);
                        }
                        updateSuspectButtonTitle();
                    }
                    finally {
                        cursor.close();
                    }
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case (R.id.menu_item_delete_crime):
                CrimeLab.getInstance(getContext()).deleteCrime(mCrime);
                getActivity().finish();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateDate() {
        mDateButton.setText(mCrime.getDate().toString());
    }

    private void updateSuspectButtonTitle() {
        if (mCrime.getSuspect() != null) {
            mChooseSuspectButton.setText(
                getString(R.string.crime_report_suspect, mCrime.getSuspect()));
        } else {
            mChooseSuspectButton.setText(getString(R.string.choose_suspect_label));
        }
    }

    private String getCrimeReport() {
        String dateStr = DateFormat.format("yyyy-MM-dd hh:mm:ss a", mCrime.getDate()).toString();
        String report = getString(R.string.crime_report,
                mCrime.getTitle(), dateStr,
                mCrime.isSolved() ?
                        getString(R.string.crime_report_solved) :
                        getString(R.string.crime_report_unsolved),
                (mCrime.getSuspect() != null) ?
                        getString(R.string.crime_report_suspect, mCrime.getSuspect()) :
                        getString(R.string.crime_report_nosuspect));
        return report;
    }
}
