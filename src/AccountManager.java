
import java.util.ArrayList;

public class AccountManager {
	private Administrator admin = new Administrator();
	private ArrayList<Student> students = new ArrayList<Student>();
	private ArrayList<AccessRequest> requests = new ArrayList<AccessRequest>();
	private ArrayList<String> grants = new ArrayList<String>();

	public static boolean isText(String text) {
		return !text.trim().equals("") && !text.contains("|");
	}

	public static boolean isDigits(String text) {
		if (text.equals(""))
			return false;
		for (int i = 0; i < text.length(); i++)
			if (!Character.isDigit(text.charAt(i)))
				return false;
		return true;
	}

	public void register(String id, String name, String email, String phone, String password) {
		if (!isText(id) || !isText(name) || !isText(email) || !isText(phone) || !isText(password))
			throw new IllegalArgumentException("Please fill in every field (the | character is not allowed).");
		if (!isDigits(id))
			throw new IllegalArgumentException("The university ID must contain digits only.");
		if (!email.contains("@") || !email.contains("."))
			throw new IllegalArgumentException("Please enter a valid email address.");
		if (!isDigits(phone) || phone.length() < 7)
			throw new IllegalArgumentException("The phone number must be at least 7 digits.");
		if (password.length() < 4)
			throw new IllegalArgumentException("The password must be at least 4 characters.");
		if (findStudent(id) != null)
			throw new IllegalArgumentException("An account with this university ID already exists.");
		AccessRequest old = findRequest(id);
		if (old != null && old.getStatus().equals("Pending"))
			throw new IllegalArgumentException("A request with this university ID is already pending.");
		requests.add(new AccessRequest(id, name, email, phone, password, "Pending"));
	}

	public void approve(String id, int minutes) {
		AccessRequest r = findRequest(id);
		if (r == null)
			throw new IllegalArgumentException("Request not found.");
		if (!r.getStatus().equals("Pending"))
			throw new IllegalArgumentException("This request was already " + r.getStatus().toLowerCase() + ".");
		if (minutes <= 0)
			throw new IllegalArgumentException("The allowance must be more than 0 minutes.");
		if (findStudent(id) != null)
			throw new IllegalArgumentException("An account with this university ID already exists.");
		students.add(new Student(r.getId(), r.getName(), r.getEmail(), r.getPhone(), r.getPassword(),
				new TimeAllowance(minutes, 0, 0)));
		r.setStatus("Approved");
	}

	public void reject(String id) {
		AccessRequest r = findRequest(id);
		if (r == null)
			throw new IllegalArgumentException("Request not found.");
		if (!r.getStatus().equals("Pending"))
			throw new IllegalArgumentException("This request was already " + r.getStatus().toLowerCase() + ".");
		r.setStatus("Rejected");
	}

	public void grantExtra(String id, int minutes, String reason, String time) {
		Student s = findStudent(id);
		if (s == null)
			throw new IllegalArgumentException("No approved student has this university ID.");
		if (minutes <= 0)
			throw new IllegalArgumentException("Extra time must be more than 0 minutes.");
		if (!isText(reason))
			throw new IllegalArgumentException("Please enter a reason for the extra time.");
		s.getAllowance().addTime(minutes);
		grants.add(id + "|" + minutes + "|" + reason + "|" + time);
	}

	public void setAllocation(String id, int minutes) {
		Student s = findStudent(id);
		if (s == null)
			throw new IllegalArgumentException("No approved student has this university ID.");
		TimeAllowance a = s.getAllowance();
		if (minutes <= 0)
			throw new IllegalArgumentException("The allowance must be more than 0 minutes.");
		if (minutes < a.getUsed() + a.getReserved())
			throw new IllegalArgumentException("The allowance cannot be less than the used + reserved time ("
					+ (a.getUsed() + a.getReserved()) + " minutes).");
		a.setAllocated(minutes);
	}

	public User login(String id, String password) {
		if (id.equals(admin.getId())) {
			if (admin.checkPassword(password))
				return admin;
			throw new IllegalArgumentException("Wrong username or password.");
		}
		Student s = findStudent(id);
		if (s != null && s.checkPassword(password))
			return s;
		AccessRequest r = findRequest(id);
		if (s == null && r != null && r.getStatus().equals("Pending"))
			throw new IllegalArgumentException("Your request is still waiting for approval.");
		if (s == null && r != null && r.getStatus().equals("Rejected"))
			throw new IllegalArgumentException("Your registration request was rejected.");
		throw new IllegalArgumentException("Wrong username or password.");
	}

	public Student findStudent(String id) {
		for (Student s : students)
			if (s.getId().equals(id))
				return s;
		return null;
	}

	public AccessRequest findRequest(String id) {
		AccessRequest found = null;
		for (AccessRequest r : requests)
			if (r.getId().equals(id))
				found = r;
		return found;
	}

	public ArrayList<Student> getStudents() {
		return students;
	}

	public ArrayList<AccessRequest> getRequests() {
		return requests;
	}

	public ArrayList<String> getGrants() {
		return grants;
	}
}