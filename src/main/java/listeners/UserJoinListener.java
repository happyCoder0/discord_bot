package listeners;

import db.UserDbHelper;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class UserJoinListener extends ListenerAdapter {
    @Override
    public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent event) {
        UserDbHelper.getInstance().addUser(event.getGuild().getId(), event.getMember().getUser().getId());
    }
}
