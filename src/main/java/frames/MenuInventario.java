package frames;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

import com.google.gson.Gson;

import Inventario.Inventario;
import cliente.Cliente;
import mensajeria.Comando;

public class MenuInventario extends JFrame {

	private JButton cancelar = new JButton("Salir");

	public MenuInventario(final Cliente cliente) {

		cancelar.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					Gson gson = new Gson();
					cliente.getPaquetePersonaje().setComando(Comando.ACTUALIZARINVENTARIO);
					cliente.getSalida().writeObject(gson.toJson(cliente.getPaquetePersonaje()));
				} catch (IOException e1) {
					JOptionPane.showMessageDialog(null, "Error al Obtener Gson Action Event Menu Inventario");
				}
				dispose();

			}

		});
		this.setTitle("Inventario");
//		this.setUndecorated(true);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		try {
			this.setLayout(new BorderLayout());
			this.add(new Inventario(cliente.getPaquetePersonaje()));
			this.add(cancelar, BorderLayout.AFTER_LAST_LINE);

		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Error creacion de Inventario. Clase MenuInventario");
		}
		this.setBounds(600, 600, 600, 600);
		this.pack();
//		this.setLocationRelativeTo(null);

		this.setLocation(800, 100);
//		this.setResizable(false);
		this.setVisible(true);
	}

}