public class GPU {
	private int id;
	private Job currentJob;

	public GPU(int id) {
		this.id = id;
		currentJob = null;
	}

	public int getId() {
		return id;
	}

	public Job getCurrentJob() {
		return currentJob;
	}

	public void setCurrentJob(Job currentJob) {
		this.currentJob = currentJob;
	}

	public boolean isBusy() {
		return currentJob != null;
	}

	public int getFreeTime() {
		return currentJob.getStartTime() + currentJob.getEstimatedDuration();
	}
}