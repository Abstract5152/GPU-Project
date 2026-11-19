import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class StudentFrame extends JFrame implements ActionListener {

    Student student;
    Scheduler s = GPUApp.scheduler;

    JLabel clockL, balanceL;

    JTextField titleF;
    JTextField appF;
    JTextField descF;
    JTextField searchF;

    JComboBox<String> sizeBox;

    JButton submitB;
    JButton searchB;
    JButton cancelB;
    JButton logoutB;

    DefaultTableModel model;
    JTable table;

    public StudentFrame(Student student) {
        this.student = student;

        setTitle(
                "GPU Utilization Management - Student "
                + student.getId()
        );

        setLayout(new BorderLayout());

        clockL = new JLabel();
        balanceL = new JLabel();
        logoutB = new JButton("Logout");

        JPanel top = new JPanel(new GridLayout(2, 2));

        top.add(clockL);
        top.add(logoutB);
        top.add(balanceL);
        top.add(new JLabel(""));

        titleF = new JTextField();
        appF = new JTextField();
        descF = new JTextField();

        String[] sizes = {
                "Small",
                "Medium",
                "Large",
                "Very large"
        };

        sizeBox = new JComboBox<String>(sizes);

        submitB = new JButton("Submit Job");

        JPanel form = new JPanel(new GridLayout(5, 2));

        form.add(new JLabel("Job Title:"));
        form.add(titleF);

        form.add(new JLabel("Application Name:"));
        form.add(appF);

        form.add(new JLabel("Description:"));
        form.add(descF);

        form.add(new JLabel(
                "Task Size (5 / 15 / 30 / 60 min):"
        ));

        form.add(sizeBox);

        form.add(new JLabel(""));
        form.add(submitB);

        String[] columns = {
                "Job ID",
                "Title",
                "Application",
                "Size",
                "Status",
                "GPU",
                "Queue Pos.",
                "Predicted Start",
                "Predicted Finish",
                "Remaining Wait",
                "Result"
        };

        model = new DefaultTableModel(columns, 0);

        table = new JTable(model);
        table.setDefaultEditor(Object.class, null);

        searchF = new JTextField(15);
        searchB = new JButton("Search");
        cancelB = new JButton("Cancel Selected Job");

        JPanel bottom = new JPanel();

        bottom.add(new JLabel(
                "Search by job ID or title:"
        ));

        bottom.add(searchF);
        bottom.add(searchB);
        bottom.add(cancelB);

        JPanel north = new JPanel(new BorderLayout());

        north.add(top, BorderLayout.NORTH);
        north.add(form, BorderLayout.CENTER);

        add(north, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);

        submitB.addActionListener(this);
        searchB.addActionListener(this);
        cancelB.addActionListener(this);
        logoutB.addActionListener(this);

        refresh();

        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1150, 600);
    }

    public void refresh() {
        TimeAllowance a = student.getAllowance();

        clockL.setText(
                "  Simulated time: "
                + s.formatTime(s.getClock())
        );

        balanceL.setText(
                "  Allocated: " + a.getAllocated()
                + " min | Used: " + a.getUsed()
                + " min | Reserved: " + a.getReserved()
                + " min | Available: " + a.getAvailable()
                + " min"
        );

        s.predict();

        model.setRowCount(0);

        String key =
                searchF.getText().trim().toLowerCase();

        for (Job j : s.getJobsOf(student.getId())) {

            if (j.getJobId().toLowerCase().contains(key)
                    || j.getTitle().toLowerCase().contains(key)) {

                Object[] row = {
                        j.getJobId(),
                        j.getTitle(),
                        j.getApplicationName(),
                        j.getTaskSize(),
                        j.getStatus(),
                        JobView.gpu(j),
                        JobView.position(j),
                        JobView.start(j, s),
                        JobView.finish(j, s),
                        JobView.wait(j, s),
                        JobView.result(j, s)
                };

                model.addRow(row);
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {

            if (e.getSource() == submitB) {

                String size =
                        (String) sizeBox.getSelectedItem();

                int answer =
                        JOptionPane.showConfirmDialog(
                                null,
                                "A " + size
                                + " job takes "
                                + Job.unitsFor(size)
                                + " GPU minutes. "
                                + "Confirm the submission?",
                                "Confirm Job",
                                JOptionPane.YES_NO_OPTION
                        );

                if (answer == JOptionPane.YES_OPTION) {

                    Job j = s.submit(
                            student,
                            titleF.getText().trim(),
                            appF.getText().trim(),
                            descF.getText().trim(),
                            size
                    );

                    GPUApp.save();

                    JOptionPane.showMessageDialog(
                            null,
                            "Job " + j.getJobId()
                            + " accepted at "
                            + s.formatTime(
                                    j.getSubmissionTime()
                            )
                            + ". Duration: "
                            + j.getEstimatedDuration()
                            + " minutes."
                    );

                    titleF.setText("");
                    appF.setText("");
                    descF.setText("");
                }

            } else if (e.getSource() == cancelB) {

                int row = table.getSelectedRow();

                if (row == -1) {
                    throw new IllegalArgumentException(
                            "Please select a job "
                            + "in the table first."
                    );
                }

                String jobId =
                        (String) model.getValueAt(row, 0);

                String reason =
                        JOptionPane.showInputDialog(
                                "Reason for cancelling "
                                + jobId + ":"
                        );

                if (reason != null) {

                    s.cancel(
                            jobId,
                            student,
                            reason
                    );

                    GPUApp.save();

                    JOptionPane.showMessageDialog(
                            null,
                            "Job " + jobId
                            + " was cancelled."
                    );
                }

            } else if (e.getSource() == logoutB) {

                GPUApp.save();

                dispose();

                new LoginFrame();

                return;
            }

        } catch (IllegalArgumentException ex) {

            JOptionPane.showMessageDialog(
                    null,
                    ex.getMessage()
            );
        }

        refresh();
    }
}