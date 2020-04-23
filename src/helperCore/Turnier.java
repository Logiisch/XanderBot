package helperCore;


import listeners.turnierReactListener;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.User;
import util.printOutTxtFile;
import util.readInTxtFile;

import java.io.IOException;
import java.util.*;

public class Turnier {
    public static HashMap<Integer, Turnier> list = new HashMap<>();


    private User autor;
    private int spielerzahl;
    private String spielname;
    private String einstellungen;
    private Message msg;
    private boolean notification;
    private ArrayList<User> registerd = new ArrayList<>();
    private RegStat regStat;

    public Turnier(User u) {
        autor=u;
        regStat = RegStat.NEEDPLAYERCOUNT;
    }
    private Turnier(User autorn,int spielerzahln, String spielnamen, String einstellungenn, Message messagen, boolean notificationn, ArrayList<User> registerdn,RegStat regStatn) {
        autor = autorn;
        spielerzahl = spielerzahln;
        spielname = spielnamen;
        einstellungen = einstellungenn;
        msg = messagen;
        notification = notificationn;
        registerd = registerdn;
        regStat = regStatn;
    }
    public User getAutor() {
        return autor;
    }

    public void setSpielerzahl(int sz) {
        spielerzahl = sz;
    }

    public int getSpielerzahl() {
        return spielerzahl;
    }

    public void setSpielname(String sn) {
        spielname = sn;
    }
    public String getSpielname() {
        return spielname;
    }
    public void setEinstellungen(String es) {
           einstellungen = es;
    }
    public String getEinstellungen() {
        return einstellungen;
    }
    public RegStat getRegStat() {
        return regStat;
    }

    public void setRegStat(RegStat rs) {
        regStat = rs;
    }
    public void setNotification(boolean n) {
        notification= n;
    }
    public void setMsg(Message ms) {
        msg = ms;
    }

    public MessageEmbed getEmbed() {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle(autor.getName()+"'s Turnier");
        eb.addField("Es wird gespielt",spielname,false);
        int restpl = spielerzahl-registerd.size();
        eb.addField("Freie Plätze",restpl+"",false);
        if (!einstellungen.equalsIgnoreCase("")) eb.appendDescription("Zusätzliche Hinweise:").appendDescription(einstellungen);
        eb.setFooter("Reagiere mit :thumbsup:,  um dich für das Turnier anzumelden!");
        return eb.build();
    }

    public static int getNextNumber() {
        int i=1;
        while (list.containsKey(i)) i++;
        return i;
    }

    public enum RegStat {
        NEEDPLAYERCOUNT,
        NEEDGAMENAME,
        NEEDDESCRIPTION,
        NEEDNOTIFICATION,
        DONE,
        STARTED
    }
    public static void onCreationFinished(int id) {
        turnierReactListener.toSupervise.put(list.get(id).msg.getId(),id);
        list.get(id).msg.addReaction("U+1F44D").queue();
        save();
    }

