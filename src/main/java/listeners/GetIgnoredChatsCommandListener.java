package listeners;

import db.IgnoredChatsDbHelper;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

public class GetIgnoredChatsCommandListener extends ListenerAdapter {
    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if(!event.getName().equalsIgnoreCase("gic")) return;

        IgnoredChatsDbHelper helper = IgnoredChatsDbHelper.getInstance();
        List<String> ignoredChatsByServerId = helper.findIgnoredChatsByServerId(
                Objects.requireNonNull(event.getGuild()).getId());

        StringBuilder messageBuilder = new StringBuilder();

        messageBuilder.append("Ignored chats ids:\n");
        ignoredChatsByServerId.forEach(id -> messageBuilder.append(id.concat("\n")));

        event.reply(messageBuilder.toString()).queue();
    }
}
