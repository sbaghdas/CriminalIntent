package com.example.suren.criminalintent;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by Suren on 12/4/16.
 */

public class DatePickerFragment extends DialogFragment {
    public static final String DLG_TAG = "DatePickerFragment";
    public static final String ARG_DATE = "date";
    public static final String RESULT_DATE =
            "com.example.suren.criminalintent.DatePickerFragment.date";

    private DatePicker mDatePicker;

    public static DatePickerFragment newInstance(Date date) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_DATE, date);
        DatePickerFragment fragment = new DatePickerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.date_dialog_content, null);
        //return super.onCreateDialog(savedInstanceState);
        AlertDialog dlg = new AlertDialog.Builder(getActivity()).
                setTitle(R.string.date_dialog_title).
                setView(view).
                setPositiveButton(R.string.date_dialog_ok_label, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Fragment targetFragment = getTargetFragment();
                        Intent intent = new Intent();
                        Calendar calendar = GregorianCalendar.getInstance();
                        calendar.set(mDatePicker.getYear(),
                                mDatePicker.getMonth(),
                                mDatePicker.getDayOfMonth());
                        intent.putExtra(RESULT_DATE, calendar.getTime());
                        if (targetFragment == null) {
                            getActivity().setResult(Activity.RESULT_OK, intent);
                        } else {
                            targetFragment.onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
                        }
                    }
                }).
                create();
        Date date = (Date)getArguments().getSerializable(ARG_DATE);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        mDatePicker = (DatePicker)view.findViewById(R.id.datePicker);
        mDatePicker.init(calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH),
                null);
        return dlg;
    }
}
