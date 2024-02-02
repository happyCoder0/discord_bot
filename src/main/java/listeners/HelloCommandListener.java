package listeners;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class HelloCommandListener extends AbstractSlashCommandListener {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (!event.getName().equalsIgnoreCase("hello")) return;
        if (event.getMember() == null) System.out.println("member is null");

        event.reply(String.format("Hello, %s", event.getMember().getEffectiveName())).queue();
    }

    @Override
    public String getName() {
        return "hello";
    }

    @Override
    public String getDescription() {
        return "Greets user";
    }
}
