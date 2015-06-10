package paketti;

import javax.swing.*; 
import javax.swing.table.*;
import javax.swing.BoxLayout;

import java.awt.*;
import java.awt.event.*;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Vector;

@SuppressWarnings ("serial")
public class MainWindow extends JFrame {

	DataBase database = null;
	
	JLabel topLabel = new JLabel();
	JTable middleTable = new JTable();
	DefaultTableModel dtm = null;
	JScrollPane scrollPane = new JScrollPane();
	
	JPanel contentPanel = null;
	JPanel centerPanel = null;
	BorderLayout layout = new BorderLayout();

    MainWindow(){
    	initComponents(); 	
    }
    
	public static void main(String[] args) {
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				new MainWindow().setVisible(true);
			}
		});
	}
    
	/** Error message pop-up */
    public void printError(Exception e){
		JOptionPane.showMessageDialog(null, e.getMessage());
    }
    
	public void initComponents(){

		try{
		database = new DataBase();
		
		}catch(Exception e){
			printError(e);
		}
		//Window settings
		setTitle("Osoitekirja");
		setPreferredSize(new Dimension(750, 500));
		
		//
		WindowEventHandler exitHandler = new WindowEventHandler();
		addWindowListener(exitHandler);
		
		contentPanel = new JPanel(new BorderLayout(5, 5));
		centerPanel = new JPanel(new BorderLayout(10, 5));
		JPanel northCenterPanel = new JPanel(new FlowLayout(FlowLayout.LEADING, 10, 0));

		//Top image
		ImageIcon icon = createImageIcon("/header.png");
		topLabel = new JLabel(icon);
		
		//Table with database info
		FillTableWithData();
		middleTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		middleTable.setRowSelectionAllowed(true);

	    scrollPane = new JScrollPane(middleTable);
	    
	    //Search field
	    JTextField searchField = new JTextField();
		searchField.setText("Hakulauseke");
		searchField.setPreferredSize(new Dimension(100, 20));
		searchField.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent e){
				searchField.selectAll();
			}

			@Override
			public void focusLost(FocusEvent e) {
				
			}
		});
		
		//Action listener that brings up a table with given keyword
		searchField.addActionListener(new ActionListener() {
		      public void actionPerformed(ActionEvent evt) {
		    	    try{
					String query = searchField.getText();
					ResultSet rSet = database.search(query);
					DefaultTableModel deff = buildTable(rSet);
					JTable table = new JTable(deff);
					table.removeColumn(table.getColumnModel().getColumn(0));
					
					JOptionPane.showMessageDialog(null, table);
		        }catch(Exception e){
		        	printError(e);
		        }
		      }
		});
		
		//Search button
		JButton searchButton = new JButton("Hae");
		searchButton.setPreferredSize(new Dimension(75, 20));
		searchButton.setRequestFocusEnabled(true);
		//Action listener that brings up a table with given keyword
		searchButton.addActionListener(new ActionListener(){
		      public void actionPerformed(ActionEvent evt) {
		    	    try{
					String query = searchField.getText();
					ResultSet rSet = database.search(query);
					DefaultTableModel deff = buildTable(rSet);
					JTable table = new JTable(deff);
					table.removeColumn(table.getColumnModel().getColumn(0));
					int om = table.getRowCount();
					if(om < 1){
						JOptionPane.showMessageDialog(null, "Hakusi ei tuottanut yhtään tulosta.");
					}else{
					JOptionPane.showMessageDialog(null, table);
					}
		        }catch(Exception e){
		        	printError(e);
		        }
		      }
		});
		
		JButton addNameButton = new JButton("Lisää tieto");
		addNameButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent evt){
				//Opens up a new name insertion window
				new NameWindow().setVisible(true);
			}
		
		});
		
		JButton editButton = new JButton("Muokkaa tietoa");
		editButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent evt){
				//Opens up a new name editing window
				int selectedRowIndex = middleTable.getSelectedRow();
				if(selectedRowIndex != -1){
				new editWindow().setVisible(true);
				}
			}
		
		});
		
		JButton deleteButton = new JButton("Poista tieto");
		deleteButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent evt){
				//Opens up a new name deleting window
				int selectedRowIndex = middleTable.getSelectedRow();
						
				if(selectedRowIndex != -1){
				new deleteWindow().setVisible(true);
				}
			}
		
		});

		northCenterPanel.add(searchField);
		northCenterPanel.add(searchButton);
		northCenterPanel.add(Box.createHorizontalStrut(180));
		northCenterPanel.add(addNameButton);
		northCenterPanel.add(editButton);
		northCenterPanel.add(deleteButton);
		centerPanel.add(northCenterPanel, BorderLayout.NORTH);
		centerPanel.add(scrollPane, BorderLayout.CENTER);
		contentPanel.add(topLabel, BorderLayout.NORTH);
		contentPanel.add(centerPanel, BorderLayout.CENTER);
		contentPanel.setFocusable(true);
		
		add(contentPanel);
		
		pack();
		
		//Set the window to appear in the middle of the screen regardless of screen size
		setLocationRelativeTo(null);
		
	}
	
	//Creates a table from database
	private void FillTableWithData() {
		try{
			database.resultSet = database.execute("SELECT * FROM Osoitekirja");
			dtm = buildTable(database.resultSet);
			middleTable.setModel(dtm);
			middleTable.removeColumn(middleTable.getColumnModel().getColumn(0));
			
		}catch(Exception e){
			printError(e);
		}  
			
	}
	
	/** Uses ResultsetMetadata to build the table with the data stored in vectors */
	public static DefaultTableModel buildTable(ResultSet rs)
	        throws SQLException {

	    ResultSetMetaData metaData = rs.getMetaData();

	    //names of columns
	    Vector<String> cNames = new Vector<String>();
	    
	    //number of columns
	    int cCount = metaData.getColumnCount();
	    
	    //names of columns
	    for (int column = 1; column <= cCount; column++) {
	        cNames.add(metaData.getColumnName(column));
	    }
	    
	    //data of the table
	    Vector<Vector<Object>> data = new Vector<Vector<Object>>();
	    while (rs.next()) {
	        Vector<Object> vector = new Vector<Object>();
	        for (int cIndex = 1; cIndex <= cCount; cIndex++) {
	            vector.add(rs.getObject(cIndex));
	        }
	        data.add(vector);
	    }
	    return new DefaultTableModel(data, cNames);
}
	
	
/** Returns an ImageIcon, or null if the path was invalid. */
	protected static ImageIcon createImageIcon(String path) {
		java.net.URL imgURL = MainWindow.class.getResource(path);
		if (imgURL != null) {
	        return new ImageIcon(imgURL);
	    } else {
	        System.err.println("Couldn't find file: " + path);
	            return null;
	        }
	    }

