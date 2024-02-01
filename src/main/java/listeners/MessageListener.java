package listeners;

import db.BannedWordsDbHelper;
import db.IgnoredChatsDbHelper;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MessageListener extends ListenerAdapter {
    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        super.onMessageReceived(event);

        User author = event.getAuthor();
        SelfUser selfUser = event.getJDA().getSelfUser();

        if (author.isBot()) return;
        if (author.getId().equals(selfUser.getId())) return;

        Guild guild = event.getGuild();

        IgnoredChatsDbHelper ignoredChatsDbHelper = IgnoredChatsDbHelper.getInstance();
        BannedWordsDbHelper bannedWordsDbHelper = BannedWordsDbHelper.getInstance();
        List<String> bannedWords = bannedWordsDbHelper.findBannedWordsByServerId(guild.getId());
        List<String> ignoredChats = ignoredChatsDbHelper.findIgnoredChatsByServerId(guild.getId());

        if(ignoredChats.contains(event.getGuildChannel().asTextChannel().getId())) return;

        String content = event.getMessage().getContentRaw().toLowerCase();

        bannedWords.forEach(w ->{
            if(content.contains(w.toLowerCase()))
                event
                        .getGuildChannel()
                        .asTextChannel()
                        .sendMessage("Pizda tebe " + author.getEffectiveName())
                        .queue();
        });
    }
}
