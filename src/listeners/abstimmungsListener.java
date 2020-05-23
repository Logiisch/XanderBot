package listeners;

import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import util.STATIC;

import javax.annotation.Nonnull;
import java.util.Timer;
import java.util.TimerTask;

public class abstimmungsListener extends ListenerAdapter {

    public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event) {
        for (String s : STATIC.VOTECHANNELS) {
            if (event.getChannel().getId().equalsIgnoreCase(s)) addReacts(event.getMessage());
        }

    }

    private void addReacts(Message m) {
        try {
            Emote yes = m.getGuild().getEmotesByName("yes", true).get(0);
            Emote no = m.getGuild().getEmotesByName("no", true).get(0);
            m.addReaction(yes).complete();
            m.addReaction(no).complete();
        } catch (Exception e) {
            Message erm = m.getChannel().sendMessage("Error while reacting to MSG!").complete();
            Timer t = new Timer();
            t.schedule(new TimerTask() {
                @Override
                public void run() {
                    erm.delete().queue();
                }
            }, 10000);
            e.printStackTrace();
        }
    }

}
