package com.example.suren.criminalintent;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class PhotoFragment extends DialogFragment {
    public static final String DLG_TAG = "PhotoFragment";

    private static final String ARG_FILE_PATH = "file_path";

    private String mFilePath;

    public static PhotoFragment newInstance(String filePath) {
        PhotoFragment fragment = new PhotoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_FILE_PATH, filePath);
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.date_dialog_content, null);
        AlertDialog dlg = new AlertDialog.Builder(getActivity()).
                setView(view).
                create();

        ImageView imageView = (ImageView)view.findViewById(R.id.photo_view);
        if (getArguments() != null) {
            String filePath = getArguments().getString(ARG_FILE_PATH);
            imageView.setImageBitmap(PictureUtils.getScaledBitmap(mFilePath, getActivity()));
        } else {
            imageView.setImageBitmap(null);
        }
        return dlg;
    }
}
