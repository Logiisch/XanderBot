package helperCore;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.Member;

import java.util.ArrayList;
import java.util.List;

public class ServerStats {

    private static final String CATEGORY_NAME = "ServerStats";
    private static final String ALL_MEMBER_COUNT = "Anzahl-Mitglieder: ";
    private static final String HUMAN_COUNT = "Anzahl-Spieler: ";
    private static final String BOT_COUNT = "Anzahl-Bots: ";
    private static final String ROLE_COUNT = "Rollen: ";
    private static final String CHANNEL_COUNT = "Channels: ";

    public static void reload(Guild g) {
        Category c = getCat(g);
        boolean[] found = new boolean[5];
        for (int i=0;i<found.length;i++) {
            found[i] = false;
        }
        int bot =0;
        int human =0;
        for (Member m:g.getMembers()) {
            if (m.getUser().isBot()) bot++; else human++;
        }
        ArrayList<Permission> allow = new ArrayList<>();
        allow.add(Permission.VIEW_CHANNEL);
        ArrayList<Permission> deny = new ArrayList<>();
        deny.add(Permission.VOICE_CONNECT);
        ArrayList<Permission> allowbot = new ArrayList<>(allow);
        allowbot.add(Permission.VOICE_CONNECT);
        for (GuildChannel gc :c.getChannels()) {
            if (gc.getName().startsWith(ALL_MEMBER_COUNT)) {
                String newName = ALL_MEMBER_COUNT+(g.getMemberCount()-1);
                if (!gc.getName().equals(newName)) {
                    gc.delete().queue();
                    c.createVoiceChannel(newName).addPermissionOverride(g.getPublicRole(),allow,deny).addPermissionOverride(g.getSelfMember(),allowbot,deny).queue();
                }
                found[0]=true;
            }
            if (gc.getName().startsWith(HUMAN_COUNT)) {
                String newName = HUMAN_COUNT+human;
                if (!gc.getName().equals(newName)) {
                    gc.delete().queue();
                    c.createVoiceChannel(newName).addPermissionOverride(g.getPublicRole(),allow,deny).addPermissionOverride(g.getSelfMember(),allowbot,deny).queue();
                }
                found[1]=true;
            }
            if (gc.getName().startsWith(BOT_COUNT)) {
                String newName = BOT_COUNT+(bot-1);
                if (!gc.getName().equals(newName)) {
                    gc.delete().queue();
                    c.createVoiceChannel(newName).addPermissionOverride(g.getPublicRole(),allow,deny).addPermissionOverride(g.getSelfMember(),allowbot,deny).queue();
                }
                found[2]=true;
            }
            if (gc.getName().startsWith(ROLE_COUNT)) {
                String newName = ROLE_COUNT+g.getRoles().size();
                if (!gc.getName().equals(newName)) {
                    gc.delete().queue();
                    c.createVoiceChannel(newName).addPermissionOverride(g.getPublicRole(),allow,deny).addPermissionOverride(g.getSelfMember(),allowbot,deny).queue();
                }
                found[3]=true;
            }
            if (gc.getName().startsWith(CHANNEL_COUNT)) {
                String newName = CHANNEL_COUNT+(g.getChannels().size()-5);
                if (!gc.getName().equals(newName)) {
                    gc.delete().queue();
                    c.createVoiceChannel(newName).addPermissionOverride(g.getPublicRole(),allow,deny).addPermissionOverride(g.getSelfMember(),allowbot,deny).queue();
                }
                found[4]=true;
            }
        }

        if (!found[0]) {
            c.createVoiceChannel(ALL_MEMBER_COUNT+(g.getMemberCount()-1)).addPermissionOverride(g.getPublicRole(),allow,deny).addPermissionOverride(g.getSelfMember(),allowbot,deny).complete();
        }
        if (!found[1]) {
            c.createVoiceChannel(HUMAN_COUNT+human).addPermissionOverride(g.getPublicRole(),allow,deny).addPermissionOverride(g.getSelfMember(),allowbot,deny).complete();
        }
        if (!found[2]) {
            c.createVoiceChannel(BOT_COUNT+(bot-1)).addPermissionOverride(g.getPublicRole(),allow,deny).addPermissionOverride(g.getSelfMember(),allowbot,deny).complete();
        }
        if (!found[3]) {
            c.createVoiceChannel(ROLE_COUNT+g.getRoles().size()).addPermissionOverride(g.getPublicRole(),allow,deny).addPermissionOverride(g.getSelfMember(),allowbot,deny).complete();
        }
        if (!found[4]) {
            c.createVoiceChannel(CHANNEL_COUNT+(g.getChannels().size()-5)).addPermissionOverride(g.getPublicRole(),allow,deny).complete();
        }


    }
    private static Category getCat(Guild g) {
        List<Category> cats =g.getCategories();
        for (Category c:cats) {
            if (c.getName().equalsIgnoreCase(CATEGORY_NAME)) {
                return c;
            }
        }
        return g.createCategory(CATEGORY_NAME).complete();
    }
    public static void reload(JDA jda) {
        for (Guild g:jda.getGuilds()) {
            reload(g);
        }
    }
}
