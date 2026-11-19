import java.io.FileNotFoundException;

import javax.swing.JOptionPane;

public class GPUApp {

    public static AccountManager accounts =
            new AccountManager();

    public static Scheduler scheduler =
            new Scheduler(accounts);

    public static void main(String[] args) {
        String errors =
                FileManager.load(accounts, scheduler);

        if (!errors.equals("")) {
            JOptionPane.showMessageDialog(
                    null,
                    errors,
                    "File error",
                    JOptionPane.ERROR_MESSAGE
            );
        }

        new LoginFrame();
    }

    public static void save() {
        try {
            FileManager.save(accounts, scheduler);

        } catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(
                    null,
                    "Could not save the data files: "
                    + e.getMessage(),
                    "File error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}