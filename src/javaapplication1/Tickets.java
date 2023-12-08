package javaapplication1;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;

@SuppressWarnings("serial")
public class Tickets extends JFrame implements ActionListener {

	// class level member objects
	Dao dao = new Dao(); // for CRUD operations
	Boolean chkIfAdmin = null;
	String ticketIssuer;

	// Main menu object items
	private JMenu mnuFile = new JMenu("File");
	private JMenu mnuAdmin = new JMenu("Admin");
	private JMenu mnuTickets = new JMenu("Tickets");
	private JMenu mnuArchive = new JMenu("Archives");

	// Sub menu item objects for all Main menu item objects
	JMenuItem mnuItemExit;
	JMenuItem mnuItemUpdate;
	JMenuItem mnuItemDelete;
	JMenuItem mnuItemClose;
	JMenuItem mnuItemOpenTicket;
	JMenuItem mnuItemViewTicket;
	JMenuItem mnuItemYourTickets;
	JMenuItem mnuItemViewArchive;

	public Tickets(Boolean isAdmin, String username) {

		chkIfAdmin = isAdmin;
		ticketIssuer = username;
		createMenu();
		prepareGUI();

	}

	private void createMenu() {

		/* Initialize sub menu items **************************************/

		// initialize sub menu item for File main menu
		mnuItemExit = new JMenuItem("Exit");
		// add to File main menu item
		mnuFile.add(mnuItemExit);

		// initialize first sub menu items for Admin main menu
		mnuItemUpdate = new JMenuItem("Update Ticket");
		// add to Admin main menu item
		mnuAdmin.add(mnuItemUpdate);

		// initialize second sub menu items for Admin main menu
		mnuItemDelete = new JMenuItem("Delete Ticket");
		// add to Admin main menu item
		mnuAdmin.add(mnuItemDelete);

		// inititalize third sub menu items for Admin main menu
		mnuItemClose = new JMenuItem("Close Ticket");
		// add to Admin main menu itme
		mnuAdmin.add(mnuItemClose);

		// initialize first sub menu item for Tickets main menu
		mnuItemOpenTicket = new JMenuItem("Open Ticket");
		// add to Ticket Main menu item
		mnuTickets.add(mnuItemOpenTicket);

		// initialize second sub menu item for Tickets main menu
		mnuItemViewTicket = new JMenuItem("View Tickets");
		// add to Ticket Main menu item
		mnuTickets.add(mnuItemViewTicket);

		//Initialize speical sub menu that is visible only to users for Tickets main menu
		mnuItemYourTickets = new JMenuItem("Your Tickets");
		// add to Ticket Main menu itme
		mnuTickets.add(mnuItemYourTickets);

		// initialize first sub menu item for Archives main menu
		mnuItemViewArchive = new JMenuItem("View Archives");
		// add to Archives Main menu
		mnuArchive.add(mnuItemViewArchive);

		// initialize any more desired sub menu items below

		/* Add action listeners for each desired menu item *************/
		mnuItemExit.addActionListener(this);
		mnuItemUpdate.addActionListener(this);
		mnuItemDelete.addActionListener(this);
		mnuItemClose.addActionListener(this);
		mnuItemOpenTicket.addActionListener(this);
		mnuItemViewTicket.addActionListener(this);
		mnuItemYourTickets.addActionListener(this);
		mnuItemViewArchive.addActionListener(this);

		 /*
		  * continue implementing any other desired sub menu items (like 
		  * for update and delete sub menus for example) with similar 
		  * syntax & logic as shown above
		 */

		 if (chkIfAdmin != null && !chkIfAdmin) {
			mnuAdmin.setVisible(false);
			mnuArchive.setVisible(false);
			mnuItemViewTicket.setVisible(false);
			
		 }
		 else {
			mnuAdmin.setVisible(true);
			mnuItemYourTickets.setVisible(false);
		 }

 
	}