/** Class that builds the window for adding a name.  */
private class NameWindow extends JFrame{
	
	//TextFields go here
		JTextField[] fields = new JTextField[4];
		
		NameWindow(){
			initUI();
		}
		
		private void initUI(){

			setTitle("Lisää tieto");
			setSize(new Dimension(100, 300));
			Container panel = getContentPane();
			panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

			//Box to save labels and textFields into
			Box box = Box.createVerticalBox();
			box.add(new JLabel("Lisää tieto"));
			
			//This loop creates labels named by table columns and also
			//adds textFields for data input and saving
			for(int i = 0; i < fields.length; i++){
				String columnName = (String) dtm.getColumnName(i+1);
				JLabel label = new JLabel(columnName);
				Box.createRigidArea(new Dimension(0,9));
				fields[i] = new JTextField();
				fields[i].addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent evt){
					  insertNewName();
					}
				});
				
				box.add(label);
				box.add(fields[i]);
			}
			
			
			JButton okButton = new JButton("OK");
			okButton.addActionListener(new ActionListener() {
			      public void actionPerformed(ActionEvent evt) {
			    	  insertNewName();
			      }
			});
			
			JButton cancel = new JButton("Peruuta");
			cancel.addActionListener(new ActionListener() {
			      public void actionPerformed(ActionEvent evt) {
			    	  //Close the window
			    	  dispose();
			      }
				});
			
			JPanel buttonPanel = new JPanel(new FlowLayout());
			
			buttonPanel.add(okButton);
			buttonPanel.add(cancel);
			
			
			panel.add(box);	
			panel.add(buttonPanel);


			pack();
			
			setLocationRelativeTo(null);
			
		}
		
		private void insertNewName(){
			
	    	String name = fields[0].getText(); 
	    	String address = fields[1].getText();
	    	String phone = fields[2].getText();
	    	String email = fields[3].getText();

			try {
				database.insert(name, address, phone, email);
				
			}catch(SQLException e) {
				printError(e);
			}
			FillTableWithData();			
			dispose();
	        
		}
		
	}

