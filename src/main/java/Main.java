import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.reflections.Reflections;
import tasks.WarningsDecreaseTask;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.*;

public class Main {
    public static void main(String[] args) {
        Properties properties = new Properties();
        try {
            properties.load(Files.newInputStream(Paths.get("src/app.properties")));
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
        }

        String token = properties.getProperty("token");

        Reflections reflections = new Reflections("listeners");
        Set<Class<? extends ListenerAdapter>> listenerClasses =
                reflections.getSubTypesOf(ListenerAdapter.class);

        ArrayList<ListenerAdapter> listeners = new ArrayList<>();

        listenerClasses.forEach(
            aClass -> {
                try {
                    System.out.println(aClass.getName());
                    if (!Modifier.isAbstract(aClass.getModifiers()))
                        listeners.add(aClass.getConstructor().newInstance());
                } catch (IllegalAccessException | InstantiationException | NoSuchMethodException |
                         InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            }
        );

        Bot bot = new Bot(token, listeners);
        bot.launch();

        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        WarningsDecreaseTask task = new WarningsDecreaseTask();

        executor.scheduleAtFixedRate(task, 0, 1, TimeUnit.DAYS);
    }
}