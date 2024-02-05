package listeners;

import db.UserDbHelper;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;

public class GetWarningsCommandListener extends AbstractSlashCommandListener {
    @Override
    public String getName() {
        return "mywarns";
    }

    @Override
    public String getDescription() {
        return "Prints quantity of your warnings";
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        super.onSlashCommandInteraction(event);

        if (!event.getName().equals(getName())) return;

        UserDbHelper helper = UserDbHelper.getInstance();
        Guild guild = event.getGuild();
        User user = event.getMember().getUser();

        event
                .reply("Your warnings: " + helper.getWarnings(guild.getId(), user.getId()))
                .queue();
    }
}
