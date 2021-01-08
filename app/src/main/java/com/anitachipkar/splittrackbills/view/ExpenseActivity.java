package com.anitachipkar.splittrackbills.view;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.anitachipkar.splittrackbills.R;
import com.anitachipkar.splittrackbills.SplitBillDatabase;
import com.anitachipkar.splittrackbills.adapter.ExpenseAdapter;
import com.anitachipkar.splittrackbills.model.Expense;
import com.anitachipkar.splittrackbills.model.Member;
import com.anitachipkar.splittrackbills.utils.AppConstants;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.wafflecopter.multicontactpicker.ContactResult;
import com.wafflecopter.multicontactpicker.LimitColumn;
import com.wafflecopter.multicontactpicker.MultiContactPicker;

import java.util.ArrayList;

import static com.anitachipkar.splittrackbills.adapter.ExpenseAdapter.selectedExpenseDateTime;
import static com.anitachipkar.splittrackbills.adapter.ExpenseAdapter.selectedExpenseId;
import static com.anitachipkar.splittrackbills.adapter.ExpenseAdapter.selectedExpenseName;
import static com.anitachipkar.splittrackbills.adapter.GroupAdapter.selectedGroupDateTime;
import static com.anitachipkar.splittrackbills.adapter.GroupAdapter.selectedGroupId;
import static com.anitachipkar.splittrackbills.adapter.GroupAdapter.selectedGroupName;

public class ExpenseActivity extends AppCompatActivity {

