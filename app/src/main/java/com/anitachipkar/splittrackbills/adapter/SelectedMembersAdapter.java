package com.anitachipkar.splittrackbills.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.anitachipkar.splittrackbills.model.Member;
import com.anitachipkar.splittrackbills.R;

import java.util.ArrayList;

public class SelectedMembersAdapter extends RecyclerView.Adapter<SelectedMembersAdapter.MyViewHolder> {

    private ArrayList<Member> memberList;
    private Context context;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        AppCompatImageView imgThumbnail, imgRemoveMember;
        public AppCompatTextView textMemberName, textMemberContact;

        public MyViewHolder(View view) {
            super(view);
            textMemberName = view.findViewById(R.id.tv_member_name);
            textMemberContact = view.findViewById(R.id.tv_member_contact_no);
            imgThumbnail = view.findViewById(R.id.ic_thumbnail);
           // imgRemoveMember = view.findViewById(R.id.ic_remove_member);

        }
    }


    public SelectedMembersAdapter(Context context, ArrayList<Member> members) {
        this.context = context;
        this.memberList = members;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_selected_members_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        Member member = memberList.get(position);
        holder.textMemberName.setText(member.getDisplayName());
        holder.textMemberContact.setText(member.getPhoneNumber());
        holder.imgThumbnail.setImageURI(member.getPhoto());
       /* holder.imgRemoveMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              *//*  memberList.remove(position);
                notifyItemRemoved(position);
                notifyDataSetChanged();*//*


            }
        });*/

    }

    @Override
    public int getItemCount() {
        return memberList.size();
    }
}