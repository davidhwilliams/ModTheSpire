package com.evacipated.cardcrawl.modthespire.steam;

import com.evacipated.cardcrawl.modthespire.ModTheSpire;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class SteamWorkshopRunner
{
    private static Process p;

    private static void startSteamAPI() throws IOException
    {
        if (p != null) return;

        String path = SteamWorkshop.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        path = URLDecoder.decode(path,  "utf-8");
        path = new File(path).getPath();
        ProcessBuilder pb = new ProcessBuilder(
            SteamSearch.findJRE(),
            "-cp", path + File.pathSeparatorChar + ModTheSpire.STS_JAR,
            "com.evacipated.cardcrawl.modthespire.steam.SteamWorkshop"
        ).redirectError(ProcessBuilder.Redirect.INHERIT);
        p = pb.start();
        Runtime.getRuntime().addShutdownHook(new Thread(p::destroy));
    }

    public static List<SteamSearch.WorkshopInfo> findWorkshopInfos()
    {
        List<SteamSearch.WorkshopInfo> workshopInfos = new ArrayList<>();
        try {
            System.out.println("Searching for Workshop items...");
            startSteamAPI();
            Scanner scanner = new Scanner(p.getInputStream()).useDelimiter("\0");
            Gson gson = new GsonBuilder().setPrettyPrinting().create();

            String result = scanner.next();
            SteamData steamData = gson.fromJson(result, SteamData.class);
            System.out.println(gson.toJson(steamData));

            ModTheSpire.LWJGL3_ENABLED = ModTheSpire.LWJGL3_ENABLED || steamData.steamDeck;
            for (SteamSearch.WorkshopInfo info : steamData.workshopInfos) {
                if (!info.hasTag("tool") && !info.hasTag("tools")) {
                    workshopInfos.add(info);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return workshopInfos;
    }

    public static void stop()
    {
        if (p == null) {
            throw new IllegalStateException();
        }

        if (p.isAlive()) {
            try {
                OutputStreamWriter writer = new OutputStreamWriter(p.getOutputStream());
                writer.write("quit\0");
                writer.flush();
                if (!p.waitFor(100, TimeUnit.MILLISECONDS)) {
                    p.destroy();
                }
            } catch (IOException | InterruptedException ignored) {
                p.destroy();
            }
        }
        p = null;
    }
}
