package com.example.suren.criminalintent;

import android.Manifest;
import android.content.ContentProvider;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
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
import android.widget.ImageButton;
import android.widget.ImageView;

import java.io.File;
import java.util.Date;
import java.util.UUID;

import static com.example.suren.criminalintent.DatePickerFragment.RESULT_DATE;

public class CrimeFragment extends Fragment {
    private static final String ARG_CRIME_ID = "crime_id";
    public static final int REQUEST_DATE = 0;
    public static final int PICK_REQUEST = 1;
    public static final int REQUEST_PHOTO = 2;

    private Crime mCrime;
    private EditText mTitleField;
    private Button mDateButton;
    private CheckBox mSolved;
    private Button mChooseSuspectButton;
    private Button mSendReportButton;
    private Button mCallSuspectButton;
    private ImageButton mPhotoButton;
    private ImageView mPhotoView;
    private File mPhotoFile;

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
            mPhotoFile = crimeLab.getPhotoFile(mCrime);
        }
        // get access to contacts
        if (ActivityCompat.checkSelfPermission(getContext(),
                Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.READ_CONTACTS}, 1);
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
                Intent i = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(i, PICK_REQUEST);
            }
        });

        mSendReportButton = (Button)v.findViewById(R.id.send_crime_report);
        mSendReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_TEXT, getCrimeReport());
                i.putExtra(Intent.EXTRA_SUBJECT, mCrime.getTitle());
                i = Intent.createChooser(i, getString(R.string.crime_report_chooser_title));
                startActivity(i);
            }
        });

        mCallSuspectButton = (Button)v.findViewById(R.id.call_suspect);
        mCallSuspectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mCrime.getSuspect() != null) {
                    Cursor contactCursor = null;
                    Cursor phoneCursor = null;
                    try {
                        contactCursor = getActivity().getContentResolver().query(
                                ContactsContract.Contacts.CONTENT_URI,
                                new String[] { ContactsContract.Contacts._ID },
                                ContactsContract.Contacts.DISPLAY_NAME + " = ?",
                                new String[] { mCrime.getSuspect() },
                                null);
                        if (contactCursor.moveToFirst()) {
                            phoneCursor = getActivity().getContentResolver().query(
                                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                    new String[] { ContactsContract.CommonDataKinds.Phone.NUMBER },
                                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                                    new String[] { Integer.toString(contactCursor.getInt(0)) },
                                    null);
                            if (phoneCursor.moveToFirst()) {
                                Intent i = new Intent(Intent.ACTION_DIAL);
                                i.setData(Uri.parse("tel:" + phoneCursor.getString(0)));
                                startActivity(i);
                            }
                        }
                    }
                    finally {
                        contactCursor.close();
                        if (phoneCursor != null) {
                            phoneCursor.close();
                        }
                    }
                }
            }
        });

        updateButtons();

        PackageManager packageManager = getActivity().getPackageManager();
        // if we can't handle Intent.ACTION_PICK then disable mChooseSuspectButton
        Intent pickIntent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        if (pickIntent.resolveActivity(packageManager) == null) {
            mChooseSuspectButton.setEnabled(false);
        }

        mPhotoButton = (ImageButton)v.findViewById(R.id.camera_button);
        mPhotoView = (ImageView)v.findViewById(R.id.crime_photo);

        Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        mPhotoButton.setEnabled(mPhotoFile != null &&
                captureImage.resolveActivity(packageManager) != null);
        mPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                Uri uri = Uri.fromFile(mPhotoFile);
                captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE), REQUEST_PHOTO);
            }
        });
        updatePhotoView();
        return v;
    }

    private void updatePhotoView() {
        // TODO: have to implement
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
                        updateButtons();
                    }
                    finally {
                        cursor.close();
                    }
                }
                break;
            case (REQUEST_PHOTO):
                updatePhotoView();
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

    private void updateButtons() {
        if (mCrime.getSuspect() != null) {
            mChooseSuspectButton.setText(
                getString(R.string.crime_report_suspect, mCrime.getSuspect()));
            mCallSuspectButton.setEnabled(true);
        } else {
            mChooseSuspectButton.setText(getString(R.string.choose_suspect_label));
            mCallSuspectButton.setEnabled(false);
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
