
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

public class LoginFrame extends JFrame implements ActionListener {
	JTextField idF;
	JPasswordField passF;
	JButton loginB, registerB;

	public LoginFrame() {
		setTitle("GPU Utilization Management - Login");
		setLayout(new GridLayout(3, 2));
		idF = new JTextField();
		passF = new JPasswordField();
		loginB = new JButton("Login");
		registerB = new JButton("Register");
		add(new JLabel("University ID / Username:", SwingConstants.CENTER));
		add(idF);
		add(new JLabel("Password:", SwingConstants.CENTER));
		add(passF);
		add(loginB);
		add(registerB);

		loginB.addActionListener(this);
		registerB.addActionListener(this);

		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(450, 160);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == loginB) {
			try {
				User u = GPUApp.accounts.login(idF.getText().trim(), new String(passF.getPassword()));
				JOptionPane.showMessageDialog(null, "Welcome " + u.getName() + " (" + u.getRole() + ")");
				dispose();
				if (u instanceof Administrator)
					new AdminFrame((Administrator) u);
				else
					new StudentFrame((Student) u);
			} catch (IllegalArgumentException ex) {
				JOptionPane.showMessageDialog(null, ex.getMessage());
			}
		} else if (e.getSource() == registerB)
			new RegisterFrame();
	}
}
