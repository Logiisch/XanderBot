package commands;

import listeners.commandListener;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.PermissionOverride;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class cmdChannelHide implements Command {
    @Override
    public boolean called(String[] args, MessageReceivedEvent event) {
        return false;
    }

    @Override
    public void action(String[] args, MessageReceivedEvent event) throws IOException {
        if (args.length<1) {
            event.getTextChannel().sendMessage("Syntax:`"+ commandListener.getPrefix(event.getGuild())+"chnl [hide/show/list] <Kanal>`!").queue();
            return;
        }
        if (args[0].equalsIgnoreCase("list")||args[0].equalsIgnoreCase("l")) {
            listChannel(event.getMember(),event.getTextChannel());
            return;
        }
        if (args.length<2) {
            event.getTextChannel().sendMessage("Syntax:`"+ commandListener.getPrefix(event.getGuild())+"chnl [hide/show/list] <Kanal>`!").queue();
            return;
        }
        if (args[0].equalsIgnoreCase("hide")||args[0].equalsIgnoreCase("h")) {
            List<TextChannel> tcs =event.getMessage().getMentionedChannels();
            if (tcs.size()!=1) {
                event.getTextChannel().sendMessage("Syntax:`"+ commandListener.getPrefix(event.getGuild())+"chnl hide [Kanal]`!").queue();
                return;
            }
            TextChannel tch = tcs.get(0);
            ArrayList<Permission> add = new ArrayList<>();
            add.add(Permission.VIEW_CHANNEL);
            tch.getManager().putPermissionOverride(Objects.requireNonNull(event.getMember()),new ArrayList<>(),add).queue();
            event.getTextChannel().sendMessage("Du kannst den Channel jetzt nicht mehr sehen!").queue();
            return;
        }
        if (args[0].equalsIgnoreCase("show")||args[0].equalsIgnoreCase("s")) {
            StringBuilder sb = new StringBuilder();
            for (int i=1;i<args.length;i++) {
                sb.append(" ").append(args[i]);
            }
            String rest = sb.toString().replaceFirst(" ","");
            rest = rest.replace("#","");
            List<TextChannel> tcs = event.getGuild().getTextChannels();
            TextChannel tch = null;
            for (TextChannel tc:tcs) {
                if (tc.getName().replace("#","").equalsIgnoreCase(rest)) {
                    tch = tc;
                }
            }
            if (tch == null) {
                event.getTextChannel().sendMessage("Leider wurde der passende Channel nicht gefunden. Bitte überprüfe die Schreibweise!").queue();
                return;
            }
            //ArrayList<Permission> add = new ArrayList<>();
            //add.add(Permission.VIEW_CHANNEL);
            tch.getManager().removePermissionOverride(Objects.requireNonNull(event.getMember())).queue();
            //tch.getManager().putPermissionOverride(Objects.requireNonNull(event.getMember()),add,new ArrayList<>()).queue();
            event.getTextChannel().sendMessage("Du kannst den Channel nun wieder sehen!").queue();
            return;
        }
        event.getTextChannel().sendMessage("Syntax:`"+ commandListener.getPrefix(event.getGuild())+"chnl [hide/show/list] <Kanal>`!").queue();
        return;
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
        return "Verstecke Channel, die du nicht sehen willst.";
    }
    private void listChannel(Member m, TextChannel tc) {
        List<TextChannel> tcs = tc.getGuild().getTextChannels();
        List<TextChannel> muted = new ArrayList<>();
        for (TextChannel tct:tcs) {
            PermissionOverride por =tct.getPermissionOverride(m);
            if (por==null) continue;
            if (por.getDenied().isEmpty()) continue;
            if (por.isRoleOverride()) continue;
            if (por.getDenied().contains(Permission.VIEW_CHANNEL)) muted.add(tct);
        }
        if (muted.isEmpty()) {
            tc.sendMessage("Aktuell hast du keinen Channel ausgeblendet!").queue();
            return;
        }
        EmbedBuilder eb = new EmbedBuilder().setColor(Color.gray).setTitle("Ausgeblendete Channel von "+m.getEffectiveName());
        for (TextChannel tct:muted) {
            eb.appendDescription(tct.getName()).appendDescription("\n");
        }
        tc.sendMessage(eb.build()).queue();
    }
}
