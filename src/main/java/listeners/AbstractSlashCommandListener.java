package listeners;

import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.Collections;
import java.util.List;

public abstract class AbstractSlashCommandListener extends ListenerAdapter {
    public abstract String getName();
    public abstract String getDescription();
    public boolean getGuildOnly() {
        return true;
    }

    public List<OptionData> getOptionDataList() {
        return Collections.emptyList();
    }
}
