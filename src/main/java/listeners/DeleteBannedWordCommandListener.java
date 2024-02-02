package listeners;

import db.BannedWordsDbHelper;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class DeleteBannedWordCommandListener extends AbstractSlashCommandListener {
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (!event.getName().equalsIgnoreCase(getName())) return;
        OptionMapping optionMapping = event.getOption("word");
        if (optionMapping == null) event.reply("Provide a banned word to delete").queue();

        String word = optionMapping.getAsString();
        BannedWordsDbHelper dbHelper = BannedWordsDbHelper.getInstance();
        dbHelper.deleteBannedWord(Objects.requireNonNull(event.getGuild()).getId(), word);
        event.reply(String.format("Word %s deleted from banned words list", word)).queue();
    }

    @Override
    public String getName() {
        return "dbw";
    }

    @Override
    public String getDescription() {
        return "Deletes word from banned words list on this server";
    }

    @Override
    public List<OptionData> getOptionDataList() {
        return Collections.singletonList(new OptionData(
                OptionType.STRING,
                "word",
                "Word to delete",
                true));
    }
}
