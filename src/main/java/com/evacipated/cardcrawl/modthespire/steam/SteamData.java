package com.evacipated.cardcrawl.modthespire.steam;

import java.util.ArrayList;
import java.util.List;

class SteamData
{
    boolean steamDeck = false;

    List<SteamSearch.WorkshopInfo> workshopInfos = new ArrayList<>();

    int count()
    {
        return workshopInfos.size();
    }
}
