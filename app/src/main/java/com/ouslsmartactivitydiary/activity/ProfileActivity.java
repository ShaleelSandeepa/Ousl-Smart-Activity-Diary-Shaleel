package com.ouslsmartactivitydiary.activity;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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

public class ProfileActivity extends AppCompatActivity {

    private ImageView backIcon;
    private AppCompatTextView textView;
    private Toolbar actionBar;

    View optionLayout;
    TextView clearProfile;
    LinearLayout optionMenu;
    AlertDialog.Builder alertDialog;

    TextView txtName, txtRegNo, txtSNumber, txtEmail, txtProgramme, txtCenter, txtPhone, txtAddress;
    EditText edtName, edtRegNo, edtSNumber, edtEmail, edtProgramme, edtCenter, edtPhone, edtAddress;
    String strName, strRegNo, strSNumber, strEmail, strProgramme, strCenter, strPhone, strAddress;
    AppCompatButton editDetails;
    TextView userName,userEmail;
    int accountID, profileCount;
    String accountUserRegNo;

    List<CourseItem> programmeList;
    RecyclerView recyclerCourseDialogProgrammes;
    CourseAdapter programmeAdapter;

    //Firebase & local DB
    FirebaseFirestore firestore;
    CollectionReference collectionReference;

    DatabaseHelper databaseHelper;
    Cursor cursor;

    @SuppressLint("ClickableViewAccessibility")
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        actionBar = findViewById(R.id.myActionBar);
        //Change action bar title
        textView = findViewById(R.id.title_actionbar);
        textView.setText("Profile");

        backIcon = findViewById(R.id.backIcon);
        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        //change the status bar color
        getWindow().setStatusBarColor(getColor(R.color.white));
        //change the status bar to dark theme
        getWindow().getDecorView().setBackgroundColor(getColor(R.color.white));
        //change status bar color to dark
        if (Build.VERSION.SDK_INT >= 23){
            View decor = getWindow().getDecorView();
            if (decor.getSystemUiVisibility()!= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)
                decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            else
                decor.setSystemUiVisibility(0);
        }

        firestore = FirebaseFirestore.getInstance();
        databaseHelper = new DatabaseHelper(this);

        userName = findViewById(R.id.userName);
        userEmail = findViewById(R.id.userEmail);

        editDetails = findViewById(R.id.editDetails);

        txtName = findViewById(R.id.txtName);
        txtRegNo = findViewById(R.id.txtRegNo);
        txtSNumber = findViewById(R.id.txtSNumber);
        txtEmail = findViewById(R.id.txtEmail);
        txtProgramme = findViewById(R.id.txtProgramme);
        txtCenter = findViewById(R.id.txtCenter);
        txtPhone = findViewById(R.id.txtPhone);
        txtAddress = findViewById(R.id.txtAddress);

        edtName = findViewById(R.id.edtName);
        edtRegNo = findViewById(R.id.edtRegNo);
        edtSNumber = findViewById(R.id.edtSNumber);
        edtEmail = findViewById(R.id.edtEmail);
        edtProgramme = findViewById(R.id.edtProgramme);
        edtCenter = findViewById(R.id.edtCenter);
        edtPhone = findViewById(R.id.edtPhone);
        edtAddress = findViewById(R.id.edtAddress);

        cursor = databaseHelper.getUserData(1);
        profileCount = cursor.getCount();
        if (profileCount != 0) {
            if (cursor.moveToFirst()) {
                strName = cursor.getString(1);
                strRegNo = cursor.getString(2);
                strSNumber = cursor.getString(3);
                strEmail = cursor.getString(4);
                strProgramme = cursor.getString(5);
                strCenter = cursor.getString(6);
                strPhone = cursor.getString(7);
                strAddress = cursor.getString(8);
                displayProfile();
            }
        }

        //create option badge
        optionLayout = getLayoutInflater().inflate(R.layout.option_layout, null);
        optionMenu = findViewById(R.id.optionMenu);
        optionMenu.setVisibility(View.GONE);
        optionMenu.bringToFront();

        clearProfile = findViewById(R.id.clearProfile);

        // Find the menu item you want to add the badge to
        MenuItem optionMenuItem = actionBar.getMenu().findItem(R.id.action_option);

