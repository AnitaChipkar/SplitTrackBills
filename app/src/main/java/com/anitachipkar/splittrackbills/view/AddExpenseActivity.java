package com.anitachipkar.splittrackbills.view;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.anitachipkar.splittrackbills.R;
import com.anitachipkar.splittrackbills.SplitBillDatabase;
import com.anitachipkar.splittrackbills.adapter.SplitAdapter;
import com.anitachipkar.splittrackbills.model.Expense;
import com.anitachipkar.splittrackbills.model.Member;
import com.anitachipkar.splittrackbills.model.SplitExpense;
import com.anitachipkar.splittrackbills.utils.AppConstants;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

import static com.anitachipkar.splittrackbills.adapter.GroupAdapter.selectedGroupId;
import static com.anitachipkar.splittrackbills.adapter.GroupAdapter.selectedGroupName;

public class AddExpenseActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {


    AppCompatSpinner spinSelectedMembers;
    AppCompatTextView textDateTime, textPaidMember, textPaidAmount;
    AppCompatButton buttonSplit, buttonSaveGroup;
    AppCompatEditText editDescription, editAmount;
    RecyclerView rcvSplitExpense;
    LinearLayout llExpenseAmount;
    String PaidByContact;
    ArrayList<SplitExpense> splitExpenses = new ArrayList<>();
    ArrayList<Member> memberList = new ArrayList<>();
  //  ArrayList<Member> memberData = new ArrayList<>();
    Integer maxExpenseId, newExpenseId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        initViews();


    }

    private void initViews() {
        SharedPreferences sharedPreferences = getSharedPreferences(AppConstants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Uri.class, new MainActivity.UriDeserializer())
                .create();
        String json = sharedPreferences.getString(AppConstants.SELECTED_MEMBERS, null);
        Type type = new TypeToken<ArrayList<Member>>() {
        }.getType();
       // memberList = gson.fromJson(json, type);
        Log.d("TAG", memberList.toString());

        SplitBillDatabase splitBillDatabase = new SplitBillDatabase(this);
        new SplitBillDatabase.myDbHelper(this);
        memberList = splitBillDatabase.getMemberData();



        rcvSplitExpense = findViewById(R.id.rcv_split_expense);
        llExpenseAmount = findViewById(R.id.ll_expense_amount);
        spinSelectedMembers = findViewById(R.id.spin_member_list);
        spinSelectedMembers.setOnItemSelectedListener(this);

        ArrayAdapter<Member> spinnerArrayAdapter = new ArrayAdapter<Member>(AddExpenseActivity.this, android.R.layout.simple_spinner_dropdown_item, memberList);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinSelectedMembers.setAdapter(spinnerArrayAdapter);

        editDescription = findViewById(R.id.edit_description);
        editAmount = findViewById(R.id.edit_amount);
        buttonSplit = findViewById(R.id.btn_split_expense_amount);
        buttonSplit.setOnClickListener(this);
        buttonSaveGroup = findViewById(R.id.btn_save_expense);
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

    @SuppressLint("SetTextI18n")
    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.btn_split_expense_amount:

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

                    //  float percentage = (50 * result) / 100;

                    for (Member member : memberList) {
                        SplitExpense splitExpense = new SplitExpense();
                        splitExpense.setSplitMemberName(member.getDisplayName());
                        splitExpense.setSplitAmount(String.valueOf(result));
                        splitExpenses.add(splitExpense);

                    }
                    rcvSplitExpense.setLayoutManager(new LinearLayoutManager(this));
                    rcvSplitExpense.setHasFixedSize(true);
                    SplitAdapter splitAdapter = new SplitAdapter(AddExpenseActivity.this, splitExpenses);
                    rcvSplitExpense.setAdapter(splitAdapter);
                    splitAdapter.notifyDataSetChanged();

                }
                break;
            case R.id.btn_save_expense:
                ArrayList<Expense> expenseList = new ArrayList<>();

                SplitBillDatabase.myDbHelper myhelper = new SplitBillDatabase.myDbHelper(this);
                Double eachAmount = Double.parseDouble(Objects.requireNonNull(editAmount.getText()).toString()) / memberList.size();
                SQLiteDatabase db = myhelper.getWritableDatabase();
                String query = "SELECT MAX(ExpenseId) FROM BillGroups";
                @SuppressLint("Recycle")
                Cursor c = db.rawQuery(query, null);
                if (c.moveToFirst()) { // if cursor is not empty
                    maxExpenseId = c.getInt(0);
                }

                newExpenseId = maxExpenseId + 1;
               /* SharedPreferences sharedPreferences = getSharedPreferences(AppConstants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
                String name = sharedPreferences.getString(AppConstants.USER_NAME, null);
                String contactNo = sharedPreferences.getString(AppConstants.USER_MOBILE_NUMBER, null);
                SharedPreferences sharedPreferences = getSharedPreferences(AppConstants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
                String name = sharedPreferences.getString(AppConstants.USER_NAME, null);
                String contactNo = sharedPreferences.getString(AppConstants.USER_MOBILE_NUMBER, null);
                Expense userExpense = new Expense();
                userExpense.contactId = "User";
                userExpense.memberName = name;
                userExpense.memberContact = contactNo;
                userExpense.amount = editAmount.getText().toString();
                userExpense.groupId = String.valueOf(selectedGroupId);
                userExpense.expenseID = String.valueOf(newExpenseId);
                userExpense.dateTime = textDateTime.getText().toString();
                userExpense.groupName = selectedGroupName;
                userExpense.expenseName = editDescription.getText().toString().trim();
                expenseList.add(userExpense);*/

                for (int i = 0; i < memberList.size(); i++) {
                    Expense expense = new Expense();
                    expense.contactId = memberList.get(i).contactId;
                    expense.memberName = memberList.get(i).displayName;
                    expense.memberContact = memberList.get(i).phoneNumber;
                    expense.amount = editAmount.getText().toString();
                    expense.groupId = String.valueOf(selectedGroupId);
                    expense.expenseID = String.valueOf(newExpenseId);
                    expense.dateTime = textDateTime.getText().toString();
                    expense.groupName = selectedGroupName;
                    expense.expenseName = editDescription.getText().toString().trim();
                    expense.paidBy =  PaidByContact;
                    if (memberList.get(i).phoneNumber.equalsIgnoreCase(PaidByContact)) {
                        expense.amount = String.valueOf(eachAmount * (memberList.size() - 1));
                        expense.eachAmount = String.valueOf(eachAmount);
                        expense.isPaid = "1";
                    } else {
                        expense.isPaid = "0";
                        expense.amount = String.valueOf(eachAmount);
                        expense.eachAmount = String.valueOf(eachAmount);
                    }
                   // expense.setMembers(memberList);
                    expenseList.add(expense);
                }

                Log.e("memberList", new Gson().toJson(memberList));
                Log.e("expenseList", new Gson().toJson(expenseList));

                SplitBillDatabase splitBillDatabase = new SplitBillDatabase(this);
                new SplitBillDatabase.myDbHelper(this);
                splitBillDatabase.insertCustomerData(expenseList);


                Intent intentGroup = new Intent(AddExpenseActivity.this, ExpenseActivity.class);
                startActivity(intentGroup);
                finish();
                break;


            default:
                break;

        }

    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    private boolean checkValidations() {

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
