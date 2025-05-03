package src;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Display extends JFrame implements ActionListener {

    private static HashMap<Integer, HashMap<String, String>> hash;

    private static JScrollPane pane;

    private static int HORIZONTAL_SCROLLBAR = 0;
    private static int VERTICAL_SCROLLBAR = 0;

    private static final int REFRESH_DELAY = 1000 * 5;

    ActionListener refreshMainPage = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                hash = Barcode.getTable(token);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null, "Could not refresh " +
                        "table: " + ex.getMessage());
                return;
            }
            HORIZONTAL_SCROLLBAR = pane.getHorizontalScrollBar().getValue();
            VERTICAL_SCROLLBAR = pane.getVerticalScrollBar().getValue();

            userPage();
        }
    };

    private final Timer REFRESH_TIMER = new Timer(REFRESH_DELAY, refreshMainPage);

    private static final int TOKEN_REFRESH_DELAY = 1000 * 60 * 15;

    ActionListener refreshToken = _ -> {
        JOptionPane.showMessageDialog(null, "Your session has expired. " +
                "Please log in again.");
        loginPage();
    };

    private final Timer TOKEN_REFRESH_TIMER = new Timer(TOKEN_REFRESH_DELAY, refreshToken);

    //Initializes some constants that handle size and font.
    private static final Dimension DEFAULT_SIZE = new Dimension(300, 20);
    private static final Font DEFAULT_FONT = new Font("Arial", Font.PLAIN, 20);
    private static final Font TITLE_FONT = new Font("Times New Romans", Font.BOLD, 30);

    //Initializes the token and username used in the program
    private static String token = "";
    private static String username = "";

    //Initializes the username field, password field, and submit button for the login page
    private static JTextField usernameField;
    private static JPasswordField passwordField;
    private static JButton submitButton;

    //Initializes the text field and button for the panel in the user page to pull specific boxes
    private static JTextField getPartField;
    private static JButton getPart;

    //Initializes the button used to submit a new instance for the database
    private static JButton submitAddToTableButton;

    JPasswordField oldPasswordChangeField;
    JPasswordField newPasswordChangeField;
    JPasswordField newPasswordChangeConfirmationField;

    public Display(){
        setTitle("ADI Wire DBMS");
        setIconImage(new ImageIcon(Objects.requireNonNull(this.getClass().getClassLoader().getResource("\\src\\data\\images\\App.jpg\\"))).getImage());
        revalidate();

        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(500, 500));
        setExtendedState(MAXIMIZED_BOTH);

        revalidate();
    }

    //Main page that the user must log in to
    public void loginPage(){
        getContentPane().removeAll();

        //Normal stuff just to ensure the frame is visible, exits the program on close, can only shrink
        //so small, and is full screen on start.

        //Initializes the final panel with a border layout.
        JPanel finalPanel = new JPanel(new BorderLayout());

        //Initializes the login panel to hold the login information i.e. the username and password
        //stuff. Also creates a box layout for the log info and centers it.
        JPanel loginPanel = new JPanel();
        loginPanel.setLayout(new BoxLayout(loginPanel, BoxLayout.Y_AXIS));
        loginPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        //Creates a label instructing the user how to login and centers it.
        JLabel loginLabel = new JLabel("Enter your username and password below.");
        loginLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginLabel.setFont(TITLE_FONT);

        //Creates a user info panel that will hold the username label and username text field with a
        //box layout
        JPanel userInfo = new JPanel();
        userInfo.setLayout(new BoxLayout(userInfo, BoxLayout.X_AXIS));

        //Creates the username label
        JLabel usernameText = new JLabel("Enter your Username: ");
        usernameText.setFont(DEFAULT_FONT);

        //Creates the username text field for the user to enter their username. Also sets the
        //size of the text field and centers it.
        usernameField = new JTextField();
        usernameField.setPreferredSize(DEFAULT_SIZE);
        usernameField.setMaximumSize(DEFAULT_SIZE);
        usernameField.setAlignmentX(Component.CENTER_ALIGNMENT);

        //Adds the username label and field to the user info panel to stick them next to each other
        userInfo.add(usernameText);
        userInfo.add(usernameField);

        //Same idea as above. Creates a password info panel to hold the password label and field.
        JPanel passwordInfo = new JPanel();
        passwordInfo.setLayout(new BoxLayout(passwordInfo, BoxLayout.X_AXIS));

        //Password label
        JLabel passwordText = new JLabel("Enter your Password: ");
        passwordText.setFont(DEFAULT_FONT);

        //Password field for the user to enter their password
        passwordField = new JPasswordField();
        passwordField.setPreferredSize(DEFAULT_SIZE);
        passwordField.setMaximumSize(DEFAULT_SIZE);
        passwordField.setAlignmentX(Component.CENTER_ALIGNMENT);

        //Adds an input map that checks if the user presses the enter key, so that it will press
        //the submit button and log the user in.
        passwordField.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "Submit");
        passwordField.getActionMap().put("Submit", Submit);

        //Adds the password label and field to the password info panel
        passwordInfo.add(passwordText);
        passwordInfo.add(passwordField);

        //The submit button that will log the user in with the username and password provided.
        submitButton = new JButton("Login");
        submitButton.setPreferredSize(DEFAULT_SIZE);
        submitButton.setMaximumSize(DEFAULT_SIZE);
        submitButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        //Adds an action listener to use the action performed method defined below with the
        //action command of login
        submitButton.addActionListener(this);
        submitButton.setActionCommand("login");

        //Adds the login components to the login panel.
        loginPanel.add(loginLabel);
        loginPanel.add(userInfo);
        loginPanel.add(passwordInfo);
        loginPanel.add(submitButton);

        //Adds the login panel to the final panel
        finalPanel.add(loginPanel, BorderLayout.CENTER);

        //Creates a scrollable pane with the final panel as the main pane
        JScrollPane pane = new JScrollPane(finalPanel);

        //Adds the scroll pane and refreshes the page
        add(pane);
        repaint();
        revalidate();
        usernameField.requestFocusInWindow();
    }

    //The main user page after the user gets logged in.
    private void userPage(){
        //Removes the previous page.
        getContentPane().removeAll();

        //Initializes a hashmap variable that will handle the information from the Box Table

        //Creates a panel with the getJPanel function created later on. Essentially, this puts
        //everything from the box table into the panel
        JPanel boxTableInfo = getJPanel(hash);

        //Creates a final panel with a border layout
        JPanel finalPanel = new JPanel(new BorderLayout());

        //Creates a title panel, button panel, and log out panel.
        JPanel titlePanel = new JPanel();
        JPanel buttonPanel = new JPanel();
        buttonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPanel logOutPanel = new JPanel(new BorderLayout());

        //Creates the user label for the main page and sets the font and alignment
        JLabel userLabel = new JLabel("Welcome " + username + "!");
        userLabel.setFont(TITLE_FONT);
        userLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        //Creates the panel for getting a table with the box id
        JPanel getBoxIDPanel = new JPanel();
        getBoxIDPanel.setLayout(new BoxLayout(getBoxIDPanel, BoxLayout.Y_AXIS));
        getBoxIDPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        //Creates the label the getting a table with the box id
        JLabel getPartNumberLabel = new JLabel("Enter Part Number: ");
        getPartNumberLabel.setFont(DEFAULT_FONT);
        getPartNumberLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        //Creates a text field with the option to press the enter key to activate the box id button
        //below.
        getPartField = new JTextField();
        getPartField.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "Get Part");
        getPartField.getActionMap().put("Get Part", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                getPart.doClick();
            }
        });
        getPartField.setMaximumSize(DEFAULT_SIZE);
        getPartField.setAlignmentX(Component.CENTER_ALIGNMENT);
        getPartField.setHorizontalAlignment(JTextField.CENTER);

        //Creates a submit button for getting a table with a box id
        getPart = new JButton("Submit");
        getPart.setActionCommand("Get Part");
        getPart.addActionListener(this);
        getPart.setAlignmentX(Component.CENTER_ALIGNMENT);
        getPart.setHorizontalAlignment(JButton.CENTER);

        //Adds the label, field, and submit button to the panel
        getBoxIDPanel.add(getPartNumberLabel);
        getBoxIDPanel.add(getPartField);
        getBoxIDPanel.add(getPart);
        getBoxIDPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        //Creates a new button for adding new entries to the table with the action command
        //Add New Entry and uses the action performed method later on
        JButton addToTable = new JButton("Add New Entry");
        addToTable.setMaximumSize(DEFAULT_SIZE);
        addToTable.setPreferredSize(DEFAULT_SIZE);
        addToTable.setActionCommand("Add New Entry");
        addToTable.addActionListener(this);

        //Creates a new button for adding new users to the user table with the action command
        //Add User and uses the action performed method later on.
        JButton addUser = new JButton("Add User");
        addUser.setMaximumSize(DEFAULT_SIZE);
        addUser.setPreferredSize(DEFAULT_SIZE);
        addUser.setActionCommand("Add User");
        addUser.addActionListener(this);

        //Creates a logout button that uses the action performed method later on
        JButton logOut = new JButton("Log out");
        logOut.setActionCommand("Logout");
        logOut.addActionListener(this);
        logOut.setMaximumSize(DEFAULT_SIZE);

        //Adds the logout button the left side of the logout panel
        logOutPanel.add(logOut, BorderLayout.WEST);

        //Adds the get box id panel and add to table panel to the button panel
        buttonPanel.add(getBoxIDPanel);
        buttonPanel.add(addToTable);
        buttonPanel.add(addUser);

        //Creates a final button and title panel to properly format the buttons and the title
        JPanel finalButtonAndTitlePanel = new JPanel(new BorderLayout());
        finalButtonAndTitlePanel.add(titlePanel, BorderLayout.NORTH);
        finalButtonAndTitlePanel.add(buttonPanel, BorderLayout.CENTER);

        //Adds the user label to the title panel
        titlePanel.add(userLabel);

        //Creates a logout and title panel to properly format the logout button in the top left hand
        //corner and have the title slightly below the logout button
        JPanel logOutAndTitlePanel = new JPanel(new BorderLayout());
        logOutAndTitlePanel.add(finalButtonAndTitlePanel, BorderLayout.CENTER);
        logOutAndTitlePanel.add(logOutPanel, BorderLayout.NORTH);

        //Creates a scroll pane with the final panel to ensure everything from the table is in view
        pane = new JScrollPane(finalPanel);

        //Adds the logout and title panel to the final panel in the top of the page. Then adds the box
        //table info to the page
        finalPanel.add(logOutAndTitlePanel, BorderLayout.NORTH);
        finalPanel.add(boxTableInfo);

        pane.getVerticalScrollBar().setUnitIncrement(16);

        //Adds the pane to the main page and refreshes.
        add(pane);
        repaint();
        revalidate();

        pane.getHorizontalScrollBar().setValue(HORIZONTAL_SCROLLBAR);
        pane.getVerticalScrollBar().setValue(VERTICAL_SCROLLBAR);
        pane.revalidate();
        pane.repaint();

        getPartField.requestFocusInWindow();

        REFRESH_TIMER.start();
    }

    private void partNumberPage(){
        //Removes the previous page.
        getContentPane().removeAll();

        //Initializes a hashmap variable that will handle the information from the Box Table

        //Creates a panel with the getJPanel function created later on. Essentially, this puts
        //everything from the box table into the panel
        JPanel boxTableInfo = getJPanel(hash);

        //Creates a final panel with a border layout
        JPanel finalPanel = new JPanel(new BorderLayout());

        //Creates a title panel, button panel, and log out panel.
        JPanel titlePanel = new JPanel();
        JPanel buttonPanel = new JPanel();
        buttonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPanel logOutPanel = new JPanel(new BorderLayout());

        //Creates the user label for the main page and sets the font and alignment
        JLabel userLabel = new JLabel("Welcome " + username + "!");
        userLabel.setFont(TITLE_FONT);
        userLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        //Creates the panel for getting a table with the box id
        JPanel getBoxIDPanel = new JPanel();
        getBoxIDPanel.setLayout(new BoxLayout(getBoxIDPanel, BoxLayout.Y_AXIS));
        getBoxIDPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        //Creates the label the getting a table with the box id
        JLabel getBoxIDLabel = new JLabel("Enter box number: ");
        getBoxIDLabel.setFont(DEFAULT_FONT);
        getBoxIDLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        //Creates a text field with the option to press the enter key to activate the box id button
        //below.
        getPartField = new JTextField();
        getPartField.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "Get Part");
        getPartField.getActionMap().put("Get Part", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                getPart.doClick();
            }
        });
        getPartField.setMaximumSize(DEFAULT_SIZE);
        getPartField.setAlignmentX(Component.CENTER_ALIGNMENT);
        getPartField.setHorizontalAlignment(JTextField.CENTER);

        //Creates a submit button for getting a table with a box id
        getPart = new JButton("Submit");
        getPart.setActionCommand("Get Part");
        getPart.addActionListener(this);
        getPart.setAlignmentX(Component.CENTER_ALIGNMENT);
        getPart.setHorizontalAlignment(JButton.CENTER);

        //Adds the label, field, and submit button to the panel
        getBoxIDPanel.add(getBoxIDLabel);
        getBoxIDPanel.add(getPartField);
        getBoxIDPanel.add(getPart);
        getBoxIDPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        //Creates a new button for adding new entries to the table with the action command
        //Add New Entry and uses the action performed method later on
        JButton addToTable = new JButton("Add New Entry");
        addToTable.setMaximumSize(DEFAULT_SIZE);
        addToTable.setPreferredSize(DEFAULT_SIZE);
        addToTable.setActionCommand("Add New Entry");
        addToTable.addActionListener(this);

        //Creates a new button for adding new users to the user table with the action command
        //Add User and uses the action performed method later on.
        JButton addUser = new JButton("Add User");
        addUser.setMaximumSize(DEFAULT_SIZE);
        addUser.setPreferredSize(DEFAULT_SIZE);
        addUser.setActionCommand("Add User");
        addUser.addActionListener(this);

        //Creates a logout button that uses the action performed method later on
        JButton logOut = new JButton("Log out");
        logOut.setActionCommand("Logout");
        logOut.addActionListener(this);
        logOut.setMaximumSize(DEFAULT_SIZE);

        //Adds the logout button the left side of the logout panel
        logOutPanel.add(logOut, BorderLayout.WEST);

        //Adds the get box id panel and add to table panel to the button panel
        buttonPanel.add(getBoxIDPanel);
        buttonPanel.add(addToTable);
        buttonPanel.add(addUser);

        //Creates a final button and title panel to properly format the buttons and the title
        JPanel finalButtonAndTitlePanel = new JPanel(new BorderLayout());
        finalButtonAndTitlePanel.add(titlePanel, BorderLayout.NORTH);
        finalButtonAndTitlePanel.add(buttonPanel, BorderLayout.CENTER);

        //Adds the user label to the title panel
        titlePanel.add(userLabel);

        //Creates a logout and title panel to properly format the logout button in the top left hand
        //corner and have the title slightly below the logout button
        JPanel logOutAndTitlePanel = new JPanel(new BorderLayout());
        logOutAndTitlePanel.add(finalButtonAndTitlePanel, BorderLayout.CENTER);
        logOutAndTitlePanel.add(logOutPanel, BorderLayout.NORTH);

        //Creates a scroll pane with the final panel to ensure everything from the table is in view
        pane = new JScrollPane(finalPanel);

        //Adds the logout and title panel to the final panel in the top of the page. Then adds the box
        //table info to the page
        finalPanel.add(logOutAndTitlePanel, BorderLayout.NORTH);
        finalPanel.add(boxTableInfo);

        pane.getVerticalScrollBar().setUnitIncrement(16);

        //Adds the pane to the main page and refreshes.
        add(pane);
        repaint();
        revalidate();

        pane.getHorizontalScrollBar().setValue(HORIZONTAL_SCROLLBAR);
        pane.getVerticalScrollBar().setValue(VERTICAL_SCROLLBAR);
        pane.revalidate();
        pane.repaint();

        getPartField.requestFocusInWindow();
    }

    private void changePasswordPage(){
        getContentPane().removeAll();

        JPanel finalPanel = new JPanel(new BorderLayout());

        JLabel passwordChangeTitle = new JLabel("Password Change");
        passwordChangeTitle.setFont(TITLE_FONT);

        JButton submitPasswordChangeButton = new JButton("Submit");

        JPanel oldPasswordPanel = new JPanel();
        oldPasswordPanel.setLayout(new BoxLayout(oldPasswordPanel, BoxLayout.X_AXIS));
        JLabel oldPasswordChangeLabel = new JLabel("Enter your old password:");
        oldPasswordChangeField = new JPasswordField();

        oldPasswordPanel.add(oldPasswordChangeLabel);
        oldPasswordPanel.add(oldPasswordChangeField);

        JPanel newPasswordPanel = new JPanel();
        newPasswordPanel.setLayout(new BoxLayout(newPasswordPanel, BoxLayout.X_AXIS));
        JLabel newPasswordChangeLabel = new JLabel("Enter your new password: ");
        newPasswordChangeField = new JPasswordField();

        newPasswordPanel.add(newPasswordChangeLabel);
        newPasswordPanel.add(newPasswordChangeField);

        JPanel newPasswordConfirmationPanel = new JPanel();
        newPasswordConfirmationPanel.setLayout(new BoxLayout(newPasswordConfirmationPanel, BoxLayout.X_AXIS));
        JLabel newPasswordChangeConfirmationLabel = new JLabel("Confirm new password: ");
        newPasswordChangeConfirmationField = new JPasswordField();

        newPasswordChangeConfirmationField.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "SubmitPasswordChange");
        newPasswordChangeConfirmationField.getActionMap().put("SubmitPasswordChange", new AbstractAction("SubmitPasswordChange") {
            @Override
            public void actionPerformed(ActionEvent e) {
                submitPasswordChangeButton.doClick();
            }
        });

        newPasswordConfirmationPanel.add(newPasswordChangeConfirmationLabel);
        newPasswordConfirmationPanel.add(newPasswordChangeConfirmationField);

        submitPasswordChangeButton.setActionCommand("Submit Password Change");
        submitPasswordChangeButton.addActionListener(this);

        JPanel completePasswordChangePanel = new JPanel();
        completePasswordChangePanel.setLayout(new BoxLayout(completePasswordChangePanel, BoxLayout.Y_AXIS));

        completePasswordChangePanel.add(oldPasswordPanel);
        completePasswordChangePanel.add(newPasswordPanel);
        completePasswordChangePanel.add(newPasswordConfirmationPanel);
        completePasswordChangePanel.add(submitPasswordChangeButton);

        finalPanel.add(passwordChangeTitle, BorderLayout.NORTH);
        finalPanel.add(completePasswordChangePanel, BorderLayout.CENTER);

        add(finalPanel);

        repaint();
        revalidate();
    }

    private JPanel getJPanel(HashMap<Integer, HashMap<String, String>> hash) {
        if (hash.isEmpty()){
            JPanel boxTableInfo = new JPanel(new BorderLayout());
            JLabel text = new JLabel("Nothing to see here.");
            text.setFont(TITLE_FONT);
            text.setAlignmentX(Component.CENTER_ALIGNMENT);
            text.setHorizontalAlignment(JLabel.CENTER);

            boxTableInfo.add(text, BorderLayout.CENTER);
            return boxTableInfo;
        }

        JPanel boxTableInfo = new JPanel();
        boxTableInfo.setLayout(new GridLayout(hash.size() + 1, 6, 20, 40));
        boxTableInfo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel partNumberLabel = new JLabel("Part Number");
        partNumberLabel.setFont(TITLE_FONT);
        JLabel partTypeLabel = new JLabel("Part Type");
        partTypeLabel.setFont(TITLE_FONT);
        JLabel partDescLabel = new JLabel("Part Description");
        partDescLabel.setFont(TITLE_FONT);
        JLabel partNoteLabel = new JLabel("Part Note");
        partNoteLabel.setFont(TITLE_FONT);
        JLabel editLabel = new JLabel("Update Part Info");
        editLabel.setFont(TITLE_FONT);
        JLabel deleteLabel = new JLabel("Delete Entry");
        deleteLabel.setFont(TITLE_FONT);

        boxTableInfo.add(partNumberLabel);
        boxTableInfo.add(partTypeLabel);
        boxTableInfo.add(partDescLabel);
        boxTableInfo.add(partNoteLabel);
        boxTableInfo.add(editLabel);
        boxTableInfo.add(deleteLabel);

        for (Map.Entry<Integer, HashMap<String, String>> entry: hash.entrySet()){
            HashMap<String, String> entryValue = entry.getValue();
            JLabel partNumber = new JLabel(entryValue.get("Part Number"));
            partNumber.setAlignmentX(Component.CENTER_ALIGNMENT);
            partNumber.setFont(DEFAULT_FONT);
            JLabel partType = new JLabel(entryValue.get("Part Type"));
            partType.setAlignmentX(Component.CENTER_ALIGNMENT);
            partType.setFont(DEFAULT_FONT);
            JLabel partDesc = new JLabel(entryValue.get("Part Description"));
            partDesc.setAlignmentX(Component.CENTER_ALIGNMENT);
            partDesc.setFont(DEFAULT_FONT);
            JLabel partNote = new JLabel(entryValue.get("Part Note"));
            partNote.setAlignmentX(Component.CENTER_ALIGNMENT);
            partNote.setFont(DEFAULT_FONT);

            JButton editButton = getJButton(partNumber.getText());
            editButton.setPreferredSize(new Dimension(40, 40));
            editButton.setMaximumSize(new Dimension(40, 40));

            JButton deleteButton = getDeleteButton(partNumber.getText());

            boxTableInfo.add(partNumber);
            boxTableInfo.add(partType);
            boxTableInfo.add(partDesc);
            boxTableInfo.add(partNote);
            boxTableInfo.add(editButton);
            boxTableInfo.add(deleteButton);
        }
        return boxTableInfo;
    }

    private JButton getDeleteButton(String partNumber) {
        JButton deleteButton = new JButton("Delete");
        deleteButton.setPreferredSize(new Dimension(40, 40));
        deleteButton.setMaximumSize(new Dimension(40, 40));
        deleteButton.addActionListener(_ -> {
            int choice = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete " +
                    "Part Number: " + partNumber + "?", "Delete Entry", JOptionPane.YES_NO_OPTION);

            if (choice != 0){
                return;
            }

            String responseCode;
            try {
                responseCode = Barcode.deleteFromTable(token, partNumber);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null, "Unable to delete entry: " +
                        ex.getMessage());
                return;
            }

            if (responseCode.equals("Not Authorized")){
                JOptionPane.showMessageDialog(null, "You are not " +
                        "authorized to complete this action. Please contact your administrator.");
                return;
            }

            try {
                hash = Barcode.getTable(token);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }

            userPage();
        });
        return deleteButton;
    }

    private JButton getJButton(String boxNumber) {
        JButton editButton = new JButton("Edit");
        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
        return editButton;
    }

    private static final Action Submit = new AbstractAction("Submit") {
        @Override
        public void actionPerformed(ActionEvent e) {
            submitButton.doClick();
        }
    };

    private static final Action GetBoxID = new AbstractAction("GetBoxID") {
        @Override
        public void actionPerformed(ActionEvent e) {
            getPart.doClick();
        }
    };

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("login")){
            Hash hashObj = new Hash();
            String password = hashObj.scrambleString(passwordField.getPassword());
            //String password = String.valueOf(passwordField.getPassword());
            username = usernameField.getText();

            token = "";

            try {
                token = Barcode.getToken(username, password);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null, "Unable to connect to server: " + ex.getMessage());
                return;
            }

            if (token.equals("Unauthorized User")){
                JOptionPane.showMessageDialog(null, "Unable to login: " + token);
                return;
            }

            if (password.equals(hashObj.scrambleString("".toCharArray()))){
                JOptionPane.showMessageDialog(null, "You need to change" +
                        " your password.");
                changePasswordPage();
                return;
            }

            //Tries to get the table from the database and stores it in the hash variable
            try {
                hash = Barcode.getTable(token);
            } catch (IOException e1) {
                JOptionPane.showMessageDialog(null, "Error loading table. Please" +
                        " try logging out and logging back in: " + e1.getMessage());
                return;
            }

            TOKEN_REFRESH_TIMER.start();
            userPage();
        }
        else if (e.getActionCommand().equals("Logout")){
            token = "";
            REFRESH_TIMER.stop();
            TOKEN_REFRESH_TIMER.stop();
            loginPage();
        }
        else if (e.getActionCommand().equals("Get Part")){
            String partNumber = getPartField.getText();
            getPartField.setText("");

            if (partNumber.isEmpty()){
                try {
                    hash = Barcode.getTable(token);
                    userPage();
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(null, "Unable to load table. " +
                            "Try signing out and back in to resolve this.");
                }
                return;
            }

            try {
                hash = Barcode.getPart(token, partNumber);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null, "Error getting boxID: " + ex.getMessage());
                return;
            }

            if (hash == null){
                try {
                    JOptionPane.showMessageDialog(null, "Unable to find " +
                            "item with the part with specified part number: " + partNumber);
                    hash = Barcode.getTable(token);
                    getContentPane().removeAll();
                    userPage();
                    return;
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(null, "Unable to load " +
                            "table. Try logging out and logging back in.");
                }
            }
            REFRESH_TIMER.stop();
            getContentPane().removeAll();
            partNumberPage();
        }
        else if (e.getActionCommand().equals("Add New Entry")){
            JFrame f = new JFrame();
            f.setVisible(true);
            f.setResizable(false);
            f.setMinimumSize(new Dimension(500, 500));
            f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            f.setLocationRelativeTo(this);

            JPanel finalPanel = new JPanel();
            finalPanel.setLayout(new BoxLayout(finalPanel, BoxLayout.Y_AXIS));

            JLabel addPartNumberLabel = new JLabel("Enter the Part Number");
            addPartNumberLabel.setFont(DEFAULT_FONT);
            addPartNumberLabel.setPreferredSize(DEFAULT_SIZE);

            JTextField addPartNumberField = new JTextField();
            addPartNumberField.setPreferredSize(DEFAULT_SIZE);

            JLabel addPartTypeLabel = new JLabel("Enter the Part Type");
            addPartTypeLabel.setFont(DEFAULT_FONT);
            addPartTypeLabel.setPreferredSize(DEFAULT_SIZE);

            JTextField addPartTypeField = new JTextField();
            addPartTypeField.setPreferredSize(DEFAULT_SIZE);

            JLabel addPartDescLabel = new JLabel("Enter the description for the part");
            addPartDescLabel.setFont(DEFAULT_FONT);
            addPartDescLabel.setPreferredSize(DEFAULT_SIZE);

            JTextField addPartDescField = new JTextField();
            addPartDescField.setPreferredSize(DEFAULT_SIZE);

            JLabel addPartNoteLabel = new JLabel("Enter note for the part");
            addPartNoteLabel.setFont(DEFAULT_FONT);
            addPartNoteLabel.setPreferredSize(DEFAULT_SIZE);

            JTextField addPartNoteField = new JTextField();
            addPartNoteField.setPreferredSize(DEFAULT_SIZE);
            addPartNoteField.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "Submit Add To Table");
            addPartNoteField.getActionMap().put("Submit Add To Table", new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    submitAddToTableButton.doClick();
                }
            });

            submitAddToTableButton = new JButton("Submit");
            submitAddToTableButton.addActionListener(_ -> {
                String partNumber = addPartNumberField.getText();
                String partType = addPartTypeField.getText();
                String partDesc = addPartDescField.getText();
                String partNote = addPartNoteField.getText();

                double wireLength;

                if (partNumber.isEmpty() || partType.isEmpty()){
                    JOptionPane.showMessageDialog(null, "Incorrect usage." +
                            " Please enter a value for part number and part type");
                    return;
                }

                String responseCode;
                try {
                    responseCode = Barcode.addToTable(token, partNumber, partType, partDesc, partNote);
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(null, "Could not add" +
                            " to table: " + ex.getMessage());
                    return;
                }

                JOptionPane.showMessageDialog(null, responseCode);

                f.dispose();

                try {
                    hash = Barcode.getTable(token);
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(null, "Unable to " +
                            "load table: " + ex.getMessage());
                }

                REFRESH_TIMER.restart();
                userPage();
            });

            finalPanel.add(addPartNumberLabel);
            finalPanel.add(addPartNumberField);
            finalPanel.add(addPartTypeLabel);
            finalPanel.add(addPartTypeField);
            finalPanel.add(addPartDescLabel);
            finalPanel.add(addPartDescField);
            finalPanel.add(addPartNoteLabel);
            finalPanel.add(addPartNoteField);
            finalPanel.add(submitAddToTableButton);

            f.add(finalPanel);

            f.revalidate();
            f.repaint();
        }
        else if (e.getActionCommand().equals("Add User")){
            JFrame f = new JFrame();
            f.setLocation(this.getLocationOnScreen());
            f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            f.setMinimumSize(new Dimension(500, 500));
            f.setVisible(true);
            f.setResizable(false);

            String[] options = {"Modify Wire Table", "Modify Parts Table","Add Users", "Delete Users"};


            JPanel finalPanel = new JPanel(new BorderLayout());

            JPanel boxPanel = new JPanel();
            boxPanel.setLayout(new BoxLayout(boxPanel, BoxLayout.Y_AXIS));

            JLabel addUserTitle = new JLabel("Add User");
            addUserTitle.setFont(TITLE_FONT);

            JLabel passwordInfo = new JLabel("Password is defaulted to be empty");

            JLabel addUsernameLabel = new JLabel("Enter Username:");
            JTextField addUsernameField = new JTextField();

            JLabel permissionsLabel = new JLabel("Select User Permissions");
            JCheckBox updateWireTablePermission = new JCheckBox(options[0]);
            JCheckBox updatePartsTablePermission = new JCheckBox(options[1]);
            JCheckBox addUserPermission = new JCheckBox(options[2]);
            JCheckBox deleteUserPermission = new JCheckBox(options[3]);

            JButton addUserButton = new JButton("Submit");
            addUserButton.setActionCommand("Add User Submit Button");
            addUserButton.addActionListener(_ -> {
                Hash hashObj = new Hash();

                String username = addUsernameField.getText();
                String password = hashObj.scrambleString("".toCharArray());
                int modifyWireBoxTableAccess = 0;
                if (updateWireTablePermission.isSelected()) {
                    modifyWireBoxTableAccess = 1;
                }
                int modifyPartsTableAccess = 0;
                if (updatePartsTablePermission.isSelected()){
                    modifyPartsTableAccess = 1;
                }

                int addUserAccess = 0;
                if (addUserPermission.isSelected()){
                    addUserAccess = 1;
                }
                int deleteUserAccess = 0;
                if (deleteUserPermission.isSelected()){
                    deleteUserAccess = 1;
                }

                String responseCode;

                try {
                    responseCode = Barcode.addUser(token, username, password, modifyWireBoxTableAccess, modifyPartsTableAccess,addUserAccess, deleteUserAccess);
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(null, "Could" +
                            " not add user: " + ex.getMessage());
                    return;
                }

                if (responseCode.equals("Not Authorized")){
                    JOptionPane.showMessageDialog(null, "You are not " +
                            "authorized to complete this action. Please contact your administrator.");
                    f.dispose();
                    return;
                }

                f.dispose();
                userPage();
            });

            boxPanel.add(passwordInfo);
            boxPanel.add(addUsernameLabel);
            boxPanel.add(addUsernameField);
            boxPanel.add(permissionsLabel);
            boxPanel.add(updateWireTablePermission);
            boxPanel.add(updatePartsTablePermission);
            boxPanel.add(addUserPermission);
            boxPanel.add(deleteUserPermission);
            boxPanel.add(addUserButton);

            finalPanel.add(addUserTitle, BorderLayout.NORTH);
            finalPanel.add(boxPanel, BorderLayout.CENTER);

            f.add(finalPanel);

            f.repaint();
            f.revalidate();
        }
        else if (e.getActionCommand().equals("Submit Password Change")){
            Hash hashObj = new Hash();

            if (!hashObj.scrambleString(newPasswordChangeField.getPassword()).equals(
                    hashObj.scrambleString(newPasswordChangeConfirmationField.getPassword())
            )){
                JOptionPane.showMessageDialog(null, "New passwords must match.");
                return;
            }
            else if (hashObj.scrambleString(newPasswordChangeField.getPassword()).equals(
                    hashObj.scrambleString("".toCharArray())
            )){
                JOptionPane.showMessageDialog(null, "New password cannot" +
                        " be empty");
                return;
            }

            String newPassword = hashObj.scrambleString(newPasswordChangeField.getPassword());
            String oldPassword = hashObj.scrambleString(oldPasswordChangeField.getPassword());

            try {
                Barcode.changePassword(token, oldPassword, newPassword);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null, "Error updating password");
                return;
            }

            JOptionPane.showMessageDialog(null, "Password changed successfully.");
            try {
                hash = Barcode.getTable(token);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null, "Could not load table.");
            }
            userPage();
        }
    }

    private static final Action submitAddToTable = new AbstractAction("Submit Add To Table") {
        @Override
        public void actionPerformed(ActionEvent e) {
            submitAddToTableButton.doClick();
        }
    };
}
