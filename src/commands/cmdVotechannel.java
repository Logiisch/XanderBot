package commands;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import util.STATIC;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class cmdVotechannel implements Command{
    @Override
    public boolean called(String[] args, MessageReceivedEvent event) {
        return false;
    }

    @Override
    public void action(String[] args, MessageReceivedEvent event)  {
        if (!Objects.requireNonNull(event.getMember()).hasPermission(Permission.ADMINISTRATOR)&&!event.getAuthor().getId().equalsIgnoreCase(STATIC.OWNERID)) {
            event.getTextChannel().sendMessage("Ich weiß nicht, ob du dazu berechtigt bist...").queue();
            return;
        }
        if (!STATIC.votechannels.containsKey(event.getGuild().getId())) {
            ArrayList<String> vcs = new ArrayList<>();
            vcs.add(event.getTextChannel().getId());
            STATIC.votechannels.put(event.getGuild().getId(),vcs);
            event.getTextChannel().sendMessage("Erfolgreich als VoteChannel festgelegt!").queue();
            STATIC.save(event.getGuild());
            return;
        }
        ArrayList<String> vcs = STATIC.votechannels.get(event.getGuild().getId());
        if(vcs.contains(event.getTextChannel().getId())) {
            vcs.remove(event.getTextChannel().getId());
            STATIC.votechannels.put(event.getGuild().getId(),vcs);
            event.getTextChannel().sendMessage("Dieser Channel ist nun kein VoteChannel mehr!").queue();
            STATIC.save(event.getGuild());
        } else {
            vcs.add(event.getTextChannel().getId());
            STATIC.votechannels.put(event.getGuild().getId(),vcs);
            event.getTextChannel().sendMessage("Erfolgreich als VoteChannel festgelegt!").queue();
            STATIC.save(event.getGuild());
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
        return "Stelle ein, ob der Bot auf Nachrichten in dem Channel mit :yes: und :no: reagieren soll";
    }
}
