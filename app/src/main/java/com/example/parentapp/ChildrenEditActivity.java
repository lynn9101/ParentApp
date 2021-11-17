package com.example.parentapp;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.parentapp.models.Child;
import com.example.parentapp.models.ChildrenManager;
import com.example.parentapp.models.Helpers;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Random;

/**
 * The ChildrenEditActivity class is an android activity and handles the addition, modification and deletion of child objects
 */

public class ChildrenEditActivity extends AppCompatActivity {
    private ChildrenManager childrenManager = ChildrenManager.getInstance();
    //private ChildrenManager spinnerChildrenManager = ChildrenManager.getInstance();
    private static final String EXTRA_MESSAGE = "Passing Edit State";
    private String title;
    private int editIndex;

    private EditText childFirstName;
    private EditText childLastName;
    private ImageView childImage;

    private Uri cameraImageUri;

    private ActivityResultLauncher<Uri> takePictureActivityLauncher;
    private ActivityResultLauncher<String> gallerySelectionActivityLauncher;

    private ArrayList<Integer> allChildID = new ArrayList<>();
    String allChildIdKey = "AllChildrenIDListKey";

    public static Intent makeLaunchIntent(Context ctx, String message) {
        Intent intent = new Intent(ctx, ChildrenEditActivity.class);
        intent.putExtra(EXTRA_MESSAGE, message);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_children_edit);

        allChildID.clear();
        Context context = getApplicationContext();
        SharedPreferences sharedPreferences = Helpers.getSharedPreference(context);
        //String childrenListKey = context.getResources().getString(R.string.shared_pref_children_list_key);

        if (sharedPreferences.contains(allChildIdKey)) {
            allChildID = getArrayPrefs(this);
        } else {
            allChildID = new ArrayList<>();
        }

        // Get intent for adding vs. editing Child
        Intent intent = getIntent();
        title = intent.getStringExtra(EXTRA_MESSAGE);
        editIndex = intent.getIntExtra("editIndex", -1);

        childFirstName = findViewById(R.id.fillFirstName);
        childLastName = findViewById(R.id.fillLastName);
        childImage = findViewById(R.id.childImage);

