import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class LoyaltyCardPointsServlet extends HttpServlet {

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

        // Print out responses in the web browser
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        // From UserLoginServlet - gets the username from that session so it can be used
        // in this servlet
        HttpSession session = request.getSession();
        String username = (String) session.getAttribute("username");
        request.setAttribute("username", username);

        try {
            double currentPoints = 0;
            PreparedStatement getLoyaltyPoints = connection.prepareStatement(
                    "SELECT points FROM Users WHERE username = ?");
            getLoyaltyPoints.setString(1, username);
            // To get the current loyalty points of the user
            ResultSet rs = getLoyaltyPoints.executeQuery();
            if (rs.next()) {
                currentPoints = rs.getDouble("points");
            }

            // Get the action (view, add, spend points) from the PointMainPage.html
            String action = request.getParameter("action");
            if (action != null) {

                if (action.equals("view")) {
                    // If its vew display their points
                    out.println("<html><title>View Points</title><h1>Viewing Points</h1><body> " +
                            "Welcome, " + username + "!" +
                            "<p>Your current loyalty points: <b>" + currentPoints + "</b></p>" +
                            "</body></html>");

                } else if (action.equals("add")) {
                    // If the action is add

                    // Check is a receipt number is entered
                    String receiptNumber = request.getParameter("receiptNumber");

                    if (receiptNumber != null && !receiptNumber.isEmpty()) {
                        // If a receipt number is entered
                        Random random = new Random();
                        double pointsToAdd = random.nextDouble(20, 100);
                        // Add a random number of points to the users current points
                        double newPoints = currentPoints + pointsToAdd;
                        // This is to update the amount of points they have in the database
                        updatePointsInDatabase(connection, username, newPoints);

                        out.println("<html><title>Add Points</title><h1>Adding Points</h1><body>" +
                                "Welcome, " + username + "!" +
                                "<p>Your current loyalty points: <b>" + currentPoints + "</b></p><b>" +
                                pointsToAdd + "</b> Points added successfully from receipt." +
                                "<p>Total Points now: <b>" + newPoints + "</b></p>" +
                                "</body></html>");

                    } else {
                        // If no receipt number is added and the selected add points let them know
                        out.println("<script>alert('No receipt number was provided');window.history.go(-1);</script>");

                    }

                } else if (action.equals("spend")) {// If they wish to spend points

                    // Get the points they wish to spend from the textfield in the
                    // PointsMainPage.html
                    String pointsToSpendString = request.getParameter("pointsToSpend");// Had to change to String in order to make sure textfield is not empty
                    
                    
                    if (pointsToSpendString != null && !pointsToSpendString.isEmpty()) {
                        double pointsToSpend = Double.parseDouble(request.getParameter("pointsToSpend"));
                    
                        if (pointsToSpend <= currentPoints ) { // if the points they want to spend is less than the
                                                                  // amount the have
                            double newPoints = currentPoints - pointsToSpend;
                            updatePointsInDatabase(connection, username, newPoints);// Update the points in the database
                            out.println("<html><title>Spend Points</title><h1>Spending Points</h1><body>" +
                                    "Welcome, " + username + "!" +
                                    "<p>Your current loyalty points: <b>" + currentPoints + "</b></p><b>" +
                                    pointsToSpend + "</b> points have successfully been spent." +
                                    "<p>You now have <b>" + newPoints + "</b> points remaining</p>" +
                                    "</body></html>");

                        } else { // Display message if the want to spend more points than they have
                            out.println("<script>alert('You cannot spend more points than you have');window.history.go(-1);</script>");
                        }
                    } else {
                        // If no amount is added and they selected spend points let them know
                        out.println("<script>alert('No amount to spend was provided');window.history.go(-1);</script>");

                    }
                }

            } else { // If an option isn't selected
                    out.println("<script>alert('Pick an option to continue!');window.history.go(-1);</script>");
            }

        } catch (SQLException e1) {
            e1.printStackTrace();
        }
    }

    // Update the amount of points in the database
    private void updatePointsInDatabase(Connection connection, String username, double newPoints) {
        try {
            PreparedStatement updatePoints = connection.prepareStatement(
                    "UPDATE users SET points = ? WHERE username = ?");
            // Updates the points in users table where the username equals the username for
            // the seesion
            updatePoints.setDouble(1, newPoints);
            updatePoints.setString(2, username);
            updatePoints.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}