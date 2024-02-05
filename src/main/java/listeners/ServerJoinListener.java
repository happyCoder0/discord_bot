package listeners;

import db.IgnoredChatsDbHelper;
import db.UserDbHelper;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ServerJoinListener extends ListenerAdapter {
    @Override
    public void onGuildJoin(@NotNull GuildJoinEvent event) {
        super.onGuildJoin(event);

        IgnoredChatsDbHelper ignoredChatsDbHelper = IgnoredChatsDbHelper.getInstance();
        UserDbHelper userDbHelper = UserDbHelper.getInstance();
        Guild guild = event.getGuild();

        System.out.println(event.getGuild().getName());
        ignoredChatsDbHelper.registerServer(event.getGuild().getId());

        guild.loadMembers().onSuccess(members -> {
            for(Member m : members) {
                if (m != event.getGuild().getSelfMember() && !m.getUser().isBot()) {
                    userDbHelper.addUser(guild.getId(), m.getUser().getId());
                }
            }
        });
    }
}
