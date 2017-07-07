package mensajeria;

import java.io.Serializable;

import javax.swing.JOptionPane;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import comandosCliente.ComandosCliente;

public class Paquete implements Serializable, Cloneable {

	public static String msjExito = "1";
	public static String msjFracaso = "0";

	private String mensaje;
	private String ip;
	private int comando;

	private static final Gson gson = new Gson();
	private String claseAEnviarPorGson;

	public Paquete() {

	}

	public Paquete(String mensaje, String nick, String ip, int comando) {
		this.mensaje = mensaje;
		this.ip = ip;
		this.comando = comando;
	}

	public Paquete(String mensaje, int comando) {
		this.mensaje = mensaje;
		this.comando = comando;
	}

	public Paquete(int comando) {
		this.comando = comando;
	}

	public void setMensaje(String mensaje) {
		this.mensaje = mensaje;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public void setComando(int comando) {
		this.comando = comando;
	}

	public String getMensaje() {
		return mensaje;
	}

	public String getIp() {
		return ip;
	}

	public int getComando() {
		return comando;
	}

	public Object clone() {
		Object obj = null;
		try {
			obj = super.clone();
		} catch (CloneNotSupportedException ex) {
			JOptionPane.showMessageDialog(null, "Error al Clonar Paquete");
		}
		return obj;
	}

	public static Paquete loadJson(String json) {
		Paquete p = gson.fromJson(json, Paquete.class);
		try {
			return (Paquete) gson.fromJson(json, Class.forName(p.claseAEnviarPorGson));
		} catch (JsonSyntaxException | ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		}

	}

	public Comando getComandoObj(String packageO) {
		try {
			Comando c;
			c = (Comando) Class.forName(packageO + "." + Comando.CLASESPOLIMORFISMO[comando]).newInstance();
			c.setPaquete(this);
			return c;
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
			return null;
		}

	}

	public String getJson() {
		this.claseAEnviarPorGson = this.getClass().getName();
		return gson.toJson(this);
	}

}