        // Set up UI elements
        setUpAppBar();
        setUpSaveButton();
        setUpDeleteButton();
        setupPortraitButton();
        setupActivityResultLaunchers();
        fillModel();
        setUpSaveButton();
    }


    @Override
    protected void onStart() {
        super.onStart();
    }

    private void setUpAppBar() {
        getSupportActionBar().setTitle(title + " Child");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources()
                .getColor(R.color.app_title_color)));
    }

    private void setUpSaveButton() {
        Button saveButton = findViewById(R.id.childSaveButton);
        saveButton.setOnClickListener(view -> {
            if (checkValidName()) {
                saveValidName();
            }
        });
    }

    private int generateChildID() {
        Random rand = new Random();
        int newChildID = rand.nextInt(100000);
        while (allChildID.contains(newChildID)) {
            newChildID = rand.nextInt(100000);
        }
        allChildID.add(newChildID);
        updateAllChildIdSharedPref();
        return newChildID;
    }

    private boolean checkValidName() {
        childFirstName = findViewById(R.id.fillFirstName);
        childLastName = findViewById(R.id.fillLastName);

        if (childFirstName.getText().toString().equals("") ||
            childLastName.getText().toString().equals(""))
        {
            String message = "Please fill out the full name!";
            Toast.makeText(ChildrenEditActivity.this, message, Toast.LENGTH_SHORT)
                    .show();
            return false;
        }

        return true;
    }

    private void saveValidName() {
        String firstName = childFirstName.getText().toString();
        String lastName = childLastName.getText().toString();
        Bitmap portraitImage = ((BitmapDrawable)childImage.getDrawable()).getBitmap();
        String message;

        if (title.equals("New")) {
            message = "New child is added.";
            int uniqueID = generateChildID();
            Child newChild = new Child(lastName, firstName, portraitImage,uniqueID);
            childrenManager.addChild(newChild);
            childrenManager.addSpinnerChild(newChild);
            Toast.makeText(ChildrenEditActivity.this, " first id is " + newChild.getUniqueID(), Toast.LENGTH_SHORT).show();
            //spinnerChildrenManager.addChild(new Child(lastName, firstName, portraitImage,uniqueID));
        } else {
            message = "Child's information has been edited.";
            int originalChildID = childrenManager.getChild(editIndex).getUniqueID();
            Child childEdited = new Child(lastName, firstName, portraitImage, originalChildID);
            childrenManager.updateChild(editIndex, childEdited);
            childrenManager.updateSpinnerChild(editIndex,childEdited);
            //spinnerChildrenManager.updateChild(editIndex, childEdited);
        }

        //Toast.makeText(ChildrenEditActivity.this, message, Toast.LENGTH_SHORT).show();
        updateChildrenListSharedPref();
        updateSpinnerChildrenListSharedPref();
        finish();
    }

    private void setUpDeleteButton() {
        FloatingActionButton deleteButton = findViewById(R.id.childDeleteButton);

        if(title.equals("Edit")) {
            deleteButton.setVisibility(View.VISIBLE);

            deleteButton.setOnClickListener(view -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(ChildrenEditActivity.this);
                builder.setMessage("Delete this child? It cannot be restored!")
                        .setPositiveButton("Confirm", (dialogInterface, i) -> {
                            int removeID = childrenManager.getChildren().get(editIndex).getUniqueID();
                            Toast.makeText(ChildrenEditActivity.this, " second id is " + removeID, Toast.LENGTH_SHORT).show();
                            childrenManager.removeChild(editIndex);
                            int deleteChild = childrenManager.getSpinnerChildByID(childrenManager.getSpinnerChildren(), removeID);
                            childrenManager.getSpinnerChildren().remove(deleteChild);
                            //spinnerChildrenManager.removeChild(editIndex);
                            updateChildrenListSharedPref();
                            updateSpinnerChildrenListSharedPref();
                            finish();
                        })
                        .setNegativeButton("Back", null);

                AlertDialog warning = builder.create();
                warning.show();
            });
        } else {
            deleteButton.setVisibility(View.INVISIBLE);
        }
    }

    private void setupPortraitButton() {
        Context me = this;

        childImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createPortraitDialog(me);
            }
        });

        Button portraitBtn = findViewById(R.id.changePortraitbtn);
        portraitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createPortraitDialog(me);
            }
        });
    }

    private void setupActivityResultLaunchers() {
        takePictureActivityLauncher = registerForActivityResult(
            new ActivityResultContracts.TakePicture(),
            new ActivityResultCallback<Boolean>() {
            @Override
            public void onActivityResult(Boolean success) {
                if (success) {
                    childImage.setImageURI(cameraImageUri);
                }
            }
        });

        gallerySelectionActivityLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(),
            new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri uri) {
                if (uri != null) {
                    childImage.setImageURI(uri);
                }
            }
        });
    }

    private void createPortraitDialog(Context me) {
        Resources resManager = me.getResources();
        AlertDialog.Builder builder = new AlertDialog.Builder(me);
        builder.setTitle(resManager.getText(R.string.image_source_dialog_title));
        builder.setItems(resManager.getStringArray(R.array.imageSourceOptions), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (item == 0) {
                    if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(me, "Camera disabled.", Toast.LENGTH_SHORT).show();
                    } else {
                        try {

                            File tempImage = File.createTempFile("tmp_camera_image", ".png", me.getCacheDir());
                            cameraImageUri = FileProvider.getUriForFile(getApplicationContext(),  BuildConfig.APPLICATION_ID + ".provider", tempImage);

                            takePictureActivityLauncher.launch(cameraImageUri);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                } else if (item == 1) {
                    gallerySelectionActivityLauncher.launch("image/*");
                } else {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void fillModel() {
        if (editIndex >= 0) {
            Child childInstance = childrenManager.getChild(editIndex);

            childFirstName.setText(childInstance.getFirstName());
            childLastName.setText(childInstance.getLastName());

            if (childInstance.hasPortrait()) {
                childImage.setImageBitmap(childInstance.getPortrait());
            }
        }
    }

    private void updateChildrenListSharedPref() {
        Context context = getApplicationContext();
        String childrenListKey = context.getResources().getString(R.string.shared_pref_children_list_key);
        Helpers.saveObjectToSharedPreference(context, childrenListKey, childrenManager.getChildren());
    }

    private void updateAllChildIdSharedPref() {
        Context context = getApplicationContext();
        Helpers.saveObjectToSharedPreference(context, allChildIdKey, allChildID);
    }

    private void updateSpinnerChildrenListSharedPref() {
        Context context = getApplicationContext();
        String spinnerChildrenKey = context.getResources().getString(R.string.shared_pref_spinner_children_list_key);
        Helpers.saveObjectToSharedPreference(context, spinnerChildrenKey, childrenManager.getSpinnerChildren());
    }

    public ArrayList<Integer> getArrayPrefs(Context mContext) {
        SharedPreferences prefs = Helpers.getSharedPreference(mContext);
        Gson gson = new Gson();
        String json = prefs.getString(allChildIdKey, "");
        Type type = new TypeToken<ArrayList<Integer>>(){}.getType();
        ArrayList<Integer> allChildrenID = gson.fromJson(json, type);
        return allChildrenID;
    }
}