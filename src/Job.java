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

	// ---------------- state changes (only these methods change the status) ----------------

	// Queued -> Running
	public void start(int gpuId, int clock) {
		if (!status.equals("Queued"))
			throw new IllegalStateException("Only a queued job can start (job " + jobId + " is " + status + ").");
		status = "Running";
		assignedGpuId = gpuId;
		startTime = clock;
	}

	// Running -> Completed; the full runtime is charged
	public void complete(int clock) {
		if (!status.equals("Running"))
			throw new IllegalStateException("Only a running job can complete (job " + jobId + " is " + status + ").");
		status = "Completed";
		finishTime = clock;
		charged = estimatedDuration;
	}

	// Queued or Running -> Cancelled; runtime is 0 for a queued job
	public void cancel(int clock, String by, String reason, int runtime) {
		if (!status.equals("Queued") && !status.equals("Running"))
			throw new IllegalStateException("Only a queued or running job can be cancelled (job " + jobId + " is " + status + ").");
		if (status.equals("Running"))
			finishTime = clock;
		status = "Cancelled";
		cancelTime = clock;
		cancelledBy = by;
		cancelReason = reason;
		charged = runtime;
	}

	// set by Scheduler.predict(); these two values are not saved to file
	public void setPrediction(int predictedStart, int queuePosition) {
		this.predictedStart = predictedStart;
		this.queuePosition = queuePosition;
	}

	// ---------------- values calculated from the records and the clock ----------------

	// minutes left on a running job (full duration if queued, 0 if finished)
	public int getRemainingRuntime(int clock) {
		if (status.equals("Running"))
			return Math.max(0, estimatedDuration - (clock - startTime));
		if (status.equals("Queued"))
			return estimatedDuration;
		return 0;
	}

	// when a running job should finish (-1 if it never started)
	public int getExpectedFinish() {
		if (startTime < 0)
			return -1;
		return startTime + estimatedDuration;
	}

	public int getPredictedFinish() {
		return predictedStart + estimatedDuration;
	}

	// remaining wait of a queued job = predicted start - current time
	public int getRemainingWait(int clock) {
		if (!status.equals("Queued"))
			return 0;
		return Math.max(0, predictedStart - clock);
	}

	// total time spent waiting in the queue
	public int getTotalWait(int clock) {
		if (startTime >= 0)
			return startTime - submissionTime;
		if (status.equals("Cancelled"))
			return cancelTime - submissionTime;
		return clock - submissionTime;
	}

	// actual minutes spent on a GPU (waiting excluded)
	public int getRuntime(int clock) {
		if (startTime < 0)
			return 0;
		if (status.equals("Running"))
			return clock - startTime;
		return finishTime - startTime;
	}

	// ---------------- getters (no setters for status, GPU or times) ----------------

	public String getJobId() { return jobId; }
	public String getStudentId() { return studentId; }
	public String getTitle() { return title; }
	public String getApplicationName() { return applicationName; }
	public String getDescription() { return description; }
	public String getTaskSize() { return taskSize; }
	public String getStatus() { return status; }
	public String getCancelledBy() { return cancelledBy; }
	public String getCancelReason() { return cancelReason; }
	public int getWorkloadUnits() { return workloadUnits; }
	public int getEstimatedDuration() { return estimatedDuration; }
	public int getSubmissionTime() { return submissionTime; }
	public int getAssignedGpuId() { return assignedGpuId; }
	public int getStartTime() { return startTime; }
	public int getFinishTime() { return finishTime; }
	public int getCancelTime() { return cancelTime; }
	public int getCharged() { return charged; }
	public int getPredictedStart() { return predictedStart; }
	public int getQueuePosition() { return queuePosition; }

	// ---------------- file storage ----------------

	// 17 fields separated by '|', in the same order as the Job(String[] f) constructor
	public String toFileLine() {
		return jobId + "|" + studentId + "|" + title + "|" + applicationName + "|" + description + "|" + taskSize + "|"
				+ workloadUnits + "|" + estimatedDuration + "|" + submissionTime + "|" + status + "|" + assignedGpuId
				+ "|" + startTime + "|" + finishTime + "|" + cancelTime + "|" + cancelledBy + "|" + cancelReason + "|"
				+ charged;
	}
}