import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class FileManager {

    public static void save(
            String fileName,
            ArrayList<? extends Storable> objects
    ) throws IOException {

        prepareFolder(fileName);

        PrintWriter output =
                new PrintWriter(new FileWriter(fileName));

        for (Storable object : objects) {
            output.println(object.toFileLine());
        }

        output.close();
    }

    public static void saveJobs(
            String fileName,
            ArrayList<Job> jobs
    ) throws IOException {

        prepareFolder(fileName);

        PrintWriter output =
                new PrintWriter(new FileWriter(fileName));

        for (Job job : jobs) {
            output.println(job.toFileLine());
        }

        output.close();
    }

    public static void saveStrings(
            String fileName,
            ArrayList<String> lines
    ) throws IOException {

        prepareFolder(fileName);

        PrintWriter output =
                new PrintWriter(new FileWriter(fileName));

        for (String line : lines) {
            output.println(line);
        }

        output.close();
    }

    public static void saveScheduler(
            String fileName,
            Scheduler scheduler
    ) throws IOException {

        prepareFolder(fileName);

        PrintWriter output =
                new PrintWriter(new FileWriter(fileName));

        output.println(
                scheduler.isClockSet() + "|"
                + scheduler.getStartMillis() + "|"
                + scheduler.getClock() + "|"
                + scheduler.getNextJobId() + "|"
                + scheduler.getTotalBusy()
        );

        output.close();
    }

    public static ArrayList<Student> loadStudents(
            String fileName
    ) throws IOException {

        ArrayList<Student> students =
                new ArrayList<Student>();

        File file = new File(fileName);

        if (!file.exists()) {
            return students;
        }

        Scanner input =
                new Scanner(new FileReader(file));

        int lineNumber = 0;

        while (input.hasNextLine()) {
            String line = input.nextLine();
            lineNumber++;

            if (line.trim().equals("")) {
                continue;
            }

            try {
                String[] f =
                        splitLine(line, 8, fileName, lineNumber);

                TimeAllowance allowance =
                        new TimeAllowance(
                                Integer.parseInt(f[5]),
                                Integer.parseInt(f[6]),
                                Integer.parseInt(f[7])
                        );

                Student student =
                        new Student(
                                f[0],
                                f[1],
                                f[2],
                                f[3],
                                f[4],
                                allowance
                        );

                students.add(student);

            } catch (NumberFormatException e) {
                input.close();
                throw damagedNumber(fileName, lineNumber, e);

            } catch (IOException e) {
                input.close();
                throw e;
            }
        }

        input.close();
        return students;
    }

    public static ArrayList<AccessRequest> loadRequests(
            String fileName
    ) throws IOException {

        ArrayList<AccessRequest> requests =
                new ArrayList<AccessRequest>();

        File file = new File(fileName);

        if (!file.exists()) {
            return requests;
        }

        Scanner input =
                new Scanner(new FileReader(file));

        int lineNumber = 0;

        while (input.hasNextLine()) {
            String line = input.nextLine();
            lineNumber++;

            if (line.trim().equals("")) {
                continue;
            }

            try {
                String[] f =
                        splitLine(line, 6, fileName, lineNumber);

                AccessRequest request =
                        new AccessRequest(
                                f[0],
                                f[1],
                                f[2],
                                f[3],
                                f[4],
                                f[5]
                        );

                requests.add(request);

            } catch (IOException e) {
                input.close();
                throw e;
            }
        }

        input.close();
        return requests;
    }

    public static ArrayList<Job> loadJobs(
            String fileName
    ) throws IOException {

        ArrayList<Job> jobs =
                new ArrayList<Job>();

        File file = new File(fileName);

        if (!file.exists()) {
            return jobs;
        }

        Scanner input =
                new Scanner(new FileReader(file));

        int lineNumber = 0;

        while (input.hasNextLine()) {
            String line = input.nextLine();
            lineNumber++;

            if (line.trim().equals("")) {
                continue;
            }

            try {
                String[] f =
                        splitLine(line, 17, fileName, lineNumber);

                jobs.add(new Job(f));

            } catch (NumberFormatException e) {
                input.close();
                throw damagedNumber(fileName, lineNumber, e);

            } catch (IOException e) {
                input.close();
                throw e;
            }
        }

        input.close();
        return jobs;
    }

    public static ArrayList<String> loadStrings(
            String fileName
    ) throws IOException {

        ArrayList<String> lines =
                new ArrayList<String>();

        File file = new File(fileName);

        if (!file.exists()) {
            return lines;
        }

        Scanner input =
                new Scanner(new FileReader(file));

        while (input.hasNextLine()) {
            String line = input.nextLine();

            if (!line.trim().equals("")) {
                lines.add(line);
            }
        }

        input.close();
        return lines;
    }

    public static boolean loadScheduler(
            String fileName,
            Scheduler scheduler
    ) throws IOException {

        File file = new File(fileName);

        if (!file.exists()) {
            return false;
        }

        Scanner input =
                new Scanner(new FileReader(file));

        if (!input.hasNextLine()) {
            input.close();

            throw new IOException(
                    "Damaged file " + fileName
                    + ": scheduler data is missing."
            );
        }

        String line = input.nextLine();

        try {
            String[] f =
                    splitLine(line, 5, fileName, 1);

            boolean clockSet;

            if (f[0].equals("true")) {
                clockSet = true;

            } else if (f[0].equals("false")) {
                clockSet = false;

            } else {
                throw new IOException(
                        "Damaged file " + fileName
                        + " at line 1: invalid clock value."
                );
            }

            scheduler.restore(
                    clockSet,
                    Long.parseLong(f[1]),
                    Integer.parseInt(f[2]),
                    Integer.parseInt(f[3]),
                    Integer.parseInt(f[4])
            );

        } catch (NumberFormatException e) {
            input.close();
            throw damagedNumber(fileName, 1, e);

        } catch (IOException e) {
            input.close();
            throw e;
        }

        while (input.hasNextLine()) {
            String extraLine = input.nextLine();

            if (!extraLine.trim().equals("")) {
                input.close();

                throw new IOException(
                        "Damaged file " + fileName
                        + ": unexpected extra scheduler data."
                );
            }
        }

        input.close();
        return true;
    }

    private static String[] splitLine(
            String line,
            int expectedFields,
            String fileName,
            int lineNumber
    ) throws IOException {

        String[] fields = line.split("\\|", -1);

        if (fields.length != expectedFields) {
            throw new IOException(
                    "Damaged file " + fileName
                    + " at line " + lineNumber
                    + ": expected " + expectedFields
                    + " fields but found "
                    + fields.length + "."
            );
        }

        return fields;
    }

    private static IOException damagedNumber(
            String fileName,
            int lineNumber,
            NumberFormatException cause
    ) {

        return new IOException(
                "Damaged file " + fileName
                + " at line " + lineNumber
                + ": a number is invalid.",
                cause
        );
    }

    private static void prepareFolder(
            String fileName
    ) throws IOException {

        File file = new File(fileName);
        File folder = file.getParentFile();

        if (folder != null
                && !folder.exists()
                && !folder.mkdirs()) {

            throw new IOException(
                    "Could not create folder: "
                    + folder.getPath()
            );
        }
    }
}