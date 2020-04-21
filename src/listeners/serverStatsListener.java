package listeners;

import helperCore.ServerStats;
import net.dv8tion.jda.api.events.channel.text.TextChannelCreateEvent;
import net.dv8tion.jda.api.events.channel.text.TextChannelDeleteEvent;
import net.dv8tion.jda.api.events.channel.voice.VoiceChannelCreateEvent;
import net.dv8tion.jda.api.events.channel.voice.VoiceChannelDeleteEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberLeaveEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleAddEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleRemoveEvent;
import net.dv8tion.jda.api.events.role.RoleCreateEvent;
import net.dv8tion.jda.api.events.role.RoleDeleteEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;

public class serverStatsListener extends ListenerAdapter {

    public void onTextChannelDelete(@Nonnull TextChannelDeleteEvent event) {
        ServerStats.reload(event.getGuild());
    }

    public void onTextChannelCreate(@Nonnull TextChannelCreateEvent event) {
        ServerStats.reload(event.getGuild());
    }

    public void onVoiceChannelDelete(@Nonnull VoiceChannelDeleteEvent event) {
        ServerStats.reload(event.getGuild());
    }

    public void onVoiceChannelCreate(@Nonnull VoiceChannelCreateEvent event) {
        ServerStats.reload(event.getGuild());
    }

    public void onGuildMemberJoin(@Nonnull GuildMemberJoinEvent event) {
        ServerStats.reload(event.getGuild());
    }

    public void onGuildMemberLeave(@Nonnull GuildMemberLeaveEvent event) {
        ServerStats.reload(event.getGuild());
    }

    public void onGuildMemberRoleAdd(@Nonnull GuildMemberRoleAddEvent event) {
        ServerStats.reload(event.getGuild());
    }

    public void onGuildMemberRoleRemove(@Nonnull GuildMemberRoleRemoveEvent event) {
        ServerStats.reload(event.getGuild());
    }

    public void onRoleCreate(@Nonnull RoleCreateEvent event) {
        ServerStats.reload(event.getGuild());
    }

    public void onRoleDelete(@Nonnull RoleDeleteEvent event) {
        ServerStats.reload(event.getGuild());
    }
}
