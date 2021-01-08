package com.anitachipkar.splittrackbills.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem;

import com.anitachipkar.splittrackbills.R;
import com.anitachipkar.splittrackbills.SplitBillDatabase;
import com.anitachipkar.splittrackbills.adapter.ExpenseAdapter;
import com.anitachipkar.splittrackbills.adapter.OverviewAdapter;
import com.anitachipkar.splittrackbills.adapter.SettlementAdapter;
import com.anitachipkar.splittrackbills.model.Expense;
import com.anitachipkar.splittrackbills.model.Member;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.anitachipkar.splittrackbills.adapter.OverviewAdapter.amountPaidByMember;

public class SettlementActivity extends AppCompatActivity {

    RecyclerView rcvExpenseOverview;
    RecyclerView rcvExpenseSettlement;
    List<Member> settlementList;
    String paidByData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settlement);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }


        initViews();


    }

    private void initViews() {
        //  paidByData = getIntent().getStringExtra("paid_by_data");


        SplitBillDatabase splitBillDatabase = new SplitBillDatabase(this);
        new SplitBillDatabase.myDbHelper(this);


        settlementList = splitBillDatabase.getMemberData();
        rcvExpenseOverview = findViewById(R.id.rcv_expense_overview);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        rcvExpenseOverview.setLayoutManager(mLayoutManager);
        rcvExpenseOverview.setItemAnimator(new DefaultItemAnimator());


       /* for (Member member : settlementList) {
            if (paidByData.equalsIgnoreCase(member.getPaidBy())) {
         */
        OverviewAdapter overviewAdapter = new OverviewAdapter(SettlementActivity.this, settlementList);

        rcvExpenseOverview.setAdapter(overviewAdapter);
        overviewAdapter.notifyDataSetChanged();

        // }
        //}


        rcvExpenseSettlement = findViewById(R.id.rcv_expense_settle_person);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        rcvExpenseSettlement.setLayoutManager(layoutManager);
        rcvExpenseSettlement.setItemAnimator(new DefaultItemAnimator());

        final SettlementAdapter settlementAdapter = new SettlementAdapter(SettlementActivity.this, settlementList);

        rcvExpenseSettlement.setAdapter(settlementAdapter);
        settlementAdapter.notifyDataSetChanged();
       /* rcvExpenseSettlement.post(new Runnable()
        {
            @Override
            public void run() {
                settlementAdapter.notifyDataSetChanged();
            }
        });*/


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
