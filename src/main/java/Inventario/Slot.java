package Inventario;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import dominio.Item;
import mensajeria.PaquetePersonaje;
import recursos.Recursos;

public class Slot extends JPanel {

  
    private BufferedImage item;
    private PaquetePersonaje paquetePersonaje;
    private JLabel label;
    private Item it;


    public Slot(Item item, PaquetePersonaje paquetePersonaje) throws IOException {
		this.item = item.getFoto();
		it = item;
		this.paquetePersonaje = paquetePersonaje;
		label = new JLabel(new ImageIcon(this.item.getScaledInstance(49, 49, Image.SCALE_DEFAULT)));
		actionListenersYLabel(item);
	}

	public Slot() {
		label = new JLabel(new ImageIcon(Recursos.noItem.getScaledInstance(49, 49, Image.SCALE_DEFAULT)));
		add(label);
	}
	
	private void actionListenersYLabel(Item item) {
		StringBuilder s = new StringBuilder();
        
			s.append("<html>" + item.getNombre() + "<br>");

        	if(item.getBonusSalud() != 0) {
        		s.append("+" + item.getBonusSalud() + " Salud " + "<br>");
        	}
        	if(item.getBonusEnergia() != 0) {
        		s.append("+" + item.getBonusEnergia() + " Energia " + "<br>");
        	}
        	if(item.getBonusFuerza() != 0) {
        		s.append("+" + item.getBonusFuerza() + " Fuerza " + "<br>");
        	}
        	if(item.getBonusDestreza() != 0) {
        		s.append("+" + item.getBonusDestreza() + " Destreza " + "<br>");
        	}
        	if(item.getBonusInteligencia() != 0) {
        		s.append("+" + item.getBonusInteligencia() + " Inteligencia");
        	}
        	s.append("</html>");
			label.setToolTipText(s.toString());
	       
	        label.addMouseListener(mouseListener);
	        
	        addMouseListener(mouseListener);
        
        add(label);
        this.validate();
        this.repaint();
        
}

	
	protected void resetLabel() {
		label.setIcon(new ImageIcon(Recursos.noItem.getScaledInstance(49, 49, Image.SCALE_DEFAULT)));
		label.setToolTipText(null);
		paquetePersonaje.removerItem(it);
		label.removeMouseListener(mouseListener);
		removeMouseListener(mouseListener);
	}

	@Override
    public Dimension getPreferredSize() {
        return new Dimension(60, 60);
    }

	public JLabel getLabel() {
		return label;
	}
	
	MouseListener mouseListener = new MouseAdapter() {
		public void mouseClicked(MouseEvent e) {
    		Object[] options = {"Equipar", "Tirar", "Cancelar"};
    		if(e.getClickCount() == 2) {
    			int answer = JOptionPane.showOptionDialog(getParent(),  "¿Qué desea hacer?", "Item: " + it.getNombre(), JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[2]);
    			//Tirar
    			if(answer == 1) {
    				paquetePersonaje.sacarBonus(it.getBonusSalud(), it.getBonusEnergia(), it.getBonusFuerza(), it.getBonusDestreza(), it.getBonusInteligencia());
    				resetLabel();
    			}
    		}
    	}
	};
}