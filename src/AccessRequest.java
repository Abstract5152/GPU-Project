public class AccessRequest implements Storable {
	private String id, name, email, phone, password, status;

	public AccessRequest(String id, String name, String email, String phone, String password, String status) {
		this.id = id;
		this.name = name;
		this.email = email;
		this.phone = phone;
		this.password = password;
		this.status = status;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getEmail() {
		return email;
	}

	public String getPhone() {
		return phone;
	}

	public String getPassword() {
		return password;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String toFileLine() {
		return id + "|" + name + "|" + email + "|" + phone + "|" + password + "|" + status;
	}
}