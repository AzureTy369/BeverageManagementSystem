import GUI.LoginGUI;
import DAO.DBConnection;


import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // Set System Look and Feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Setup database connection
        DBConnection.setupDatabase();

        // Start the login view
        SwingUtilities.invokeLater(() -> new LoginGUI().setVisible(true));
    }
}