    RecyclerView rcvExpenseList;
    public static ArrayList<Expense> expenseList = new ArrayList<>();
    ViewGroup llParentLayout;
    private ArrayList<ContactResult> results = new ArrayList<>();
    ArrayList<Member> memberList = new ArrayList<>();
    private static final int CONTACT_PICKER_REQUEST = 1;
    ArrayList<Member> members = new ArrayList<>();
    SplitBillDatabase splitBillDatabase;
    public static String name, contactNo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense);
        initViews();
    }

    private void initViews() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        SharedPreferences sharedPreferences = getSharedPreferences(AppConstants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        name = sharedPreferences.getString(AppConstants.USER_NAME, null);
        contactNo = sharedPreferences.getString(AppConstants.USER_MOBILE_NUMBER, null);

        splitBillDatabase = new SplitBillDatabase(this);
        new SplitBillDatabase.myDbHelper(this);

        Log.e("Step", "1");
        addExpense();
        findViewById(R.id.fab_add_expense).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentAddExpense = new Intent(ExpenseActivity.this, AddExpenseActivity.class);
                startActivity(intentAddExpense);
                finish();
            }
        });

    }

    private void addExpense() {
        Log.e("Step", "2");
        splitBillDatabase.getExpenseData();


        rcvExpenseList = findViewById(R.id.rcv_expense_list);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        rcvExpenseList.setLayoutManager(mLayoutManager);
        rcvExpenseList.setItemAnimator(new DefaultItemAnimator());

        ExpenseAdapter expenseAdapter = new ExpenseAdapter(ExpenseActivity.this, expenseList);

        rcvExpenseList.setAdapter(expenseAdapter);
        expenseAdapter.notifyDataSetChanged();

    }

    private void addRemoveContactDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(ExpenseActivity.this);
        final View customLayout = getLayoutInflater().inflate(R.layout.layout_dialog_parent_member, null);
        builder.setView(customLayout);
        final AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        llParentLayout = customLayout.findViewById(R.id.ll_add_parent_layout);

        members = splitBillDatabase.getMemberData();
        //  members = splitBillDatabase.getMemberData();

        for (Member member : members) {
            memberList.add(member);
            addLayout(member.getDisplayName(), member.getPhoneNumber());
        }

       /* for (int i = 0; i < memberList.size(); i++) {
            Expense expense = new Expense();
            expense.contactId = memberList.get(i).contactId;
            expense.memberName = memberList.get(i).displayName;
            expense.memberContact = memberList.get(i).phoneNumber;
            expense.groupId = String.valueOf(selectedGroupId);
            expense.groupName = String.valueOf(selectedGroupName);
            expense.dateTime = String.valueOf(selectedGroupDateTime);
            expense.expenseID = String.valueOf(selectedExpenseId);
            expense.expenseName = String.valueOf(selectedExpenseName);
            expense.dateTime = String.valueOf(selectedExpenseDateTime);
            expenseList.add(expense);
            Log.e("memberList", new Gson().toJson(members));
            // addLayout(expense.memberName, expense.memberContact);


        }
        splitBillDatabase.insertCustomerData(expenseList);*/

        AppCompatImageView imgCloseAddRemove = customLayout.findViewById(R.id.ic_close_add_remove);
        imgCloseAddRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        AppCompatButton buttonAddContact = customLayout.findViewById(R.id.btn_add_contact);
        AppCompatButton buttonSaveContact = customLayout.findViewById(R.id.btn_save_contact);

        buttonAddContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getContacts();
            }
        });

        buttonSaveContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("TAG", members.toString());
                // splitBillDatabase.getExpenseData();
                splitBillDatabase.insertCustomerData(expenseList);

                Toast.makeText(ExpenseActivity.this, "Contact Saved Successfully!", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });


        dialog.show();

    }

    private void addLayout(String memberName, String memberContact) {
        final View layout = LayoutInflater.from(this).inflate(R.layout.layout_dialog_child_member, llParentLayout, false);

        final AppCompatTextView textAddMember = layout.findViewById(R.id.tv_add_member);
        final AppCompatTextView textAddContactNo = layout.findViewById(R.id.tv_add_contact_no);
        final AppCompatImageView imgRemoveMember = layout.findViewById(R.id.ic_dialog_remove_member);

        textAddMember.setText(memberName);
        textAddContactNo.setText(memberContact);
        imgRemoveMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                llParentLayout.removeView(layout);
                splitBillDatabase.deleteContactData(String.valueOf(selectedGroupId), textAddContactNo.getText().toString().trim());
                Toast.makeText(getApplicationContext(),"Members removed successfully !!", Toast.LENGTH_SHORT).show();

            }
        });

        llParentLayout.addView(layout);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_add_remove) {
            addRemoveContactDialog();
            return true;
        }
        onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    private void getContacts() {
        results.clear();
        new MultiContactPicker.Builder(ExpenseActivity.this)
                .theme(R.style.CustomPickerTheme)
                .hideScrollbar(false)
                .showTrack(true)
                .searchIconColor(Color.WHITE)
                .setChoiceMode(MultiContactPicker.CHOICE_MODE_MULTIPLE)
                .handleColor(ContextCompat.getColor(ExpenseActivity.this, R.color.colorPrimary))
                .bubbleColor(ContextCompat.getColor(ExpenseActivity.this, R.color.colorPrimary))
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
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(ExpenseActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_CLEAR_TASK |
                Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        this.finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CONTACT_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                results.clear();
                results.addAll(MultiContactPicker.obtainResult(data));
                if (results.size() > 0) {
                    memberList.clear();
                    expenseList.clear();

                    for (int i = 0; i < results.size(); i++) {
                        Member member = new Member(results.get(i).getContactID(),
                                results.get(i).getDisplayName(),
                                results.get(i).getPhoneNumbers().get(0).getNumber(),
                                results.get(i).getPhoto(),
                                results.get(i).getThumbnail(),
                                null,null,null,null,null,null);
                        memberList.add(member);
                        //  addLayout(results.get(i).getDisplayName(), results.get(i).getPhoneNumbers().get(0).getNumber());
                    }

                    for (int i = 0; i < memberList.size(); i++) {
                        Expense expense = new Expense();
                        expense.contactId = memberList.get(i).contactId;
                        expense.memberName = memberList.get(i).displayName;
                        expense.memberContact = memberList.get(i).phoneNumber;
                        expense.groupId = String.valueOf(selectedGroupId);
                        expense.groupName = String.valueOf(selectedGroupName);
                        expense.dateTime = String.valueOf(selectedGroupDateTime);
                        expense.expenseID = String.valueOf(selectedExpenseId);
                        expense.expenseName = String.valueOf(selectedExpenseName);
                        expense.dateTime = String.valueOf(selectedExpenseDateTime);
                        expenseList.add(expense);
                        Log.e("memberList", new Gson().toJson(members));
                        addLayout(expense.memberName, expense.memberContact);


                    }
                    splitBillDatabase.insertCustomerData(expenseList);
                    // splitBillDatabase.insertCustomerData(expenseList);

                }
               /* Gson gson = new GsonBuilder()
                        .registerTypeAdapter(Uri.class, new MainActivity.UriSerializer())
                        .create();
                String jsonSelectedMember = gson.toJson(memberList);
                SharedPreferences sharedPreferences = getSharedPreferences(AppConstants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(AppConstants.SELECTED_MEMBERS, jsonSelectedMember);
                editor.apply();*/
                //   recreate();
            } else if (resultCode == RESULT_CANCELED) {
                System.out.println("User closed the picker without selecting items.");
            }
        }
    }


}
