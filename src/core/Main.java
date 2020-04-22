package core;



import commands.cmdChannelHide;
import commands.cmdCode;
import commands.cmdHelp;
import commands.cmdShutdown;
import listeners.commandListener;
import listeners.readyListener;
import listeners.serverStatsListener;
import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import util.SECRETS;
import util.STATIC;

import javax.security.auth.login.LoginException;
import java.util.ArrayList;
import java.util.List;

import static util.readInTxtFile.Read;

public class Main {
    public static JDABuilder builder = new JDABuilder(AccountType.BOT);

    public static void main(String[] args) {
        // JDABuilder builder = new JDABuilder(AccountType.BOT);
        if (!SECRETS.loadTokens()) return;
        builder.setToken(SECRETS.getTOKEN());
        builder.setAutoReconnect(true);
        builder.setStatus(OnlineStatus.ONLINE);
        String Version = STATIC.VERSION;


        builder.setActivity(Activity.playing(Version));
        System.out.println("Starte auf " + Version + " ...|Written by Logii");
        addListeners();
        addCommands();
        //readInStartValues();

        try {
            JDA jda = builder.build();
            StartThreads(jda);
        } catch (LoginException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void addListeners() {

        builder.addEventListeners(new commandListener());
        builder.addEventListeners(new readyListener());
        //builder.addEventListeners(new serverStatsListener());


    }

    public static void addCommands() {
        commandHandler.commands.put("chnl", new cmdChannelHide());
        commandHandler.commands.put("help", new cmdHelp());
        commandHandler.commands.put("shutdown", new cmdShutdown());
        commandHandler.commands.put("code", new cmdCode());


    }

    public static void StartThreads(JDA jda) {


    }
}
