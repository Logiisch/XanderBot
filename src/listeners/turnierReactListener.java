package listeners;

import helperCore.Turnier;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;
import java.util.HashMap;

public class turnierReactListener extends ListenerAdapter {

    public static HashMap<String,Integer> toSupervise = new HashMap<>();

    //richtiges Emote.getName: "👍"

    public void onGuildMessageReactionAdd(@Nonnull GuildMessageReactionAddEvent event) {
        if (!toSupervise.containsKey(event.getMessageId())) return;
        if (!event.getReaction().getReactionEmote().getName().equalsIgnoreCase("\uD83D\uDC4D")) {
            System.out.println("Habe Reaktion "+ event.getReaction().getReactionEmote().getName()+ "erhalten!");
            return;
        }
        int tn = toSupervise.get(event.getMessageId());
        Turnier.refreshMembers(tn,true);


    }

    public void onGuildMessageReactionRemove(@Nonnull GuildMessageReactionRemoveEvent event) {

        if (!toSupervise.containsKey(event.getMessageId())) return;
        if (!event.getReaction().getReactionEmote().getName().equalsIgnoreCase("\uD83D\uDC4D")) return;
        int tn = toSupervise.get(event.getMessageId());
        Turnier.refreshMembers(tn,true);

    }

}
