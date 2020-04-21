package listeners;

import core.commandHandler;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import util.STATIC;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class commandListener extends ListenerAdapter {
    public static List<User> Blocked = new ArrayList<>();
    public static HashMap<Guild, String> cstmPrfx = new HashMap<>();
    public static ArrayList<String> log = new ArrayList<>();
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        String CD = event.getMessage().getContentDisplay();
        Guild g;
        try {
            g = event.getGuild();
        } catch (Exception e) {
            return;
        }
        /*if (menuListener.UserInMenu.containsKey(event.getAuthor())) {
            return;
        }*/



        if (CD.startsWith(getPrefix(g).toLowerCase()) && !Objects.equals(event.getMessage().getAuthor().getId(), event.getJDA().getSelfUser().getId())) {
            if (listeners.commandListener.Blocked.contains(event.getAuthor()) ) {

                    event.getTextChannel().sendMessage("Du bist aktuell blockiert!").queue();
                    return;

            }
            /*if (CleverbotListener.CleverbotUsers.containsKey(event.getAuthor())) {
                event.getAuthor().openPrivateChannel().complete().sendMessage("Du bist aktuell im Cleverbot-Mudus. Um ihn zu verlassen, gib ``stop`` ein!").queue();
                return;
            }*/
            try {
                writeCmd(event);
                commandHandler.handleCommand(commandHandler.parser.parse(CD,  event));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
    public static String getPrefix(Guild g) {
        return STATIC.PREFIX;
    }
    public static void writeCmd(MessageReceivedEvent event) {
        int Stunde = event.getMessage().getTimeCreated().getHour();
        Stunde = Stunde + 2;
        if (Stunde > 23) {
            Stunde = Stunde - 24;
        }
        String out = "[" + Stunde+":"+event.getMessage().getTimeCreated().getMinute()+"]:"+event.getAuthor().getName()+" in "+event.getTextChannel().getName()+" auf "+event.getGuild().getName() + ":\""+event.getMessage().getContentDisplay()+"\"";
        log.add(out);
        try {
            util.printOutTxtFile.Write("cmdLog.txt", log);
        } catch (Exception e) {
            System.out.println("Fehler beim Log speichern!");
            e.printStackTrace();
        }
    }

}