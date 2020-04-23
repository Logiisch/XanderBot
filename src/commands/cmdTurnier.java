package commands;

import helperCore.Turnier;
import listeners.commandListener;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import util.STATIC;

import java.awt.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class cmdTurnier implements Command {
    private static final int[] PLAYERCOUNTALLOWED = {4,8,16,32,64};


    private static HashMap<User, Integer> creating = new HashMap<>();
    private static HashMap<User, Integer> setting = new HashMap<>();
    @Override
    public boolean called(String[] args, MessageReceivedEvent event) {
        return false;
    }

    @Override
    public void action(String[] args, MessageReceivedEvent event) throws IOException {
        if (!event.getAuthor().getId().equalsIgnoreCase(STATIC.OWNERID)) {
            event.getTextChannel().sendMessage("Aktuell ist dieser Befehl nur zu Entwicklungszwecken verfügbar. Aber bald ist er fertig und einsatztbereit!").queue();
            return;
        }


        if (creating.containsKey(event.getAuthor())) {
            create(event, args);
            return;
        }
        if (setting.containsKey(event.getAuthor())) {
            set(event, args);
            return;
        }
        if (args.length<1) {

            EmbedBuilder eb = new EmbedBuilder().setColor(Color.GREEN).setTitle("Subcommands zum Befehl \"turnier\"");
            eb.addField("create","Erstelle ein neues Turnier",false);
            eb.addField("set","Bearbeite die Einstellungen eines Turniers",false);

            event.getTextChannel().sendMessage(eb.build()).queue();
            return;
        }
        switch (args[0].toLowerCase()) {
            case "create":
                create(event, args);
                break;
            case "set":
                set(event, args);
                break;
                default:
                    EmbedBuilder eb = new EmbedBuilder().setColor(Color.GREEN).setTitle("Subcommands zum Befehl \"turnier\"");
                    eb.addField("create","Erstelle ein neues Turnier",false);
                    eb.addField("set","Bearbeite die Einstellungen eines Turniers",false);

                    event.getTextChannel().sendMessage(eb.build()).queue();
        }
    }

    @Override
    public void executed(boolean success, MessageReceivedEvent event) {

    }

    @Override
    public String help() {
        return null;
    }

    @Override
    public boolean isPrivate() {
        return false;
    }

    @Override
    public String Def() {
        return "Erstelle ein Turnier!";
    }


    private void create(MessageReceivedEvent event, String[] args) {
        String prefix = commandListener.getPrefix(event.getGuild());
        if (!creating.containsKey(event.getAuthor())) {
            int nbr = Turnier.getNextNumber();
            Turnier tnr = new Turnier(event.getAuthor());
            Turnier.list.put(nbr,tnr);
            creating.put(event.getAuthor(),nbr);
            event.getTextChannel().sendMessage("Ein neues Turnier wurde erstellt. Wie viele Spieler sollen mitspielen können? Antworte mit  `"+prefix+"turnier [Anzahl]`!").queue();
            return;
        }
        int id = creating.get(event.getAuthor());
        if (!Turnier.list.containsKey(id)) {
            event.getTextChannel().sendMessage("Anscheinend ist bei der Erstellung etwas schief gelaufen. Bitte fange noch einmal von vorne an!").queue();
            creating.remove(event.getAuthor());
            return;
        }
        if (args.length>0) {
            if (args[0].equalsIgnoreCase("exit")) {
                creating.remove(event.getAuthor());
                Turnier.list.remove(id);
                event.getTextChannel().sendMessage("Erstellug abgebrochen!").queue();
                return;
            }
        }
        Turnier tn = Turnier.list.get(id);
        switch (tn.getRegStat()) {
            case NEEDPLAYERCOUNT:
                needplayercount(event,args,id,tn,prefix);
                break;
            case NEEDGAMENAME:
                needgamename(event,args,id,tn,prefix);
                break;
            case NEEDDESCRIPTION:
                needdescription(event,args,id,tn,prefix);
                break;
            case NEEDNOTIFICATION:
                neednotification(event,args,id,tn,prefix);
                break;
            case DONE:
                registrationdone(event,args,id,tn,prefix);
                break;
                default:
                    event.getTextChannel().sendMessage("Anscheinend ist etwas schief gelaufen. Bitte starte erneut!").queue();
                    Turnier.list.remove(id);
                    creating.remove(event.getAuthor());
        }
    }

    private void set (MessageReceivedEvent event, String[] args) {

    }

    private void needplayercount(MessageReceivedEvent event, String[] args, int tid, Turnier tn,String prefix) {
        if (args.length<1) {
            event.getTextChannel().sendMessage("Wie viele Spieler sollen mitspielen können? Antworte mit  `"+prefix+"turnier [Anzahl]`!\nNutze jederzeit `"+prefix+"turnier exit`, um abzubrechen!").queue();
            return;
        }
        int anzahl = 0;
        try {
            anzahl = Integer.parseInt(args[0]);
        } catch (Exception e) {
            event.getTextChannel().sendMessage("Wie viele Spieler sollen mitspielen können? Antworte mit  `"+prefix+"turnier [Anzahl]`!\nNutze jederzeit `"+prefix+"turnier exit`, um abzubrechen!").queue();
            return;
        }
        boolean allowed = false;
        for (int i:PLAYERCOUNTALLOWED) {
            if (i==anzahl) {
                allowed=true;
            }
        }
        if (!allowed) {
            StringBuilder all= new StringBuilder();
            for (int i:PLAYERCOUNTALLOWED) all.append(",").append(i);
            all = new StringBuilder(all.toString().replaceFirst(",", ""));
            event.getTextChannel().sendMessage("Erlaubte Spielerzahlen sind:\n"+all.toString()).queue();
            return;
        }
        tn.setSpielerzahl(anzahl);
        tn.setRegStat(Turnier.RegStat.NEEDGAMENAME);
        Turnier.list.put(tid,tn);
        event.getTextChannel().sendMessage("Welches Spiel wird gespielt? Antworte mit `"+prefix+"turnier [Name]`!").queue();

    }

    private void needgamename(MessageReceivedEvent event, String[] args, int tid, Turnier tn,String prefix) {
        if (args.length<1) {
            event.getTextChannel().sendMessage("Welches Spiel wird gespielt? Antworte mit  `"+prefix+"turnier [Name]`!\nNutze jederzeit `"+prefix+"turnier exit`, um abzubrechen!").queue();
            return;
        }
        StringBuilder sb = new StringBuilder();
        for (String arg : args) {
            sb.append(" ").append(arg);
        }
        String in = sb.toString().replaceFirst(" ","");
        if (in.contains("§")) {
            event.getTextChannel().sendMessage("Aus technischen Gründen kannst du leider das §-Zeichen nicht verwenden!").queue();
            return;
        }
        tn.setSpielname(in);
        tn.setRegStat(Turnier.RegStat.NEEDDESCRIPTION);
        Turnier.list.put(tid,tn);
        event.getTextChannel().sendMessage("Mit welchen Eintellungen wird gespielt? Antworte mit `"+prefix+"turnier [Einstellungen]`! Wenn du keine zusätzlichen Einstellungen festlegen willst, antworte einfach mit `"+prefix+"turnier`!").queue();
    }

    private void needdescription(MessageReceivedEvent event, String[] args, int tid, Turnier tn,String prefix) {
        if (args.length==0) {
            tn.setEinstellungen("");
            tn.setRegStat(Turnier.RegStat.NEEDNOTIFICATION);
            Turnier.list.put(tid,tn);
            event.getTextChannel().sendMessage("Es wurden keine zusätzlichen Einstellungen hinzugefügt!\nMöchtest du über Spieler informiert werden, die dem Turnier beitreten oder es verlassen? Antworte mit `"+prefix+"turnier [y/n]`").queue();
            return;
        }
        StringBuilder sb = new StringBuilder();
        for (String arg:args) {
            sb.append(" ").append(arg);
        }
        String in = sb.toString().replaceFirst(" ","");
        if (in.contains("§")) {
            event.getTextChannel().sendMessage("Aus technischen Gründen kannst du leider das §-Zeichen nicht verwenden!").queue();
            return;
        }
        tn.setEinstellungen(in);
        tn.setRegStat(Turnier.RegStat.NEEDNOTIFICATION);
        Turnier.list.put(tid,tn);
        event.getTextChannel().sendMessage("Möchtest du über Spieler informiert werden, die dem Turnier beitreten oder es verlassen? Antworte mit `"+prefix+"turnier [y/n]`").queue();
    }
    private void neednotification(MessageReceivedEvent event, String[] args, int tid, Turnier tn,String prefix) {
        if (args.length<1) {
            event.getTextChannel().sendMessage("Möchtest du über Spieler informiert werden, die dem Turnier beitreten oder es verlassen? Antworte mit `"+prefix+"turnier [y/n]`!\nNutze jederzeit `"+prefix+"turnier exit`, um abzubrechen!").queue();
            return;
        }
        switch (args[0]) {
            case "y":
            case "yes":
            case "j":
            case "ja":
                tn.setNotification(true);
                tn.setRegStat(Turnier.RegStat.DONE);
                Turnier.list.put(tid,tn);
                event.getTextChannel().sendMessage("In welchem Textchannel soll das Turnier angekündigt werden? Antworte mit `"+prefix+"turnier [Channel als #Mention]`!").queue();
                break;
            case "n":
            case "no":
            case "nein":
                tn.setNotification(false);
                tn.setRegStat(Turnier.RegStat.DONE);
                Turnier.list.put(tid,tn);
                event.getTextChannel().sendMessage("In welchem Textchannel soll das Turnier angekündigt werden? Antworte mit `"+prefix+"turnier [Channel als #Mention]`!").queue();
                break;
                default:
                    event.getTextChannel().sendMessage("Möchtest du über Spieler informiert werden, die dem Turnier beitreten oder es verlassen? Antworte mit `"+prefix+"turnier [y/n]`!\nNutze jederzeit `"+prefix+"turnier exit`, um abzubrechen!").queue();
        }
    }
    private void registrationdone(MessageReceivedEvent event, String[] args, int tid, Turnier tn, String prefix) {
        List<TextChannel> tcs = event.getMessage().getMentionedChannels();
        if (tcs.size()>1){
            event.getTextChannel().sendMessage("Aus technischen Gründen kann das Turnier nur in einem Channel gelistet werden!").queue();
            return;
        }
        if (tcs.isEmpty()) {
            event.getTextChannel().sendMessage("In welchem Textchannel soll das Turnier angekündigt werden? Antworte mit `"+prefix+"turnier [Channel als #Mention]`!\nNutze jederzeit `"+prefix+"turnier exit`, um abzubrechen!").queue();
            return;
        }
        TextChannel tc = tcs.get(0);
        Message msg = tc.sendMessage(tn.getEmbed()).complete();
        tn.setMsg(msg);
        msg.pin().queue();
        Turnier.list.put(tid,tn);
        event.getTextChannel().sendMessage("Dein Turnier wurde erfolgreich erstellt. Schau in deinen PN's für mehr Infos!").queue();
        event.getAuthor().openPrivateChannel().complete().sendMessage("Dein Turnier wurde erstellt. Es hat die `ID "+tid+"`! Um die Einstellungen deines Turniers nachträglich zu ändern, nutze `"+prefix+"turnier set "+tid+"`!").queue();
        creating.remove(event.getAuthor());
        Turnier.onCreationFinished(tid);
    }
}
