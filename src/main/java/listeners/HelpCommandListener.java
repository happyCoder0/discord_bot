package listeners;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class HelpCommandListener extends ListenerAdapter {
    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if(!event.getName().equalsIgnoreCase("help")) return;

        String content = "";
        try {
            content = Files.readAllLines(Paths.get("src/helpMessage.txt"))
                    .stream()
                    .reduce("", (a, b) -> a + "\n" + b + "\n");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        event.reply(content).queue();
    }
}
