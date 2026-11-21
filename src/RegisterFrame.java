
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

public class RegisterFrame extends JFrame implements ActionListener {
	JTextField idF, nameF, emailF, phoneF;
	JPasswordField passF;
	JButton submitB, cancelB;

	public RegisterFrame() {
		setTitle("Student Registration");
		setLayout(new GridLayout(6, 2));
		idF = new JTextField();
		nameF = new JTextField();
		emailF = new JTextField();
		phoneF = new JTextField();
		passF = new JPasswordField();
		submitB = new JButton("Submit Request");
		cancelB = new JButton("Cancel");
		add(new JLabel("University ID:", SwingConstants.CENTER));
		add(idF);
		add(new JLabel("Name:", SwingConstants.CENTER));
		add(nameF);
		add(new JLabel("Email:", SwingConstants.CENTER));
		add(emailF);
		add(new JLabel("Phone Number:", SwingConstants.CENTER));
		add(phoneF);
		add(new JLabel("Password:", SwingConstants.CENTER));
		add(passF);
		add(submitB);
		add(cancelB);

		submitB.addActionListener(this);
		cancelB.addActionListener(this);

		setVisible(true);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setSize(400, 260);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == submitB) {
			try {
				GPUApp.accounts.register(idF.getText().trim(), nameF.getText().trim(), emailF.getText().trim(),
						phoneF.getText().trim(), new String(passF.getPassword()));
				GPUApp.save();
				JOptionPane.showMessageDialog(null, "Your request was sent. Please wait for the administrator to approve it.");
				dispose();
			} catch (IllegalArgumentException ex) {
				JOptionPane.showMessageDialog(null, ex.getMessage());
			}
		} else if (e.getSource() == cancelB)
			dispose();
	}
}
