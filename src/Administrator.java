
public class Administrator extends User {
	public Administrator() {
		super("admin", "Administrator", "gpu2026");
	}

	public String getRole() {
		return "Administrator";
	}

	public String toFileLine() {
		return getId() + "|" + getName();
	}
}
