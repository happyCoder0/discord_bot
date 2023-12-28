package listeners;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.sql.Connection;

public class HelloCommandListener extends AbstractCommandListener {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (!event.getName().equalsIgnoreCase(getName())) return;
        if (event.getMember() == null) System.out.println("member is null");
        event.reply(String.format("Hello, %s", event.getMember().getEffectiveName())).queue();
    }

    @Override
    public String getName() {
        return "hello";
    }

    @Override
    public String getDescription() {
        return  "Greets the user";
    }
}
