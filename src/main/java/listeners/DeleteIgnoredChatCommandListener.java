package listeners;

import db.IgnoredChatsDbHelper;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public class DeleteIgnoredChatCommandListener extends AbstractSlashCommandListener {
    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        super.onSlashCommandInteraction(event);

        if (!event.getName().equalsIgnoreCase(getName())) return;

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

    @Override
    public String getName() {
        return "dic";
    }

    @Override
    public String getDescription() {
        return "Deletes chat from ignored list";
    }

    @Override
    public List<OptionData> getOptionDataList() {
        return Collections.singletonList(new OptionData(
                OptionType.STRING,
                "chat_id",
                "Id of chat to remove",
                true));
    }
}
