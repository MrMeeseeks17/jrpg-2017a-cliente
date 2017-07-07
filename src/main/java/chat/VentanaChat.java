package chat;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import juego.Pantalla;

//import chatServidor.Message;

import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.JTextArea;

public class VentanaChat extends JInternalFrame implements MessageHandler {

	private JPanel contentPane;
	private JScrollPane scrollPane;
	private JTextField textField;
	private JEditorPane editorPane;
	private Conexion conn;
	private String nombrePj;
	/**
	 * Create the frame.
	 */
	public VentanaChat(final String nombrePj) {	
		
		setResizable(false);
		setBounds(555, 355, 400, 400);
		setVisible(true);
		setClosable(false);

		this.nombrePj = nombrePj;

		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		scrollPane = new JScrollPane();
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setBounds(0, 0, 235, 180);
		contentPane.add(scrollPane);

		editorPane = new JEditorPane();
		editorPane.setContentType("text/Plain");
		editorPane.setForeground(Color.BLACK);
		editorPane.setBackground(Color.WHITE);
		scrollPane.setViewportView(editorPane);
		editorPane.setEditable(false);
		editorPane.setText("Bienvenido a WOME...\r\n\r\n");

		textField = new JTextField();
		textField.setForeground(Color.BLACK);
		textField.setBackground(Color.WHITE);
		textField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent arg0) {
				if(arg0.getKeyCode() == KeyEvent.VK_ENTER)
					enviarMensaje(nombrePj);
				if(arg0.getKeyCode() == KeyEvent.VK_0)
					Pantalla.dibujarInventario();				
			}
		});
		textField.setBounds(0, 185, 233, 27);
		contentPane.add(textField);
		textField.setColumns(10);
		ArchivoDePropiedades adp = new ArchivoDePropiedades("config.properties");
		adp.lectura();
		try {
			conn = new Conexion(adp.getIP(), adp.getPuerto(), nombrePj, this);
			conn.start();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Error al tratar de iniciar conexion con archivo Properties");
		}
	}
	
	public void enviarMensaje(final String nombrePj) {
		try {
			conn.sendChat(nombrePj, textField.getText());
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Error al enviar mensaje");
		}
		if(textField.getText().toString().contains("@")) {
			String mensaje = textField.getText().toString().substring(1);
			String destino = textField.getText().toString().substring(1, textField.getText().toString().indexOf(" "));
			mensaje = mensaje.substring(mensaje.indexOf(" ") + 1);
			editorPane.setText(editorPane.getText()  + "Para " +  destino + ": " + mensaje + "<br>");
			textField.setText("");
		}
		else {
			editorPane.setText(editorPane.getText()  + nombrePj + ": " + textField.getText() + "<br>");
			textField.setText("");
		}
	}
	
	public void recibido(String str){
		editorPane.setText(editorPane.getText() + str);
	}

	@Override
	public void messageReceived(Mensaje m) {
		//Mensaje a todos
		if(m.getDestination().equals("all")){
			if(m.getType() == Mensaje.USR_MSJ && !m.getSource().equals(nombrePj)){
				recibido(m.getSource()+ ": "+m.getText());
			}
		}else{		//MensajePrivado
			if(m.getType() == Mensaje.USR_MSJ){
				recibido("<< De " + m.getSource()+ ": "+m.getText() + " >>");
				setVisible(true);
			}
			if(m.getType() == Mensaje.STATUS_INFO){
				if(!m.getText().contains(nombrePj))
					recibido("## Info: "+m.getText());
			}
		}
		
	}

}