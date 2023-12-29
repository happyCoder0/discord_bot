import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;

import java.util.List;

public class Bot {
    public Bot(String token, List<ListenerAdapter> listeners) {
        JDABuilder builder = JDABuilder
                .createLight(token);
        listeners.forEach(builder::addEventListeners);

        jda = builder.build();

        jda.updateCommands()
                .addCommands(Commands
                        .slash("dbw", "Deletes word from banned words list on this server")
                        .setGuildOnly(true)
                        .addOption(OptionType.STRING, "word", "Word to delete", true))
                .addCommands(Commands
                        .slash("abw", "Adds new banned word for this server")
                        .setGuildOnly(true)
                        .addOption(OptionType.STRING, "word", "Word to ban", true))
                .addCommands(Commands
                        .slash("gbw", "Prints list of banned words on this server")
                        .setGuildOnly(true))
                .addCommands(Commands
                        .slash("hello", "Greets user")
                        .setGuildOnly(true))
                .queue();
    }

    private final JDA jda;

    public void launch() {
        System.out.println("Launched");

        CommandListUpdateAction commandListUpdateAction = jda.updateCommands();
        try {
            jda.awaitReady();
            //jda.awaitShutdown();
        } catch (InterruptedException ex) {
            System.out.println(ex.getMessage());
        }
    }
}