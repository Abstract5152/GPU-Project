
public class TimeAllowance {
	private int allocated, used, reserved;

	public TimeAllowance(int allocated, int used, int reserved) {
		this.allocated = allocated;
		this.used = used;
		this.reserved = reserved;
	}

	public int getAllocated() {
		return allocated;
	}

	public int getUsed() {
		return used;
	}

	public int getReserved() {
		return reserved;
	}

	public int getAvailable() {
		return allocated - used - reserved;
	}

	public void setAllocated(int minutes) {
		allocated = minutes;
	}

	public void addTime(int minutes) {
		allocated = allocated + minutes;
	}

	public void reserve(int minutes) {
		reserved = reserved + minutes;
	}

	public void release(int minutes) {
		reserved = reserved - minutes;
	}

	public void charge(int minutes) {
		used = used + minutes;
	}
}