package listeners;

import db.IgnoredChatsDbHelper;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class ServerJoinListener extends ListenerAdapter {
    @Override
    public void onGuildJoin(@NotNull GuildJoinEvent event) {
        super.onGuildJoin(event);

        IgnoredChatsDbHelper ignoredChatsDbHelper = IgnoredChatsDbHelper.getInstance();
        System.out.println(event.getGuild().getName());
        ignoredChatsDbHelper.registerServer(event.getGuild().getId());
    }
}
