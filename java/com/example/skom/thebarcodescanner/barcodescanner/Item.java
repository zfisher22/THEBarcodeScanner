package com.example.skom.thebarcodescanner.barcodescanner;

/**
 * Created by Zach on 2/28/2015.
 */
public class Item {
    private String site;
    private String price;

    public Item(String site, String price) {
        super();
        this.site = site;
        this.price = price;
    }

    public String getSite() {
        return site;
    }

    public String getPrice() {
        return price;
    }

}