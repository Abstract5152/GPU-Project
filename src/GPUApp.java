import java.io.IOException;

public class GPUApp {

    public static AccountManager accounts = new AccountManager();
    public static Scheduler scheduler = new Scheduler(accounts);

    private static final String DATA_FOLDER = "data/";

    public static void main(String[] args) {
        try {
            loadAll();

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    saveAll();
                } catch (IOException e) {
                    System.out.println(
                            "Could not save the program data: "
                            + e.getMessage()
                    );
                }
            }));

            System.out.println("Program data loaded successfully.");

            // Uncomment this after Member 1 adds LoginFrame.java:
            // new LoginFrame();

        } catch (IOException e) {
            System.out.println(
                    "Could not load the program data: "
                    + e.getMessage()
            );
        }
    }

    public static void loadAll() throws IOException {

        accounts.getStudents().clear();

        accounts.getStudents().addAll(
                FileManager.loadStudents(
                        DATA_FOLDER + "students.txt"
                )
        );

        accounts.getRequests().clear();

        accounts.getRequests().addAll(
                FileManager.loadRequests(
                        DATA_FOLDER + "requests.txt"
                )
        );

        accounts.getGrants().clear();

        accounts.getGrants().addAll(
                FileManager.loadStrings(
                        DATA_FOLDER + "grants.txt"
                )
        );

        scheduler = new Scheduler(accounts);

        FileManager.loadScheduler(
                DATA_FOLDER + "scheduler.txt",
                scheduler
        );

        for (Job job : FileManager.loadJobs(
                DATA_FOLDER + "jobs.txt")) {

            scheduler.addLoadedJob(job);
        }
    }

    public static void saveAll() throws IOException {

        FileManager.save(
                DATA_FOLDER + "students.txt",
                accounts.getStudents()
        );

        FileManager.save(
                DATA_FOLDER + "requests.txt",
                accounts.getRequests()
        );

        FileManager.saveStrings(
                DATA_FOLDER + "grants.txt",
                accounts.getGrants()
        );

        FileManager.saveJobs(
                DATA_FOLDER + "jobs.txt",
                scheduler.getJobs()
        );

        FileManager.saveScheduler(
                DATA_FOLDER + "scheduler.txt",
                scheduler
        );
    }
}