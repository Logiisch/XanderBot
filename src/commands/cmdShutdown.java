package commands;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import util.STATIC;

import java.io.IOException;
import java.util.Objects;

public class cmdShutdown implements Command {
    @Override
    public boolean called(String[] args, MessageReceivedEvent event) {

        return false;
    }

    @Override
    public void action(String[] args, MessageReceivedEvent event) throws IOException {
        if (!event.getAuthor().getId().equalsIgnoreCase(Objects.requireNonNull(event.getGuild().getOwner()).getUser().getId())&&!event.getAuthor().getId().equalsIgnoreCase(STATIC.OWNERID)) {
            event.getTextChannel().sendMessage("Du kannst das nicht. Wenn du denkst, es sei wichtig, sag bitte Xander oder Logii bescheid!").queue();
            return;
        }
        STATIC.LOOPBOLEAN=false;
        event.getTextChannel().sendMessage("Bot wird gestoppt..").queue();
        event.getJDA().shutdownNow();
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
        return "Not-Aus";
    }
}
