package Mubook;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.swing.JFrame;
import javax.swing.JScrollPane;

public class Mubook extends JFrame implements WindowListener{
	
	public final static int DEFAULT_WIDTH = 1000;
	public final static int DEFAULT_HEIGHT = 600;
	public static Connection conn = null;
	private static final String driver = "com.mysql.cj.jdbc.Driver";
	public static String userDB = "mtapp";
	public static String pass = "E!2Hw5*ja";
	private static final String url = "jdbc:mysql://mubook.duckdns.org:3306/mubook";
	JScrollPane pDisplay;
	
	public Mubook() {
		super("Mubook");
		this.connectToDb();
		try {
			this.generateInserts();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void connectToDb() {
		try {
			Class.forName(driver);
			conn = DriverManager.getConnection(url, Mubook.userDB, Mubook.pass);
			if(conn != null) {
				System.out.println("Conexión establecida");
			}
		}catch(ClassNotFoundException | SQLException e) {
			System.out.println("Error al conectar " + e);
		}
	}
	
	public void generateInserts() throws SQLException {
		ResultSet rs;
		PreparedStatement ps = null;
		Calendar c = Calendar.getInstance();
		int id = -1;
			ps = conn.prepareStatement("SELECT reservationId FROM RESERVATION WHERE (DATE(?) BETWEEN initDate AND finalDate)"
					+ "                                            OR (DATE(?) BETWEEN  initDate AND finalDate);");
            
            rs = ps.executeQuery();
            while(rs.next()) {
            	id = rs.getInt(1);
            }
            
            if(id != -1) {
            	System.out.println("No se puede introducir");
            }else {
            	System.out.println("Se puede introducir");
            }
			
		for(int i = 0; i < 10000; i++) {
			Date randomDate = createRandomDate(2011, 2021);
			c.setTime(randomDate);
			c.add(Calendar.DATE, 10);
			Date finDate = c.getTime();
			String initDate = randomDate.toString();
			String finalDate = finDate.toString();
			
			ps.setString(1, initDate);
            ps.setString(2, finalDate);
		}
	}
	
	public static int createRandomIntBetween(int start, int end) {
        return start + (int) Math.round(Math.random() * (end - start));
    }
	
	public static Date createRandomDate(int startYear, int endYear) {
        int day = createRandomIntBetween(1, 28);
        int month = createRandomIntBetween(1, 12);
        int year = createRandomIntBetween(startYear, endYear);

        Calendar c = new GregorianCalendar(year, month, day);
        Date date = c.getTime();
        
        return date;
    }
	
	@Override
	public void windowOpened(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowClosing(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowClosed(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowIconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowActivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	public static void main(String[] args) {
		Mubook mubook = new Mubook();
	}
}
