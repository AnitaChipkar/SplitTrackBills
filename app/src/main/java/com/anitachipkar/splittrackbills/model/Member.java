package com.anitachipkar.splittrackbills.model;

import android.net.Uri;

import java.io.Serializable;

public class Member implements Serializable {

    public String contactId;
    public String displayName;
    public String phoneNumber;
    public String amount;
    public String eachAmount;
    public Uri photo;
    public Uri thumbnail;
    public String GroupId;
    public String ExpenseId;
    public String isPaid;
    public String paidBy;


    public Member(String contactId, String displayName, String phoneNumber, Uri photo,
                  Uri thumbnail, String groupId, String expenseId, String eachAmountValue, String amountValue, String isPaidValue, String paidByPerson) {
        this.contactId = contactId;
        this.displayName = displayName;
        this.phoneNumber = phoneNumber;
        this.photo = photo;
        this.thumbnail = thumbnail;
        GroupId = groupId;
        ExpenseId = expenseId;
        eachAmount = eachAmountValue;
        amount = amountValue;
        isPaid = isPaidValue;
        paidBy = paidByPerson;
    }

    /* public Member(String contactId, String displayName, String phoneNumber, String groupId) {
         this.contactId = contactId;
         this.displayName = displayName;
         this.phoneNumber = phoneNumber;
         GroupId = groupId;
     }
 */

    public String getIsPaid() {
        return isPaid;
    }

    public void setIsPaid(String isPaid) {
        this.isPaid = isPaid;
    }

    public String getPaidBy() {
        return paidBy;
    }

    public void setPaidBy(String paidBy) {
        this.paidBy = paidBy;
    }

    public String getGroupId() {
        return GroupId;
    }

    public void setGroupId(String groupId) {
        GroupId = groupId;
    }


    public String getExpenseId() {
        return ExpenseId;
    }

    public void setExpenseId(String expenseId) {
        ExpenseId = expenseId;
    }

    public Uri getPhoto() {
        return photo;
    }

    public void setPhoto(Uri photo) {
        this.photo = photo;
    }

    public Uri getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(Uri thumbnail) {
        this.thumbnail = thumbnail;
    }


    public String getContactId() {
        return contactId;
    }

    public void setContactId(String contactId) {
        this.contactId = contactId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override
    public String toString() {
        return displayName;
    }


    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getEachAmount() {
        return eachAmount;
    }

    public void setEachAmount(String eachAmount) {
        this.eachAmount = eachAmount;
    }


}
