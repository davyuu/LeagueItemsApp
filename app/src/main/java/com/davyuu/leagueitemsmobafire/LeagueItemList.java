package com.davyuu.leagueitemsmobafire;

import java.util.List;
import java.util.Map;

/**
 * Created by David Yu on 2016-07-02.
 */
public class LeagueItemList {
    private List<String> itemNameList;
    private final Map<String, String> itemTotalPriceList;
    private final Map<String, Integer> itemImageList;

    LeagueItemList(List<String> itemNameList, Map<String, Integer> itemImageList,
                   Map<String, String> itemTotalPriceList){
        this.itemNameList = itemNameList;
        this.itemTotalPriceList = itemTotalPriceList;
        this.itemImageList = itemImageList;
    }
}
