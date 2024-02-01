import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;

import java.util.List;

public class Bot {
    public Bot(String token, List<ListenerAdapter> listeners) {
        JDABuilder builder = JDABuilder
                .createLight(token, GatewayIntent.GUILD_MESSAGES, GatewayIntent.MESSAGE_CONTENT);
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
                .addCommands(Commands
                        .slash("aic", "Adds chat with provided id to ignored list")
                        .setGuildOnly(true)
                        .addOption(OptionType.STRING, "chat_id", "Id of chat to ignore", true))
                .addCommands(Commands
                        .slash("help", "Prints help message")
                        .setGuildOnly(true))
                .addCommands(Commands
                        .slash("gic", "Gets list of all ignored chats on this server")
                        .setGuildOnly(true))
                .addCommands(Commands
                        .slash("dic", "Deletes chat from ignored list")
                        .setGuildOnly(true)
                        .addOption(OptionType.STRING, "chat_id", "Id of chat to remove", true))
                .queue();
    }

    private final JDA jda;

    public void launch() {
        System.out.println("Launched");

        CommandListUpdateAction commandListUpdateAction = jda.updateCommands();
        try {
            jda.awaitReady();
        } catch (InterruptedException ex) {
            System.out.println(ex.getMessage());
        }
    }
}