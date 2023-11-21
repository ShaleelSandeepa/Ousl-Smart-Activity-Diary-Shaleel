package com.ouslsmartactivitydiary.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.ouslsmartactivitydiary.CourseAdapter;
import com.ouslsmartactivitydiary.item.CourseItem;
import com.ouslsmartactivitydiary.R;
import com.ouslsmartactivitydiary.data.DatabaseHelper;

import java.util.ArrayList;
import java.util.List;

public class AddNewCourse extends AppCompatDialogFragment {

    public static final String TAG = "AddNewCourse";
    Context context;

    //Firebase & local DB
    FirebaseFirestore firestore;
    CollectionReference collectionReference;
    List<String> arrayCategories;
    DatabaseHelper databaseHelper;
    Cursor cursor;

    //widgets
    EditText editTextDegree, editTextAcademicLevel;
    Button buttonAdd;
    ProgressBar progressBar;
    AlertDialog.Builder alertDialog;

    //Recycler
    RecyclerView recyclerCourseDialog, recyclerCourseDialogProgrammes, recyclerCourseDialogLevels;
    List<CourseItem> courseItemList, programmeList, levelsList;
    CourseAdapter courseAdapter, programmeAdapter, levelsAdapter;

    OnDismissListener onDismissListener;

    String programmeDocID;
    String edtTextDegree, edtTextAcademic, searchDegree, searchYear;
    boolean isDegree = false;
    boolean isYear = false;
    boolean isFound = false;

    public static AddNewCourse newInstance() {
        return new AddNewCourse();
    }

