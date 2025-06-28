package testgame;

import javax.swing.*;
import java.awt.*;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class SecureCommunicationApp extends JFrame {
    private UserManager userManager = new UserManager();
    private int currentUserId = -1; // Track logged-in user

 

public class CipherManager {
    public static String caesarCipher(String message, int shift, boolean encrypt) {
        StringBuilder result = new StringBuilder();
        for (char character : message.toCharArray()) {
            if (Character.isLetter(character)) {
                char base = Character.isLowerCase(character) ? 'a' : 'A';
                int offset = encrypt ? shift : -shift;
                char newChar = (char) ((character - base + offset + 26) % 26 + base);
                result.append(newChar);
            } else {
                result.append(character);  // Non-alphabet characters remain the same
            }
        }
        return result.toString();
    }

    public static String vigenereCipher(String message, String key, boolean encrypt) {
        StringBuilder result = new StringBuilder();
        key = key.toLowerCase();
        int keyIndex = 0;
        for (char character : message.toCharArray()) {
            if (Character.isLetter(character)) {
                char base = Character.isLowerCase(character) ? 'a' : 'A';
                int shift = key.charAt(keyIndex % key.length()) - 'a';
                if (!encrypt) shift = -shift;
                char newChar = (char) ((character - base + shift + 26) % 26 + base);
                result.append(newChar);
                keyIndex++;
            } else {
                result.append(character);
            }
        }
        return result.toString();
    }
}

public class Database {
    private static final String URL = "jdbc:sqlite:secure_communication.db";

    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    public static void createTables() {
        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
            String createUsersTable = "CREATE TABLE IF NOT EXISTS users (" +
                                      "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                                      "username TEXT UNIQUE," +
                                      "password TEXT)";
            String createMessagesTable = "CREATE TABLE IF NOT EXISTS messages (" +
                                         "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                                         "user_id INTEGER," +
                                         "encrypted_message TEXT," +
                                         "cipher_type TEXT)";
            stmt.execute(createUsersTable);
            stmt.execute(createMessagesTable);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
    

public class UserManager {
    public boolean loginUser(String username, String password) {
        // Check if the user exists and password matches
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (Connection conn = Database.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();  // Return true if user exists
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void registerUser(String username, String password) {
        // Save new user to the database
        String sql = "INSERT INTO users (username, password) VALUES (?, ?)";
        try (Connection conn = Database.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void saveMessage(int userId, String message, String cipherType) {
        String sql = "INSERT INTO messages (user_id, encrypted_message, cipher_type) VALUES (?, ?, ?)";
        try (Connection conn = Database.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setString(2, message);
            pstmt.setString(3, cipherType);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

   
    public SecureCommunicationApp() {
        // Initialize database
        Database.createTables();

        // Set up the frame
        setTitle("Secure Communication System");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new CardLayout());

        // Create panels
        JPanel loginPanel = createLoginPanel();
        JPanel registerPanel = createRegisterPanel();
        JPanel dashboardPanel = createDashboardPanel();

        // Add panels to frame
        add(loginPanel, "login");
        add(registerPanel, "register");
        add(dashboardPanel, "dashboard");

        // Show login panel initially
        CardLayout layout = (CardLayout) getContentPane().getLayout();
        layout.show(getContentPane(), "login");
    }

    private JPanel createLoginPanel() {
        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
        JLabel usernameLabel = new JLabel("Username:");
        JLabel passwordLabel = new JLabel("Password:");
        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        JButton loginButton = new JButton("Login");
        JButton registerButton = new JButton("Register");

        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.add(usernameLabel);
        panel.add(usernameField);
        panel.add(passwordLabel);
        panel.add(passwordField);
        panel.add(loginButton);
        panel.add(registerButton);

        loginButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            if (userManager.loginUser(username, password)) {
                currentUserId = 1; // Replace with actual user ID from DB
                CardLayout layout = (CardLayout) getContentPane().getLayout();
                layout.show(getContentPane(), "dashboard");
            } else {
                JOptionPane.showMessageDialog(this, "Invalid credentials!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        registerButton.addActionListener(e -> {
            CardLayout layout = (CardLayout) getContentPane().getLayout();
            layout.show(getContentPane(), "register");
        });

        return panel;
    }

    private JPanel createRegisterPanel() {
        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
        JLabel usernameLabel = new JLabel("Username:");
        JLabel passwordLabel = new JLabel("Password:");
        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        JButton registerButton = new JButton("Register");
        JButton backButton = new JButton("Back");

        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.add(usernameLabel);
        panel.add(usernameField);
        panel.add(passwordLabel);
        panel.add(passwordField);
        panel.add(registerButton);
        panel.add(backButton);

        registerButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            userManager.registerUser(username, password);
            JOptionPane.showMessageDialog(this, "Registration successful!");
            CardLayout layout = (CardLayout) getContentPane().getLayout();
            layout.show(getContentPane(), "login");
        });

        backButton.addActionListener(e -> {
            CardLayout layout = (CardLayout) getContentPane().getLayout();
            layout.show(getContentPane(), "login");
        });

        return panel;
    }

    private JPanel createDashboardPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Top section for buttons
        JPanel buttonPanel = new JPanel(new GridLayout(1, 4, 10, 10));
        JButton encryptButton = new JButton("Encrypt Message");
        JButton viewButton = new JButton("View Messages");
        JButton decryptButton = new JButton("Decrypt Messages");
        JButton logoutButton = new JButton("Logout");
        buttonPanel.add(encryptButton);
        buttonPanel.add(viewButton);
        buttonPanel.add(decryptButton);
        buttonPanel.add(logoutButton);

        // Center section for message display
        JTextArea messageArea = new JTextArea();
        messageArea.setEditable(false);
        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(messageArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Messages"));

        panel.add(buttonPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Encrypt button action
        encryptButton.addActionListener(e -> {
            String message = JOptionPane.showInputDialog(this, "Enter message to encrypt:");
            if (message != null) {
                String[] options = {"Caesar", "Vigenere"};
                int cipherChoice = JOptionPane.showOptionDialog(
                        this, "Choose Cipher:", "Cipher Selection",
                        JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]
                );
                if (cipherChoice == 0) {
                    String key = JOptionPane.showInputDialog(this, "Enter Caesar key (integer):");
                    if (key != null) {
                        try {
                            int shift = Integer.parseInt(key);
                            String encrypted = CipherManager.caesarCipher(message, shift, true);
                            userManager.saveMessage(currentUserId, encrypted, "Caesar");
                            JOptionPane.showMessageDialog(this, "Message encrypted and saved!");
                        } catch (NumberFormatException ex) {
                            JOptionPane.showMessageDialog(this, "Invalid key!", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                } else if (cipherChoice == 1) {
                    String key = JOptionPane.showInputDialog(this, "Enter Vigenere key:");
                    if (key != null) {
                        String encrypted = CipherManager.vigenereCipher(message, key, true);
                        userManager.saveMessage(currentUserId, encrypted, "Vigenere");
                        JOptionPane.showMessageDialog(this, "Message encrypted and saved!");
                    }
                }
            }
        });

        // View messages button action
        viewButton.addActionListener(e -> {
            StringBuilder messages = new StringBuilder();
            String sql = "SELECT encrypted_message, cipher_type FROM messages WHERE user_id = ?";
            try (Connection conn = Database.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, currentUserId);
                ResultSet rs = pstmt.executeQuery();
                while (rs.next()) {
                    messages.append("Cipher: ").append(rs.getString("cipher_type"))
                            .append("\nMessage: ").append(rs.getString("encrypted_message"))
                            .append("\n\n");
                }
            } catch (SQLException ex) {
                messages.append("Error retrieving messages: ").append(ex.getMessage());
            }
            messageArea.setText(messages.toString());
        });

        // Decrypt messages button action
        decryptButton.addActionListener(e -> {
            String cipherText = JOptionPane.showInputDialog(this, "Enter the encrypted message to decrypt:");
            if (cipherText != null && !cipherText.isEmpty()) {
                String key = JOptionPane.showInputDialog(this, "Enter decryption key:");
                if (key != null && !key.isEmpty()) {
                    String[] options = {"Caesar", "Vigenere"};
                    int cipherChoice = JOptionPane.showOptionDialog(
                            this, "Choose Cipher:", "Cipher Selection",
                            JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]
                    );
                    String cipherType = (cipherChoice == 0) ? "Caesar" : "Vigenere";

                    String sql = "SELECT * FROM messages WHERE encrypted_message = ? AND user_id = ?";
                    try (Connection conn = Database.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
                        pstmt.setString(1, cipherText);
                        pstmt.setInt(2, currentUserId);
                        ResultSet rs = pstmt.executeQuery();

                        if (rs.next()) {
                            String storedCipherType = rs.getString("cipher_type");

                            if (!storedCipherType.equals(cipherType)) {
                                JOptionPane.showMessageDialog(this, "Cipher type mismatch!", "Error", JOptionPane.ERROR_MESSAGE);
                                return;
                            }

                            String decryptedMessage;
                            if (cipherType.equals("Caesar")) {
                                try {
                                    int shiftKey = Integer.parseInt(key);
                                    decryptedMessage = CipherManager.caesarCipher(cipherText, shiftKey, false);
                                } catch (NumberFormatException ex) {
                                    JOptionPane.showMessageDialog(this, "Invalid key format for Caesar Cipher!", "Error", JOptionPane.ERROR_MESSAGE);
                                    return;
                                }
                            } else { // Vigenere
                                decryptedMessage = CipherManager.vigenereCipher(cipherText, key, false);
                            }

                            JOptionPane.showMessageDialog(this, "Decrypted Message: " + decryptedMessage, "Decryption Success", JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            JOptionPane.showMessageDialog(this, "Message not found!", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(this, "Error accessing the database: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        // Logout button action
        logoutButton.addActionListener(e -> {
            currentUserId = -1;
            CardLayout layout = (CardLayout) getContentPane().getLayout();
            layout.show(getContentPane(), "login");
        });

        return panel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SecureCommunicationApp app = new SecureCommunicationApp();
            app.setVisible(true);
        });
    }
}
