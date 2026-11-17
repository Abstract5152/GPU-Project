
public abstract class User implements Storable {
	private String id, name, password;

	public User(String id, String name, String password) {
		this.id = id;
		this.name = name;
		this.password = password;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getPassword() {
		return password;
	}

	public boolean checkPassword(String password) {
		return this.password.equals(password);
	}

	public abstract String getRole();
}
