package com.anitachipkar.splittrackbills.view;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.anitachipkar.splittrackbills.SplitBillDatabase;
import com.anitachipkar.splittrackbills.adapter.SplitAdapter;
import com.anitachipkar.splittrackbills.model.Expense;
import com.anitachipkar.splittrackbills.model.SplitExpense;
import com.anitachipkar.splittrackbills.utils.AppConstants;
import com.anitachipkar.splittrackbills.model.Member;
import com.anitachipkar.splittrackbills.R;
import com.anitachipkar.splittrackbills.adapter.SelectedMembersAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.wafflecopter.multicontactpicker.MultiContactPicker;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

public class CreateGroupActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    ArrayList<Member> memberList = new ArrayList<>();
    ArrayList<SplitExpense> splitExpenses = new ArrayList<>();
    RecyclerView rcvSelectedMembers, rcvSplitExpense;
    LinearLayout llExpenseAmount;
    SelectedMembersAdapter selectedMembersAdapter;
    String PaidByContact;
    AppCompatSpinner spinSelectedMembers;
    AppCompatTextView textDateTime, textPaidMember, textPaidAmount;
    AppCompatButton buttonSplit, buttonSaveGroup;
    Integer maxGroupId, newGroupId, maxExpenseId, newExpenseId;
    AppCompatEditText editGroupName, editDescription, editAmount;
    public static final int PERMISSIONS_REQUEST_READ_CONTACTS = 1;
    public static final int PERMISSION_SEND_SMS = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        getSelectedMembers();
        initViews();


    }

    private void getSelectedMembers() {
        SharedPreferences sharedPreferences = getSharedPreferences(AppConstants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Uri.class, new MainActivity.UriDeserializer())
                .create();
        String json = sharedPreferences.getString(AppConstants.SELECTED_MEMBERS, null);
        Type type = new TypeToken<ArrayList<Member>>() {
        }.getType();
        memberList = gson.fromJson(json, type);
        Log.d("TAG", memberList.toString());
    }

    private void initViews() {
        rcvSelectedMembers = findViewById(R.id.rcv_selected_members);
        rcvSplitExpense = findViewById(R.id.rcv_split_expense);
        selectedMembersAdapter = new SelectedMembersAdapter(CreateGroupActivity.this, memberList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        rcvSelectedMembers.setLayoutManager(mLayoutManager);
        rcvSelectedMembers.setItemAnimator(new DefaultItemAnimator());
        rcvSelectedMembers.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        rcvSelectedMembers.setAdapter(selectedMembersAdapter);
        selectedMembersAdapter.notifyDataSetChanged();

        spinSelectedMembers = findViewById(R.id.spin_member_list);
        spinSelectedMembers.setOnItemSelectedListener(this);

        ArrayAdapter<Member> spinnerArrayAdapter = new ArrayAdapter<>(CreateGroupActivity.this, android.R.layout.simple_spinner_dropdown_item, memberList);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinSelectedMembers.setAdapter(spinnerArrayAdapter);
        llExpenseAmount = findViewById(R.id.ll_expense_amount);
        editGroupName = findViewById(R.id.edit_group_name);
        editDescription = findViewById(R.id.edit_description);
        editAmount = findViewById(R.id.edit_amount);
        buttonSplit = findViewById(R.id.btn_split_amount);
        buttonSplit.setOnClickListener(this);
        buttonSaveGroup = findViewById(R.id.btn_save_group);
        buttonSaveGroup.setOnClickListener(this);
        textPaidMember = findViewById(R.id.tv_paid_member);
        textPaidAmount = findViewById(R.id.tv_paid_amount);
        textDateTime = findViewById(R.id.tv_date_time);

        // Set current date and time
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("EEE,d/MM/yyyy  hh:mm a", Locale.US);
        String dateTime = sdf.format(cal.getTime());
        textDateTime.setText(dateTime);
    }


    private boolean checkValidations() {
        if (editGroupName.getText().toString().isEmpty()) {
            editGroupName.setError("Enter Group Name");
            return false;
        } else {
            editGroupName.setError(null);
        }

        if (editDescription.getText().toString().isEmpty()) {
            editDescription.setError("Enter Description");
            return false;
        } else {
            editDescription.setError(null);
        }

        if (editAmount.getText().toString().isEmpty()) {
            editAmount.setError("Enter Amount");
            return false;
        } else {
            editAmount.setError(null);
        }
        return true;
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.btn_split_amount:
                if (checkValidations()) {
                    splitExpenses.clear();
                    llExpenseAmount.setVisibility(View.VISIBLE);
                    String amount = editAmount.getText().toString();
                    textPaidMember.setText("Paid by: " + spinSelectedMembers.getSelectedItem().toString());
                    PaidByContact = memberList.get(spinSelectedMembers.getSelectedItemPosition()).phoneNumber;
                    textPaidAmount.setText("\u20B9  " + amount);
                    float subtotal = Float.parseFloat(amount);
                    float members = memberList.size();

                    float result = (subtotal / members);

                    for (Member member : memberList) {
                        SplitExpense splitExpense = new SplitExpense();
                        splitExpense.setSplitMemberName(member.getDisplayName());
                        splitExpense.setSplitAmount(String.valueOf(result));
                        splitExpenses.add(splitExpense);

                    }
                    rcvSplitExpense.setLayoutManager(new LinearLayoutManager(this));
                    rcvSplitExpense.setHasFixedSize(true);
                    SplitAdapter splitAdapter = new SplitAdapter(CreateGroupActivity.this, splitExpenses);
                    rcvSplitExpense.setAdapter(splitAdapter);
                    splitAdapter.notifyDataSetChanged();

                }
                break;

            case R.id.btn_save_group:
                ArrayList<Expense> expenseList = new ArrayList<>();

                SplitBillDatabase.myDbHelper myhelper = new SplitBillDatabase.myDbHelper(this);
                Double eachAmount = Double.parseDouble(Objects.requireNonNull(editAmount.getText()).toString()) / memberList.size();
                SQLiteDatabase db = myhelper.getWritableDatabase();
                String query = "SELECT MAX(GroupId) FROM BillGroups";
                @SuppressLint("Recycle")
                Cursor c = db.rawQuery(query, null);
                if (c.moveToFirst()) { // if cursor is not empty
                    maxGroupId = c.getInt(0);
                }

                String query1 = "SELECT MAX(ExpenseID) FROM BillGroups";
                @SuppressLint("Recycle")
                Cursor c1 = db.rawQuery(query1, null);
                if (c1.moveToFirst()) { // if cursor is not empty
                    maxExpenseId = c1.getInt(0);
                }

                newGroupId = maxGroupId + 1;
                newExpenseId = maxExpenseId + 1;

               /* SharedPreferences sharedPreferences = getSharedPreferences(AppConstants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
                String name = sharedPreferences.getString(AppConstants.USER_NAME, null);
                String contactNo = sharedPreferences.getString(AppConstants.USER_MOBILE_NUMBER, null);
                //  Member addMember = new Member("User", name, contactNo, null, null, null);
                Expense userExpense = new Expense();
                userExpense.contactId = "User";
                userExpense.memberName = name;
                userExpense.memberContact = contactNo;
                userExpense.amount = String.valueOf(eachAmount);
                userExpense.groupId = String.valueOf(newGroupId);
                userExpense.expenseID = String.valueOf(newExpenseId);
                userExpense.dateTime = textDateTime.getText().toString();
                userExpense.groupName = editGroupName.getText().toString();
                userExpense.expenseName = editDescription.getText().toString().trim();
                expenseList.add(userExpense);*/


                for (int i = 0; i < memberList.size(); i++) {
                    Expense expense = new Expense();
                    expense.contactId = memberList.get(i).contactId;
                    expense.memberName = memberList.get(i).displayName;
                    expense.memberContact = memberList.get(i).phoneNumber;
                    expense.groupId = String.valueOf(newGroupId);
                    expense.expenseID = String.valueOf(newExpenseId);
                    expense.dateTime = textDateTime.getText().toString();
                    expense.groupName = editGroupName.getText().toString();
                    expense.paidBy = PaidByContact;
                    if (memberList.get(i).phoneNumber.equalsIgnoreCase(PaidByContact)) {
                        expense.amount = String.valueOf(eachAmount * (memberList.size() - 1));
                        expense.eachAmount = String.valueOf(eachAmount);
                        expense.isPaid = "1";
                    } else {
                        expense.isPaid = "0";
                        expense.amount = String.valueOf(eachAmount);
                        expense.eachAmount = String.valueOf(eachAmount);
                    }

                    expense.expenseName = editDescription.getText().toString().trim();
                    expenseList.add(expense);
                    // requestSmsPermission();

                }


                Log.e("memberList", new Gson().toJson(memberList));
                Log.e("expenseList", new Gson().toJson(expenseList));

                SplitBillDatabase splitBillDatabase = new SplitBillDatabase(this);
                new SplitBillDatabase.myDbHelper(this);
                splitBillDatabase.insertCustomerData(expenseList);

                Intent intentGroup = new Intent(CreateGroupActivity.this, MainActivity.class);
                intentGroup.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK |
                        Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intentGroup);
                break;

            default:
                break;

        }

    }

    private void requestSmsPermission() {

        // check permission is given
        if (ContextCompat.checkSelfPermission(CreateGroupActivity.this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            // request permission (see result in onRequestPermissionsResult() method)
            ActivityCompat.requestPermissions(CreateGroupActivity.this,
                    new String[]{Manifest.permission.SEND_SMS},
                    PERMISSION_SEND_SMS);
        } else {
            // permission already granted run sms send
            sendSms();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_READ_CONTACTS: {

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted
                    sendSms();
                } else {
                    // permission denied
                }
                return;
            }
        }
    }

    private void sendSms() {
        {
            String SENT = "SMS_SENT";
            String DELIVERED = "SMS_DELIVERED";

            PendingIntent sentPI = PendingIntent.getBroadcast(this, 0,
                    new Intent(SENT), 0);

            PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0,
                    new Intent(DELIVERED), 0);

            //---when the SMS has been sent---
            registerReceiver(new BroadcastReceiver() {
                @Override
                public void onReceive(Context arg0, Intent arg1) {
                    switch (getResultCode()) {
                        case Activity.RESULT_OK:
                            Toast.makeText(getBaseContext(), "SMS sent",
                                    Toast.LENGTH_SHORT).show();
                            break;
                        case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                            Toast.makeText(getBaseContext(), "Generic failure",
                                    Toast.LENGTH_SHORT).show();
                            break;
                        case SmsManager.RESULT_ERROR_NO_SERVICE:
                            Toast.makeText(getBaseContext(), "No service",
                                    Toast.LENGTH_SHORT).show();
                            break;
                        case SmsManager.RESULT_ERROR_NULL_PDU:
                            Toast.makeText(getBaseContext(), "Null PDU",
                                    Toast.LENGTH_SHORT).show();
                            break;
                        case SmsManager.RESULT_ERROR_RADIO_OFF:
                            Toast.makeText(getBaseContext(), "Radio off",
                                    Toast.LENGTH_SHORT).show();
                            break;
                    }
                }
            }, new IntentFilter(SENT));

            //---when the SMS has been delivered---
            registerReceiver(new BroadcastReceiver() {
                @Override
                public void onReceive(Context arg0, Intent arg1) {
                    switch (getResultCode()) {
                        case Activity.RESULT_OK:
                            Toast.makeText(getBaseContext(), "SMS delivered",
                                    Toast.LENGTH_SHORT).show();
                            break;
                        case Activity.RESULT_CANCELED:
                            Toast.makeText(getBaseContext(), "SMS not delivered",
                                    Toast.LENGTH_SHORT).show();
                            break;
                    }
                }
            }, new IntentFilter(DELIVERED));

            SmsManager sms = SmsManager.getDefault();
            sms.sendTextMessage("+918087114267", null, "message", sentPI, deliveredPI);
        }
    }


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return super.onOptionsItemSelected(item);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }


}
