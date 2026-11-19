import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class FileManager {

    public static void save(AccountManager am, Scheduler s)
            throws FileNotFoundException {

        PrintWriter out = new PrintWriter("students.txt");

        for (Student st : am.getStudents()) {
            out.println(st.toFileLine());
        }

        out.close();

        out = new PrintWriter("requests.txt");

        for (AccessRequest r : am.getRequests()) {
            out.println(r.toFileLine());
        }

        out.close();

        out = new PrintWriter("grants.txt");

        for (String g : am.getGrants()) {
            out.println(g);
        }

        out.close();

        out = new PrintWriter("jobs.txt");

        for (Job j : s.getJobs()) {
            out.println(j.toFileLine());
        }

        out.close();

        out = new PrintWriter("clock.txt");

        out.println(s.isClockSet());
        out.println(s.getStartMillis());
        out.println(s.getClock());
        out.println(s.getNextJobId());
        out.println(s.getTotalBusy());

        out.close();
    }

    private static ArrayList<String[]> read(String fileName)
            throws FileNotFoundException {

        ArrayList<String[]> lines =
                new ArrayList<String[]>();

        Scanner in = new Scanner(new File(fileName));

        while (in.hasNextLine()) {
            String line = in.nextLine();

            if (!line.trim().equals("")) {
                lines.add(line.split("\\|", -1));
            }
        }

        in.close();
        return lines;
    }

    public static String load(AccountManager am, Scheduler s) {
        String errors = "";

        try {
            for (String[] f : read("students.txt")) {
                am.getStudents().add(
                        new Student(
                                f[0],
                                f[1],
                                f[2],
                                f[3],
                                f[4],
                                new TimeAllowance(
                                        Integer.parseInt(f[5]),
                                        Integer.parseInt(f[6]),
                                        Integer.parseInt(f[7])
                                )
                        )
                );
            }

        } catch (FileNotFoundException e) {

        } catch (Exception e) {
            errors = errors
                    + "students.txt is damaged and could not be fully read.\n";
        }

        try {
            for (String[] f : read("requests.txt")) {
                am.getRequests().add(
                        new AccessRequest(
                                f[0],
                                f[1],
                                f[2],
                                f[3],
                                f[4],
                                f[5]
                        )
                );
            }

        } catch (FileNotFoundException e) {

        } catch (Exception e) {
            errors = errors
                    + "requests.txt is damaged and could not be fully read.\n";
        }

        try {
            for (String[] f : read("grants.txt")) {
                am.getGrants().add(
                        f[0] + "|"
                        + f[1] + "|"
                        + f[2] + "|"
                        + f[3]
                );
            }

        } catch (FileNotFoundException e) {

        } catch (Exception e) {
            errors = errors
                    + "grants.txt is damaged and could not be fully read.\n";
        }

        try {
            Scanner in = new Scanner(new File("clock.txt"));

            s.restore(
                    in.nextBoolean(),
                    in.nextLong(),
                    in.nextInt(),
                    in.nextInt(),
                    in.nextInt()
            );

            in.close();

        } catch (FileNotFoundException e) {

        } catch (Exception e) {
            errors = errors
                    + "clock.txt is damaged and could not be read.\n";
        }

        try {
            for (String[] f : read("jobs.txt")) {
                s.addLoadedJob(new Job(f));
            }

        } catch (FileNotFoundException e) {

        } catch (Exception e) {
            errors = errors
                    + "jobs.txt is damaged and could not be fully read.\n";
        }

        return errors;
    }
}