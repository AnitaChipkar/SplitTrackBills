package com.anitachipkar.splittrackbills.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.anitachipkar.splittrackbills.R;
import com.anitachipkar.splittrackbills.model.Group;
import com.anitachipkar.splittrackbills.model.Member;
import com.anitachipkar.splittrackbills.view.ExpenseActivity;

import java.util.ArrayList;
import java.util.Objects;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.MyViewHolder> {

    private ArrayList<Group> groupList;
    private Context context;
    public static Integer selectedGroupId = 0;
    public static String selectedGroupName = "";
    public static String selectedGroupDateTime = "";


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public AppCompatTextView textGroupFirstLetter, textGroupName,
                textGroupDate, textGroupExpenseCount, textGroupMemberCount, textGroupAmount;
        public LinearLayout ll_groupLayout;

        public MyViewHolder(View view) {
            super(view);
            textGroupFirstLetter = view.findViewById(R.id.tv_group_first_letter);
            textGroupName = view.findViewById(R.id.tv_group_name);
            textGroupDate = view.findViewById(R.id.tv_group_date);
            textGroupExpenseCount = view.findViewById(R.id.tv_group_expense_count);
            textGroupMemberCount = view.findViewById(R.id.tv_group_member_count);
            textGroupAmount = view.findViewById(R.id.tv_group_amount);
            ll_groupLayout = view.findViewById(R.id.ll_groupLayout);
        }
    }

    public GroupAdapter(Context context, ArrayList<Group> groups) {
        this.context = context;
        this.groupList = groups;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_group_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final Group group = groupList.get(position);
        holder.textGroupName.setText(group.getGroupName());
        char firstLetter = Objects.requireNonNull(group.getGroupName()).charAt(0);
        holder.textGroupFirstLetter.setText(String.valueOf(firstLetter));
        holder.textGroupMemberCount.setText(group.getGroupMemberCount());
        holder.textGroupExpenseCount.setText(group.getGroupExpenseCount());
        holder.textGroupAmount.setText("\u20B9 " + group.getGroupEachAmount());
        holder.textGroupDate.setText(group.getGroupDateTime());

        holder.ll_groupLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedGroupId = Integer.valueOf(group.groupId);
                selectedGroupName = group.groupName;
                selectedGroupDateTime = group.groupDateTime;
                Log.e("selectedGroup", String.valueOf(selectedGroupId));
                Intent intentExpense = new Intent(context, ExpenseActivity.class);
                context.startActivity(intentExpense);

            }
        });
    }

    @Override
    public int getItemCount() {
        return groupList.size();
    }
}