package core;



import commands.cmdChannelHide;
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
    public static void main (String[] args) {
        // JDABuilder builder = new JDABuilder(AccountType.BOT);
        if (!SECRETS.loadTokens())  return;
        builder.setToken(SECRETS.getTOKEN());
        builder.setAutoReconnect(true);
        builder.setStatus(OnlineStatus.ONLINE);
        String Version =  STATIC.VERSION ;

        
        builder.setActivity(Activity.playing(Version));
        System.out.println("Starte auf "+Version+" ...");
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
    public  static  void addListeners() {

        builder.addEventListeners(new commandListener());
        builder.addEventListeners(new readyListener());
        //builder.addEventListeners(new serverStatsListener());


    }
    public  static  void  addCommands() {
        commandHandler.commands.put("chnl", new cmdChannelHide());
        commandHandler.commands.put("help", new cmdHelp());
        commandHandler.commands.put("shutdown", new cmdShutdown());


    }
    public static void StartThreads(JDA jda) {


    }
    /*public static void readInStartValues() {
List<String> Startwerte = new ArrayList<>();
try {
    Startwerte =Read("Values.txt");
} catch (Exception e) {
    System.out.println("Keine Startwerte konnten eingelesen werden: ");
    e.printStackTrace();
    return;
}

try {
    TimerThread.neuID = Integer.parseInt(Startwerte.get(1));
} catch (Exception e) {
    System.out.println("TimerThread.neuID konnte nicht gefunden werden!");
    e.printStackTrace();

}

        try {
    activityListener.day = Startwerte.get(3);
        } catch (Exception e) {
            System.out.println("activityListener.day konnte nicht gefunden werden!");
            e.printStackTrace();
        }
        try {
            cmdGetPoints.nextDate = Startwerte.get(5);
        } catch (Exception e) {
            System.out.println("cmdgetPoints.nextDate konnte nicht gefunden werden!");
            e.printStackTrace();
        }
        try {
            HiskiGiveawayThread.lastDay = Startwerte.get(7);
        } catch (Exception e) {
            System.out.println("HiskiGiveawayThread.lastDay konnte nicht gefunden werden!");
            e.printStackTrace();
        }

System.out.println("Wenn bis hier keine Fehlermeldung, Startwerte erfolgreich eingelesen");
    }*/
}