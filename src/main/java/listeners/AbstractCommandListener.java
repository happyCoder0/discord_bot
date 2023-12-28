package listeners;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.sql.Connection;

public abstract class AbstractCommandListener extends ListenerAdapter {

    @Override
    public abstract void onSlashCommandInteraction(SlashCommandInteractionEvent event);

    public abstract String getName();

    public abstract String getDescription();
    public SlashCommandData getData() {
        return Commands.slash(getName(), getDescription()).setGuildOnly(true);
    }
}
