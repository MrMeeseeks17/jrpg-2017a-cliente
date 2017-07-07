package Inventario;


import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.MatteBorder;

import dominio.Item;
import mensajeria.PaquetePersonaje;

public class Inventario extends JInternalFrame {
	
	
	private static final int CANTCOLUMNAS = 3;
	private static final int CANTFILAS = 3;
	private ArrayList<Item> items;	
    public Inventario(PaquetePersonaje paquetePersonaje) throws IOException {
    	
    	((javax.swing.plaf.basic.BasicInternalFrameUI)this.getUI()).setNorthPane(null);
//    	setResizable(false);
		setBounds(200, 200, 200, 200);
		setVisible(true);
//		setClosable(false);

        setLayout(new GridBagLayout());
        items = new ArrayList<Item>(paquetePersonaje.getItems());
        GridBagConstraints gbc = new GridBagConstraints();
        for (int row = 0; row <CANTFILAS; row++) {
            for (int col = 0; col < CANTCOLUMNAS; col++) {
                gbc.gridx = col;
                gbc.gridy = row;
                Slot cellPane;
                if(!items.isEmpty()) {
                	cellPane = new Slot(items.get(0), paquetePersonaje);
                	items.remove(0);
                } else {
                	cellPane = new Slot();
                }
                
                
                Border border = null;
				if (row < CANTFILAS - 1) {
					if (col < CANTCOLUMNAS - 1) {
						border = new MatteBorder(1, 1, 0, 0, Color.DARK_GRAY);
					} else {
						border = new MatteBorder(1, 1, 0, 1, Color.DARK_GRAY);
					}
				} else {
					if (col < CANTCOLUMNAS - 1) {
						border = new MatteBorder(1, 1, 1, 0, Color.DARK_GRAY);
					} else {
						border = new MatteBorder(1, 1, 1, 1, Color.DARK_GRAY);
					}
				}
                cellPane.setBorder(border);
                gbc.weightx = gbc.weighty = 1.0;
                gbc.fill = GridBagConstraints.BOTH;

                add(cellPane, gbc);
            }
        }
    }
}

//public class Inventario extends JPanel {
//	private static final int CANTCOLUMNAS = 3;
//	private static final int CANTFILAS = 3;
//	private ArrayList<Item> items;	
//    public Inventario(PaquetePersonaje paquetePersonaje) throws IOException {
//        setLayout(new GridBagLayout());
//        items = new ArrayList<Item>(paquetePersonaje.getItems());
//        GridBagConstraints gbc = new GridBagConstraints();
//        for (int row = 0; row <CANTFILAS; row++) {
//            for (int col = 0; col < CANTCOLUMNAS; col++) {
//                gbc.gridx = col;
//                gbc.gridy = row;
//                Slot cellPane;
//                if(!items.isEmpty()) {
//                	cellPane = new Slot(items.get(0), paquetePersonaje);
//                	items.remove(0);
//                } else {
//                	cellPane = new Slot();
//                }
//                
//                
//                Border border = null;
//				if (row < CANTFILAS - 1) {
//					if (col < CANTCOLUMNAS - 1) {
//						border = new MatteBorder(1, 1, 0, 0, Color.DARK_GRAY);
//					} else {
//						border = new MatteBorder(1, 1, 0, 1, Color.DARK_GRAY);
//					}
//				} else {
//					if (col < CANTCOLUMNAS - 1) {
//						border = new MatteBorder(1, 1, 1, 0, Color.DARK_GRAY);
//					} else {
//						border = new MatteBorder(1, 1, 1, 1, Color.DARK_GRAY);
//					}
//				}
//                cellPane.setBorder(border);
//                gbc.weightx = gbc.weighty = 1.0;
//                gbc.fill = GridBagConstraints.BOTH;
//
//                add(cellPane, gbc);
//            }
//        }
//    }
//}