package listeners;

import db.BannedWordsDbHelper;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.postgresql.util.PSQLException;

import java.util.*;

public class AddBannedWordCommandListener extends AbstractSlashCommandListener {
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (!event.getName().equalsIgnoreCase(getName())) return;
        OptionMapping optionMapping = event.getOption("word");
        if (optionMapping == null) event.reply("Provide a word to ban").queue();

        String word = optionMapping.getAsString();
        BannedWordsDbHelper dbHelper = BannedWordsDbHelper.getInstance();
        try {
            dbHelper.insert(Objects.requireNonNull(event.getGuild()).getId(), word);
        } catch (PSQLException e) {
            event.reply(e.getServerErrorMessage().getMessage()).queue();
        }
        event.reply(String.format("Added word %s to banned words list", word)).queue();
    }

    @Override
    public String getName() {
        return "abw";
    }

    @Override
    public String getDescription() {
        return "Adds new banned word for this server";
    }

    @Override
    public List<OptionData> getOptionDataList() {
        return Collections.singletonList(new OptionData(
                OptionType.STRING,
                "word",
                "Word to ban",
                true));
    }
}
