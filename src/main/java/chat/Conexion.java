package chat;

//import chatServidor.Message;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;

import javax.swing.JOptionPane;

public class Conexion extends Thread{
	
	private Socket sock;
	private DataInputStream in;
	private DataOutputStream out;
	private String user;
	private InterfazMensaje mh;
	private int stop = 0;

	public Conexion(String host, int port, String user, InterfazMensaje mh) throws Exception {
		this.sock = new Socket(host, port);
		this.user = user;
		this.mh = mh;
		this.in = new DataInputStream(new BufferedInputStream(this.sock.getInputStream()));
		this.out = new DataOutputStream(new BufferedOutputStream(this.sock.getOutputStream()));
	}

	public void stopRequest(){
		stop=1;
	}
	
	@Override
	public void run() {
		boolean c = true;
		while (c && stop==0) {
			try {
				Mensaje msg = new Mensaje(in.readUTF());
				process(msg);
			}catch(EOFException e){
				JOptionPane.showMessageDialog(null, "Error en Conexion Chat. El flujo de entrada llega al final antes de leer todos los bytes.");
			}catch (IOException e) {
				JOptionPane.showMessageDialog(null, "Posible bloqueo de conexion por datos demaciado Grandes");
				c=false;
			}
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
			}
		}
		kissoff();
	}

	private void kissoff() {
		try {
			this.sock.close();
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Imposible cerrar Socket Conexion Chat. Posible puerto cerrado o null pointer");
		}
	}

	private void process(Mensaje msg) throws IOException {
		/**
		 * Mensajes de protocolo
		 */
		if (msg.getType() == Mensaje.SERVER_ASK) {
			if (msg.getText().equals("login")) {
				login();
				return;
			}
		}
		
		/**
		 * Envia para ser mostrado 
		 */
		this.mh.messageReceived(msg);

	}

	private void login() throws IOException {
		Mensaje m = new Mensaje();
		m.setDestination("user");
		m.setText(this.user);
		m.setType(Mensaje.CLIENT_DATA);
		this.out.writeUTF(m.toString());
		this.out.flush();
	}
	
	public void sendChat(String usr,String text) throws IOException{
		Mensaje m = new Mensaje();
		m.setType(Mensaje.USR_MSJ);
		m.setSource(usr);
		if(text.contains("@")) {
			String dest = text.substring(1, text.indexOf(" "));
			String txt = text.substring(text.indexOf(" ") + 1);
			m.setDestination(dest);
			m.setText(txt);
		}
		else {
			m.setSource(usr);
			m.setDestination("all");
			m.setText(text);
		}
		this.out.writeUTF(m.toString());
		this.out.flush();
	}

}
