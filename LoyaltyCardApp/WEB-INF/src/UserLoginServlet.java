import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class UserLoginServlet extends HttpServlet{
	
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws 
	ServletException, IOException{

		Connection connection = null;
		//Get the user's input from the HTML Page
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		
		try {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
            } catch (ClassNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }           connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/loyaltyapp?serverTimezone=UTC", "root", "root");
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

				Statement checkLogin = null;
				try {
					checkLogin = connection.createStatement();
						 
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
						
						ResultSet rs = null;
						try {
							rs = checkLogin.executeQuery("select * from users");
						} catch (SQLException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						response.setContentType("text/html");
						PrintWriter out = response.getWriter();
					try {
					boolean isUser = false;
						while(rs.next()) {
							try {
								if(rs.getString(1).equalsIgnoreCase(username)&&rs.getString(2).equals(password)) {	
									isUser = true;
									break;
								}								
							} catch (SQLException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

}
						if (isUser) {//If their details is in the database send them to page that allows them to
                            //see how many loyalty points they have now or choose to remove/spend points or add points from a receipt
							out.println("");
						}
						else {//If not, send response to the user saying login info not correct
							out.println("");
						}
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		
		
	}

}
