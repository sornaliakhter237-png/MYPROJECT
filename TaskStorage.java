package dayworkschedule;

import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

public class TaskStorage {
    private static final String FILENAME = System.getProperty("user.home") + File.separator + "tasks.dat";

    public static List<Task> load() {
        Path p = Paths.get(FILENAME);
        if (!Files.exists(p)) {
            return new ArrayList<>();
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILENAME))) {
            Object obj = ois.readObject();
            if (obj instanceof List) {
                return (List<Task>) obj;
            } else {
                return new ArrayList<>();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public static void save(List<Task> tasks) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILENAME))) {
            oos.writeObject(tasks);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
