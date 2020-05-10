package listeners;

import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import util.STATIC;

import javax.annotation.Nonnull;
import java.util.Objects;

public class verifyListener extends ListenerAdapter {

    public void onGuildMemberRoleAdd(@Nonnull GuildMemberRoleAddEvent event) {
        if (event.getRoles().contains(event.getGuild().getRoleById(STATIC.ROLE_VERIFIED)))
            event.getGuild().removeRoleFromMember(event.getMember(), Objects.requireNonNull(event.getGuild().getRoleById(STATIC.ROLE_UNVERIFIED))).queue();

    }
}
