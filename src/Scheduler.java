import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Scheduler {
	private ArrayList<GPU> gpus = new ArrayList<GPU>();
	private ArrayList<Job> jobs = new ArrayList<Job>();
	private AccountManager accounts;
	private boolean clockSet;
	private long startMillis;
	private int clock, nextJobId, totalBusy;

	public Scheduler(AccountManager accounts) {
		this.accounts = accounts;
		for (int i = 1; i <= 4; i++)
			gpus.add(new GPU(i));
		nextJobId = 101;
	}

	public void setClock(Date start) {
		if (clockSet)
			throw new IllegalArgumentException("The clock is already set. Use Advance Minute from now on.");
		startMillis = start.getTime();
		clockSet = true;
		clock = 0;
	}

	public void restore(boolean clockSet, long startMillis, int clock, int nextJobId, int totalBusy) {
		this.clockSet = clockSet;
		this.startMillis = startMillis;
		this.clock = clock;
		this.nextJobId = nextJobId;
		this.totalBusy = totalBusy;
	}

	public void addLoadedJob(Job j) {
		jobs.add(j);
		if (j.getStatus().equals("Running"))
			gpus.get(j.getAssignedGpuId() - 1).setCurrentJob(j);
	}

	public String formatTime(int minute) {
		if (!clockSet || minute < 0)
			return "-";
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		return format.format(new Date(startMillis + minute * 60000L));
	}

	public Job submit(Student s, String title, String app, String description, String taskSize) {
		if (!clockSet)
			throw new IllegalArgumentException("The administrator has not set the simulation clock yet.");
		if (!AccountManager.isText(title) || !AccountManager.isText(app) || !AccountManager.isText(description))
			throw new IllegalArgumentException("Please fill in the title, application name and description (no | character).");
		int units = Job.unitsFor(taskSize);
		if (units == 0)
			throw new IllegalArgumentException("Please choose a task size.");
		TimeAllowance a = s.getAllowance();
		if (a.getAvailable() == 0)
			throw new IllegalArgumentException("You have no available GPU minutes. New jobs are blocked.");
		if (units > a.getAvailable())
			throw new IllegalArgumentException("This job needs " + units + " minutes but you only have "
					+ a.getAvailable() + " minutes available.");
		Job j = new Job("J" + nextJobId, s.getId(), title, app, description, taskSize, clock);
		nextJobId++;
		a.reserve(units);
		jobs.add(j);
		dispatch();
		return j;
	}

	private void dispatch() {
		for (GPU g : gpus) {
			if (!g.isBusy()) {
				Job next = null;
				for (Job j : jobs)
					if (next == null && j.getStatus().equals("Queued"))
						next = j;
				if (next != null) {
					next.start(g.getId(), clock);
					g.setCurrentJob(next);
				}
			}
		}
	}

	public void advanceMinute() {
		if (!clockSet)
			throw new IllegalArgumentException("Set the simulation clock first.");
		clock++;
		for (GPU g : gpus) {
			if (g.isBusy()) {
				totalBusy++;
				Job j = g.getCurrentJob();
				if (clock >= g.getFreeTime()) {
					j.complete(clock);
					TimeAllowance a = accounts.findStudent(j.getStudentId()).getAllowance();
					a.release(j.getEstimatedDuration());
					a.charge(j.getEstimatedDuration());
					g.setCurrentJob(null);
				}
			}
		}
		dispatch();
	}

	public void cancel(String jobId, User by, String reason) {
		Job j = findJob(jobId);
		if (j == null)
			throw new IllegalArgumentException("Job not found.");
		if (by instanceof Student && !j.getStudentId().equals(by.getId()))
			throw new IllegalArgumentException("You can only cancel your own jobs.");
		if (!j.getStatus().equals("Queued") && !j.getStatus().equals("Running"))
			throw new IllegalArgumentException("Only queued or running jobs can be cancelled.");
		if (reason == null || !AccountManager.isText(reason))
			throw new IllegalArgumentException("Please enter a reason for the cancellation.");
		TimeAllowance a = accounts.findStudent(j.getStudentId()).getAllowance();
		if (j.getStatus().equals("Queued")) {
			j.cancel(clock, by.getId(), reason, 0);
			a.release(j.getEstimatedDuration());
		} else {
			int runtime = clock - j.getStartTime();
			gpus.get(j.getAssignedGpuId() - 1).setCurrentJob(null);
			j.cancel(clock, by.getId(), reason, runtime);
			a.release(j.getEstimatedDuration());
			a.charge(runtime);
			dispatch();
		}
	}

	public void predict() {
		int[] free = new int[4];
		for (int i = 0; i < 4; i++) {
			if (gpus.get(i).isBusy())
				free[i] = gpus.get(i).getFreeTime();
			else
				free[i] = clock;
		}
		int position = 1;
		for (Job j : jobs) {
			if (j.getStatus().equals("Queued")) {
				int best = 0;
				for (int i = 1; i < 4; i++)
					if (free[i] < free[best])
						best = i;
				j.setPrediction(free[best], position);
				free[best] = free[best] + j.getEstimatedDuration();
				position++;
			}
		}
	}

	public Job findJob(String jobId) {
		for (Job j : jobs)
			if (j.getJobId().equalsIgnoreCase(jobId))
				return j;
		return null;
	}

	public ArrayList<Job> getJobsOf(String studentId) {
		ArrayList<Job> list = new ArrayList<Job>();
		for (Job j : jobs)
			if (j.getStudentId().equals(studentId))
				list.add(j);
		return list;
	}
	public int count(String status) {
		int n = 0;
		for (Job j : jobs)
			if (j.getStatus().equals(status))
				n++;
		return n;
	}
	public int busyGpus() {
		int n = 0;
		for (GPU g : gpus)
			if (g.isBusy())
				n++;
		return n;
	}

	public String getUtilization() {
		if (clock == 0)
			return "N/A";
		return String.format("%.1f%%", totalBusy * 100.0 / (4 * clock));
	}

	public ArrayList<GPU> getGpus() {
		return gpus;
	}

	public ArrayList<Job> getJobs() {
		return jobs;
	}

	public boolean isClockSet() {
		return clockSet;
	}

	public long getStartMillis() {
		return startMillis;
	}

	public int getClock() {
		return clock;
	}

	public int getNextJobId() {
		return nextJobId;
	}

	public int getTotalBusy() {
		return totalBusy;
	}
}