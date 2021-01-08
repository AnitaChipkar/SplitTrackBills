package com.anitachipkar.splittrackbills;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.anitachipkar.splittrackbills.model.Expense;
import com.anitachipkar.splittrackbills.model.Group;
import com.anitachipkar.splittrackbills.model.Member;
import com.anitachipkar.splittrackbills.utils.AppConstants;
import com.google.gson.Gson;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.anitachipkar.splittrackbills.SplitBillDatabase.myDbHelper.amount;
import static com.anitachipkar.splittrackbills.SplitBillDatabase.myDbHelper.eachAmount;
import static com.anitachipkar.splittrackbills.SplitBillDatabase.myDbHelper.paidBy;
import static com.anitachipkar.splittrackbills.adapter.ExpenseAdapter.amountValue;
import static com.anitachipkar.splittrackbills.adapter.ExpenseAdapter.eachAmountValue;
import static com.anitachipkar.splittrackbills.adapter.ExpenseAdapter.isPaidValue;
import static com.anitachipkar.splittrackbills.adapter.ExpenseAdapter.paidByValue;
import static com.anitachipkar.splittrackbills.adapter.ExpenseAdapter.selectedExpenseId;
import static com.anitachipkar.splittrackbills.adapter.GroupAdapter.selectedGroupId;
import static com.anitachipkar.splittrackbills.view.ExpenseActivity.contactNo;
import static com.anitachipkar.splittrackbills.view.ExpenseActivity.expenseList;
import static com.anitachipkar.splittrackbills.view.ExpenseActivity.name;
import static com.anitachipkar.splittrackbills.view.MainActivity.groupList;


@SuppressLint("Recycle")
public class SplitBillDatabase {
    private myDbHelper myhelper;

    public SplitBillDatabase(Context context) {
        myhelper = new myDbHelper(context);
    }

    //  Inserting Downloaded Customer Data in table TABLE_CUSTOMERS
    public void insertCustomerData(List<Expense> getGroupContacts) {
        try {
            SQLiteDatabase dbb = myhelper.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            for (int i = 0; i < getGroupContacts.size(); i++) {
                contentValues.put(myDbHelper.groupID, getGroupContacts.get(i).groupId);
                contentValues.put(myDbHelper.expenseID, getGroupContacts.get(i).expenseID);
                contentValues.put(myDbHelper.contactID, getGroupContacts.get(i).contactId);
                contentValues.put(myDbHelper.isPaid, getGroupContacts.get(i).isPaid);
                contentValues.put(paidBy, getGroupContacts.get(i).paidBy);
                contentValues.put(myDbHelper.expenseName, getGroupContacts.get(i).expenseName);
                contentValues.put(myDbHelper.contactName, getGroupContacts.get(i).memberName);
                contentValues.put(myDbHelper.contactNumber, getGroupContacts.get(i).memberContact);
                contentValues.put(myDbHelper.groupName, getGroupContacts.get(i).groupName);
                contentValues.put(myDbHelper.createdDate, getGroupContacts.get(i).dateTime);
                try {
                    if (getGroupContacts.get(i).amount != null && getGroupContacts.get(i).eachAmount != null) {
                        contentValues.put(myDbHelper.amount, getGroupContacts.get(i).amount);
                        contentValues.put(myDbHelper.eachAmount, getGroupContacts.get(i).eachAmount);
                    }
                } catch (Exception e) {
                    contentValues.put(myDbHelper.amount, "0");
                    contentValues.put(myDbHelper.eachAmount, "0");
                }
                int id = dbb.updateWithOnConflict(myDbHelper.TABLE_GROUPS, contentValues, myDbHelper.groupID + " = ?" +
                                " AND " + myDbHelper.contactName + " = ?" + " AND " + myDbHelper.expenseID + " = ?" + " AND "
                                + myDbHelper.contactID + " = ?", new String[]{getGroupContacts.get(i).groupId,
                                getGroupContacts.get(i).memberName, getGroupContacts.get(i).expenseID, getGroupContacts.get(i).contactId},
                        SQLiteDatabase.CONFLICT_IGNORE);
                if (id == 0) {
                    dbb.insert(myDbHelper.TABLE_GROUPS, null, contentValues);
                }
            }
        } catch (Exception ignored) {

        }
    }// End



