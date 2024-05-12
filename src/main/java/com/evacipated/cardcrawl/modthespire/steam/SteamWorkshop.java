package com.evacipated.cardcrawl.modthespire.steam;

import com.codedisaster.steamworks.*;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Scanner;

public class SteamWorkshop
{
    private static final int appId = 646570;

    private static SteamUGC workshop;
    private static SteamData data;

    public static void main(String[] args)
    {
        try {
            try {
                SteamAPI.loadLibraries();
            } catch (NoSuchMethodError ignored) {
                // Running an older version of the game, before steamworks4j 1.9.0
            }
            if (!SteamAPI.init()) {
                System.err.println("Could not connect to Steam. Is it running?");
                System.exit(1);
            }
        } catch (SteamException e) {
            System.err.println(e.getMessage());
            System.exit(2);
        }

        data = new SteamData();

        if (SteamAPI.isSteamRunning(true)) {
            Runtime.getRuntime().addShutdownHook(new Thread(SteamAPI::shutdown));

            SteamFriends friends = new SteamFriends(new FriendsCallback());
            friends.setRichPresence("status", "ModTheSpire");
            friends.setRichPresence("steam_display", "#Status");
            friends.dispose();

            try {
                SteamUtils utils = new SteamUtils(() -> {});
                data.steamDeck = utils.isSteamRunningOnSteamDeck();
            } catch (NoSuchMethodError | IllegalAccessError ignored) {}

            workshop = new SteamUGC(new UGCCallback());
            int items = workshop.getNumSubscribedItems();

            SteamPublishedFileID[] publishedFileIDS = new SteamPublishedFileID[items];
            items = workshop.getSubscribedItems(publishedFileIDS);

            System.err.println("subbed items: " + items);

            SteamUGCQuery query = workshop.createQueryUGCDetailsRequest(Arrays.asList(publishedFileIDS));
            workshop.sendQueryUGCRequest(query);

            Scanner scanner = new Scanner(System.in).useDelimiter("\0");

            loop:
            while (SteamAPI.isSteamRunning()) {
                try {
                    Thread.sleep(66L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                SteamAPI.runCallbacks();

                try {
                    if (System.in.available() > 0) {
                        String command = scanner.next();
                        switch (command) {
                            case "quit":
                                break loop;
                        }
                    }
                } catch (IOException ignored) {}
            }
        }

        SteamAPI.shutdown();
    }

    private static class UGCCallback implements SteamUGCCallback {

        int resultsReceived = 0;

        @Override
        public void onUGCQueryCompleted(SteamUGCQuery query, int numResultsReturned, int totalMatchingResults, boolean isCachedData, SteamResult result)
        {
            if (query.isValid()) {
                System.err.println("result: " + result);
                System.err.println("numResultsReturned: " + numResultsReturned);
                System.err.println("totalMatchingResults: " + totalMatchingResults);
                System.err.println("isCachedData: " + isCachedData);
                for (int i = 0; i < numResultsReturned; ++i) {
                    SteamUGCDetails details = new SteamUGCDetails();
                    if (workshop.getQueryUGCResult(query, i, details)) {
                        Collection<SteamUGC.ItemState> state = workshop.getItemState(details.getPublishedFileID());
                        if (state.contains(SteamUGC.ItemState.Installed)) {
                            SteamUGC.ItemInstallInfo info = new SteamUGC.ItemInstallInfo();
                            if (workshop.getItemInstallInfo(details.getPublishedFileID(), info)) {
                                SteamSearch.WorkshopInfo workshopInfo = new SteamSearch.WorkshopInfo(
                                    details.getTitle(),
                                    details.getPublishedFileID().toString(),
                                    info.getFolder(),
                                    details.getTimeUpdated(),
                                    details.getTags()
                                );
                                data.workshopInfos.add(workshopInfo);
                            }
                        }
                    } else {
                        System.err.println("query valid? " + query.isValid());
                        System.err.println("index: " + i);
                        System.err.println("Query result failed");
                    }
                }
            } else {
                System.err.println("Not a valid query?");
            }

            resultsReceived += numResultsReturned;
            if (resultsReceived >= totalMatchingResults) {
                Gson gson = new Gson();
                String json = gson.toJson(data);
                System.out.println(json);
                System.out.println('\0');
            }
            workshop.releaseQueryUserUGCRequest(query);
        }

        @Override
        public void onSubscribeItem(SteamPublishedFileID publishedFileID, SteamResult result) {

        }

        @Override
        public void onUnsubscribeItem(SteamPublishedFileID publishedFileID, SteamResult result) {

        }

        @Override
        public void onRequestUGCDetails(SteamUGCDetails details, SteamResult result) {

        }

        @Override
        public void onCreateItem(SteamPublishedFileID publishedFileID, boolean needsToAcceptWLA, SteamResult result) {

        }

        @Override
        public void onSubmitItemUpdate(SteamPublishedFileID publishedFileID, boolean needsToAcceptWLA, SteamResult result)
        {

        }

        @Override
        public void onDownloadItemResult(int appID, SteamPublishedFileID publishedFileID, SteamResult result) {

        }

        @Override
        public void onUserFavoriteItemsListChanged(SteamPublishedFileID publishedFileID, boolean wasAddRequest, SteamResult result) {

        }

        @Override
        public void onSetUserItemVote(SteamPublishedFileID publishedFileID, boolean voteUp, SteamResult result) {

        }

        @Override
        public void onGetUserItemVote(SteamPublishedFileID publishedFileID, boolean votedUp, boolean votedDown, boolean voteSkipped, SteamResult result) {

        }

        @Override
        public void onStartPlaytimeTracking(SteamResult result) {

        }

        @Override
        public void onStopPlaytimeTracking(SteamResult result) {

        }

        @Override
        public void onStopPlaytimeTrackingForAllItems(SteamResult result) {

        }

        @Override
        public void onDeleteItem(SteamPublishedFileID publishedFileID, SteamResult result)
        {

        }
    }

    private static class FriendsCallback implements SteamFriendsCallback {
        @Override
        public void onSetPersonaNameResponse(boolean success, boolean localSuccess, SteamResult result)
        {

        }

        @Override
        public void onPersonaStateChange(SteamID steamID, SteamFriends.PersonaChange change)
        {

        }

        @Override
        public void onGameOverlayActivated(boolean active)
        {

        }

        @Override
        public void onGameLobbyJoinRequested(SteamID steamIDLobby, SteamID steamIDFriend)
        {

        }

        @Override
        public void onAvatarImageLoaded(SteamID steamID, int image, int width, int height)
        {

        }

        @Override
        public void onFriendRichPresenceUpdate(SteamID steamIDFriend, int appID)
        {

        }

        @Override
        public void onGameRichPresenceJoinRequested(SteamID steamIDFriend, String connect)
        {

        }

        @Override
        public void onGameServerChangeRequested(String server, String password)
        {

        }
    }
}
