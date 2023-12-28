import listeners.AbstractCommandListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;

import java.util.List;

public class Bot extends ListenerAdapter {
    public Bot(String token, List<SlashCommandData> slashCommandData, List<AbstractCommandListener> listeners) {
        JDABuilder builder = JDABuilder
                .createLight(token)
                .enableIntents(GatewayIntent.MESSAGE_CONTENT);

        listeners.forEach(builder::addEventListeners);
        jda = builder.build();

        CommandListUpdateAction commandListUpdateAction = jda.updateCommands();
        slashCommandData.forEach((d) -> commandListUpdateAction.addCommands(d).queue());
    }

    private final JDA jda;

    public void launch() {
        System.out.println("Launched");
        try {
            jda.awaitReady();
            //jda.awaitShutdown();
        } catch (InterruptedException ex) {
            System.out.println(ex.getMessage());
        }
    }
}