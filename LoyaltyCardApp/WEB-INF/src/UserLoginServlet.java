import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class UserLoginServlet extends HttpServlet {

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		Connection connection = null;
		// Get the user's input from the login HTML Page
		String username = request.getParameter("username");
		String password = request.getParameter("password");

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

		// Check that the username and password is found in the database - this means
		// they are registered
		Statement checkLogin = null;
		ResultSet rs = null;
		try {
			checkLogin = connection.createStatement();
			rs = checkLogin.executeQuery("select * from users");

		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		// Print out text in web browser
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();

		try {
			boolean isUser = false;
			while (rs.next()) {
				try {
					// Check if the username and password from databse match the user input from
					// html file
					if (rs.getString(1).equalsIgnoreCase(username) && rs.getString(2).equals(password)) {
						isUser = true;// Set this to true if it matches
						break;
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
			if (isUser) {// If their details is in the database send them to page that allows them to
				// see how many loyalty points they have now or choose to remove/spend points or
				// add points from a receipt

				// This allows the username to be kept for the LoyaltyCardServlet in order to
				// update the points in the database
				HttpSession session = request.getSession();
				session.setAttribute("username", username);
				response.sendRedirect("PointsMainPage.html");

			} else {
				// If not, send alert to the user saying login info not correct
				out.println("<script>alert('Login info is incorrect!');window.history.go(-1);</script>");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
