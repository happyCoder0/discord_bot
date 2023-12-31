package listeners;

import db.IgnoredChatsDbHelper;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.jetbrains.annotations.NotNull;

public class AddIgnoredChatCommandListener extends ListenerAdapter {
    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if (!event.getName().equalsIgnoreCase("aic")) return;

        OptionMapping optionMapping = event.getOption("chat_id");
        if (optionMapping == null) event.reply("Provide chat id").queue();

        String chatId = optionMapping.getAsString();

        IgnoredChatsDbHelper dbHelper = new IgnoredChatsDbHelper();
        dbHelper.insert(event.getGuild().getId(), chatId);

        event.reply(String.format("Chat %s added to ignored list", chatId)).queue();
    }
}
