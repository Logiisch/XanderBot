package listeners;

import helperCore.ServerStats;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;

public class readyListener extends ListenerAdapter {

    public void onReady(@Nonnull ReadyEvent event) {

        //ServerStats.reload(event.getJDA());

    }


}
