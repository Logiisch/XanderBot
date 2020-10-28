package core;


import commands.*;
import listeners.abstimmungsListener;
import listeners.commandListener;
import listeners.readyListener;
import listeners.turnierReactListener;
import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import util.SECRETS;
import util.STATIC;

import java.util.ArrayList;
import java.util.Arrays;

public class Main {
    public static JDABuilder builder = JDABuilder.create(new ArrayList<>());

    public static void main(String[] args) {
        // JDABuilder builder = new JDABuilder(AccountType.BOT);
        if (!SECRETS.loadTokens()) return;
        ArrayList<GatewayIntent> gis = new ArrayList<>(Arrays.asList(GatewayIntent.values()));
        builder = JDABuilder.create(gis);
        builder.setToken(SECRETS.getTOKEN());
        builder.setAutoReconnect(true);
        builder.setStatus(OnlineStatus.ONLINE);
        String Version = STATIC.VERSION;


        builder.setActivity(Activity.playing(Version + "|Written by Logii"));
        System.out.println("Starte auf " + Version + " ...");
        addListeners();
        addCommands();
        //readInStartValues();

        try {
            JDA jda = builder.build();
            STATIC.load(jda);
            StartThreads(jda);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void addListeners() {

        builder.addEventListeners(new commandListener());
        builder.addEventListeners(new readyListener());
        //builder.addEventListeners(new serverStatsListener());
        builder.addEventListeners(new turnierReactListener());
        builder.addEventListeners(new abstimmungsListener());


    }

    public static void addCommands() {
        commandHandler.commands.put("chnl", new cmdChannelHide());
        commandHandler.commands.put("help", new cmdHelp());
        commandHandler.commands.put("shutdown", new cmdShutdown());
        commandHandler.commands.put("code", new cmdCode());
        commandHandler.commands.put("turnier", new cmdTurnier());
        commandHandler.commands.put("commands", new cmdCommands());
        commandHandler.commands.put("votechannel",new cmdVotechannel());


    }

    public static void StartThreads(JDA jda) {


    }
}
