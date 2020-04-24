package commands;

import helperCore.Turnier;
import listeners.commandListener;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import util.STATIC;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

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


       /*
        if (args.length>0) {
            if (args[0].equalsIgnoreCase("seelistenerlist")) {
                StringBuilder sb = new StringBuilder();
                for (Map.Entry<String, Integer> ent:turnierReactListener.toSupervise.entrySet()) {
                    sb.append(ent.getKey()).append(":").append(ent.getValue()).append("\n");
                }
                event.getTextChannel().sendMessage(sb.toString()).queue();
                return;
            }
        }
        */
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
            event.getTextChannel().sendMessage("Ein neues Turnier wurde erstellt. Welchen Typ soll das Turnier haben? Antworte mit  `" + prefix + "turnier [Typ]`!\nVerfügbare Typen:\n`KO`:KO-System\n`JGJ`Jeder-gegen-Jeden").queue();
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
            case NEEDMATCHTYPE:
                needmatchtype(event, args, id, tn, prefix);
                break;
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


    private void needmatchtype(MessageReceivedEvent event, String[] args, int tid, Turnier tn, String prefix) {
        if (args.length < 1) {
            event.getTextChannel().sendMessage("Welchen Typ soll das Turnier haben? Antworte mit  `" + prefix + "turnier [Typ]`!\nVerfügbare Typen:\n`KO`:KO-System\n`JGJ`Jeder-gegen-Jeden\n\nNutze jederzeit `" + prefix + "turnier exit`, um abzubrechen!").queue();
            return;
        }
        switch (args[0].toUpperCase()) {
            case "KO":
                tn.setMatchtype(Turnier.MatchType.KOSYSTEM);
                tn.setRegStat(Turnier.RegStat.NEEDPLAYERCOUNT);
                Turnier.list.put(tid, tn);
                event.getTextChannel().sendMessage("Wie viele Spieler sollen mitspielen können? Antworte mit  `" + prefix + "turnier [Anzahl]`!").queue();
                break;
            case "JGJ":
                tn.setMatchtype(Turnier.MatchType.GRUPPENSYSTEM);
                tn.setRegStat(Turnier.RegStat.NEEDPLAYERCOUNT);
                Turnier.list.put(tid, tn);
                event.getTextChannel().sendMessage("Wie viele Spieler sollen mitspielen können? Antworte mit  `" + prefix + "turnier [Anzahl]`!").queue();
                break;
            default:
                event.getTextChannel().sendMessage("Welchen Typ soll das Turnier haben? Antworte mit  `" + prefix + "turnier [Typ]`!\nVerfügbare Typen:\n`KO`:KO-System\n`JGJ`Jeder-gegen-Jeden\n\nNutze jederzeit `" + prefix + "turnier exit`, um abzubrechen!").queue();
                break;
        }

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
        int[] allowedplyrs = Turnier.userlimit(tn.getMatchtype());
        if (allowedplyrs.length > 0) {
            for (int i : allowedplyrs) {
                if (i == anzahl) {
                    allowed = true;
                }
            }
            if (!allowed) {
                StringBuilder all = new StringBuilder();
                for (int i : allowedplyrs) all.append(",").append(i);
                all = new StringBuilder(all.toString().replaceFirst(",", ""));
                event.getTextChannel().sendMessage("Erlaubte Spielerzahlen für diesen Turniertyp sind:\n" + all.toString()).queue();
                return;
            }
        }
        if (!Turnier.playerCountSinnvoll(anzahl)) {
            event.getTextChannel().sendMessage("Netter Versuch, aber so ein Turnier spielen wir nicht!").queue();
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

    private void set(MessageReceivedEvent event, String[] args) {
        if (!setting.containsKey(event.getAuthor())) args = shortenArgs(args);
        String prefix = commandListener.getPrefix(event.getGuild());
        if (args.length < 1) {
            if (!setting.containsKey(event.getAuthor())) {
                event.getTextChannel().sendMessage("Usage:`" + prefix + "turnier set [ID des Turniers]`").queue();
                return;
            }
        }
        if (!setting.containsKey(event.getAuthor())) {
            String nbrstr = args[0].replace("ID", "");
            int nbr = 0;
            try {
                nbr = Integer.parseInt(nbrstr);
            } catch (NumberFormatException e) {
                event.getTextChannel().sendMessage("Usage:`" + prefix + "turnier set [ID des Turniers ohne \"ID\"]`").queue();
                return;
            }
            if (!Turnier.list.containsKey(nbr)) {
                event.getTextChannel().sendMessage("Ein Turnier mit der `ID " + nbr + "` konnte nicht gefunden werden").queue();
                return;
            }
            Turnier tn = Turnier.list.get(nbr);
            if (!tn.getAutor().getId().equalsIgnoreCase(event.getAuthor().getId())) {
                event.getTextChannel().sendMessage("Das Turnier mit der `ID " + nbr + "` gehört nicht dir, sondern " + tn.getAutor().getName() + "!").queue();
                return;
            }
            setting.put(event.getAuthor(), nbr);
            tn.setRegStat(Turnier.RegStat.SETMAIN);
            Turnier.list.put(nbr, tn);
            event.getTextChannel().sendMessage("Du bearbeitest jetzt das Turnier mit der `ID " + nbr + "`!").queue();
            event.getTextChannel().sendMessage(helpSet(Objects.requireNonNull(event.getMember()))).queue();
            return;

        }
        int tid = setting.get(event.getAuthor());
        Turnier tn = Turnier.list.get(tid);

        if (args.length < 1) {
            if (tn.getRegStat().equals(Turnier.RegStat.SETMAIN)) {
                event.getTextChannel().sendMessage(helpSet(Objects.requireNonNull(event.getMember()))).queue();
                return;
            } else {
                event.getTextChannel().sendMessage(getTask(tn.getRegStat(), prefix, true)).queue();
                return;
            }
        }
        if (args[0].equalsIgnoreCase("exit")) {
            setting.remove(event.getAuthor());
            event.getTextChannel().sendMessage("Einstellungsmenü verlassen!").queue();
            return;
        }
        if (args[0].equalsIgnoreCase("back")) {
            tn.setRegStat(Turnier.RegStat.SETMAIN);
            Turnier.list.put(tid, tn);
            event.getTextChannel().sendMessage(helpSet(Objects.requireNonNull(event.getMember()))).queue();
            return;
        }

        switch (tn.getRegStat()) {
            case SETMAIN:
                setmain(event, args, tid, tn, prefix);
                break;
            case SETGAMENAME:
                setgamename(event, args, tid, tn, prefix);
                break;
            case SETDELETE:
                setdelete(event, args, tid, tn, prefix);
                break;
            case SETPLAYERCOUNT:
                setplayercount(event, args, tid, tn, prefix);
                break;
            case SETKICK:
                setkick(event, args, tid, tn, prefix);
                break;
            case SETDESCRIPTION:
                setdescription(event, args, tid, tn, prefix);
                break;


            default:
                tn.setRegStat(Turnier.RegStat.DONE);
                Turnier.list.put(tid, tn);
                setting.remove(event.getAuthor());
                event.getTextChannel().sendMessage("Etwas scheint schiefgelaufen zu sein. Bearbeitung abgebrochen. Bitte probiere es nochmal!").queue();
                break;
        }
        Turnier.save();

    }

    private void setmain(MessageReceivedEvent event, String[] args, int tid, Turnier tn, String prefix) {
        if (args.length < 1) {
            event.getTextChannel().sendMessage(helpSet(Objects.requireNonNull(event.getMember()))).queue();
            return;
        }

        switch (args[0].toLowerCase()) {
            case "game":
                tn.setRegStat(Turnier.RegStat.SETGAMENAME);
                event.getTextChannel().sendMessage(getTask(Turnier.RegStat.SETGAMENAME, prefix, false)).queue();
                break;
            case "delete":
                tn.setRegStat(Turnier.RegStat.SETDELETE);
                event.getTextChannel().sendMessage(getTask(Turnier.RegStat.SETDELETE, prefix, false)).queue();
                break;
            case "playercount":
                tn.setRegStat(Turnier.RegStat.SETPLAYERCOUNT);
                event.getTextChannel().sendMessage(getTask(Turnier.RegStat.SETPLAYERCOUNT, prefix, false)).queue();
                break;
            case "kick":
                tn.setRegStat(Turnier.RegStat.SETKICK);
                event.getTextChannel().sendMessage(getTask(Turnier.RegStat.SETKICK, prefix, false)).queue();
                event.getTextChannel().sendMessage(showPlayers(tn, prefix)).queue();
                break;
            case "description":
                tn.setRegStat(Turnier.RegStat.SETDESCRIPTION);
                event.getTextChannel().sendMessage(getTask(Turnier.RegStat.SETDESCRIPTION, prefix, false)).queue();
                break;
            case "notification":
                tn.setNotification(!tn.getNotification());
                Turnier.list.put(tid, tn);
                String notstat = tn.getNotification() ? "an" : "aus";
                event.getTextChannel().sendMessage("Die Benachrichtigungen sind nun " + notstat + "!").queue();
                break;


            default:
                event.getTextChannel().sendMessage(helpSet(Objects.requireNonNull(event.getMember()))).queue();
                break;
        }
        Turnier.list.put(tid, tn);

    }


    private void setgamename(MessageReceivedEvent event, String[] args, int tid, Turnier tn, String prefix) {
        if (args.length < 1) {
            event.getTextChannel().sendMessage(getTask(Turnier.RegStat.SETGAMENAME, prefix, true)).queue();
            return;
        }
        if (args.length == 1) {
            tn.setSpielname(args[0]);
        } else {
            StringBuilder in = new StringBuilder();
            for (String s : args) {
                in.append(" ").append(s);
            }
            if (in.toString().contains("§")) {
                event.getTextChannel().sendMessage("Asu technischenn Gründen kannst du das §-Zeichen nciht nutzen!").queue();
                return;
            }
            tn.setSpielname(in.toString());
        }
        tn.setRegStat(Turnier.RegStat.SETMAIN);
        tn.getMsg().editMessage(tn.getEmbed()).queue();
        Turnier.list.put(tid, tn);
        event.getTextChannel().sendMessage("Turniername erfolgreich auf " + tn.getSpielname() + " geändert! Zum Hauptmenü zurückgekehrt!").queue();
    }

    private void setdelete(MessageReceivedEvent event, String[] args, int tid, Turnier tn, String prefix) {
        if (args.length < 1) {
            event.getTextChannel().sendMessage(getTask(Turnier.RegStat.SETDELETE, prefix, true)).queue();
            return;
        }
        if (args[0].equals("yesIwant")) {
            if (Turnier.delete(tid)) {
                event.getTextChannel().sendMessage("Löschen von Turnier `ID " + tid + "`erfolgreich!").queue();
                setting.remove(event.getAuthor());
            } else {
                event.getTextChannel().sendMessage("Beim Löschen gab es seinen Fehler. Bitte kontaktiere Logii!").queue();
                tn.setRegStat(Turnier.RegStat.SETMAIN);
                Turnier.list.put(tid, tn);
            }
            return;
        }
        event.getTextChannel().sendMessage("Abgebrochen!").queue();
        tn.setRegStat(Turnier.RegStat.SETMAIN);
        Turnier.list.put(tid, tn);
    }

    private void setplayercount(MessageReceivedEvent event, String[] args, int tid, Turnier tn, String prefix) {

        if (args.length < 1) {
            event.getTextChannel().sendMessage(getTask(Turnier.RegStat.SETPLAYERCOUNT, prefix, true)).queue();
            return;
        }
        int neu = 0;
        try {
            neu = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            event.getTextChannel().sendMessage(getTask(Turnier.RegStat.SETPLAYERCOUNT, prefix, true)).queue();
            return;
        }
        int[] ul = Turnier.userlimit(tn.getMatchtype());
        if (ul.length != 0) {
            boolean found = false;
            StringBuilder zul = new StringBuilder();
            for (int i : ul) {
                if (i == neu) found = true;
                zul.append(",").append(i);
            }
            if (!found) {

                event.getTextChannel().sendMessage("Für diese Turnierform ist diese Anzahl nicht zuläassig!\nZulässige Anzahlen sind:\n" + zul.toString().replaceFirst(",", "")).queue();
                return;
            }

        }
        if (neu > tn.getCurrentPlayerCount()) {
            event.getTextChannel().sendMessage("Dein Turnier hat zu viele Spieler! Bitte entferne mindestens " + (neu - tn.getCurrentPlayerCount()) + " Spieler, um das Turnier auf diese Größezu begrenzen!").queue();
            return;
        }
        if (!Turnier.playerCountSinnvoll(neu)) {
            event.getTextChannel().sendMessage("Nice Try, aber so ein Turnier wird nicht gespielt!").queue();
            return;
        }
        tn.setSpielerzahl(neu);
        tn.setRegStat(Turnier.RegStat.SETMAIN);
        Turnier.list.put(tid, tn);
        event.getTextChannel().sendMessage("Die Spielerzahl wurde erfolgreich aktualisiert!").queue();

    }

    private void setkick(MessageReceivedEvent event, String[] args, int tid, Turnier tn, String prefix) {
        if (args.length < 1) {
            event.getTextChannel().sendMessage(showPlayers(tn, prefix)).queue();
            return;
        }
        int nr = 0;
        try {
            nr = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            event.getTextChannel().sendMessage(getTask(Turnier.RegStat.SETKICK, prefix, true)).queue();
            event.getTextChannel().sendMessage(showPlayers(tn, prefix)).queue();
            return;
        }
        if (nr > tn.getCurrentPlayerCount()) {
            event.getTextChannel().sendMessage("So viele Spieler haben sich noch nicht angemeldet!").queue();
            return;
        }
        User u = tn.getRegisterd().get(nr - 1);
        tn.kick(u);
        event.getTextChannel().sendMessage("Erfolgreich User " + u.getName() + " gekickt!").queue();
        tn.setRegStat(Turnier.RegStat.SETMAIN);
        Turnier.list.put(tid, tn);
    }

    private void setdescription(MessageReceivedEvent event, String[] args, int tid, Turnier tn, String prefix) {
        if (args.length < 1) {
            event.getTextChannel().sendMessage(getTask(Turnier.RegStat.SETDESCRIPTION, prefix, true)).queue();
            return;
        }
        if (args.length == 1) {
            if (args[0].contains("§")) {
                event.getTextChannel().sendMessage("Aus technischenn Gründen kannst du das §-Zeichen nciht nutzen!").queue();
                return;
            }
            tn.setEinstellungen(args[0]);
        } else {
            StringBuilder in = new StringBuilder();
            for (String s : args) {
                in.append(" ").append(s);
            }
            if (in.toString().contains("§")) {
                event.getTextChannel().sendMessage("Aus technischenn Gründen kannst du das §-Zeichen nciht nutzen!").queue();
                return;
            }
            tn.setEinstellungen(in.toString());
        }
        tn.setRegStat(Turnier.RegStat.SETMAIN);
        tn.getMsg().editMessage(tn.getEmbed()).queue();
        Turnier.list.put(tid, tn);
        event.getTextChannel().sendMessage("Turnierbeschreibung erfolgreich auf " + tn.getEinstellungen() + " geändert! Zum Hauptmenü zurückgekehrt!").queue();
    }


    private MessageEmbed showPlayers(Turnier tn, String prefix) {
        ArrayList<User> usrs = tn.getRegisterd();

        EmbedBuilder eb = new EmbedBuilder().setColor(Color.gray).setTitle(tn.getAutor().getName() + "'s Turnier").setAuthor("Anmedeliste").setFooter("Kicke jemanden mit \"" + prefix + "turnier [Nummer]\"!");
        int i = 1;
        for (User u : usrs) {
            eb.appendDescription(i++ + ") " + u.getName() + "\n");
        }
        return eb.build();
    }


    private String[] shortenArgs(String[] args) {
        if (args.length == 1) {
            args = new String[]{};
        }
        if (args.length > 1) {
            String[] help = new String[args.length - 1];
            for (int i = 0; i < args.length - 1; i++) {
                help[i] = args[i + 1];
            }
            args = help;
        }
        return args;
    }

    private String getTask(Turnier.RegStat rs, String prefix, boolean addback) {
        String out = "";
        switch (rs) {
            case SETGAMENAME:
                out = "Gib einen neues Spiel ein. Nutze dafür `" + prefix + "turnier [Name]`";
                break;
            case SETDELETE:
                out = "Willst du das Turnier wirklich löschen? dann gib jetzt `" + prefix + "turnier yesIwant` ein!";
                break;
            case SETPLAYERCOUNT:
                out = "Wie viele Spieler sollen in deinem Turnier mitspielen können? Nutze `" + prefix + "turnier [Anzahl]`";
                break;
            case SETKICK:
                out = "Wen willst du vom Turnier kicken?";
                break;
            case SETDESCRIPTION:
                out = "Wie soll die neuen Beschreibung sein? Nutze `" + prefix + "turnier [Beschreibung]`!";
                break;
            default:
                out = "???";
        }
        if (addback) {
            return out + "\nNutze jederzeit `" + prefix + "turnier back`, um zurück zum Hauptmenü zu kommen!";
        } else return out;
    }

    private MessageEmbed helpSet(Member member) {
        String prefix = commandListener.getPrefix(member.getGuild());
        EmbedBuilder eb = new EmbedBuilder().setColor(Color.blue).setTitle((member).getEffectiveName() + "'s Turnier").setAuthor("Subcommands, nach " + prefix + "turnier zu benutzen");
        eb.addField("game", "Ändere das Spiel, was gespielt wird", false);
        eb.addField("description", "Ändere die Turnierbeschreibung", false);
        eb.addField("playercount", "Ändere die Anzahl der Teilnehmer", false);
        eb.addField("kick", "Entferne jemanden aus dem Turnier", false);
        eb.addField("notification", "Ändere die Benachrichtigungseinstellungen", false);


        eb.addField("delete", "Lösche das Turnier", false);

        eb.addField("back", "Zurück zu diesem Hauptmenü", false);
        eb.addField("exit", "Setting-Menü verlassen", false);


        return eb.build();
    }
}
