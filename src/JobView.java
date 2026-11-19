public class JobView {
	public static String gpu(Job j) {
		if (j.getAssignedGpuId() == 0)
			return "-";
		return "GPU" + j.getAssignedGpuId();
	}

	public static String position(Job j) {
		if (j.getStatus().equals("Queued"))
			return "" + j.getQueuePosition();
		return "-";
	}

	public static String start(Job j, Scheduler s) {
		if (j.getStatus().equals("Queued"))
			return s.formatTime(j.getPredictedStart());
		return s.formatTime(j.getStartTime());
	}

	public static String finish(Job j, Scheduler s) {
		if (j.getStatus().equals("Queued"))
			return s.formatTime(j.getPredictedStart() + j.getEstimatedDuration());
		if (j.getStatus().equals("Running"))
			return s.formatTime(j.getStartTime() + j.getEstimatedDuration());
		return s.formatTime(j.getFinishTime());
	}

	public static String wait(Job j, Scheduler s) {
		if (j.getStatus().equals("Queued"))
			return (j.getPredictedStart() - s.getClock()) + " min";
		return "-";
	}

	public static String result(Job j, Scheduler s) {
		if (j.getStatus().equals("Completed"))
			return "Completed, ran " + j.getRuntime(s.getClock()) + " min, charged " + j.getCharged() + " min";
		if (j.getStatus().equals("Cancelled"))
			return "Cancelled by " + j.getCancelledBy() + " at " + s.formatTime(j.getCancelTime()) + " ("
					+ j.getCancelReason() + "), charged " + j.getCharged() + " min";
		return "-";
	}
}
