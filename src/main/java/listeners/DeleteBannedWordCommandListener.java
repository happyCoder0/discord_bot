package listeners;

import db.BannedWordsDbHelper;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

import java.util.Objects;

public class DeleteBannedWordCommandListener extends ListenerAdapter {
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (!event.getName().equalsIgnoreCase("dbw")) return;
        OptionMapping optionMapping = event.getOption("word");
        if (optionMapping == null) event.reply("Provide a banned word to delete").queue();

        String word = optionMapping.getAsString();
        BannedWordsDbHelper dbHelper = new BannedWordsDbHelper();
        dbHelper.deleteBannedWord(Objects.requireNonNull(event.getGuild()).getId(), word);
        event.reply(String.format("Word %s deleted from banned words list", word)).queue();
    }
}