        // Set the badge as the action view for the menu item
        optionMenuItem.setActionView(optionLayout);
        optionLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (optionMenu.getVisibility() == View.VISIBLE) {
                    optionMenu.setVisibility(View.GONE);
                } else {
                    optionMenu.setVisibility(View.VISIBLE);
                }

            }
        });

        clearProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                optionMenu.setVisibility(View.GONE);
                alertDialog = new AlertDialog.Builder(ProfileActivity.this);
                alertDialog.setTitle("Clear Profile");
                alertDialog.setMessage("Do you want clear profile?");

                //When click "Yes" it will execute this
                alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        strName = "";
                        strRegNo = "";
                        strSNumber = "";
                        strEmail = "";
                        strProgramme = "";
                        strCenter = "";
                        strPhone = "";
                        strAddress = "";

                        edtName.setText("");
                        edtRegNo.setText("");
                        edtSNumber.setText("");
                        edtEmail.setText("");
                        edtProgramme.setText("");
                        edtCenter.setText("");
                        edtPhone.setText("");
                        edtAddress.setText("");

                        boolean isCleared = databaseHelper.updateProfileDetails(1, strName, strRegNo, strSNumber, strEmail, strProgramme, strCenter, strPhone, strAddress);
                        if (isCleared) {
                            displayProfile();
                            Toast.makeText(ProfileActivity.this, "Profile Cleared !", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                //When click "No" it will execute this
                alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                alertDialog.show();
            }
        });

        editDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                cursor = databaseHelper.getUserData(1);
                profileCount = cursor.getCount();

                if (editDetails.getText().equals("Edit Details")) {

                    txtName.setVisibility(View.GONE);
                    txtRegNo.setVisibility(View.GONE);
                    txtSNumber.setVisibility(View.GONE);
                    txtEmail.setVisibility(View.GONE);
                    txtProgramme.setVisibility(View.GONE);
                    txtCenter.setVisibility(View.GONE);
                    txtPhone.setVisibility(View.GONE);
                    txtAddress.setVisibility(View.GONE);

                    edtName.setVisibility(View.VISIBLE);
                    edtRegNo.setVisibility(View.VISIBLE);
                    edtSNumber.setVisibility(View.VISIBLE);
                    edtEmail.setVisibility(View.VISIBLE);
                    edtProgramme.setVisibility(View.VISIBLE);
                    edtCenter.setVisibility(View.VISIBLE);
                    edtPhone.setVisibility(View.VISIBLE);
                    edtAddress.setVisibility(View.VISIBLE);

                    if (!txtName.getText().equals("-    "))
                        edtName.setText(txtName.getText());
                    if (!txtRegNo.getText().equals("-    "))
                        edtRegNo.setText(txtRegNo.getText());
                    if (!txtSNumber.getText().equals("-    "))
                        edtSNumber.setText(txtSNumber.getText());
                    if (!txtEmail.getText().equals("-    "))
                        edtEmail.setText(txtEmail.getText());
                    if (!txtProgramme.getText().equals("-    "))
                        edtProgramme.setText(txtProgramme.getText());
                    if (!txtCenter.getText().equals("-    "))
                        edtCenter.setText(txtCenter.getText());
                    if (!txtPhone.getText().equals("-    "))
                        edtPhone.setText(txtPhone.getText());
                    if (!txtAddress.getText().equals("-    "))
                        edtAddress.setText(txtAddress.getText());

                    editDetails.setText("Save Details");
                } else if (editDetails.getText().equals("Save Details")) {

                    strName = String.valueOf(edtName.getText());
                    strRegNo = String.valueOf(edtRegNo.getText());
                    strSNumber = String.valueOf(edtSNumber.getText());
                    strEmail = String.valueOf(edtEmail.getText());
                    strProgramme = String.valueOf(edtProgramme.getText());
                    strCenter = String.valueOf(edtCenter.getText());
                    strPhone = String.valueOf(edtPhone.getText());
                    strAddress = String.valueOf(edtAddress.getText());

                    if (profileCount == 0) {
                        boolean isInsert = databaseHelper.insertProfileDetails(strName, strRegNo, strSNumber, strEmail, strProgramme, strCenter, strPhone, strAddress);
                        if (isInsert) {
                            displayProfile();
                            Toast.makeText(ProfileActivity.this, "Details saved", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ProfileActivity.this, "Details not saved !", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        boolean isUpdate = databaseHelper.updateProfileDetails(1, strName, strRegNo, strSNumber, strEmail, strProgramme, strCenter, strPhone, strAddress);
                        if (isUpdate) {
                            displayProfile();
                            Toast.makeText(ProfileActivity.this, "Details updated", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ProfileActivity.this, "Details not update !", Toast.LENGTH_SHORT).show();
                        }
                    }
                    recyclerCourseDialogProgrammes.setVisibility(View.GONE);

                }
                cursor.close();
            }
        });

        recyclerCourseDialogProgrammes = findViewById(R.id.recyclerCourseDialogProgrammes);
        programmeList = new ArrayList<>();

        edtProgramme.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    // The cursor is now active
                    recyclerCourseDialogProgrammes.setVisibility(View.VISIBLE);

                    getProgrammeDocuments();

                    programmeAdapter = new CourseAdapter(ProfileActivity.this, programmeList, new CourseAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(CourseItem position) {
                            edtProgramme.setText(position.getProgramme());
                            recyclerCourseDialogProgrammes.setVisibility(View.GONE);
                        }
                    });
                    recyclerCourseDialogProgrammes.setLayoutManager(new LinearLayoutManager(ProfileActivity.this, LinearLayoutManager.VERTICAL, false));
                    recyclerCourseDialogProgrammes.setAdapter(programmeAdapter);
                    programmeAdapter.notifyDataSetChanged();
                }
                return false;
            }
        });

    }

    public void getProgrammeDocuments() {
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

    //display profile details in user profile interface.
    public void displayProfile() {
        if (strName.equals("") || strName.equals("null")) {
            txtName.setText("-    ");
            userName.setText("Name");
        } else {
            txtName.setText(strName);
            userName.setText(strName);
        }

        if (strRegNo.equals("") || strRegNo.equals("null")) {
            txtRegNo.setText("-    ");
        } else {
            txtRegNo.setText(strRegNo);
        }

        if (strSNumber.equals("") || strSNumber.equals("null")) {
            txtSNumber.setText("-    ");
        } else {
            txtSNumber.setText(strSNumber);
        }

        if (strEmail.equals("") || strEmail.equals("null")) {
            txtEmail.setText("-    ");
            userEmail.setText("email");
        } else {
            txtEmail.setText(strEmail);
            userEmail.setText(strEmail);
        }

        if (strProgramme.equals("") || strProgramme.equals("null")) {
            txtProgramme.setText("-    ");
        } else {
            txtProgramme.setText(strProgramme);
        }

        if (strCenter.equals("") || strCenter.equals("null")) {
            txtCenter.setText("-    ");
        } else {
            txtCenter.setText(strCenter);
        }

        if (strPhone.equals("") || strPhone.equals("null")) {
            txtPhone.setText("-    ");
        } else {
            txtPhone.setText(strPhone);
        }

        if (strAddress.equals("") || strAddress.equals("null")) {
            txtAddress.setText("-    ");
        } else {
            txtAddress.setText(strAddress);
        }

        txtName.setVisibility(View.VISIBLE);
        txtRegNo.setVisibility(View.VISIBLE);
        txtSNumber.setVisibility(View.VISIBLE);
        txtEmail.setVisibility(View.VISIBLE);
        txtProgramme.setVisibility(View.VISIBLE);
        txtCenter.setVisibility(View.VISIBLE);
        txtPhone.setVisibility(View.VISIBLE);
        txtAddress.setVisibility(View.VISIBLE);

        edtName.setVisibility(View.GONE);
        edtRegNo.setVisibility(View.GONE);
        edtSNumber.setVisibility(View.GONE);
        edtEmail.setVisibility(View.GONE);
        edtProgramme.setVisibility(View.GONE);
        edtCenter.setVisibility(View.GONE);
        edtPhone.setVisibility(View.GONE);
        edtAddress.setVisibility(View.GONE);

        editDetails.setText("Edit Details");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}