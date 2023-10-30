import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
//import java.sql.ResultSet;
import java.sql.SQLException;
//import java.sql.Statement;

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

        String username = request.getParameter("username");
            String password = request.getParameter("password");
            String confirmPassword = request.getParameter("confirmPassword");


    

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        // Check that the user has unique username and entered the password correctly twice
        try {
            int regPoints = 100;

           /** PreparedStatement checkUsername = connection.prepareStatement(
                "SELECT username FROM users WHERE username = ?");
            checkUsername.setString(1, username);
            ResultSet rs = checkUsername.executeQuery();
            //Validate that the username is not taken
            if(rs.next()){
                out.println("Username is already taken.");
                //response.sendRedirect("usernameTaken.html");
            }
            else*/ if (password.equals(confirmPassword)) 
            {// Insert into users       
                PreparedStatement createUser = connection.prepareStatement(
                        "INSERT into users "
                                + "(username, password, points)" + " VALUES (?, ?, ?)");
                // Pass in the values as parameters
                createUser.setString(1, username);
                createUser.setString(2, password);
                createUser.setInt(3, regPoints);// When a user registers that automatically get 100 points
                int rowsUpdated = createUser.executeUpdate();
                createUser.close();

                if (rowsUpdated > 0){               
                    //Registration is successful
                    response.sendRedirect("UserLogin.html");
                }
                
            } 
            else if (!password.equals(confirmPassword)) 
            {// If the password doesn't match
            out.print("Passwords do not match!");
                //response.sendRedirect("registerError.html");
            }
        } catch (SQLException e1) {
            e1.printStackTrace();
        }

    }

}
