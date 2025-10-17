package za.ac.cput.studentenrollmentapp.client;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;
import java.util.List;
import za.ac.cput.studentenrollmentapp.domain.Student;
import za.ac.cput.studentenrollmentapp.domain.Course;

/**
 *
 * @author 220239215 Lisakhanya Tshokolo
 */
public class EnrollmentClient extends JFrame implements ActionListener {

    //network component
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    //student , admin and add cpurse components
    private JTextField txtUsername, txtStudId, txtFirst, txtLast, txtEmail, txtPass;
    private JTextField txtCourseCode, txtCourseName;
    private JPasswordField txtPassword;
    private JComboBox<String> cmbRole;
    private JButton btnLogin, btnLogout, btnAdminLogout;
    private JButton btnAddStudent, btnAddCourse;
    private JButton btnEnroll, btnrefresh, btnrefresh2;

    private JTextField txtSearchCourse, txtSearchStudent;
    private JButton btnSearchCourse, btnSearchStudent;
    private JButton btnRefreshCourseSearch, btnRefreshStudentSearch;
    private JTable tblCourseResults, tblStudentResults;

    private JTable tblCourses, tblMyCourses;
    private String currentUserId, currentRole;

    //panels
    private JPanel mainPanel;
    private CardLayout cardLayout;

    public EnrollmentClient() {
        setTitle("Student Enrollment System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 500);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        createLoginPanel();
        createAdminPanel();
        createStudentPanel();

        add(mainPanel);
        showLoginPanel();
    }

    private void createLoginPanel() {
        //loginPanel
        JPanel loginPanel = new JPanel();
        loginPanel.setLayout(new BoxLayout(loginPanel, BoxLayout.Y_AXIS));
        loginPanel.setBorder(BorderFactory.createEmptyBorder(50, 100, 50, 100));

        JLabel lblTitle = new JLabel("Student Enrollment System", JLabel.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 20));
        lblTitle.setAlignmentX(CENTER_ALIGNMENT);
        loginPanel.add(lblTitle);
        loginPanel.add(Box.createRigidArea(new Dimension(0, 30)));

        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        userPanel.add(new JLabel("Username:"));
        txtUsername = new JTextField(15);
        userPanel.add(txtUsername);
        loginPanel.add(userPanel);
        loginPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        JPanel passPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        passPanel.add(new JLabel("Password:"));
        txtPassword = new JPasswordField(15);
        passPanel.add(txtPassword);
        loginPanel.add(passPanel);
        loginPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        JPanel rolePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        rolePanel.add(new JLabel("Role:"));
        cmbRole = new JComboBox<>(new String[]{"student", "admin"});
        rolePanel.add(cmbRole);
        loginPanel.add(rolePanel);
        loginPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        JPanel buttonPanel = new JPanel();
        btnLogin = new JButton("Login");
        JButton btnExit = new JButton("Exit");
        buttonPanel.add(btnLogin);
        buttonPanel.add(btnExit);
        loginPanel.add(buttonPanel);

        mainPanel.add(loginPanel, "LOGIN");

        btnLogin.addActionListener(this);
        btnExit.addActionListener(e -> System.exit(0));
        txtPassword.addActionListener(this);
    }

    private void createAdminPanel() {
        //admin panel
        JPanel adminPanel = new JPanel(new BorderLayout());

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        headerPanel.add(new JLabel("Admin Dashboard"), BorderLayout.WEST);

        btnAdminLogout = new JButton("Logout");
        headerPanel.add(btnAdminLogout, BorderLayout.EAST);
        adminPanel.add(headerPanel, BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.add("Add Student", createStudentFormPanel());
        tabs.add("Add Course", createCourseFormPanel());
        tabs.add("Search Course", createCourseQueryPanel());
        tabs.add("Search Student", createStudentQueryPanel());
        adminPanel.add(tabs, BorderLayout.CENTER);

        mainPanel.add(adminPanel, "ADMIN");
        btnAdminLogout.addActionListener(this);
    }

    private JPanel createStudentFormPanel() {
        //add student panel
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        txtStudId = new JTextField(15);
        txtFirst = new JTextField(15);
        txtLast = new JTextField(15);
        txtPass = new JTextField(15);
        txtEmail = new JTextField(15);

        panel.add(createRow("Student Number:", txtStudId));
        panel.add(createRow("First Name:", txtFirst));
        panel.add(createRow("Last Name:", txtLast));
        panel.add(createRow("Password:", txtPass));
        panel.add(createRow("Email:", txtEmail));

        JPanel btnPanel = new JPanel();
        btnAddStudent = new JButton("Add Student");
        btnPanel.add(btnAddStudent);
        panel.add(btnPanel);

        btnAddStudent.addActionListener(this);

        return panel;
    }

    private JPanel createCourseFormPanel() {
        //enroll course panel
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(50, 100, 50, 100));

        txtCourseCode = new JTextField(15);
        txtCourseName = new JTextField(15);

        panel.add(createRow("Course Code:", txtCourseCode));
        panel.add(createRow("Course Name:", txtCourseName));

        JPanel btnPanel = new JPanel();
        btnAddCourse = new JButton("Add Course");
        btnrefresh = new JButton("refresh");
        btnPanel.add(btnAddCourse);
        btnPanel.add(btnrefresh);
        panel.add(btnPanel);

        btnrefresh.addActionListener(this);
        btnAddCourse.addActionListener(this);

        return panel;
    }

