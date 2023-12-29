package listeners;

import db.BannedWordsDbHelper;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.List;

public class GetBannedWordsCommandListener extends ListenerAdapter {
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (!event.getName().equalsIgnoreCase("gbw")) return;
        BannedWordsDbHelper dbHelper = new BannedWordsDbHelper();
        List<String> words = dbHelper.findBannedWordsByServerId(event.getGuild().getId());

        StringBuilder builder = new StringBuilder();
        words.forEach(word -> builder.append(word.concat("\n")));

        event.reply(builder.toString()).queue();
    }
}
