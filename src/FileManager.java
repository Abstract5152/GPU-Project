import java.io.*;
import java.util.ArrayList;

public class FileManager {

    public static void save(String fileName,
                            ArrayList<? extends Storable> objects)
                            throws IOException {

        PrintWriter output =
                new PrintWriter(new FileWriter(fileName));

        for (Storable object : objects) {
            output.println(object.toFileLine());
        }

        output.close();
    }

    public static void saveClock(String fileName, int clock)
                                 throws IOException {

        PrintWriter output =
                new PrintWriter(new FileWriter(fileName));

        output.println(clock);
        output.close();
    }
}