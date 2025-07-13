package com.example.smarttuitionmanager;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.*;
import android.widget.*;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import java.util.*;

public class StudentAssignment extends Fragment {

    private Spinner spinnerCourse;
    private EditText editTextTitle;
    private TextView selectedFileText;
    private Uri selectedPdfUri;
    private LinearLayout uploadedListLayout;

    private final List<String> dummySubjects = Arrays.asList("Maths", "Science", "English");

    private final ActivityResultLauncher<Intent> pdfPickerLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == getActivity().RESULT_OK && result.getData() != null) {
                    selectedPdfUri = result.getData().getData();
                    String fileName = selectedPdfUri.getLastPathSegment();
                    selectedFileText.setText("Selected: " + fileName);
                }
            });

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_student_assignment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button courseBtn = view.findViewById(R.id.Cmaterial);
        Button assignmentsBtn = view.findViewById(R.id.assignments);
        Button resultsBtn = view.findViewById(R.id.results);
        Button selectPdfBtn = view.findViewById(R.id.btn_select_pdf);
        Button uploadBtn = view.findViewById(R.id.btn_upload);

        spinnerCourse = view.findViewById(R.id.spinner_course);
        editTextTitle = view.findViewById(R.id.editText_title);
        selectedFileText = view.findViewById(R.id.text_selected_file);

        // ✅ Get reference to main vertical layout
        LinearLayout mainLayout = view.findViewById(R.id.main_assignment_layout);
        uploadedListLayout = new LinearLayout(requireContext());
        uploadedListLayout.setOrientation(LinearLayout.VERTICAL);
        mainLayout.addView(uploadedListLayout); // ✅ Append below upload button

        // ✅ Set spinner values
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, dummySubjects);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCourse.setAdapter(adapter);

        // ✅ Navigation
        courseBtn.setOnClickListener(v -> replaceFragment(new StudentCourseGuide()));
        resultsBtn.setOnClickListener(v -> replaceFragment(new StudentResults()));
        assignmentsBtn.setEnabled(false); // You're already on this screen

        // ✅ PDF Picker
        selectPdfBtn.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("application/pdf");
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            pdfPickerLauncher.launch(intent);
        });

        // ✅ Upload simulation
        uploadBtn.setOnClickListener(v -> {
            String title = editTextTitle.getText().toString().trim();
            String subject = spinnerCourse.getSelectedItem().toString();

            if (title.isEmpty()) {
                editTextTitle.setError("Enter title");
                return;
            }

            if (selectedPdfUri == null) {
                Toast.makeText(getContext(), "Please select a PDF", Toast.LENGTH_SHORT).show();
                return;
            }

            displayUploadedItem(subject, title);
            clearForm();
        });
    }

    private void displayUploadedItem(String subject, String title) {
        TextView item = new TextView(requireContext());
        item.setText("📄 " + subject + " - " + title);
        item.setTextColor(getResources().getColor(android.R.color.black));
        item.setPadding(8, 8, 8, 8);
        uploadedListLayout.addView(item);
    }

    private void clearForm() {
        editTextTitle.setText("");
        selectedFileText.setText("No file selected");
        selectedPdfUri = null;
    }

    private void replaceFragment(Fragment fragment) {
        FragmentTransaction transaction = requireActivity()
                .getSupportFragmentManager()
                .beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
