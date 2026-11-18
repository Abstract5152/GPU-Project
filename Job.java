public class Job {
	private String jobId, studentId, title, applicationName, description, taskSize, status, cancelledBy, cancelReason;
	private int workloadUnits, estimatedDuration, submissionTime, assignedGpuId, startTime, finishTime, cancelTime, charged;
	private int predictedStart, queuePosition;

	public Job(String jobId, String studentId, String title, String applicationName, String description,
			String taskSize, int submissionTime) {
		this.jobId = jobId;
		this.studentId = studentId;
		this.title = title;
		this.applicationName = applicationName;
		this.description = description;
		this.taskSize = taskSize;
		this.submissionTime = submissionTime;
		workloadUnits = unitsFor(taskSize);
		estimatedDuration = workloadUnits;
		status = "Queued";
		assignedGpuId = 0;
		startTime = -1;
		finishTime = -1;
		cancelTime = -1;
		cancelledBy = "";
		cancelReason = "";
		charged = 0;
	}

	public Job(String[] f) {
		jobId = f[0];
		studentId = f[1];
		title = f[2];
		applicationName = f[3];
		description = f[4];
		taskSize = f[5];
		workloadUnits = Integer.parseInt(f[6]);
		estimatedDuration = Integer.parseInt(f[7]);
		submissionTime = Integer.parseInt(f[8]);
		status = f[9];
		assignedGpuId = Integer.parseInt(f[10]);
		startTime = Integer.parseInt(f[11]);
		finishTime = Integer.parseInt(f[12]);
		cancelTime = Integer.parseInt(f[13]);
		cancelledBy = f[14];
		cancelReason = f[15];
		charged = Integer.parseInt(f[16]);
	}

	public static int unitsFor(String taskSize) {
		if (taskSize.equals("Small"))
			return 5;
		else if (taskSize.equals("Medium"))
			return 15;
		else if (taskSize.equals("Large"))
			return 30;
		else if (taskSize.equals("Very large"))
			return 60;
		return 0;
	}
}