    private JPanel createCourseQueryPanel() {
        //
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Search panel at top
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Course Code:"));
        txtSearchCourse = new JTextField(10);
        searchPanel.add(txtSearchCourse);

        btnSearchCourse = new JButton("Search Students");
        searchPanel.add(btnSearchCourse);
        
        btnRefreshCourseSearch = new JButton("Refresh");
        searchPanel.add(btnRefreshCourseSearch);
    
        panel.add(searchPanel, BorderLayout.NORTH);

        // Results table
        String[] columns = {"Student ID", "First Name", "Last Name", "Email"};
        tblCourseResults = new JTable(new DefaultTableModel(columns, 0));
        panel.add(new JScrollPane(tblCourseResults), BorderLayout.CENTER);

        btnSearchCourse.addActionListener(this);
        btnRefreshCourseSearch.addActionListener(this);

        return panel;
    }

    private JPanel createStudentQueryPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Search panel at top
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Student ID:"));
        txtSearchStudent = new JTextField(10);
        searchPanel.add(txtSearchStudent);

        btnSearchStudent = new JButton("Search Courses");
        searchPanel.add(btnSearchStudent);
        
        btnRefreshStudentSearch = new JButton("Refresh");
        searchPanel.add(btnRefreshStudentSearch);

        panel.add(searchPanel, BorderLayout.NORTH);
        String[] columns = {"Course Code", "Course Name"};
        tblStudentResults = new JTable(new DefaultTableModel(columns, 0));
        panel.add(new JScrollPane(tblStudentResults), BorderLayout.CENTER);

        btnSearchStudent.addActionListener(this);
        btnRefreshStudentSearch.addActionListener(this);

