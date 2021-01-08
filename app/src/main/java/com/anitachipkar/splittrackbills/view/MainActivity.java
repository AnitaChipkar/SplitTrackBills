package com.anitachipkar.splittrackbills.view;


import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.anitachipkar.splittrackbills.SplitBillDatabase;
import com.anitachipkar.splittrackbills.adapter.GroupAdapter;
import com.anitachipkar.splittrackbills.model.Expense;
import com.anitachipkar.splittrackbills.model.Group;
import com.anitachipkar.splittrackbills.utils.AppConstants;
import com.anitachipkar.splittrackbills.model.Member;
import com.anitachipkar.splittrackbills.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;
import com.wafflecopter.multicontactpicker.ContactResult;
import com.wafflecopter.multicontactpicker.LimitColumn;
import com.wafflecopter.multicontactpicker.MultiContactPicker;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.anitachipkar.splittrackbills.view.ExpenseActivity.expenseList;

public class MainActivity extends AppCompatActivity {

    private static final int CONTACT_PICKER_REQUEST = 1;
    private ArrayList<ContactResult> results = new ArrayList<>();
    ArrayList<Member> memberList = new ArrayList<>();
    public static ArrayList<Group> groupList = new ArrayList<>();
    public static final int PERMISSIONS_REQUEST_READ_CONTACTS = 1;
    SharedPreferences sharedPreferences;
    String userName, userMobileNumber;
    Gson gson;
    RecyclerView rcvGroupList;
    LinearLayout llNoGroups;
    AppCompatTextView textGroups;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //   Toolbar toolbar = findViewById(R.id.toolbar);
        // setSupportActionBar(toolbar);
        initViews();

    }

    private void initViews() {

        addGroup();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sharedPreferences = getSharedPreferences(AppConstants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
                userName = sharedPreferences.getString(AppConstants.USER_NAME, null);
                userMobileNumber = sharedPreferences.getString(AppConstants.USER_MOBILE_NUMBER, null);
                if (userName == null && userMobileNumber == null) {
                    saveContactDialog();
                } else {
                    requestContactPermission();
                }


            }
        });
    }


    private void saveContactDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        final View customLayout = getLayoutInflater().inflate(R.layout.layout_dialog_register_number, null);
        builder.setView(customLayout);
        final AppCompatEditText editName = customLayout.findViewById(R.id.edit_name);
        final AppCompatEditText editMobileNumber = customLayout.findViewById(R.id.edit_mobile_number);


        AppCompatButton buttonAddContact = customLayout.findViewById(R.id.btn_save_contact);
        final AlertDialog dialog = builder.create();
        dialog.setCancelable(false);

        buttonAddContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editName.getText().toString().isEmpty()) {
                    editName.setError("Enter Name");
                } else if (editMobileNumber.getText().toString().isEmpty()) {
                    editMobileNumber.setError("Enter Mobile Number");
                } else {
                    editName.setError(null);
                    editMobileNumber.setError(null);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(AppConstants.USER_NAME, Objects.requireNonNull(editName.getText()).toString());
                    editor.putString(AppConstants.USER_MOBILE_NUMBER, Objects.requireNonNull(editMobileNumber.getText()).toString());
                    editor.apply();
                    Toast.makeText(MainActivity.this, "Contact Saved Successfully!", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    requestContactPermission();
                }
            }
        });

        dialog.show();

    }

    private void addGroup() {
        gson = new Gson();
        rcvGroupList = findViewById(R.id.rcv_group_list);
        llNoGroups = findViewById(R.id.ll_no_groups);
        textGroups = findViewById(R.id.tv_all_groups);

        SplitBillDatabase splitBillDatabase = new SplitBillDatabase(this);
        new SplitBillDatabase.myDbHelper(this);
        splitBillDatabase.getGroupData();

        if (!groupList.isEmpty()) {
            llNoGroups.setVisibility(View.GONE);
            rcvGroupList.setVisibility(View.VISIBLE);
            textGroups.setVisibility(View.VISIBLE);
            GroupAdapter groupAdapter = new GroupAdapter(MainActivity.this, groupList);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
            rcvGroupList.setLayoutManager(mLayoutManager);
            rcvGroupList.setItemAnimator(new DefaultItemAnimator());
            rcvGroupList.setAdapter(groupAdapter);
            groupAdapter.notifyDataSetChanged();
        } else {
            llNoGroups.setVisibility(View.VISIBLE);
            rcvGroupList.setVisibility(View.GONE);
            textGroups.setVisibility(View.GONE);
        }


    }

    public void requestContactPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        android.Manifest.permission.READ_CONTACTS)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Access needed");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setMessage("Please enable access.");
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @TargetApi(Build.VERSION_CODES.M)
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            requestPermissions(
                                    new String[]
                                            {android.Manifest.permission.READ_CONTACTS, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}
                                    , PERMISSIONS_REQUEST_READ_CONTACTS);

                        }
                    });
                    builder.show();
                } else {
                    ActivityCompat.requestPermissions(this,
                            new String[]{android.Manifest.permission.READ_CONTACTS, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            PERMISSIONS_REQUEST_READ_CONTACTS);
                }
            } else {
                getContacts();
            }
        } else {
            getContacts();
        }
    }

    private void getContacts() {
        new MultiContactPicker.Builder(MainActivity.this)
                .theme(R.style.CustomPickerTheme)
                .hideScrollbar(false)
                .showTrack(true)
                .searchIconColor(Color.WHITE)
                .setChoiceMode(MultiContactPicker.CHOICE_MODE_MULTIPLE)
                .handleColor(ContextCompat.getColor(MainActivity.this, R.color.colorPrimary))
                .bubbleColor(ContextCompat.getColor(MainActivity.this, R.color.colorPrimary))
                .bubbleTextColor(Color.WHITE)
                .setTitleText("Select Members")
                .setSelectedContacts(results)
                .setLoadingType(MultiContactPicker.LOAD_SYNC)
                //.trackColor(getResources().getColor(R.color.colorPrimary))
                .limitToColumn(LimitColumn.PHONE)
                .setActivityAnimations(android.R.anim.fade_in, android.R.anim.fade_out,
                        android.R.anim.fade_in,
                        android.R.anim.fade_out) //Optional - default: No animation overrides
                .showPickerForResult(CONTACT_PICKER_REQUEST);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_READ_CONTACTS: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getContacts();
                } else {
                    Toast.makeText(this, "You have disabled a contacts permission", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }

    }
    public static class UriSerializer implements JsonSerializer<Uri> {
        public JsonElement serialize(Uri src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.toString());
        }
    }

    public static class UriDeserializer implements JsonDeserializer<Uri> {
        @Override
        public Uri deserialize(final JsonElement src, final Type srcType,
                               final JsonDeserializationContext context) throws JsonParseException {
            return Uri.parse(src.getAsString());
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CONTACT_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                results.clear();
                memberList.clear();
                results.addAll(MultiContactPicker.obtainResult(data));
                if (results.size() > 0) {
                    String name = sharedPreferences.getString(AppConstants.USER_NAME, null);
                    String contactNo = sharedPreferences.getString(AppConstants.USER_MOBILE_NUMBER, null);
                    Member addMember = new Member("User", name, contactNo, null,
                            null, null,null,null,null,null,null);
                    memberList.add(addMember);


                    for (int i = 0; i < results. size(); i++) {
                        Member member = new Member(results.get(i).getContactID(),
                                results.get(i).getDisplayName(),
                                results.get(i).getPhoneNumbers().get(0).getNumber(),
                                results.get(i).getPhoto(),
                                results.get(i).getThumbnail(),
                                null,null,null,null,null,null);
                        memberList.add(member);


                    }

                    Log.e("memberList", new Gson().toJson(memberList));

                }
                Intent intentExpense = new Intent(MainActivity.this, CreateGroupActivity.class);
                startActivity(intentExpense);

                gson = new GsonBuilder()
                        .registerTypeAdapter(Uri.class, new UriSerializer())
                        .create();
                String jsonSelectedMember = gson.toJson(memberList);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(AppConstants.SELECTED_MEMBERS, jsonSelectedMember);
                editor.apply();
             //   recreate();
            } else if (resultCode == RESULT_CANCELED) {
                System.out.println("User closed the picker without selecting items.");
            }
        }
    }


}
