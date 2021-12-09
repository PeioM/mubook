package controladores;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import Mubook.Mubook;
import paneles.PanelItems;
import paneles.PanelPrincipal;
import paneles.PanelReservas;

public class ControladorMubook implements ActionListener{
	Mubook mubook;
	
	public ControladorMubook(Mubook mubook){
		this.mubook = mubook;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String accion = e.getActionCommand();
		
		switch(accion) {
		case "reserva":
				mubook.getpDisplay().setViewportView(new PanelReservas(mubook));
			break;
		case "items":
				mubook.getpDisplay().setViewportView(new PanelItems(mubook));
			break;
		case "inicio":
				mubook.getpDisplay().setColumnHeaderView(new PanelPrincipal());
			break;
		}
	}
}
