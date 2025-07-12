package com.example.smarttuitionmanager;

import android.app.Dialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import java.util.ArrayList;

public class DrillDownListDialogFragment extends DialogFragment {
    private static final String ARG_TITLE = "title";
    private static final String ARG_LIST = "list";

    public static DrillDownListDialogFragment newInstance(String title, ArrayList<String> list) {
        DrillDownListDialogFragment fragment = new DrillDownListDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        args.putStringArrayList(ARG_LIST, list);
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        String title = getArguments() != null ? getArguments().getString(ARG_TITLE) : "Details";
        ArrayList<String> list = getArguments() != null ? getArguments().getStringArrayList(ARG_LIST) : new ArrayList<>();

        View view = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_list_item_1, null);
        ListView listView = new ListView(getContext());
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener((parent, v, position, id) -> dismiss());

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext())
                .setTitle(title)
                .setView(listView)
                .setNegativeButton("Close", (dialog, which) -> dialog.dismiss());
        return builder.create();
    }
} 