    public static void refreshMembers(int turnum,boolean messageonfull) {
        Message msg = list.get(turnum).msg;
        List<MessageReaction> mrs =msg.getReactions();
        for (MessageReaction mr:mrs) {
            if (!mr.getReactionEmote().getName().equalsIgnoreCase("\uD83D\uDC4D"))continue;
            List<User> usrs =mr.retrieveUsers().complete();
            compUsers(turnum,usrs,messageonfull);
        }
        save();
    }
    private static void compUsers(int turnum,List<User> usrs,boolean mesaageonfull) {
        Turnier tn = list.get(turnum);
        User slf = usrs.get(0).getJDA().getSelfUser();
        usrs.remove(slf);

        ArrayList<User> newadded = new ArrayList<>(usrs);
        newadded.removeAll(tn.registerd);
        ArrayList<User> removed = new ArrayList<>(tn.registerd);
        removed.removeAll(usrs);

        if (!newadded.isEmpty()) {
            if (tn.registerd.size()>=tn.spielerzahl) {
                for (User u:newadded) {
                    u.openPrivateChannel().complete().sendMessage("Leider ist das Turnier schon voll!").queue();
                }
            }
            else {
                if (newadded.size() == 1) {
                    if (tn.notification) {
                        tn.autor.openPrivateChannel().complete().sendMessage("Der User " + newadded.get(0).getName() + " ist deinem Tunier mit der `ID " + turnum + "` beigetreten.").queue();
                    }
                } else {
                    if (tn.notification) {
                        String usrss = "";
                        for (User u : newadded) {
                            usrss += "," + u.getName();
                        }
                        tn.autor.openPrivateChannel().complete().sendMessage("Deinem Turnier mit der `ID " + turnum + "` sind folgende User beigetreten: " + usrss.replaceFirst(",", "")).queue();
                    }
                }
                tn.registerd.addAll(newadded);

            }
        }

        if (!removed.isEmpty()) {
            if (removed.size()==1) {
                if (tn.notification) {
                    tn.autor.openPrivateChannel().complete().sendMessage("Der User "+removed.get(0).getName()+" hat dein Tunier mit der `ID "+turnum+"` verlassen.").queue();
                }
            } else {
                if (tn.notification) {
                    StringBuilder usrss = new StringBuilder();
                    for (User u: removed) {
                        usrss.append(",").append(u.getName());
                    }
                    tn.autor.openPrivateChannel().complete().sendMessage("Dein Turnier mit der `ID "+turnum+"` haben folgende User verlassen: "+ usrss.toString().replaceFirst(",","")).queue();
                }
            }
            tn.registerd.removeAll(removed);
        }

        if (tn.notification) {
            tn.autor.openPrivateChannel().complete().sendMessage("Insgesamt sind damit nun "+tn.registerd.size()+" Spieler deinem Turnier beigetreten!").queue();

        }
        if (tn.registerd.size()==tn.spielerzahl&&mesaageonfull) {
            if (tn.notification) tn.autor.openPrivateChannel().complete().sendMessage("Dein Turnier ist damit voll. Starte es mit `x!turnier start "+turnum+"`!").queue();
            else tn.autor.openPrivateChannel().complete().sendMessage("Dein Turnier mit der `ID "+turnum+"` ist soeben voll geworden. Starte es mit `x!turnier start "+turnum+"`!").queue();
        }

        tn.msg.editMessage(tn.getEmbed()).queue();
        list.put(turnum,tn);


    }
    //Saving/loading it to permanent memory
    //Syntax: Seperator:§
    //id,autorid,spielerzahl,spielname,einstellungen,messageid,textchannelid,notification,regusers,restat
    //id: just numeric value
    //autorid: id of the autor, numeric value
    //spielerzahl: numeric value
    //spielname: as text
    //EInstellungen: as text
    //messageid, as numeric value
    //textchannelid: as numeric value
    //notification: booleans written out
    //regusersids: seperated by ,
    //restat: All caps


    private static void save() {
        Set<Integer> numbrs =list.keySet();
        ArrayList<String> out = new ArrayList<>();
        for (int nbr: numbrs) {
            String res = saveSingle(nbr, list.get(nbr));
            out.add(res);
        }
        try {
            printOutTxtFile.Write("data/turniere.txt",out);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    private static String saveSingle(int nummer, Turnier tn) {
        String out = nummer+"§";//0
        out += tn.autor.getId()+"§";//1
        out += tn.spielerzahl+"§";//2
        out += tn.spielname+"§";//3
        out += tn.einstellungen+"§";//4
        out += tn.msg.getId()+"§";//5
        out += tn.msg.getTextChannel().getId()+"§";//6
        out += tn.notification+"§";//7

        String part = "";
        for (User u:tn.registerd) {
            part += ","+u.getId();
        }
        part = part.replaceFirst(",", "");
        out += part+"§";//8
        out += tn.regStat.toString().toUpperCase();//9
        return out;

    }
    private static int load(String whole,JDA jda) throws Exception {
        String[] split = whole.split("§");
        int nbr = Integer.parseInt(split[0]);
        User autor = jda.getUserById(split[1]);
        int spielerzahl = Integer.parseInt(split[2]);
        String Spielname = split[3];
        String Einstellungen = split[4];
        Message msg = Objects.requireNonNull(jda.getTextChannelById(split[6])).getHistoryAround(split[5],5).complete().getMessageById(split[5]);
        boolean notify = false;
        if (split[7].equalsIgnoreCase("true")) notify = true;
        ArrayList<User> registerd = new ArrayList<>();
         if (split[8].length()>5) {
             String[] usersplit = split[8].split(",");
             for (String s : usersplit) {
                 registerd.add(jda.getUserById(s));
             }
         }
        RegStat rs = RegStat.valueOf(split[9]);
        Turnier tn = new Turnier(autor,spielerzahl,Spielname,Einstellungen,msg,notify,registerd,rs);
        list.put(nbr,tn);
        return nbr;

    }
    public static void load(JDA jda) {
        ArrayList<String> in = new ArrayList<>();
        try {
            in =readInTxtFile.Read("data/turniere.txt");
        } catch (IOException e) {
            System.err.println("turniere.txt konnte nicht geladen werden!");
            return;
        }
        for (String s:in) {
             if(s.length()<3) continue;
            int nbr = 0;
            try {
                nbr = load(s,jda);
                refreshMembers(nbr,false);
                turnierReactListener.toSupervise.put(list.get(nbr).msg.getId(),nbr);
                System.out.println("Loaded turnier nbr. "+nbr);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

}
