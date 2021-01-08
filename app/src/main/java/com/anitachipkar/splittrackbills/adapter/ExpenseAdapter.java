package com.anitachipkar.splittrackbills.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.anitachipkar.splittrackbills.R;
import com.anitachipkar.splittrackbills.SplitBillDatabase;
import com.anitachipkar.splittrackbills.model.Expense;
import com.anitachipkar.splittrackbills.model.Member;
import com.anitachipkar.splittrackbills.view.SettlementActivity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.MyViewHolder> {

    private ArrayList<Expense> expenseList;
    public static Integer selectedExpenseId = 0;
    public static String selectedExpenseName = "";
    public static String selectedExpenseDateTime = "";
    public static String eachAmountValue = "";
    public static String amountValue = "";
    public static String isPaidValue = "";
    public static String paidByValue = "";
    private String amountPaidBy;
    Context context;
    List<Member> memberList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public AppCompatTextView textExpenseFirstLetter, textExpenseName, textExpensePaidBy, textExpenseDate, textExpenseAmount;

        public MyViewHolder(View view) {
            super(view);
            textExpenseFirstLetter = view.findViewById(R.id.tv_expense_first_letter);
            textExpenseName = view.findViewById(R.id.tv_expense_name);
            textExpensePaidBy = view.findViewById(R.id.tv_expense_paid_by);
            textExpenseDate = view.findViewById(R.id.tv_expense_date);
            textExpenseAmount = view.findViewById(R.id.tv_expense_amount);

        }
    }

    public ExpenseAdapter(Context context, ArrayList<Expense> expenses) {
        this.context = context;
        this.expenseList = expenses;
        memberList = new ArrayList<>();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_expense_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final Expense expense = expenseList.get(position);
        holder.textExpenseName.setText(expense.getExpenseName());
        char firstLetter = Objects.requireNonNull(expense.getExpenseName()).charAt(0);
        holder.textExpenseFirstLetter.setText(String.valueOf(firstLetter));

        holder.textExpenseAmount.setText("\u20B9 " + expense.getEachAmount());
        if (expense.memberContact.equalsIgnoreCase(expense.paidBy)) {
            amountPaidBy = expense.memberName;
        }
        holder.textExpenseDate.setText(expense.getDateTime());
        holder.textExpensePaidBy.setText("Paid by " + expense.memberName);


        selectedExpenseId = Integer.valueOf(expense.expenseID);
        selectedExpenseName = expense.getExpenseName();
        selectedExpenseDateTime = expense.getDateTime();


        SplitBillDatabase splitBillDatabase = new SplitBillDatabase(context);
        new SplitBillDatabase.myDbHelper(context);
        memberList = splitBillDatabase.getMemberData();
        float members = memberList.size();
        float subtotal = Float.parseFloat(expense.getAmount());
        float result = (subtotal / members);
        eachAmountValue = String.valueOf(result);
        amountValue = expense.getAmount();
        isPaidValue = expense.getIsPaid();
        paidByValue = expense.getPaidBy();
        Log.e("selectedGroup", String.valueOf(selectedExpenseId));

      /*  final Member member = new Member(expense.getContactId(), expense.getMemberName(), expense.getMemberContact(), null,
                null, expense.getGroupId(), expense.getExpenseID(), expense.getEachAmount(), expense.getAmount(), expense.getIsPaid(), expense.getPaidBy());
        memberList.add(member);
*/

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentSettle = new Intent(context, SettlementActivity.class);
                context.startActivity(intentSettle);
            }
        });


    }

    @Override
    public int getItemCount() {
        return expenseList.size();
    }
}