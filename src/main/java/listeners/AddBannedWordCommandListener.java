package listeners;

import db.BannedWordsDbHelper;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.postgresql.util.PSQLException;

public class AddBannedWordCommandListener extends ListenerAdapter {
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (!event.getName().equalsIgnoreCase("abw")) return;
        OptionMapping optionMapping = event.getOption("word");
        if (optionMapping == null) event.reply("Provide a word to ban").queue();

        String word = optionMapping.getAsString();
        BannedWordsDbHelper dbHelper = new BannedWordsDbHelper();
        try {
            dbHelper.insert(event.getGuild().getId(), word);
        } catch (PSQLException e) {
            event.reply(e.getServerErrorMessage().getMessage()).queue();
        }
        event.reply(String.format("Added word %s to banned words list", word)).queue();
    }
}
