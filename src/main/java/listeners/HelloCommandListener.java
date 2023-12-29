package listeners;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class HelloCommandListener extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (!event.getName().equalsIgnoreCase("hello")) return;
        if (event.getMember() == null) System.out.println("member is null");
        event.reply(String.format("Hello, %s", event.getMember().getEffectiveName())).queue();
    }
}
