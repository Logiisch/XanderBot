package listeners;

import helperCore.ServerStats;
import helperCore.Turnier;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import util.STATIC;

import javax.annotation.Nonnull;

public class readyListener extends ListenerAdapter {

    public void onReady(@Nonnull ReadyEvent event) {

        //ServerStats.reload(event.getJDA());
        STATIC.SELFID = event.getJDA().getSelfUser().getId();
        Turnier.load(event.getJDA());

    }


}
