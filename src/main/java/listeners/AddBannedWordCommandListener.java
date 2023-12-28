package listeners;

import db.BannedWordsDbHelper;
import db.DbUtil;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.sql.Connection;

public class AddBannedWordCommandListener extends AbstractCommandListener {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (!event.getName().equalsIgnoreCase(getName())) return;
        OptionMapping optionMapping = event.getOption("word");
        if (optionMapping == null) event.reply("Provide a word to ban").queue();

        String word = optionMapping.getAsString();
        BannedWordsDbHelper dbHelper = new BannedWordsDbHelper();
        dbHelper.insert(event.getGuild().getId(), word);
        event.reply(String.format("Added word %s to banned words list", word)).queue();
    }

    @Override
    public String getName() {
        return "abw";
    }

    @Override
    public String getDescription() {
        return "adds new banned word for this server";
    }

    @Override
    public SlashCommandData getData() {
        return super.getData().addOptions(
                new OptionData(OptionType.STRING, "word", "word to ban")
                        .setRequired(true));
    }
}
