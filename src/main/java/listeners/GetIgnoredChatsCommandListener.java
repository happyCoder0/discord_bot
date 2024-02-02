package listeners;

import db.IgnoredChatsDbHelper;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

public class GetIgnoredChatsCommandListener extends AbstractSlashCommandListener {
    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if(!event.getName().equalsIgnoreCase(getName())) return;

        IgnoredChatsDbHelper helper = IgnoredChatsDbHelper.getInstance();
        List<String> ignoredChatsByServerId = helper.findIgnoredChatsByServerId(
                Objects.requireNonNull(event.getGuild()).getId());

        StringBuilder messageBuilder = new StringBuilder();

        messageBuilder.append("Ignored chats ids:\n");
        ignoredChatsByServerId.forEach(id -> messageBuilder.append(id.concat("\n")));

        event.reply(messageBuilder.toString()).queue();
    }

    @Override
    public String getName() {
        return "gic";
    }

    @Override
    public String getDescription() {
        return "Gets list of all ignored chats on this server";
    }
}
