package util;


import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;



public class STATIC {
    public static String VERSION = "4.6";
    public static String PREFIX = "x!";
    public static String ADDLINK = "https://discordapp.com/oauth2/authorize?client_id=701952667131445299&scope=bot&permissions=269610064";
    public static String CODELINK = "https://github.com/Logiisch/XanderBot";
    public static String OWNERID = "318457868917407745";
    public static String XANDERID = "402094664016199680";
    public static String SELFID = "701952667131445299";

    public static boolean LOOPBOLEAN = true;

    public static Map<String, ArrayList<String>> votechannels = new HashMap<>();

    public static void load(JDA jda) {
        for (Guild g:jda.getGuilds()) {
            File f = new File("./data/"+g.getId()+"/");
            if (!f.exists()) continue;
            loadSingle(g,f);
        }
    }
    private static void loadSingle(Guild g,File parent) {
        File f = new File(parent.getAbsolutePath() + "votechannels.txt");
        if (!f.exists()) return;
        ArrayList<String> in;
        try {
            in = readInTxtFile.Read(f.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        votechannels.put(g.getId(), in);
    }

    public static void save(Guild g) {
        if (!votechannels.containsKey(g.getId())) return;
        try {
            File f = new File("data/"+g.getId());
            if (!f.exists()) //noinspection ResultOfMethodCallIgnored
                f.mkdirs();
            printOutTxtFile.Write("data/"+g.getId()+"/votechannels.txt",votechannels.get(g.getId()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void save(JDA jda) {
        for (Guild g: jda.getGuilds()) save(g);
    }
}

