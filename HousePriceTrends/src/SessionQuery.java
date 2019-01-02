import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.Vector;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

import org.sqlite.*;

@SuppressWarnings("serial")
public class SessionQuery extends JFrame implements ActionListener{
	private Connection connection = null;
	
	private Vector<String> columnHeaders = new Vector<>();
	
	private String filepath;
	private JTextField instructions = new JTextField("Please input the postcode you would like to search for. You must enter a minimum of 1 characters eg. S, SY16 or SY16 4BN");

	private JButton search = new JButton("Search");
	private JButton newSearch = new JButton(new AbstractAction("New search") {
		@Override
		public void actionPerformed(ActionEvent event) {
			remove(panel2);
			remove(panel3);
			add(panel1);
			postcodeField.setText("");
			setSize(900,170);
			revalidate();
			repaint();
		}
	});

	private JTextField postcodeField = new JTextField(30);
	private JPanel panel1 = new JPanel();
	private JPanel panel2, panel3;
	
	public SessionQuery(String _filepath) {
		filepath = _filepath;
		DocumentListener textListener = new TextListener();
		
		columnHeaders.add("Sale Price (£)");
		columnHeaders.add("Sale Date");
		columnHeaders.add("Postcode");
		columnHeaders.add("House Number/Name");
		columnHeaders.add("Street");
		columnHeaders.add("Locality");
		columnHeaders.add("Town");
		columnHeaders.add("District");
		columnHeaders.add("County");
		
		setTitle("House prices");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new BorderLayout());
		setSize(900,170);
		setLocation(300,200);
		
		panel1.setLayout(new GridLayout(0,1,0,5));
		instructions.setEditable(false);
		instructions.setHorizontalAlignment(JTextField.CENTER);
		postcodeField.requestFocusInWindow();
		postcodeField.setHorizontalAlignment(JTextField.CENTER);
		
		panel1.add(instructions);
		panel1.add(postcodeField);
		panel1.add(search);
		panel1.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		
		add(panel1);
		postcodeField.requestFocusInWindow();
		postcodeField.getDocument().addDocumentListener(textListener);
		
		search.setEnabled(false);
		search.addActionListener(this);
		
		setVisible(true);
	}
	
	public void actionPerformed(ActionEvent event) {
		try {
			connection = DriverManager.getConnection("jdbc:sqlite:" + filepath);
//			connection = DriverManager.getConnection("jdbc:sqlite:C:/code/HousePriceTrendsV1/month-house-prices.db");
			String postcode = postcodeField.getText();
			Statement statement = connection.createStatement();
			String query = "SELECT * FROM sales WHERE postcode LIKE '" + postcode + "%';";
			ResultSet results = statement.executeQuery(query);
			
			JTable table = new JTable(populateModel(results));
			table.moveColumn(3, 0);
			table.moveColumn(4, 1);
			table.moveColumn(5, 2);
			table.moveColumn(6, 3);
			table.moveColumn(7, 4);
			table.moveColumn(8, 5);
			table.moveColumn(8, 6);
			table.moveColumn(8, 7);
			
			JScrollPane scrollPane = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
			
			panel2 = new JPanel();
			panel2.setLayout(new FlowLayout());
			panel2.add(newSearch);
			
			panel3 = new JPanel();
			panel3.setLayout(new GridLayout(0,1));
			panel3.add(scrollPane);
			panel3.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
			
			remove(panel1);
			add(panel2, BorderLayout.NORTH);
			add(panel3, BorderLayout.CENTER);

			setSize(1100, 450);
			revalidate();
			repaint();
		}
		catch(SQLException sqlex) {
			sqlex.printStackTrace();
		}
	}
	
	public DefaultTableModel populateModel(ResultSet results) {	
		Vector<Vector<Object>> updatedData = new Vector<Vector<Object>>();
		try {
			while (results.next()) {
				
				Vector<Object> vector = new Vector<Object>();
				
				for(int column = 1; column <= 16; column++) {
					if(column == 2 || column == 4 || (column >= 10 && column <= 14)) {
						vector.add(results.getObject(column));
					}
					else if(column == 8) {
						if(results.getString(9) == "") {
							vector.add(results.getObject(8));
						}
						else {
							String propIdentifier;
							propIdentifier = results.getString("saon") + " " + results.getString("paon");
							vector.add(propIdentifier);
						}
					}
					else if(column == 3) {						
						String shortDate = results.getString(3).substring(0, 10);
						vector.add(shortDate);
					}
				}
			updatedData.add(vector);
			}
		}
		catch (SQLException e) {
			
		}
		DefaultTableModel model = new DefaultTableModel(updatedData, columnHeaders);
		return model;
	}
	
	public void checkFieldsNotEmpty() {
		if(postcodeField.getText().isEmpty()) {
			search.setEnabled(false);
			return;
		}
		search.setEnabled(true);
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