        return panel;
    }

    private JPanel createRow(String label, JTextField field) {
        //create row to make the gui look clean and readable
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT));
        row.add(new JLabel(label));
        row.add(field);
        return row;
    }

    private void createStudentPanel() {
        //student panel
        JPanel studentPanel = new JPanel(new BorderLayout());

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        headerPanel.add(new JLabel("Student Dashboard"), BorderLayout.WEST);

        btnLogout = new JButton("Logout");
        headerPanel.add(btnLogout, BorderLayout.EAST);
        studentPanel.add(headerPanel, BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.add("Available Courses", createAvailableCoursesPanel());
        tabs.add("My Enrollments", createMyCoursesPanel());
        studentPanel.add(tabs, BorderLayout.CENTER);

        mainPanel.add(studentPanel, "STUDENT");
        btnLogout.addActionListener(this);
    }

    private JPanel createAvailableCoursesPanel() {
        //available course panel 
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] columns = {"Course Code", "Course Name"};
        tblCourses = new JTable(new DefaultTableModel(columns, 0));
        panel.add(new JScrollPane(tblCourses), BorderLayout.CENTER);

        JPanel btnPanel = new JPanel();
        btnEnroll = new JButton("Enroll in Selected Course");
        btnPanel.add(btnEnroll);
        panel.add(btnPanel, BorderLayout.SOUTH);

        btnEnroll.addActionListener(this);

        return panel;
    }

    private JPanel createMyCoursesPanel() {
        //student course panel
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] columns = {"Course Code", "Course Name"};
        tblMyCourses = new JTable(new DefaultTableModel(columns, 0));
        panel.add(new JScrollPane(tblMyCourses), BorderLayout.CENTER);

        JPanel btnPanel = new JPanel();
        btnrefresh2 = new JButton("Refresh");
        btnPanel.add(btnrefresh2);
        panel.add(btnPanel, BorderLayout.SOUTH);

        btnrefresh2.addActionListener(this);
        return panel;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();

        try {
            if (src == btnLogin || src == txtPassword) {
                loginUser();
            } else if (src == btnLogout || src == btnAdminLogout) {
                logout();
            } else if (src == btnAddStudent) {
                addStudent();
            } else if (src == btnAddCourse) {
                addCourse();
            } else if (src == btnrefresh) {
                loadCourses();
            } else if (src == btnrefresh2) {
                loadMyCourses();
            } else if (src == btnEnroll) {
                enrollCourse();
            } else if (src == btnSearchCourse) {
                searchStudentsByCourse();
            } else if (src == btnSearchStudent) {
                searchCoursesByStudent();
            } else if (src == btnRefreshCourseSearch) {
                refreshCourseSearch();
            } else if (src == btnRefreshStudentSearch) {
                refreshStudentSearch();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void loginUser() throws Exception {
        //log in user(admin or student)
        String username = txtUsername.getText().trim().toLowerCase();
        String password = new String(txtPassword.getPassword()).trim();
        String role = cmbRole.getSelectedItem().toString().trim().toLowerCase();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter username & password!");
            return;
        }

        connectToServer();

        out.writeObject("LOGIN");
        out.writeObject(username);
        out.writeObject(password);
        out.writeObject(role);
        out.flush();

        boolean success = in.readBoolean();
        if (success) {
            currentUserId = username;
            currentRole = role;
            JOptionPane.showMessageDialog(this, "Welcome " + username);

            if (role.equals("admin")) {
                showAdminPanel();
            } else {
                showStudentPanel();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Invalid credentials!");
        }
    }

    private void connectToServer() throws IOException {
        //connecting to the server
        if (socket == null || socket.isClosed()) {
            socket = new Socket("localhost", 12345);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
        }
    }

    private void showLoginPanel() {
        //login panel
        cardLayout.show(mainPanel, "LOGIN");
        clearLoginFields();
    }

    private void showAdminPanel() {
        //admin panel
        cardLayout.show(mainPanel, "ADMIN");
    }

    private void showStudentPanel() throws Exception {
        //studentpanel
        cardLayout.show(mainPanel, "STUDENT");
        loadCourses();
        loadMyCourses();
    }

    private void logout() {
        //logout
        try {
            if (out != null) {
                out.writeObject("EXIT");
                out.flush();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (Exception ignored) {
        }
        showLoginPanel();
        JOptionPane.showMessageDialog(this, "Logged out successfully!");
    }

    private void clearLoginFields() {
        //clear fields
        txtUsername.setText("");
        txtPassword.setText("");
        cmbRole.setSelectedIndex(0);
    }

    private void clearStudentFields() {
        //clear fields
        txtStudId.setText("");
        txtFirst.setText("");
        txtLast.setText("");
        txtEmail.setText("");
        txtPass.setText("");
    }

    private void clearCourseFields() {
        //clear fields
        txtCourseCode.setText("");
        txtCourseName.setText("");
    }

    private void addStudent() throws Exception {
        //add student to the database
        if (txtStudId.getText().isEmpty() || txtFirst.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please Fill all fields!");
            return;
        }

        Student s = new Student(
                txtStudId.getText().trim().toLowerCase(),
                txtFirst.getText().trim(),
                txtLast.getText().trim(),
                txtPass.getText().trim(),
                txtEmail.getText().trim(),
                "student"
        );

        out.writeObject("ADD_STUDENT");
        out.writeObject(s);
        out.flush();

        boolean added = in.readBoolean();
        JOptionPane.showMessageDialog(this, added ? "Student added!" : "Failed to add student!");
        if (added) {
            clearStudentFields();
        }
    }

    private void addCourse() throws Exception {
        //addcourse to the database
        if (txtCourseCode.getText().isEmpty() || txtCourseName.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please Fill all fields!");
            return;
        }

        Course c = new Course(txtCourseCode.getText().trim().toUpperCase(), txtCourseName.getText().trim());
        out.writeObject("ADD_COURSE");
        out.writeObject(c);
        out.flush();

        boolean added = in.readBoolean();
        JOptionPane.showMessageDialog(this, added ? "Course added!" : "Failed to add course!");
        if (added) {
            clearCourseFields();
        }
    }

    private void enrollCourse() throws Exception {
        //student enroll to the course
        int row = tblCourses.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a course first to enroll in!");
            return;
        }
        String courseCode = tblCourses.getValueAt(row, 0).toString();

        out.writeObject("ENROL_STUDENT");
        out.writeObject(currentUserId);
        out.writeObject(courseCode);
        out.flush();

        boolean success = in.readBoolean();
        JOptionPane.showMessageDialog(this, success ? "Enrolled successfully!" : "Already enrolled!");
    }

    private void loadCourses() throws IOException, ClassNotFoundException {
        //view available courses
        try {
            out.writeObject("GET_COURSES");
            out.flush();
            List<Course> courses = (List<Course>) in.readObject();
            DefaultTableModel model = (DefaultTableModel) tblCourses.getModel();
            model.setRowCount(0);
            for (Course c : courses) {
                model.addRow(new Object[]{c.getCourseCode(), c.getCourseName()});
            }
            System.out.println("Loaded " + courses.size() + " courses");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading courses: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadMyCourses() throws IOException, ClassNotFoundException {
        //student view thier course they enrolled in
        try {
            out.writeObject("GET_STUDENT_COURSES");
            out.writeObject(currentUserId);
            out.flush();
            List<Course> myCourses = (List<Course>) in.readObject();
            DefaultTableModel model = (DefaultTableModel) tblMyCourses.getModel();
            model.setRowCount(0);
            for (Course c : myCourses) {
                model.addRow(new Object[]{c.getCourseCode(), c.getCourseName()});
            }
            System.out.println("Loaded " + myCourses.size() + " enrolled courses");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading your courses: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void searchStudentsByCourse() throws IOException, ClassNotFoundException {
        String courseCode = txtSearchCourse.getText().trim().toUpperCase();

        if (courseCode.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a course code!");
            return;
        }

        out.writeObject("GET_STUDENTS_BY_COURSE");
        out.writeObject(courseCode);
        out.flush();

        List<Student> students = (List<Student>) in.readObject();
        DefaultTableModel model = (DefaultTableModel) tblCourseResults.getModel();
        model.setRowCount(0);

        for (Student student : students) {
            model.addRow(new Object[]{
                student.getStudentNumber(),
                student.getFirstName(),
                student.getLastName(),
                student.getEmail()
            });
        }

        if (students.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No students found for course: " + courseCode);
        } else {
            JOptionPane.showMessageDialog(this, "Found " + students.size() + " student(s) for course: " + courseCode);
        }
    }

    private void searchCoursesByStudent() throws IOException, ClassNotFoundException {
        String studentNumber = txtSearchStudent.getText().trim().toLowerCase();

        if (studentNumber.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a student ID!");
            return;
        }

        out.writeObject("GET_COURSES_BY_STUDENT");
        out.writeObject(studentNumber);
        out.flush();

        List<Course> courses = (List<Course>) in.readObject();
        DefaultTableModel model = (DefaultTableModel) tblStudentResults.getModel();
        model.setRowCount(0);

        for (Course course : courses) {
            model.addRow(new Object[]{
                course.getCourseCode(),
                course.getCourseName(),});
        }

        if (courses.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No courses found for student: " + studentNumber);
        } else {
            JOptionPane.showMessageDialog(this, "Found " + courses.size() + " course(s) for student: " + studentNumber);
        }

    }

    private void refreshCourseSearch() {
        // Clear the course search field and table
        txtSearchCourse.setText("");
        DefaultTableModel model = (DefaultTableModel) tblCourseResults.getModel();
        model.setRowCount(0);
        JOptionPane.showMessageDialog(this, "Course search cleared!");
    }

    private void refreshStudentSearch() {
        // Clear the student search field and table
        txtSearchStudent.setText("");
        DefaultTableModel model = (DefaultTableModel) tblStudentResults.getModel();
        model.setRowCount(0);
        JOptionPane.showMessageDialog(this, "Student search cleared!");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new EnrollmentClient().setVisible(true));
    }
}
