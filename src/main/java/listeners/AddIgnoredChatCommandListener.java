package listeners;

import db.IgnoredChatsDbHelper;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public class AddIgnoredChatCommandListener extends AbstractSlashCommandListener {
    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if (!event.getName().equalsIgnoreCase(getName())) return;

        OptionMapping optionMapping = event.getOption("chat_id");
        if (optionMapping == null) event.reply("Provide chat id").queue();

        String chatId = optionMapping.getAsString();

        IgnoredChatsDbHelper dbHelper = IgnoredChatsDbHelper.getInstance();
        dbHelper.insert(event.getGuild().getId(), chatId);

        event.reply(String.format("Chat %s added to ignored list", chatId)).queue();
    }

    @Override
    public String getName() {
        return "aic";
    }

    @Override
    public String getDescription() {
        return "Adds chat with provided id to ignored list";
    }

    @Override
    public List<OptionData> getOptionDataList() {
        return Collections.singletonList(new OptionData(
                OptionType.STRING,
                "chat_id",
                "Id of chat to ignore",
                true));
    }
}
