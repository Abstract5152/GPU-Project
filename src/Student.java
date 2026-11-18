public class Student extends User {
	private String email, phone;
	private TimeAllowance allowance;

	public Student(String id, String name, String email, String phone, String password, TimeAllowance allowance) {
		super(id, name, password);
		this.email = email;
		this.phone = phone;
		this.allowance = allowance;
	}

	public String getEmail() {
		return email;
	}

	public String getPhone() {
		return phone;
	}

	public TimeAllowance getAllowance() {
		return allowance;
	}

	public String getRole() {
		return "Student";
	}

	public String toFileLine() {
		return getId() + "|" + getName() + "|" + email + "|" + phone + "|" + getPassword() + "|"
				+ allowance.getAllocated() + "|" + allowance.getUsed() + "|" + allowance.getReserved();
	}
}