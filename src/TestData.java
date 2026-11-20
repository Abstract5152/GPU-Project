import java.text.SimpleDateFormat;

public class TestData {

    static AccountManager am = new AccountManager();
    static Scheduler s = new Scheduler(am);
    static Administrator admin = new Administrator();

    static void run(int minutes) {
        for (int i = 0; i < minutes; i++) {
            s.advanceMinute();
        }
    }

    static void submit(
            String id,
            String title,
            String app,
            String size
    ) {
        s.submit(
                am.findStudent(id),
                title,
                app,
                title + " using " + app,
                size
        );
    }

    public static void main(String[] args) throws Exception {

        am.register(
                "100000001",
                "Ahmed Ali",
                "ahmed@ku.ac.ae",
                "0501111111",
                "pass1"
        );

        am.register(
                "100000002",
                "Sara Salim",
                "sara@ku.ac.ae",
                "0502222222",
                "pass2"
        );

        am.register(
                "100000003",
                "Amna Rashid",
                "amna@ku.ac.ae",
                "0503333333",
                "pass3"
        );

        am.register(
                "100000004",
                "Basim Humaid",
                "basim@ku.ac.ae",
                "0504444444",
                "pass4"
        );

        am.register(
                "100000005",
                "Mona Saeed",
                "mona@ku.ac.ae",
                "0505555555",
                "pass5"
        );

        am.register(
                "100000006",
                "Khalid Omar",
                "khalid@ku.ac.ae",
                "0506666666",
                "pass6"
        );

        am.register(
                "100000007",
                "Noor Hassan",
                "noor@ku.ac.ae",
                "0507777777",
                "pass7"
        );

        am.register(
                "100000008",
                "Hamad Yousef",
                "hamad@ku.ac.ae",
                "0508888888",
                "pass8"
        );

        am.approve("100000001", 100);
        am.approve("100000002", 100);
        am.approve("100000003", 60);
        am.approve("100000004", 100);
        am.approve("100000005", 100);

        am.reject("100000006");

        s.setClock(
                new SimpleDateFormat(
                        "dd/MM/yyyy HH:mm"
                ).parse("23/11/2026 08:00")
        );

        am.grantExtra(
                "100000004",
                30,
                "Final year project deadline",
                s.formatTime(s.getClock())
        );

        submit(
                "100000001",
                "CNN training",
                "PyTorch",
                "Medium"
        );

        submit(
                "100000002",
                "Data cleaning",
                "Pandas",
                "Small"
        );

        submit(
                "100000003",
                "Model training",
                "TensorFlow",
                "Large"
        );

        submit(
                "100000004",
                "Protein folding",
                "AlphaFold",
                "Very large"
        );

        submit(
                "100000005",
                "Image processing",
                "OpenCV",
                "Small"
        );

        submit(
                "100000001",
                "Hyperparameter search",
                "PyTorch",
                "Small"
        );

        run(5);

        submit(
                "100000002",
                "Video rendering",
                "Blender",
                "Large"
        );

        s.cancel(
                "J107",
                am.findStudent("100000002"),
                "Submitted by mistake"
        );

        run(3);

        s.cancel(
                "J104",
                admin,
                "GPU maintenance needed"
        );

        submit(
                "100000005",
                "Speech model",
                "Whisper",
                "Medium"
        );

        run(7);

        submit(
                "100000003",
                "Model evaluation",
                "TensorFlow",
                "Large"
        );

        submit(
                "100000004",
                "Genome analysis",
                "Python",
                "Medium"
        );

        run(4);

        s.cancel(
                "J110",
                am.findStudent("100000004"),
                "Wrong input data"
        );

        submit(
                "100000001",
                "Text generation",
                "Transformers",
                "Large"
        );

        submit(
                "100000002",
                "Chart rendering",
                "Matplotlib",
                "Small"
        );

        run(10);

        submit(
                "100000005",
                "Object detection",
                "YOLO",
                "Medium"
        );

        submit(
                "100000004",
                "Climate model",
                "Fortran",
                "Large"
        );

        submit(
                "100000001",
                "Translation model",
                "Transformers",
                "Small"
        );

        submit(
                "100000002",
                "Recommender system",
                "Spark",
                "Medium"
        );

        run(6);

        submit(
                "100000005",
                "Face recognition",
                "OpenCV",
                "Small"
        );

        submit(
                "100000002",
                "Sentiment analysis",
                "Scikit-learn",
                "Small"
        );

        submit(
                "100000004",
                "Weather forecast",
                "Python",
                "Medium"
        );

        FileManager.save(am, s);

        System.out.println(
                "Sample data saved. Clock: "
                + s.formatTime(s.getClock())
                + ", jobs: "
                + s.getJobs().size()
        );
    }
}