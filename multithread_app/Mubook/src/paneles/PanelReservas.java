package paneles;

import java.awt.Color;
import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class PanelReservas extends JScrollPane{
	JFrame ventana;
	
	public PanelReservas(JFrame ventana) {
		this.ventana = ventana;
		this.setBorder(null);
		this.setBackground(Color.red);
		this.setViewportView(crearPanelVentana());
	}

	private Component crearPanelVentana() {
		JPanel panel = new JPanel(new BorderLayout());
		
		
		
		return panel;
	}
}
