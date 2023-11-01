import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class UserRegistrationServlet extends HttpServlet {

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Connection connection = null;
        try {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
            } catch (ClassNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/loyaltyapp?serverTimezone=UTC", "root", "root");
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // Get the username, password and password confirmation from the
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");

        // Print out responses
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        try {
            int regPoints = 100; // User gets 100 points when they register

            // This stops there being more than one user with the same username
            PreparedStatement checkUsername = connection.prepareStatement(
                    "SELECT username FROM users WHERE username = ?");
            checkUsername.setString(1, username);
            ResultSet rs = checkUsername.executeQuery();
            // Validate that the username is not taken - not in the database
            if (rs.next()) {
                // A popup appears letting the user know the username is taken
                out.println("<script>alert('Username is taken!');window.history.go(-1);</script>");
            } else {
                if (password.equals(confirmPassword)) {// Insert into users if passwords match
                    PreparedStatement createUser = connection.prepareStatement(
                            "INSERT into users "
                                    + "(username, password, points)" + " VALUES (?, ?, ?)");
                    // Pass in the values as parameters
                    createUser.setString(1, username);
                    createUser.setString(2, password);
                    createUser.setInt(3, regPoints);// When a user registers that automatically get 100 points
                    int rowsUpdated = createUser.executeUpdate();
                    createUser.close();

                    if (rowsUpdated > 0) {
                        // Registration is successful takes them to the login page
                        response.sendRedirect("UserLogin.html");
                    }

                } else if (!password.equals(confirmPassword)) {// If the password doesn't match
                    // A pop up will appear saying your password does not match
                    // Takes them back to the previous window
                    out.println("<script>alert('Passwords do not match!');window.history.go(-1);</script>");
                }
            }
        } catch (SQLException e1) {
            e1.printStackTrace();
        }

    }

}