	private void prepareGUI() {

		// create JMenu bar
		JMenuBar bar = new JMenuBar();
		bar.add(mnuFile); // add main menu items in order, to JMenuBar
		bar.add(mnuAdmin);
		bar.add(mnuTickets);
		bar.add(mnuArchive);
		// add menu bar components to frame
		setJMenuBar(bar);

		addWindowListener(new WindowAdapter() {
			// define a window close operation
			public void windowClosing(WindowEvent wE) {
				System.exit(0);
			}
		});
		// set frame options
		setSize(400, 400);
		getContentPane().setBackground(Color.LIGHT_GRAY);
		setLocationRelativeTo(null);
		setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// implement actions for sub menu items
		if (e.getSource() == mnuItemExit) {
			System.exit(0);
		} else if (e.getSource() == mnuItemOpenTicket) {

			// get ticket information
			String ticketName = JOptionPane.showInputDialog(null, "Enter your login username");
			String ticketDesc = JOptionPane.showInputDialog(null, "Enter a ticket description");

			// insert ticket information to database

			int id = dao.insertRecords(ticketName, ticketDesc);

			// display results if successful or not to console / dialog box
			if (id != 0) {
				System.out.println("Ticket ID : " + id + " created successfully!!!");
				JOptionPane.showMessageDialog(null, "Ticket id: " + id + " created");
			} else
				System.out.println("Ticket cannot be created!!!");
		}

		else if (e.getSource() == mnuItemViewTicket) {
			resetView();
		}

		//For users only, reads their tickets
		else if (e.getSource() == mnuItemYourTickets) {

			try {
			// Clear existing components before refreshing the view
			getContentPane().removeAll();

			// Retrieve ticket details
			JTable jt = new JTable(ticketsJTable.buildTableModel(dao.readYourTickets(ticketIssuer)));
			jt.setBounds(30, 40, 200, 400);
			JScrollPane sp = new JScrollPane(jt);
			add(sp);

			// Refresh the frame to display the updated view
			revalidate();
			repaint();
		}
		catch (SQLException e1) {
			e1.printStackTrace();
		}
		}

		else if (e.getSource() == mnuItemViewArchive) {
			resetArchiveView();
		}

		else if (e.getSource() == mnuItemDelete) {

			// Opens text box
			String ticketID = JOptionPane.showInputDialog(null, "Enter ticket ID");
		
			// insert ticket information to database

			dao.deleteRecords(ticketID);

			//Automatically updates the table
			resetView();
		}

		else if (e.getSource() == mnuItemUpdate) {
			
			// Opens text box
			String ticketID = JOptionPane.showInputDialog(null,"Enter ticket ID");
			String ticketDesc = JOptionPane.showInputDialog(null, "Enter new ticket description");

			dao.updateRecords(ticketID, ticketDesc);

			//Automatically updates the table
			resetView();
		}

		else if (e.getSource() == mnuItemClose) {

			// Opens text box
			String ticketID = JOptionPane.showInputDialog("Enter the ticket ID");
			String ticketDesc = JOptionPane.showInputDialog("What was the issue?");

			// insert ticket information to database

			dao.closeRecords(ticketID, ticketDesc);

			// Automatically updates the archive table
			resetArchiveView();
		}
		/*
		 * continue implementing any other desired sub menu items (like for update and
		 * delete sub menus for example) with similar syntax & logic as shown above
		 */

	}

	//Reset button to update the table without having to reload the application
	private void resetView() {
		try {
			// Clear existing components before refreshing the view
			getContentPane().removeAll();

			// Retrieve ticket details
			JTable jt = new JTable(ticketsJTable.buildTableModel(dao.readRecords()));
			jt.setBounds(30, 40, 200, 400);
			JScrollPane sp = new JScrollPane(jt);
			add(sp);

			// Refresh the frame to display the updated view
			revalidate();
			repaint();
		}
		catch (SQLException e1) {
			e1.printStackTrace();
		}
	}

	private void resetArchiveView() {
		try {
			// Clear existing components before refreshing the view
			getContentPane().removeAll();

			// Retrieve ticket details
			JTable jt = new JTable(ticketsJTable.buildTableModel(dao.readArchiveRecords()));
			jt.setBounds(30, 40, 200, 400);
			JScrollPane sp = new JScrollPane(jt);
			add(sp);

			// Refresh the frame to display the updated view
			revalidate();
			repaint();
		}
		catch (SQLException e1) {
			e1.printStackTrace();
		}
	}
}

