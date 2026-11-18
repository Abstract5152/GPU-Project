import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class AdminFrame extends JFrame implements ActionListener {
	Administrator admin;
	AccountManager am = GPUApp.accounts;
	Scheduler s = GPUApp.scheduler;
	JLabel clockL, summaryL;
	JSpinner clockSpinner;
	JButton setClockB, advanceB, logoutB, cancelB, approveB, rejectB, searchB, grantB;
	JTextField searchF, minutesF, reasonF;
	JTextArea studentA;
	DefaultTableModel gpuModel, jobModel, doneModel, balanceModel, requestModel;
	JTable jobTable, requestTable;

	public AdminFrame(Administrator admin) {
		this.admin = admin;
		setTitle("GPU Utilization Management - Administrator");
		setLayout(new BorderLayout());

		clockL = new JLabel();
		clockSpinner = new JSpinner(new SpinnerDateModel());
		clockSpinner.setEditor(new JSpinner.DateEditor(clockSpinner, "dd/MM/yyyy HH:mm"));
		setClockB = new JButton("Set Clock");
		advanceB = new JButton("Advance Minute (+1)");
		logoutB = new JButton("Logout");
		JPanel top = new JPanel();
		top.add(clockL);
		top.add(new JLabel("   Start date and time:"));
		top.add(clockSpinner);
		top.add(setClockB);
		top.add(advanceB);
		top.add(logoutB);
		add(top, BorderLayout.NORTH);

		String[] gpuCols = { "GPU", "Availability", "Current Job", "Remaining Runtime", "Expected Finish" };
		String[] jobCols = { "Job", "Student", "Application", "Size", "Status", "GPU", "Queue Pos.", "Predicted Start",
				"Predicted Finish", "Remaining Wait", "Cancellation" };
		String[] doneCols = { "Job", "Student", "GPU", "Submitted", "Start", "Finish", "Runtime", "Total Queue Wait",
				"Charged" };
		String[] balanceCols = { "Student", "Name", "Allocated", "Used", "Reserved", "Available" };
		String[] requestCols = { "University ID", "Name", "Email", "Phone", "Status" };
		gpuModel = new DefaultTableModel(gpuCols, 0);
		jobModel = new DefaultTableModel(jobCols, 0);
		doneModel = new DefaultTableModel(doneCols, 0);
		balanceModel = new DefaultTableModel(balanceCols, 0);
		requestModel = new DefaultTableModel(requestCols, 0);
		JTable gpuTable = new JTable(gpuModel);
		jobTable = new JTable(jobModel);
		JTable doneTable = new JTable(doneModel);
		JTable balanceTable = new JTable(balanceModel);
		requestTable = new JTable(requestModel);
		gpuTable.setDefaultEditor(Object.class, null);
		jobTable.setDefaultEditor(Object.class, null);
		doneTable.setDefaultEditor(Object.class, null);
		balanceTable.setDefaultEditor(Object.class, null);
		requestTable.setDefaultEditor(Object.class, null);

		summaryL = new JLabel();
		cancelB = new JButton("Cancel Selected Job");
		JPanel upper = new JPanel(new GridLayout(1, 2));
		upper.add(titled(gpuTable, "GPU status"));
		upper.add(titled(balanceTable, "Student allowances"));
		JPanel tables = new JPanel(new GridLayout(3, 1));
		tables.add(upper);
		tables.add(titled(jobTable, "All jobs (select one to cancel it)"));
		tables.add(titled(doneTable, "Completed jobs"));
		JPanel dashboard = new JPanel(new BorderLayout());
		dashboard.add(summaryL, BorderLayout.NORTH);
		dashboard.add(tables, BorderLayout.CENTER);
		dashboard.add(cancelB, BorderLayout.SOUTH);

		approveB = new JButton("Approve Selected");
		rejectB = new JButton("Reject Selected");
		JPanel requestButtons = new JPanel();
		requestButtons.add(approveB);
		requestButtons.add(rejectB);
		JPanel requests = new JPanel(new BorderLayout());
		requests.add(new JScrollPane(requestTable), BorderLayout.CENTER);
		requests.add(requestButtons, BorderLayout.SOUTH);

		searchF = new JTextField(12);
		searchB = new JButton("Search");
		minutesF = new JTextField(5);
		reasonF = new JTextField(20);
		grantB = new JButton("Grant Extra Time");
		studentA = new JTextArea();
		studentA.setEditable(false);
		JPanel searchPanel = new JPanel();
		searchPanel.add(new JLabel("University ID:"));
		searchPanel.add(searchF);
		searchPanel.add(searchB);
		JPanel grantPanel = new JPanel();
		grantPanel.add(new JLabel("Extra minutes:"));
		grantPanel.add(minutesF);
		grantPanel.add(new JLabel("Reason:"));
		grantPanel.add(reasonF);
		grantPanel.add(grantB);
		JPanel students = new JPanel(new BorderLayout());
		students.add(searchPanel, BorderLayout.NORTH);
		students.add(new JScrollPane(studentA), BorderLayout.CENTER);
		students.add(grantPanel, BorderLayout.SOUTH);

		JTabbedPane tabs = new JTabbedPane();
		tabs.add("Dashboard", dashboard);
		tabs.add("Registration Requests", requests);
		tabs.add("Students", students);
		add(tabs, BorderLayout.CENTER);

		setClockB.addActionListener(this);
		advanceB.addActionListener(this);
		logoutB.addActionListener(this);
		cancelB.addActionListener(this);
		approveB.addActionListener(this);
		rejectB.addActionListener(this);
		searchB.addActionListener(this);
		grantB.addActionListener(this);

		refresh();
		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(1300, 800);
	}

	private JScrollPane titled(JTable table, String title) {
		JScrollPane pane = new JScrollPane(table);
		pane.setBorder(BorderFactory.createTitledBorder(title));
		return pane;
	}

	public void refresh() {
		clockL.setText("Simulated time: " + s.formatTime(s.getClock()));
		clockSpinner.setEnabled(!s.isClockSet());
		setClockB.setEnabled(!s.isClockSet());
		s.predict();
		summaryL.setText("   Queued: " + s.count("Queued") + "    Running: " + s.count("Running") + "    Completed: "
				+ s.count("Completed") + "    Cancelled: " + s.count("Cancelled") + "    Busy GPUs: " + s.busyGpus()
				+ " / 4    Utilization: " + s.getUtilization());

		gpuModel.setRowCount(0);
		for (GPU g : s.getGpus()) {
			if (g.isBusy()) {
				Object[] row = { "GPU" + g.getId(), "Busy", g.getCurrentJob().getJobId(),
						(g.getFreeTime() - s.getClock()) + " min", s.formatTime(g.getFreeTime()) };
				gpuModel.addRow(row);
			} else {
				Object[] row = { "GPU" + g.getId(), "Free", "-", "-", "-" };
				gpuModel.addRow(row);
			}
		}

		jobModel.setRowCount(0);
		doneModel.setRowCount(0);
		for (Job j : s.getJobs()) {
			String cancellation = "-";
			if (j.getStatus().equals("Cancelled"))
				cancellation = JobView.result(j, s);
			Object[] row = { j.getJobId(), j.getStudentId(), j.getApplicationName(), j.getTaskSize(), j.getStatus(),
					JobView.gpu(j), JobView.position(j), JobView.start(j, s), JobView.finish(j, s), JobView.wait(j, s),
					cancellation };
			jobModel.addRow(row);
			if (j.getStatus().equals("Completed")) {
				Object[] done = { j.getJobId(), j.getStudentId(), JobView.gpu(j), s.formatTime(j.getSubmissionTime()),
						s.formatTime(j.getStartTime()), s.formatTime(j.getFinishTime()), j.getRuntime(s.getClock()) + " min",
						(j.getStartTime() - j.getSubmissionTime()) + " min", j.getCharged() + " min" };
				doneModel.addRow(done);
			}
		}

		balanceModel.setRowCount(0);
		for (Student st : am.getStudents()) {
			TimeAllowance a = st.getAllowance();
			Object[] row = { st.getId(), st.getName(), a.getAllocated(), a.getUsed(), a.getReserved(), a.getAvailable() };
			balanceModel.addRow(row);
		}

		requestModel.setRowCount(0);
		for (AccessRequest r : am.getRequests()) {
			Object[] row = { r.getId(), r.getName(), r.getEmail(), r.getPhone(), r.getStatus() };
			requestModel.addRow(row);
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// the button actions are added on Friday
	}
}