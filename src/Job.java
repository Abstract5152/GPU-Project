public class Job implements Storable {
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

	public void start(int gpuId, int time) {
		assignedGpuId = gpuId;
		startTime = time;
		status = "Running";
	}

	public void complete(int time) {
		finishTime = time;
		charged = estimatedDuration;
		status = "Completed";
	}

	public void cancel(int time, String by, String reason, int charge) {
		if (status.equals("Running"))
			finishTime = time;
		cancelTime = time;
		cancelledBy = by;
		cancelReason = reason;
		charged = charge;
		status = "Cancelled";
	}

	public void setPrediction(int predictedStart, int queuePosition) {
		this.predictedStart = predictedStart;
		this.queuePosition = queuePosition;
	}

	public int getRuntime(int clock) {
		if (status.equals("Running"))
			return clock - startTime;
		if (finishTime >= 0)
			return finishTime - startTime;
		return 0;
	}

	public String getJobId() {
		return jobId;
	}

	public String getStudentId() {
		return studentId;
	}

	public String getTitle() {
		return title;
	}

	public String getApplicationName() {
		return applicationName;
	}

	public String getDescription() {
		return description;
	}

	public String getTaskSize() {
		return taskSize;
	}

	public int getWorkloadUnits() {
		return workloadUnits;
	}

	public int getEstimatedDuration() {
		return estimatedDuration;
	}

	public int getSubmissionTime() {
		return submissionTime;
	}

	public String getStatus() {
		return status;
	}

	public int getAssignedGpuId() {
		return assignedGpuId;
	}

	public int getStartTime() {
		return startTime;
	}

	public int getFinishTime() {
		return finishTime;
	}

	public int getCancelTime() {
		return cancelTime;
	}

	public String getCancelledBy() {
		return cancelledBy;
	}

	public String getCancelReason() {
		return cancelReason;
	}

	public int getCharged() {
		return charged;
	}

	public int getPredictedStart() {
		return predictedStart;
	}

	public int getQueuePosition() {
		return queuePosition;
	}

	public String toFileLine() {
		return jobId + "|" + studentId + "|" + title + "|" + applicationName + "|" + description + "|" + taskSize
				+ "|" + workloadUnits + "|" + estimatedDuration + "|" + submissionTime + "|" + status + "|"
				+ assignedGpuId + "|" + startTime + "|" + finishTime + "|" + cancelTime + "|" + cancelledBy + "|"
				+ cancelReason + "|" + charged;
	}
}
