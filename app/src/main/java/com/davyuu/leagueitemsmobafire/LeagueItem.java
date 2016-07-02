package com.davyuu.leagueitemsmobafire;

/**
 * Created by David Yu on 2016-07-02.
 */
public class LeagueItem {
    String name;
    int imageId;
    String totalPrice;

    LeagueItem(String name, int imageId, String totalPrice){
        this.name = name;
        this.imageId = imageId;
        this.totalPrice = totalPrice;
    }
}
