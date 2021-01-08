package com.anitachipkar.splittrackbills.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.anitachipkar.splittrackbills.R;
import com.anitachipkar.splittrackbills.model.Expense;
import com.anitachipkar.splittrackbills.model.Member;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.anitachipkar.splittrackbills.adapter.ExpenseAdapter.isPaidValue;
import static com.anitachipkar.splittrackbills.adapter.ExpenseAdapter.paidByValue;
import static com.anitachipkar.splittrackbills.adapter.SettlementAdapter.spentAmountByMember;

public class OverviewAdapter extends RecyclerView.Adapter<OverviewAdapter.MyViewHolder> {

    private List<Member> members;
    private Context context;
    public static String amountPaidByMember = "";
    String eachAmountValue,amount;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public AppCompatTextView textOverviewFirstLetter, textOverviewName, textSpentAmount, textOverviewAmount;

        public MyViewHolder(View view) {
            super(view);
            textOverviewFirstLetter = view.findViewById(R.id.tv_expense_overview_first_letter);
            textOverviewName = view.findViewById(R.id.tv_expense_overview_name);
            textSpentAmount = view.findViewById(R.id.tv_expense_overview_spent);
            textOverviewAmount = view.findViewById(R.id.tv_expense_overview_amount);


        }
    }

    public OverviewAdapter(Context context, List<Member> memberList) {
        this.context = context;
        this.members = memberList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_expense_overview_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        Member member = members.get(position);

        if (!"".equalsIgnoreCase(member.getEachAmount()) && !"".equalsIgnoreCase(member.getAmount()))
        {
            Float floatValue = Float.parseFloat(member.getEachAmount());
            eachAmountValue = "\u20B9 " + new DecimalFormat("##.##").format(floatValue);

            Float floatAmount = Float.parseFloat(member.getAmount());
            amount = "\u20B9 " + new DecimalFormat("##.##").format(floatAmount);


        }


        //  if ("1".equalsIgnoreCase(member.isPaid)) {
        if ("1".equalsIgnoreCase(isPaidValue) || member.phoneNumber.equalsIgnoreCase(paidByValue)) {
            String amountPaidBy = member.getDisplayName();
            amountPaidByMember = member.getDisplayName();
            holder.textOverviewName.setText(amountPaidBy);

            char firstLetter = Objects.requireNonNull(member.getDisplayName()).charAt(0);
            holder.textOverviewFirstLetter.setText(String.valueOf(firstLetter));


            holder.textOverviewName.setTextColor(context.getResources().getColor(R.color.colorGreen));
            holder.textOverviewAmount.setTextColor(context.getResources().getColor(R.color.colorGreen));
            holder.textOverviewAmount.setText(amount);

            float subtotal = Float.parseFloat(member.getEachAmount());
            float memberCount = members.size();

            float result = (subtotal * memberCount);
            String totalPaidValue = "\u20B9 " + new DecimalFormat("##.##").format(result);


            holder.textSpentAmount.setText("Spent Amount : " + totalPaidValue);
        } else {
            holder.textOverviewName.setText(member.getDisplayName());
            char firstLetter = Objects.requireNonNull(member.getDisplayName()).charAt(0);
            holder.textOverviewFirstLetter.setText(String.valueOf(firstLetter));
            holder.textOverviewName.setTextColor(context.getResources().getColor(R.color.colorRed));
            holder.textOverviewAmount.setTextColor(context.getResources().getColor(R.color.colorRed));
            holder.textOverviewAmount.setText(eachAmountValue);


            if (spentAmountByMember) {
                float floatPaidFrom = Float.parseFloat(member.getEachAmount());
                String totalPaidFrom = "\u20B9 " + new DecimalFormat("##.##").format(floatPaidFrom);


                holder.textSpentAmount.setText("Spent Amount : " + totalPaidFrom);

            } else {
                float floatPaidFrom = Float.parseFloat("0");
                String totalPaidFrom = "\u20B9 " + new DecimalFormat("##.##").format(floatPaidFrom);


                holder.textSpentAmount.setText("Spent Amount : " + totalPaidFrom);

            }


        }


    }

    @Override
    public int getItemCount() {
        return members.size();
    }
}