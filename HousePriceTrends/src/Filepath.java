import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

@SuppressWarnings("serial")
public class Filepath extends JFrame implements ActionListener{
	private String filepath;
	private JTextField instructions = new JTextField("Enter the filepath for the database you want to use eg. C:/Documents/mydatabase.db");
	private JTextField filepathField = new JTextField("", 40);
	private JButton load = new JButton("Load");
	
	public Filepath() {
		setTitle("House prices");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new FlowLayout());
		setSize(900,170);
		setLocation(300,200);
		
		DocumentListener textListener = new TextListener();

		instructions.setEditable(false);
		instructions.setHorizontalAlignment(JTextField.CENTER);
		filepathField.requestFocusInWindow();
		
		add(instructions);
		add(filepathField);
		add(load);
		
		filepathField.getDocument().addDocumentListener(textListener);
		
		load.addActionListener(this);
		load.setEnabled(false);
		
		setVisible(true);
	}
	
	public void actionPerformed(ActionEvent event) {
		filepath = filepathField.getText();
		dispose();
		new SessionQuery(filepath);
	}
	
	public void checkFieldsNotEmpty() {
		if(filepathField.getText().isEmpty()) {
			load.setEnabled(false);
			return;
		}
		load.setEnabled(true);
	}
	
	private class TextListener implements DocumentListener{
		public void changedUpdate(DocumentEvent event) {
			checkFieldsNotEmpty();
		}
		
		public void insertUpdate(DocumentEvent event) {
			checkFieldsNotEmpty();
		}
		
		public void removeUpdate(DocumentEvent event) {
			checkFieldsNotEmpty();
		}
	}
}