    @SuppressLint("ClickableViewAccessibility")
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        context = getContext();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.add_new_course, null);

        firestore = FirebaseFirestore.getInstance();
        databaseHelper = new DatabaseHelper(getContext());

        recyclerCourseDialog = view.findViewById(R.id.recyclerCourseDialog);
        courseItemList = new ArrayList<>();

        recyclerCourseDialogProgrammes = view.findViewById(R.id.recyclerCourseDialogProgrammes);
        programmeList = new ArrayList<>();

        recyclerCourseDialogLevels = view.findViewById(R.id.recyclerCourseDialogLevels);
        levelsList = new ArrayList<>();

        progressBar = view.findViewById(R.id.progressBar);
        editTextDegree = view.findViewById(R.id.editTextDegree);
        editTextAcademicLevel = view.findViewById(R.id.editTextAcademicYear);
        buttonAdd = view.findViewById(R.id.buttonAdd);
        buttonAdd.setEnabled(false);
        buttonAdd.setBackgroundColor(Color.GRAY);

        editTextDegree.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    // The cursor is now active
                    courseItemList.clear();
                    recyclerCourseDialogProgrammes.setVisibility(View.VISIBLE);

                    getProgrammeDocuments();

                    programmeAdapter = new CourseAdapter(getContext(), programmeList, new CourseAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(CourseItem position) {
                            editTextDegree.setText(position.getProgramme());
                            programmeDocID = position.getMainDocId();
                            recyclerCourseDialogProgrammes.setVisibility(View.GONE);
                        }
                    });
                    recyclerCourseDialogProgrammes.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
                    recyclerCourseDialogProgrammes.setAdapter(programmeAdapter);
                    programmeAdapter.notifyDataSetChanged();
                }
                return false;
            }
        });

        editTextDegree.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                buttonAdd.setEnabled(false);
                buttonAdd.setBackgroundColor(Color.GRAY);

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().equals("")) {
                    isDegree = false;
                    buttonAdd.setEnabled(false);
                    buttonAdd.setBackgroundColor(Color.GRAY);
                } else {
                    isDegree = true;
                    if (isYear) {
                        buttonAdd.setEnabled(true);
                        buttonAdd.setBackgroundColor(getResources().getColor(R.color.Theme));
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        editTextAcademicLevel.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    // The cursor is now active
                    courseItemList.clear();
                    recyclerCourseDialogLevels.setVisibility(View.VISIBLE);

                    getLevels(programmeDocID);

                    levelsAdapter = new CourseAdapter(getContext(), levelsList, new CourseAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(CourseItem position) {
                            editTextAcademicLevel.setText(position.getLevel());
                            recyclerCourseDialogLevels.setVisibility(View.GONE);
                        }
                    });
                    recyclerCourseDialogLevels.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
                    recyclerCourseDialogLevels.setAdapter(levelsAdapter);
                    levelsAdapter.notifyDataSetChanged();
                }
                return false;
            }
        });

        editTextAcademicLevel.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().equals("")) {
                    isYear = false;
                    buttonAdd.setEnabled(false);
                    buttonAdd.setBackgroundColor(Color.GRAY);
                } else {
                    isYear = true;
                    if (isDegree) {
                        buttonAdd.setEnabled(true);
                        buttonAdd.setBackgroundColor(getResources().getColor(R.color.Theme));
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onClick(View v) {
                edtTextDegree = editTextDegree.getText().toString();
                edtTextAcademic = editTextAcademicLevel.getText().toString();

                loadCourses();
                courseAdapter = new CourseAdapter(getContext(), courseItemList, new CourseAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(CourseItem position) {
                        alertDialog = new AlertDialog.Builder(requireContext());
                        alertDialog.setTitle("Add Course");
                        alertDialog.setMessage("Add "+position.getCourseCode() +" - "+ position.getCourseName());

                        //When click "Yes" it will execute this
                        alertDialog.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                addCourseFromDialog(position);
                            }
                        });

                        //When click "No" it will execute this
                        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        alertDialog.show();
                    }
                });
                recyclerCourseDialog.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
                recyclerCourseDialog.setAdapter(courseAdapter);
                courseAdapter.notifyDataSetChanged();
            }
        });

        builder.setView(view);
        return builder.create();
    }

    public void loadCourses() {
        progressBar.setVisibility(View.VISIBLE);
        courseItemList.clear();

        cursor = databaseHelper.getAllCourses();

        for (String category : arrayCategories) {
            collectionReference = firestore.collection("courses").document(programmeDocID)
                    .collection(category);
            collectionReference.orderBy(FieldPath.documentId(), Query.Direction.ASCENDING)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @SuppressLint("NotifyDataSetChanged")
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {

                                for (DocumentSnapshot documentSnapshot : task.getResult()) {
                                    isFound = false;
                                    String courseCode = documentSnapshot.getId();
                                    String courseName = documentSnapshot.getString("name");
                                    if (documentSnapshot.getString("level").equals(edtTextAcademic)) {
                                        if (cursor.moveToFirst()) {
                                            do {
                                                if (cursor.getString(1).equals(courseCode)){
                                                    isFound = true;
                                                    break;
                                                }
                                            } while (cursor.moveToNext());
                                        }
                                        if (!isFound) {
                                            courseItemList.add(new CourseItem(6, courseCode, courseName));
                                        }
                                        cursor.moveToFirst();

                                    }
                                }

                                // Notify your adapter or update UI here
                                progressBar.setVisibility(View.GONE);
                                courseAdapter.notifyDataSetChanged();
                            } else {
                                // Handle the error
                                Exception e = task.getException();
                                if (e != null) {
                                    // Log or handle the exception
                                }
                            }
                        }
                    });
        }

    }

    public void getProgrammeDocuments() {
        progressBar.setVisibility(View.VISIBLE);
        programmeList.clear();
        collectionReference = firestore.collection("courses");
        collectionReference.orderBy(FieldPath.documentId(), Query.Direction.ASCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            for (DocumentSnapshot documentSnapshot : task.getResult()) {
                                String programme = documentSnapshot.getString("programmeName");
                                programmeList.add(new CourseItem(documentSnapshot.getId(), programme, 4));
                            }

                            // Notify your adapter or update UI here
                            progressBar.setVisibility(View.GONE);
                            programmeAdapter.notifyDataSetChanged();

                        } else {
                            // Handle the error
                            Exception e = task.getException();
                            if (e != null) {
                                // Log or handle the exception
                            }
                        }
                    }
                });
    }

    public void getLevels(String programmeDocID) {
        progressBar.setVisibility(View.VISIBLE);
        levelsList.clear();
        collectionReference = firestore.collection("courses");
        collectionReference.orderBy(FieldPath.documentId(), Query.Direction.ASCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            for (DocumentSnapshot documentSnapshot : task.getResult()) {
                                if (programmeDocID == null) {
                                    Toast.makeText(context, "Please Select Degree Programme", Toast.LENGTH_SHORT).show();
                                } else {
                                    if (documentSnapshot.getId().equals(programmeDocID)) {
                                        List<String> array = (List<String>) documentSnapshot.get("levels");
                                        arrayCategories = (List<String>) documentSnapshot.get("categories");
                                        for (String level : array) {
                                            levelsList.add(new CourseItem(5, level));
                                        }
                                    }
                                }
                            }

                            // Notify your adapter or update UI here
                            progressBar.setVisibility(View.GONE);
                            levelsAdapter.notifyDataSetChanged();

                        } else {
                            // Handle the error
                            Exception e = task.getException();
                            if (e != null) {
                                // Log or handle the exception
                            }
                        }
                    }
                });
    }

    @SuppressLint("NotifyDataSetChanged")
    public void addCourseFromDialog(CourseItem position) {
        boolean isCourseAdd = databaseHelper.insertCourse(position.getCourseCode(), position.getCourseName());
        if (isCourseAdd) {
            Toast.makeText(context, ("Successfully added "+position.getCourseCode()), Toast.LENGTH_SHORT).show();
            courseItemList.remove(position);
            courseAdapter.notifyDataSetChanged();
        } else {
            Toast.makeText(context, "failed to add course", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        if (onDismissListener != null) {
            onDismissListener.onDismissListen();
        } else {
            Log.e("AddNewCourse", "onDismissListener is null");
        }
    }

    public interface OnDismissListener {
        void onDismissListen();
    }

    public void setOnDismissListener(OnDismissListener onDismissListener) {
        this.onDismissListener = onDismissListener;
    }

}