    public void deleteContactData(String groupId, String ContactNumber) {
        try {
            SQLiteDatabase dbb = myhelper.getWritableDatabase();
            dbb.delete(myDbHelper.TABLE_GROUPS, myDbHelper.groupID + " = ?" + " AND "
                    + myDbHelper.contactNumber + " = ?", new String[]{groupId, ContactNumber});
            AdjustExpenseAmounts(groupId);

        } catch (Exception ignored) {
        }
    }

    @SuppressLint("Recycle")
    private void AdjustExpenseAmounts(String groupId) {
        SQLiteDatabase db = myhelper.getWritableDatabase();
        String query = "SELECT distinct(ExpenseID) FROM BillGroups where GroupId = "+ groupId;
        Cursor c = db.rawQuery(query, null);
        while (c.moveToNext()) {
            String getExpenseCount = "SELECT Count(ExpenseID) as TotalMembers, totalAmount as TotalAmt FROM BillGroups where ExpenseID = "+ c.getInt(0);
            Cursor c1 = db.rawQuery(getExpenseCount, null);
            if (c1.moveToFirst()) {
                String newAmount = String.valueOf(c1.getInt(c1.getColumnIndex("TotalAmt")) / c1.getInt(c1.getColumnIndex("TotalMembers")));
                Log.e("neAmt", String.valueOf(newAmount));
                String updateExpense = "Update BillGroups set perAmount ="+newAmount+" where ExpenseId = " + c.getInt(0);
                try{
                    Cursor c2 = db.rawQuery(updateExpense, null);
                    c2.moveToNext();
                }catch (Exception e){
                    Log.e("exc",e.toString());
                }
            }
        }

    }


    public void getGroupData() {
        groupList.clear();
        try {
            SQLiteDatabase db = myhelper.getWritableDatabase();
            String[] columns = {myDbHelper.groupID, myDbHelper.groupName, myDbHelper.createdDate, myDbHelper.eachAmount, myDbHelper.amount, myDbHelper.contactNumber};
            Cursor cursor = db.query(myDbHelper.TABLE_GROUPS, columns, null, null, myDbHelper.groupID, null, null);
            while (cursor.moveToNext()) {
                Group group = new Group();
                group.groupId = cursor.getString(cursor.getColumnIndex(myDbHelper.groupID));
                group.groupName = cursor.getString(cursor.getColumnIndex(myDbHelper.groupName));
                Integer GroupExpense = 0;
                String query = "SELECT SUM(eachAmount),Count(distinct(ExpenseID)) as Total, " +
                        "count(distinct(contactNumber)) as TotalUser FROM BillGroups where GroupId ="
                        + cursor.getString(cursor.getColumnIndex(myDbHelper.groupID));

                @SuppressLint("Recycle")
                Cursor c = db.rawQuery(query, null);
                if (c.moveToFirst()) { // if cursor is not empty
                    // do your workx
                    GroupExpense = c.getInt(0);
                    group.groupMemberCount = String.valueOf(c.getInt(c.getColumnIndex("TotalUser")));
                    group.groupExpenseCount = String.valueOf(c.getInt(c.getColumnIndex("Total")));
                }
                group.groupEachAmount = String.valueOf(GroupExpense);
                group.groupAmount = String.valueOf(GroupExpense);
                group.groupDateTime = cursor.getString(cursor.getColumnIndex(myDbHelper.createdDate));
                groupList.add(group);

                Log.e("groupList", new Gson().toJson(groupList));
            }
        } catch (Exception ignored) {
        }
    } // End

