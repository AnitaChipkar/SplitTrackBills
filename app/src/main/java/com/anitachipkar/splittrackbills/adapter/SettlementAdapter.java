package com.anitachipkar.splittrackbills.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.anitachipkar.splittrackbills.R;
import com.anitachipkar.splittrackbills.model.Expense;
import com.anitachipkar.splittrackbills.model.Member;
import com.anitachipkar.splittrackbills.utils.AppConstants;
import com.anitachipkar.splittrackbills.view.MainActivity;
import com.anitachipkar.splittrackbills.view.SettlementActivity;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.anitachipkar.splittrackbills.adapter.OverviewAdapter.amountPaidByMember;

public class SettlementAdapter extends RecyclerView.Adapter<SettlementAdapter.MyViewHolder> {

    private List<Member> memberList;
    private Context context;
    public static boolean spentAmountByMember = false;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public AppCompatTextView textPaidPersonFirstLetter, textPaidPersonName, textPaidPersonAmount, textSettlePersonFirstLetter, textSettlePersonName;

        public MyViewHolder(View view) {
            super(view);
            textPaidPersonFirstLetter = view.findViewById(R.id.tv_paid_person_first_letter);
            textPaidPersonName = view.findViewById(R.id.tv_paid_person_name);
            textPaidPersonAmount = view.findViewById(R.id.tv_paid_person_amount);
            textSettlePersonFirstLetter = view.findViewById(R.id.tv_settle_person_first_letter);
            textSettlePersonName = view.findViewById(R.id.tv_settle_person_name);


        }
    }

    public SettlementAdapter(Context context, List<Member> members) {
        this.context = context;
        this.memberList = members;
        notifyDataSetChanged();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_settle_person_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final Member member = memberList.get(position);
        if (amountPaidByMember != null) {
            holder.textSettlePersonName.setText(amountPaidByMember);

            char firstLetter = Objects.requireNonNull(amountPaidByMember).charAt(0);
            holder.textSettlePersonFirstLetter.setText(String.valueOf(firstLetter));


            char firstLetter1 = Objects.requireNonNull(member.getDisplayName()).charAt(0);
            holder.textPaidPersonName.setText(member.getDisplayName());
            holder.textPaidPersonFirstLetter.setText(String.valueOf(firstLetter1));
            if (!"".equalsIgnoreCase(member.getEachAmount())) {

                float floatPaidFrom = Float.parseFloat(member.getEachAmount());
                String totalPaidFrom = "\u20B9 " + new DecimalFormat("##.##").format(floatPaidFrom);
                holder.textPaidPersonAmount.setText(totalPaidFrom);
            }


            if (amountPaidByMember.equalsIgnoreCase(member.getDisplayName())) {
                holder.itemView.setVisibility(View.GONE);
                ViewGroup.LayoutParams params = holder.itemView.getLayoutParams();
                params.height = 0;
                holder.itemView.setLayoutParams(params);
            }
            SharedPreferences sharedPreferences = context.getSharedPreferences(AppConstants.SHARED_PREFERENCES, Context.MODE_PRIVATE);

           /* if ("amountSettled".equals(sharedPreferences.getString("settled",null)))
            {
                holder.itemView.setVisibility(View.GONE);
                ViewGroup.LayoutParams params = holder.itemView.getLayoutParams();
                params.height = 0;
                holder.itemView.setLayoutParams(params);

            }*/
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    spentAmountByMember = true;
                    holder.itemView.setVisibility(View.GONE);
                    ViewGroup.LayoutParams params = holder.itemView.getLayoutParams();
                    params.height = 0;
                    holder.itemView.setLayoutParams(params);
                    dialogSettleAmount(member.getDisplayName(), amountPaidByMember, member.getEachAmount());

                }
            });


        }


    }

    @SuppressLint("SetTextI18n")
    private void dialogSettleAmount(final String memberPaid, String memberSettle, String memberSettleAmount) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View customLayout = inflater.inflate(R.layout.layout_dialog_settle_amount, null);
        builder.setView(customLayout);

        final AlertDialog dialog = builder.create();
        dialog.setCancelable(false);

        AppCompatTextView textPaidFirstName = customLayout.findViewById(R.id.tv_paid_settle_first_letter);
        AppCompatTextView textSettleFirstName = customLayout.findViewById(R.id.tv_settle_member_first_letter);
        AppCompatTextView textPaidMember = customLayout.findViewById(R.id.tv_paid_member_name);
        AppCompatTextView textSettleMember = customLayout.findViewById(R.id.tv_settle_member_name);
        AppCompatTextView textSettleAmount = customLayout.findViewById(R.id.tv_settle_member_amount);
        //  final AppCompatImageView imgCloseDialog = customLayout.findViewById(R.id.ic_close_settle_dialog);

        char paidFirstLetter = Objects.requireNonNull(memberPaid).charAt(0);
        textPaidFirstName.setText(String.valueOf(paidFirstLetter));

        char settleFirstLetter = Objects.requireNonNull(memberSettle).charAt(0);
        textSettleFirstName.setText(String.valueOf(settleFirstLetter));


        textPaidMember.setText(memberPaid);
        textSettleMember.setText(memberSettle);
        float floatSettle = Float.parseFloat(memberSettleAmount);
        String settleAmount = "\u20B9 " + new DecimalFormat("##.##").format(floatSettle);

        textSettleAmount.setText(settleAmount);


        AppCompatButton buttonSettleAmount = customLayout.findViewById(R.id.btn_settle_amount);

        buttonSettleAmount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedPreferences = context.getSharedPreferences(AppConstants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("settled", "amountSettled");
                editor.apply();
                dialog.dismiss();


            }
        });
         /* imgCloseDialog.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View view) {
                  dialog.dismiss();
              }
          });*/


        dialog.show();

    }

    @Override
    public int getItemCount() {
        return memberList.size();
    }
}