package listeners;

import db.IgnoredChatsDbHelper;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.jetbrains.annotations.NotNull;

public class DeleteIgnoredChatCommandListener extends ListenerAdapter {
    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        super.onSlashCommandInteraction(event);

        if (!event.getName().equalsIgnoreCase("dic")) return;

        OptionMapping optionMapping = event.getOption("chat_id");
        if (optionMapping == null) event.reply("Provide chat id").queue();

        String chatId = optionMapping.getAsString();

        IgnoredChatsDbHelper helper = IgnoredChatsDbHelper.getInstance();
        String serverId = event.getGuild().getId();

        if (!helper.exists(serverId, chatId)) {
            event.reply("Chat with id " + chatId + " is not ignored").queue();
            return;
        }

        helper.delete(serverId, chatId);
        event.reply("Chat with id " + chatId + " was deleted").queue();
    }
}
