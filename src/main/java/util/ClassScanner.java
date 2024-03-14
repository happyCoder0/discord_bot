package util;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ClassScanner {
    public static List<Class<?>> scanForListenerClasses() throws Exception {
        String path = "src/main/java/listeners";
        File directory = new File(path);

        List<String> names = Arrays.stream(directory.listFiles())
                .map(File::getName)
                .map(name -> name.split("\\.")[0])
                .collect(Collectors.toList());

        return names.stream()
                .filter(name -> !name.equals("AbstractSlashCommandListener"))
                .map(name -> {
                    try {
                        return Class.forName("listeners." + name);
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());
    }
}
