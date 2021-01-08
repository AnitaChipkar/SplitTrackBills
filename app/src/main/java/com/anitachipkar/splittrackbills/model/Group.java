package com.anitachipkar.splittrackbills.model;


import java.io.Serializable;

public class Group implements Serializable {

    public String groupId;
    public String groupName;
    public String groupAmount;
    public String groupEachAmount;
    public String groupMemberCount;
    public String groupExpenseCount;
    public String groupDateTime;


    public String getGroupEachAmount() {
        return groupEachAmount;
    }

    public void setGroupEachAmount(String groupEachAmount) {
        this.groupEachAmount = groupEachAmount;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupAmount() {
        return groupAmount;
    }

    public void setGroupAmount(String groupAmount) {
        this.groupAmount = groupAmount;
    }

    public String getGroupMemberCount() {
        return groupMemberCount;
    }

    public void setGroupMemberCount(String groupMemberCount) {
        this.groupMemberCount = groupMemberCount;
    }

    public String getGroupExpenseCount() {
        return groupExpenseCount;
    }

    public void setGroupExpenseCount(String groupExpenseCount) {
        this.groupExpenseCount = groupExpenseCount;
    }

    public String getGroupDateTime() {
        return groupDateTime;
    }

    public void setGroupDateTime(String groupDateTime) {
        this.groupDateTime = groupDateTime;
    }


}