    public void insertExpenseData(List<Expense> getGroupContacts) {
        try {
            SQLiteDatabase dbb = myhelper.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            for (int i = 0; i < getGroupContacts.size(); i++) {
                contentValues.put(myDbHelper.groupID, getGroupContacts.get(i).groupId);
                contentValues.put(myDbHelper.contactName, getGroupContacts.get(i).memberName);
                contentValues.put(myDbHelper.contactNumber, getGroupContacts.get(i).memberContact);
                contentValues.put(myDbHelper.groupName, getGroupContacts.get(i).groupName);
                contentValues.put(myDbHelper.createdDate, getGroupContacts.get(i).dateTime);
                try {
                    if (getGroupContacts.get(i).amount != null) {
                        contentValues.put(myDbHelper.amount, getGroupContacts.get(i).amount);
                    }
                } catch (Exception e) {
                    contentValues.put(myDbHelper.amount, "0");
                }
                int id = dbb.updateWithOnConflict(myDbHelper.TABLE_GROUPS, contentValues,
                        myDbHelper.groupID + " = ?" + " AND " + myDbHelper.contactName + " = ?",
                        new String[]{getGroupContacts.get(i).groupId, getGroupContacts.get(i).memberName}, SQLiteDatabase.CONFLICT_IGNORE);
                if (id == 0) {
                    dbb.insert(myDbHelper.TABLE_GROUPS, null, contentValues);
                }
            }
        } catch (Exception ignored) {

        }
    }// End

    public void getExpenseData() {
        Log.e("Step", "3");
        expenseList.clear();
        try {
            SQLiteDatabase db = myhelper.getWritableDatabase();
            String[] columns = {myDbHelper.groupID, myDbHelper.expenseName, myDbHelper.contactNumber,
                    myDbHelper.contactName, myDbHelper.isPaid, paidBy,
                    myDbHelper.eachAmount, myDbHelper.amount, myDbHelper.createdDate, myDbHelper.amount, myDbHelper.expenseID};
            Cursor cursor = db.query(myDbHelper.TABLE_GROUPS, columns, myDbHelper.groupID + "=?", new String[]{String.valueOf(selectedGroupId)}, myDbHelper.expenseID, null, null);
            while (cursor.moveToNext()) {
                Expense expense = new Expense();
                expense.expenseID = cursor.getString(cursor.getColumnIndex(myDbHelper.expenseID));
                expense.expenseName = cursor.getString(cursor.getColumnIndex(myDbHelper.expenseName));
                expense.memberName = cursor.getString(cursor.getColumnIndex(myDbHelper.contactName));
                expense.paidBy = cursor.getString(cursor.getColumnIndex(paidBy));
                expense.isPaid = cursor.getString(cursor.getColumnIndex(myDbHelper.isPaid));
                expense.dateTime = cursor.getString(cursor.getColumnIndex(myDbHelper.createdDate));

                String query = "SELECT SUM(eachAmount) FROM BillGroups where ExpenseId =" +
                        cursor.getString(cursor.getColumnIndex(myDbHelper.expenseID));
                @SuppressLint("Recycle")
                Cursor c = db.rawQuery(query, null);
                if (c.moveToFirst()) {
                    expense.eachAmount = String.valueOf(c.getInt(0));
                    expense.amount = String.valueOf(c.getInt(0));
                }

                String getpaidbyQuery = "SELECT contactNumber as Number, contactName as Name FROM BillGroups " +
                        "where ExpenseId =" + cursor.getString(cursor.getColumnIndex(myDbHelper.expenseID)) + " AND isPaid = 1";
                @SuppressLint("Recycle")
                Cursor paidByCursor = db.rawQuery(getpaidbyQuery, null);
                if (paidByCursor.moveToFirst()) { // if cursor is not empty
                    expense.memberContact = String.valueOf(paidByCursor.getString(paidByCursor.getColumnIndex("Number")));
                    expense.memberName = String.valueOf(paidByCursor.getString(paidByCursor.getColumnIndex("Name")));
                }
                expenseList.add(expense);
            }
            Log.e("Step", "4");
            Log.e("expenseList", new Gson().toJson(expenseList));
        } catch (Exception e) {
            Log.e("Exc", e.toString());
        }
    } // End

