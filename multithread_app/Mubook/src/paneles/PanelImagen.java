package paneles;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;

import javax.swing.ImageIcon;

import gestionpantallas.PanelGradiente;

public class PanelImagen extends PanelGradiente {

	private static final long serialVersionUID = 1L;
	ImageIcon mainImage;

	public PanelImagen() {
		super(new BorderLayout());
		
		this.setBackground(Color.white);
		this.mainImage = new ImageIcon("images/logo_trasp.png");
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		// Colocar las imagenes de fondo
		g.drawImage(mainImage.getImage(), this.getWidth()/2 - mainImage.getIconWidth()/2, this.getHeight()/2 - mainImage.getIconHeight()/2, null);
		}
}