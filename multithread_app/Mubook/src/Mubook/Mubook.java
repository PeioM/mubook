package Mubook;

import java.awt.Color;
import java.awt.Toolkit;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import controladores.ControladorMubook;
import paneles.PanelPrincipal;

public class Mubook extends JFrame implements WindowListener{
	
	public final static int DEFAULT_WIDTH = 1000;
	public final static int DEFAULT_HEIGHT = 600;
	public static Connection conn = null;
	private static final String driver = "com.mysql.cj.jdbc.Driver";
	public static String userDB = "mtapp";
	public static String pass = "E!2Hw5*ja";
	private static final String url = "jdbc:mysql://mubook.duckdns.org:3306/mubook";
	
	ControladorMubook controlador;
	
	JScrollPane pDisplay;
	
	public Mubook() {
		super("Mubook");
		//this.connectToDb();
		
		this.setIconImage(new ImageIcon("images/logo_web.png").getImage());
		
		controlador = new ControladorMubook(this);
		
		pDisplay = new JScrollPane();
		pDisplay.setViewportView(new PanelPrincipal());
		pDisplay.setBorder(null);
		
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		int width = (int)toolkit.getScreenSize().getWidth();
		int height = (int)toolkit.getScreenSize().getHeight();
		
		this.setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
		this.setLocation(width / 2 - DEFAULT_WIDTH / 2, height / 2 - DEFAULT_HEIGHT / 2);
		
		JSplitPane pVentana = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, crearPanelElementos(), pDisplay);
		pVentana.setDividerLocation(pVentana.getMinimumDividerLocation());
		pVentana.setBorder(null);
		
		this.setContentPane(pVentana);
		this.setJMenuBar(crearBarraMenu());
		this.setVisible(true);
		this.setBackground(Color.white);
		this.setExtendedState(JFrame.MAXIMIZED_BOTH);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		/*try {
			this.generateInserts();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	}

	private JMenuBar crearBarraMenu() {
		JMenuBar menuBar = new JMenuBar();
		
		menuBar.add(crearMenuInicio());
		menuBar.add(crearMenuBibliotecario());
		
		return menuBar;
	}

	private JMenu crearMenuBibliotecario() {
		JMenu menu = new JMenu("Panels");
		
		JMenuItem reservas = new JMenuItem("Reservations");
		JMenuItem items = new JMenuItem("Item");
		
		menu.add(reservas);
		menu.add(items);
		
		reservas.addActionListener(controlador);
		reservas.setActionCommand("reserva");
		
		items.addActionListener(controlador);
		items.setActionCommand("items");
		
		return menu;
	}

	private JMenu crearMenuInicio() {
		JMenu menu = new JMenu("Home");
		JMenuItem inicio = new JMenuItem("Home");
		
		menu.add(inicio);

		inicio.addActionListener(controlador);
		inicio.setActionCommand("inicio");
		
		return menu;
	}

	private Component crearPanelElementos() {
		JPanel panel = new JPanel(new GridLayout(3, 1));
		panel.setBackground(Color.WHITE);
		JButton bHome, bReservas, bDatos;
		
		bHome = new JButton(new ImageIcon("images/home.png"));
		bHome.setSize(new Dimension(300,100));
		panel.add(bHome);
		bHome.setActionCommand("inicio");
		bHome.addActionListener(controlador);
		
		bReservas = new JButton(new ImageIcon("images/reservation.png"));
		bReservas.setSize(new Dimension(300,100));
		panel.add(bReservas);
		bReservas.setActionCommand("reserva");
		bReservas.addActionListener(controlador);
		
		bDatos = new JButton(new ImageIcon("images/items.png"));
		bDatos.setSize(new Dimension(300,100));
		panel.add(bDatos);
		bDatos.addActionListener(controlador);
		bDatos.setActionCommand("items");
		
		return panel;
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
	
	public void disconnectFromDB() {
		conn = null;
		if(conn == null) {
			System.out.println("Conexión terminada");
		}
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
	
	public JScrollPane getpDisplay() {
		return pDisplay;
	}
	
	public void generateInserts() throws SQLException {
		int pk = 56;
		ResultSet rs;
		PreparedStatement ps = null;
		Calendar c = Calendar.getInstance();
		int randItem = -1;
		int randUser = -1;
		int id = -1;
		Random rand = new Random();
		String sqlIns="";
			
		for(int i = 0; i < 10000; i++) {
			id = -1;
			Date randomDate = createRandomDate(2011, 2021);
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			c.setTime(randomDate);
			c.add(Calendar.DATE, 10);
			Date finDate = c.getTime();
			String initDate = df.format(randomDate);
			String finalDate = df.format(finDate);
			
			randItem = (int) (Math.random() * 8 + 1);	
			ps = conn.prepareStatement("SELECT reservationId FROM RESERVATION WHERE (DATE(?) BETWEEN initDate AND finalDate)"
						+ "                                            OR (DATE(?) BETWEEN  initDate AND finalDate)"
						+ "												AND ptrItem = ?;");
			
			ps.setString(1, initDate);
            ps.setString(2, finalDate);
            ps.setInt(3, randItem);
            
            rs = ps.executeQuery();
            while(rs.next()) {
            	id = rs.getInt(1);
            }
            
            randUser = rand.nextInt((5 - 3) + 1) + 3;
            
            if(id != -1) {
            	System.out.println("No se puede introducir");
            }else {
            	sqlIns = "INSERT INTO RESERVATION (reservationId, ptrUser, ptrItem, ptrRoom, initDate, finalDate)"
            			+ "VALUES (?, ?, ?, NULL, DATE(?), DATE(?))";
            	ps = conn.prepareStatement(sqlIns);
            	ps.setInt(1, pk);
            	ps.setInt(2, randUser);
            	ps.setInt(3, randItem);
            	ps.setString(4, initDate);
            	ps.setString(5, finalDate);
            	
            	ps.executeUpdate();
            	System.out.println("Introducido");
            	pk++;
            }
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
	
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		Mubook mubook = new Mubook();
	}
}
