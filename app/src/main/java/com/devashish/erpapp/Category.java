package com.devashish.erpapp;

public class Category {
    public Category(String item_category, String item_id) {
        this.item_category = item_category;
        this.item_id = item_id;
    }

    Category(){

    }

    public String getItem_category() {

        return item_category;
    }

    public void setItem_category(String item_category) {
        this.item_category = item_category;
    }

    public String getItem_id() {
        return item_id;
    }

    public void setItem_id(String item_id) {
        this.item_id = item_id;
    }

    String item_category,item_id;
}
