package listeners;

import db.BannedWordsDbHelper;
import db.IgnoredChatsDbHelper;
import db.UserDbHelper;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.restaction.AuditableRestAction;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class MessageListener extends ListenerAdapter {
    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        super.onMessageReceived(event);

        User author = event.getAuthor();
        SelfUser selfUser = event.getJDA().getSelfUser();

        if (author.isBot()) return;
        if (author.getId().equals(selfUser.getId())) return;

        Guild guild = event.getGuild();
        Member member = event.getMember();

        IgnoredChatsDbHelper ignoredChatsDbHelper = IgnoredChatsDbHelper.getInstance();
        BannedWordsDbHelper bannedWordsDbHelper = BannedWordsDbHelper.getInstance();
        UserDbHelper userDbHelper = UserDbHelper.getInstance();
        List<String> bannedWords = bannedWordsDbHelper.findBannedWordsByServerId(guild.getId());
        List<String> ignoredChats = ignoredChatsDbHelper.findIgnoredChatsByServerId(guild.getId());

        if(ignoredChats.contains(event.getGuildChannel().asTextChannel().getId())) return;

        String content = event.getMessage().getContentRaw().toLowerCase();

        bannedWords.forEach(w ->{
            if(content.contains(w.toLowerCase())) {
                int warnings = userDbHelper.getWarnings(guild.getId(), member.getUser().getId());
                MessageCreateAction messageCreateAction = event
                        .getGuildChannel()
                        .asTextChannel()
                        .sendMessage("Pizda tebe " + author.getEffectiveName());

                if (warnings >= 2) {
                    if (!member.isOwner()) {
                        event.getGuild()
                                .ban(UserSnowflake.fromId(member.getUser().getId()), 1, TimeUnit.DAYS)
                                .reason("Swear words usage")
                                .queue();

                        event.getGuildChannel()
                                .asTextChannel()
                                .sendMessage(member.getEffectiveName() + " was banned for 1 day. Pizda emu")
                                .queue();
                        return;
                    }

                    userDbHelper.updateWarnings(guild.getId(), member.getUser().getId(), 0);
                }

                userDbHelper.updateWarnings(guild.getId(), member.getUser().getId(), warnings + 1);
                messageCreateAction.queue();
            }
        });
    }
}
