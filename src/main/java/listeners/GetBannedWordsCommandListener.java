package listeners;

import db.BannedWordsDbHelper;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.List;

public class GetBannedWordsCommandListener extends AbstractSlashCommandListener {
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (!event.getName().equalsIgnoreCase(getName())) return;

        BannedWordsDbHelper dbHelper = BannedWordsDbHelper.getInstance();
        List<String> words = dbHelper.findBannedWordsByServerId(event.getGuild().getId());

        StringBuilder builder = new StringBuilder();
        words.forEach(word -> builder.append(word.concat("\n")));

        event.reply(builder.toString()).queue();
    }

    @Override
    public String getName() {
        return "gbw";
    }

    @Override
    public String getDescription() {
        return "Prints list of banned words on this server";
    }
}
