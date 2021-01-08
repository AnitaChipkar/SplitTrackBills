package com.anitachipkar.splittrackbills.model;

import java.io.Serializable;

public class SplitExpense implements Serializable {


    public String splitId;
    public String splitMemberName;
    public String splitAmount;

    public String getSplitId() {
        return splitId;
    }

    public void setSplitId(String splitId) {
        this.splitId = splitId;
    }

    public String getSplitMemberName() {
        return splitMemberName;
    }

    public void setSplitMemberName(String splitMemberName) {
        this.splitMemberName = splitMemberName;
    }

    public String getSplitAmount() {
        return splitAmount;
    }

    public void setSplitAmount(String splitAmount) {
        this.splitAmount = splitAmount;
    }


}