/** Class that builds the window for editing a name. */
	private class editWindow extends JFrame{
		
		//This integer has rowid of the row that is selected
		int selectedRowIndex = middleTable.getSelectedRow();
		//TextFields go here
		JTextField[] fields = new JTextField[4]; 
	
		editWindow(){
			//getSelectedRow returns -1 if no row is selected
			if(selectedRowIndex != -1){
			initUI();
			}
		}
		
		private void initUI(){
			setTitle("Muokkaa nimeä");
			setSize(new Dimension(100, 300));
			JPanel panel = new JPanel();
			panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

			//Box to save labels and textFields into
			Box box = Box.createVerticalBox();
			box.add(new JLabel("Muokkaa tietoa"));
			//This loop creates labels named by table columns and inserts text from the selected row
			//on respective textFields
			for(int i = 0; i < fields.length; i++){
				String columnName = (String) dtm.getColumnName(i+1);
				String textString = (String) dtm.getValueAt(selectedRowIndex, i+1);
				JLabel label = new JLabel(columnName);
				Box.createRigidArea(new Dimension(0,5));
				fields[i] = new JTextField(textString);
				fields[i].addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent evt){
					  addEditedName();
					}
				});
				
				box.add(label);
				box.add(fields[i]);
			}			
			
			JButton okButton = new JButton("OK");
			okButton.addActionListener(new ActionListener() {
			      public void actionPerformed(ActionEvent evt) {
			    	  addEditedName();
			      }//actionPerformed
			      });
			
			JButton cancel = new JButton("Peruuta");
			cancel.addActionListener(new ActionListener() {
			      public void actionPerformed(ActionEvent evt) {
			    	  dispose();
			      }
				});
			
			JPanel buttonPanel = new JPanel();
			buttonPanel.setLayout(new FlowLayout());
			buttonPanel.add(okButton);
			buttonPanel.add(cancel);
			
			panel.add(box);
			panel.add(buttonPanel);
			
			add(panel);
		
			pack();
			
			setLocationRelativeTo(null);

		}
		/** This function pushes edited information into the database and updates the table. */
		private void addEditedName(){
		    String name = fields[0].getText(); 
		    String address = fields[1].getText();
		    String phone = fields[2].getText();
		    String email = fields[3].getText();
			Object id_object = (dtm.getValueAt(selectedRowIndex, 0));
			int id = (Integer) id_object;
			
			try{
				database.update(name, address, phone, email, id);
				}catch(SQLException e) {
					printError(e);
				}
			FillTableWithData();
			dispose();
	        
		}
		
}
	/** Class that builds the window for deleting a name. */

	private class deleteWindow extends JFrame{
		
		Container container = getContentPane();
		
		int selectedRowIndex = middleTable.getSelectedRow();
		Object selectedName = dtm.getValueAt(selectedRowIndex, 1);
		String name = selectedName.toString();
		
		deleteWindow(){
			initUI();
		}
		
		private void initUI(){

			container.setLayout(new BorderLayout());
			
			JPanel southPanel = new JPanel(new FlowLayout());
			
			JLabel msgText = new JLabel("Haluatko varmasti poistaa henkilön " + name + " tiedot?" );
			
			JButton okButton = new JButton("Kyllä");
			okButton.addActionListener(new ActionListener() {
			      public void actionPerformed(ActionEvent evt) {
					try {
						int id = 0;
						database.preparedStatement = database.connection.prepareStatement("SELECT ID FROM Osoitekirja WHERE NIMI = ?");
						database.preparedStatement.setString(1, name);
						ResultSet rSet = database.preparedStatement.executeQuery();
						if(rSet.next()){
						 id = ((Number) rSet.getObject(1)).intValue();
						}
						
			//A second try-catch is needed because you cannot write to a table while a SELECT is active on that same table. 
						try{
						database.delete(id);
						}catch(Exception e) {
							printError(e);
							}
					}catch(Exception e) {
						printError(e);
					}
					FillTableWithData();
					dispose();
			        }//actionPerformed
			      });
			
			JButton cancel = new JButton("Peruuta");
			cancel.addActionListener(new ActionListener() {
			      public void actionPerformed(ActionEvent evt) {
			    	  dispose();
			      }
				});

			southPanel.add(okButton);
			southPanel.add(cancel);
			
			container.add(msgText, BorderLayout.NORTH);
			container.add(southPanel, BorderLayout.SOUTH);
			
			pack();
			
			setLocationRelativeTo(null);

		}
		
	}
	
	/** class that handles exiting the program. shuts down the connection
	 *  to the database and quits */
	
	class WindowEventHandler extends WindowAdapter {
		  public void windowClosing(WindowEvent evt) {
			try{
			database.disconnect();
			}catch(Exception e){
				printError(e);
			}
			System.exit(0);
		  }
	}

}//end class

	