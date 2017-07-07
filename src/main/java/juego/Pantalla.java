package juego;

import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import com.google.gson.Gson;

import Inventario.Inventario;
import chat.VentanaChat;
import cliente.Cliente;
import frames.MenuJugar;
import interfaz.MenuInfoPersonaje;
import mensajeria.Comando;
import mensajeria.Paquete;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.font.FontRenderContext;

import javax.swing.WindowConstants;

import estados.Estado;
import frames.MenuInventario;

public class Pantalla {

	private JFrame pantalla;
	private Canvas canvas;

	private static VentanaChat ventanaChat;
	private final Gson gson = new Gson();

	private static Cliente c;
	public Pantalla(final String NOMBRE, final int ANCHO, final int ALTO, final Cliente cliente) {
		c = cliente;
		
		pantalla = new JFrame(NOMBRE);
		pantalla.setIconImage(Toolkit.getDefaultToolkit().getImage("src/main/java/frames/IconoWome.png"));
		pantalla.setCursor(Toolkit.getDefaultToolkit().createCustomCursor(
				new ImageIcon(MenuJugar.class.getResource("/cursor.png")).getImage(), new Point(0, 0),
				"custom cursor"));

		pantalla.setSize(ANCHO, ALTO);
		pantalla.setResizable(false);
		pantalla.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		pantalla.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent evt) {
				try {
					Paquete p = new Paquete();
					p.setComando(Comando.DESCONECTAR);
					p.setIp(cliente.getMiIp());
					cliente.getSalida().writeObject(gson.toJson(p));
					cliente.getEntrada().close();
					cliente.getSalida().close();
					cliente.getSocket().close();
					System.exit(0);
				} catch (IOException e) {
					JOptionPane.showMessageDialog(null, "Fallo al intentar cerrar la aplicación.");
				}
			}
		});
		pantalla.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_I) {
					if (Estado.getEstado().esEstadoDeJuego()) {
						dibujarInventario(cliente);
					}
				}
			}

		});
		pantalla.setLocationRelativeTo(null);
		pantalla.setVisible(false);

		ventanaChat = new VentanaChat(cliente.getPaquetePersonaje().getNombre());
		pantalla.add(ventanaChat);		
	
		
		
		canvas = new Canvas();
		canvas.setPreferredSize(new Dimension(ANCHO, ALTO));
		canvas.setMaximumSize(new Dimension(ANCHO, ALTO));
		canvas.setMinimumSize(new Dimension(ANCHO, ALTO));
		canvas.setFocusable(false);

		pantalla.add(canvas);
		pantalla.pack();
		
//		dibujarInventario(cliente);
	}

	public Canvas getCanvas() {
		return canvas;
	}

	public JFrame getFrame() {
		return pantalla;
	}

	public void mostrar() {
		pantalla.setVisible(true);
	}

	public static void centerString(Graphics g, Rectangle r, String s) {
		FontRenderContext frc = new FontRenderContext(null, true, true);

		Rectangle2D r2D = g.getFont().getStringBounds(s, frc);
		int rWidth = (int) Math.round(r2D.getWidth());
		int rHeight = (int) Math.round(r2D.getHeight());
		int rX = (int) Math.round(r2D.getX());
		int rY = (int) Math.round(r2D.getY());

		int a = (r.width / 2) - (rWidth / 2) - rX;
		int b = (r.height / 2) - (rHeight / 2) - rY;

		g.drawString(s, r.x + a, r.y + b);
	}
	
	public static void dibujarInventario() {
		dibujarInventario(c);
	}
	
	public static void dibujarInventario(Cliente cliente) {
		MenuInventario menu = new MenuInventario(cliente);
		menu.setVisible(true);
	}

	public static void invisibleChat() {
		ventanaChat.setVisible(false);
	}

	public static void visibleChat() {
		ventanaChat.setVisible(true);
	}
}