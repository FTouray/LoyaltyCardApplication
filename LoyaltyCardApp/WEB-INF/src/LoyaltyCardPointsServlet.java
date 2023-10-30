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
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        HttpSession session = request.getSession();
        String username = (String) session.getAttribute("username");
        request.setAttribute("username", username);

        try {
            int currentPoints = 0;
            PreparedStatement getLoyaltyPoints = connection.prepareStatement(
                    "SELECT points FROM Users WHERE username = ?");
            getLoyaltyPoints.setString(1, username);
            ResultSet rs = getLoyaltyPoints.executeQuery();
            if (rs.next()) {
                currentPoints = rs.getInt("points");
            }

           //TO FIX
/**Not getting points from database
 * Not updating database points
 * Not getting user from databse
 */
                String action = request.getParameter("action");
                if (action != null) {

                    if (action.equals("view")) {

                        out.println("<html><body>");
                        out.println("Welcome, " + username + "!");
                        out.println("<p>Your current loyalty points: " + currentPoints + "</p>");
                        out.println("</body></html>");

                    } else if (action.equals("add")) {

                        String receiptNumber = request.getParameter("receiptNumber");

                        if (receiptNumber != null && !receiptNumber.isEmpty()) {
                            Random random = new Random();
                            int pointsToAdd = random.nextInt(20, 100);
                            updatePointsInDatabase(connection, username, currentPoints);
                            currentPoints += pointsToAdd;
                            out.println("<html><body>");
                            out.println("Welcome, " + username + "!");
                            out.println("<p>Your current loyalty points: " + currentPoints + "</p>");
                            out.println("Points added successfully.");
                            out.println("</body></html>");

                        } else {
                            out.println("No receipt number was provided");
                            return;
                        }

                    } else if (action.equals("spend")) {

                        int pointsToSpend = Integer.parseInt(request.getParameter("pointsToSpend"));
                        if (currentPoints - pointsToSpend >= 0) {
                            currentPoints -= pointsToSpend;
                            updatePointsInDatabase(connection, username, currentPoints);
                            out.println("<html><body>");
                            out.println("Welcome, " + username + "!");
                            out.println("<p>Your current loyalty points: " + currentPoints + "</p>");
                            out.println("Points spent successfully.");
                            out.println("</body></html>");

                        } else {
                            out.println("You cannot spend more points than you have");
                            return;
                        }
                    }

                }
            
        } catch (SQLException e1) {
            e1.printStackTrace();
        }
    }

    private void updatePointsInDatabase(Connection connection, String username, int newPoints) {
        try {
            PreparedStatement updatePoints = connection
                    .prepareStatement("UPDATE users SET points = ? WHERE username = ?");
            updatePoints.setInt(1, newPoints);
            updatePoints.setString(2, username);
            updatePoints.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}