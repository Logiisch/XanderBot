package commands;

import listeners.commandListener;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import util.STATIC;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class cmdCommands implements Command {
    @Override
    public boolean called(String[] args, MessageReceivedEvent event) {
        return false;
    }

    @Override
    public void action(String[] args, MessageReceivedEvent event) throws IOException {
        if (!event.getAuthor().getId().equalsIgnoreCase(Objects.requireNonNull(event.getGuild().getOwner()).getUser().getId()) && !event.getAuthor().getId().equalsIgnoreCase(STATIC.OWNERID)) {
            event.getTextChannel().sendMessage("Du hast dafür keine Berechtigungen!").queue();
            return;
        }
        if (args.length < 1) {
            event.getTextChannel().sendMessage("Verfügbare Subcommands: `allow`,`deny`,`list`").queue();
            return;
        }
        ArrayList<String> blckd = commandListener.blockedCmds.getOrDefault(event.getGuild(), new ArrayList<>());
        if (args[0].equalsIgnoreCase("list")) {
            if (blckd.isEmpty()) {
                event.getTextChannel().sendMessage("Keine Befehle blockiert!").queue();
                return;
            }
            if (blckd.contains("all")) {
                event.getTextChannel().sendMessage("Alle Befehle blockiert!").queue();
                return;
            }
            StringBuilder sb = new StringBuilder("Aktuell blockiert sind:\n\n");

            for (String s : blckd) {
                sb.append(s).append("\n");
            }
            event.getTextChannel().sendMessage(sb.toString()).queue();
            return;
        }
        if (args.length < 2) {
            event.getTextChannel().sendMessage("Usage: x!commands [allow/deny] [Befehl]").queue();
            return;
        }
        if (args[0].equalsIgnoreCase("allow")) {
            blckd.remove(args[1]);
            event.getTextChannel().sendMessage("Befehl " + args[1] + " freigegeben!").queue();
            commandListener.blockedCmds.put(event.getGuild(), blckd);
            return;
        }
        if (args[0].equalsIgnoreCase("deny")) {
            blckd.add(args[1]);
            event.getTextChannel().sendMessage("Befehl " + args[1] + " blockiert!!").queue();
            commandListener.blockedCmds.put(event.getGuild(), blckd);
            return;
        }
        event.getTextChannel().sendMessage("Verfügbare Subcommands: `allow`,`deny`,`list`").queue();
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
        return "Blockierung und Freigabe bestimmter Befehle";
    }
}
