package com.anitachipkar.splittrackbills.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.anitachipkar.splittrackbills.R;
import com.anitachipkar.splittrackbills.model.Expense;
import com.anitachipkar.splittrackbills.model.SplitExpense;

import java.text.DecimalFormat;
import java.util.List;

public class SplitAdapter extends RecyclerView.Adapter<SplitAdapter.ViewHolder> {

    private final List<SplitExpense> splitExpenseList;

    private Context context;


    public static class ViewHolder extends RecyclerView.ViewHolder {
        AppCompatTextView textExpenseName;
        AppCompatEditText editExpenseValue;


        ViewHolder(View view) {
            super(view);
            textExpenseName = view.findViewById(R.id.tv_expense_member_name);
            editExpenseValue = view.findViewById(R.id.edit_expense_member_value);
            editExpenseValue.setKeyListener(null);

        }
    }

    public SplitAdapter(Context context, List<SplitExpense> splitExpenseList) {
        this.context = context;
        this.splitExpenseList = splitExpenseList;

    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, final int viewType) {
        final View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_split_expense_row, parent, false);


        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final SplitExpense splitExpense = splitExpenseList.get(position);
        holder.textExpenseName.setText(splitExpense.getSplitMemberName());
        Float floatValue = Float.parseFloat(splitExpense.getSplitAmount());
        // holder.editExpenseValue.setText(String.valueOf(floatValue));
        String expenseValue = "\u20B9 " + new DecimalFormat("##.##").format(floatValue);
        holder.editExpenseValue.setText(expenseValue);

    }

    @Override
    public int getItemCount() {
        return splitExpenseList.size();
    }

}

