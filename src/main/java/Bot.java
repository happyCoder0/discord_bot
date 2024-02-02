import listeners.AbstractSlashCommandListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;

import java.util.List;
import java.util.stream.Collectors;

public class Bot {
    public Bot(String token, List<ListenerAdapter> listeners) {
        JDABuilder builder = JDABuilder
                .createLight(token, GatewayIntent.GUILD_MESSAGES, GatewayIntent.MESSAGE_CONTENT);
        listeners.forEach(builder::addEventListeners);

        jda = builder.build();


        CommandListUpdateAction commandListUpdateAction = jda.updateCommands();

        List<AbstractSlashCommandListener> slashCommandListeners = listeners
                .stream()
                .filter(l -> l instanceof AbstractSlashCommandListener)
                .map(l -> (AbstractSlashCommandListener) l)
                .collect(Collectors.toList());

        List<SlashCommandData> slashCommandData = slashCommandListeners.stream().map(l -> Commands
                        .slash(l.getName(), l.getDescription())
                        .setGuildOnly(l.getGuildOnly())
                        .addOptions(l.getOptionDataList()))
                .collect(Collectors.toList());

        commandListUpdateAction.addCommands(slashCommandData).queue();
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