    public ArrayList<Member> getMemberData() {
        Log.e("Step", "3");
        ArrayList<Member> memberList = new ArrayList<>();
        memberList.clear();
        try {
            SQLiteDatabase db = myhelper.getWritableDatabase();
            String[] columns = {myDbHelper.groupID, myDbHelper.expenseID, myDbHelper.contactName, myDbHelper.contactNumber, myDbHelper.contactID,
                    myDbHelper.eachAmount, myDbHelper.amount, myDbHelper.isPaid, paidBy};

            Member addMember = new Member("User", name, contactNo, null, null,
                    String.valueOf(selectedGroupId), String.valueOf(selectedExpenseId), eachAmountValue, amountValue, isPaidValue, paidByValue);
            memberList.add(addMember);

            Cursor cursor = db.query(myDbHelper.TABLE_GROUPS, columns, myDbHelper.groupID + "=?" + " AND " + myDbHelper.expenseID + " = ?", new String[]{String.valueOf(selectedGroupId), String.valueOf(selectedExpenseId)}, myDbHelper.contactID, null, null);
            while (cursor.moveToNext()) {


                Member member = new Member(cursor.getString(cursor.getColumnIndex(myDbHelper.contactID)),
                        cursor.getString(cursor.getColumnIndex(myDbHelper.contactName)),
                        cursor.getString(cursor.getColumnIndex(myDbHelper.contactNumber)), null, null,
                        cursor.getString(cursor.getColumnIndex(myDbHelper.groupID)),
                        cursor.getString(cursor.getColumnIndex(myDbHelper.expenseID)),
                        cursor.getString(cursor.getColumnIndex(myDbHelper.eachAmount)),
                        cursor.getString(cursor.getColumnIndex(myDbHelper.amount)),
                        cursor.getString(cursor.getColumnIndex(myDbHelper.isPaid)),
                        cursor.getString(cursor.getColumnIndex(paidBy)));

                String query = "SELECT Amount,eachAmount FROM BillGroups where ExpenseId =" + cursor.getString(cursor.getColumnIndex(myDbHelper.contactID));
                @SuppressLint("Recycle")
                Cursor c = db.rawQuery(query, null);
                if (c.moveToFirst()) {
                    member.amount = String.valueOf(c.getInt(0));
                    member.eachAmount = String.valueOf(c.getInt(1));
                }
                memberList.add(member);
            }
            Log.e("Step", "4");
            Log.e("expenseList", new Gson().toJson(memberList));
        } catch (Exception e) {
            Log.e("Exc", e.toString());
        }
        return memberList;
    } // End

    public static class myDbHelper extends SQLiteOpenHelper {

        public static final String TABLE_GROUPS = "BillGroups";
        public static final String DATABASE_NAME = "SplitBill";
        public static final int DATABASE_Version = 1;
        public static final String groupID = "GroupId";
        public static final String expenseID = "ExpenseID";
        public static final String contactID = "contactID";
        public static final String contactName = "contactName";
        public static final String amount = "Amount";
        public static final String eachAmount = "eachAmount";
        public static final String contactNumber = "contactNumber";
        public static final String groupName = "groupName";
        public static final String expenseName = "expenseName";
        public static final String isPaid = "isPaid";
        public static final String paidBy = "paidBy";
        public static final String createdDate = "createdDate";

        private static final String CREATE_TABLE_CUSTOMER = "CREATE TABLE " + TABLE_GROUPS +
                " (" + groupID + " VARCHAR(500) ," + expenseID + " VARCHAR(500) ," + contactID + " VARCHAR(500) ," + contactName + " VARCHAR(500) ,"
                + amount + " VARCHAR(500)," + eachAmount + " VARCHAR(500)," +
                contactNumber + " VARCHAR(500)," + groupName + " VARCHAR(500)," + expenseName + " VARCHAR(500)," +
                isPaid + " VARCHAR(500)," + paidBy + " VARCHAR(500)," + createdDate + " VARCHAR(500) )";

        private Context context;

        public myDbHelper(Context context) {
            // super(context, Environment.getExternalStorageDirectory()+ File.separator+"SplitBill"+File.separator+DATABASE_NAME+"_V"+BuildConfig.VERSION_CODE, null, DATABASE_Version);
            super(context, DATABASE_NAME, null, DATABASE_Version);
            this.context = context;
        }

        public void onCreate(SQLiteDatabase db) {
            try {
                db.execSQL(CREATE_TABLE_CUSTOMER);
            } catch (Exception e) {
                Message.message(context, "" + e);
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            try {
                Message.message(context, "OnUpgrade");
                db.execSQL("DROP TABLE IF EXISTS " + TABLE_GROUPS);
                onCreate(db);
            } catch (Exception e) {
                Message.message(context, "" + e);
            }
        }
    }

    public static class Message {
        public static void message(Context context, String message) {
            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
        }
    }
}