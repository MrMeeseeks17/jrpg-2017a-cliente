package cliente;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import javax.swing.JOptionPane;

import com.google.gson.Gson;

import frames.MenuCarga;
import frames.MenuCreacionPj;
import frames.MenuJugar;
import frames.MenuMapas;
import juego.Juego;
import mensajeria.Comando;
import mensajeria.Paquete;
import mensajeria.PaquetePersonaje;
import mensajeria.PaqueteUsuario;
/**La clase Cliente tiene como función  
 * ejecutar el cliente.
 */
public class Cliente extends Thread {

	private Socket cliente;
	private String miIp;
	private ObjectInputStream entrada;
	private ObjectOutputStream salida;

	// Objeto gson
	private final Gson gson = new Gson();

	// Paquete usuario y paquete personaje
	private PaqueteUsuario paqueteUsuario;
	private PaquetePersonaje paquetePersonaje;

	// Acciones que realiza el usuario
	private int accion;

	// Ip y puerto
	private String ip;
	private final int puerto = 9999;
	/**Pide la accion
	 * @return Devuelve la accion
	 */
	public int getAccion() {
		return accion;
	}
	/**Setea la accion
	 * @param accion accion a setear
	 */
	public void setAccion(final int accion) {
		this.accion = accion;
	}

	private Juego wome;
	private MenuCarga menuCarga;
	/**Constructor del Cliente
	 */
	public Cliente() {

		ip = "localhost";
			
		try {
			cliente = new Socket(ip, puerto);
			miIp = cliente.getInetAddress().getHostAddress();
			entrada = new ObjectInputStream(cliente.getInputStream());
			salida = new ObjectOutputStream(cliente.getOutputStream());
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Fallo al iniciar la aplicación. "
					+ "Revise la conexión con el servidor.");
		}
	}

	public Cliente(String ip, int puerto) {
		try {
			cliente = new Socket(ip, puerto);
			miIp = cliente.getInetAddress().getHostAddress();
			entrada = new ObjectInputStream(cliente.getInputStream());
			salida = new ObjectOutputStream(cliente.getOutputStream());
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Fallo al iniciar la aplicación. "
					+ "Revise la conexión con el servidor.");
		}
}
	
	@Override
	public void run() {
		synchronized(this) {
			try {

				// Creo el paquete que le voy a enviar al servidor
				paqueteUsuario = new PaqueteUsuario();

				while (!paqueteUsuario.isInicioSesion()) {

					// Muestro el menú principal
					new MenuJugar(this).setVisible(true);

					// Creo los paquetes que le voy a enviar al servidor
					paqueteUsuario = new PaqueteUsuario();
					paquetePersonaje = new PaquetePersonaje();

					// Espero a que el usuario seleccione alguna accion
					wait();

					switch (getAccion()) {

					case Comando.REGISTRO:
						paqueteUsuario.setComando(Comando.REGISTRO);
						break;
					case Comando.INICIOSESION:
						paqueteUsuario.setComando(Comando.INICIOSESION);
						break;
					case Comando.SALIR:
						paqueteUsuario.setIp(getMiIp());
						paqueteUsuario.setComando(Comando.SALIR);
						break;
					default:
						break;
					}

					// Le envio el paquete al servidor
					salida.writeObject(gson.toJson(paqueteUsuario));

					// Recibo el paquete desde el servidor
					String cadenaLeida = (String) entrada.readObject();
					Paquete paquete = gson.fromJson(cadenaLeida, Paquete.class);

					switch (paquete.getComando()) {

					case Comando.REGISTRO:
						if (paquete.getMensaje().equals(Paquete.msjExito)) {

							// Abro el menu para la creaci�n del personaje
							MenuCreacionPj menuCreacionPJ = new MenuCreacionPj(this, paquetePersonaje);
							menuCreacionPJ.setVisible(true);

							// Espero a que el usuario cree el personaje
							wait();

							// Le envio los datos al servidor
							paquetePersonaje.setComando(Comando.CREACIONPJ);
							salida.writeObject(gson.toJson(paquetePersonaje));
							JOptionPane.showMessageDialog(null, "Registro exitoso.");

							// Recibo el paquete personaje con los datos (la id incluida)
							paquetePersonaje = gson.fromJson((String) entrada.readObject(), PaquetePersonaje.class);

							// Indico que el usuario ya inicio sesion
							paqueteUsuario.setInicioSesion(true);

						} else {
							if (paquete.getMensaje().equals(Paquete.msjFracaso)) {
								JOptionPane.showMessageDialog(null, "No se pudo registrar.");
							}
							// El usuario no pudo iniciar sesión
							paqueteUsuario.setInicioSesion(false);
						}
						break;

					case Comando.INICIOSESION:
						if (paquete.getMensaje().equals(Paquete.msjExito)) {

							// El usuario ya inicio sesi�n
							paqueteUsuario.setInicioSesion(true);

							// Recibo el paquete personaje con los datos
							paquetePersonaje = gson.fromJson(cadenaLeida, PaquetePersonaje.class);

						} else {
							if (paquete.getMensaje().equals(Paquete.msjFracaso))
								JOptionPane.showMessageDialog(null, "Error al iniciar sesión."
										+ " Revise el usuario y la contraseña");

							// El usuario no pudo iniciar sesión
							paqueteUsuario.setInicioSesion(false);
						}
						break;

					case Comando.SALIR:
						// El usuario no pudo iniciar sesión
						paqueteUsuario.setInicioSesion(false);
						salida.writeObject(gson.toJson(new Paquete(Comando.DESCONECTAR), Paquete.class));
						cliente.close();
						break;
						
					
					default:
						break;
					}

				}

				// Creo un paquete con el comando mostrar mapas
				paquetePersonaje.setComando(Comando.MOSTRARMAPAS);

				// Abro el menu de eleccion del mapa
				MenuMapas menuElegirMapa = new MenuMapas(this);
				menuElegirMapa.setVisible(true);

				// Espero a que el usuario elija el mapa
				wait();

				// Establezco el mapa en el paquete personaje
				paquetePersonaje.setIp(miIp);

				// Le envio el paquete con el mapa seleccionado
				salida.writeObject(gson.toJson(paquetePersonaje));

				// Instancio el juego y cargo los recursos
				wome = new Juego("World Of the Middle Earth", 800, 600, this, paquetePersonaje);

				// Muestro el menu de carga
				menuCarga = new MenuCarga(this);
				menuCarga.setVisible(true);

				// Espero que se carguen todos los recursos
				wait();

				// Inicio el juego
				wome.start();

				// Finalizo el menu de carga
				menuCarga.dispose();

			} catch (IOException | InterruptedException | ClassNotFoundException e) {
				JOptionPane.showMessageDialog(null, "Fallo la conexión con el servidor durante el inicio de sesión.");
			}
		}

	}
	/**Pide el cliente
	 * @return Devuelve el cliente
	 */
	public Socket getSocket() {
		return cliente;
	}
	/**Setea el cliente
	 * @param cliente cliente a setear
	 */
	public void setSocket(final Socket cliente) {
		this.cliente = cliente;
	}
	/**Pide la ip
	 * @return Devuelve la ip
	 */
	public String getMiIp() {
		return miIp;
	}
	/**Setea la ip
	 * @param miIp ip a setear
	 */
	public void setMiIp(final String miIp) {
		this.miIp = miIp;
	}
	/**Pide la entrada
	 * @return Devuelve la entrada
	 */
	public ObjectInputStream getEntrada() {
		return entrada;
	}
	/**Setea la entrada
	 * @param entrada entrada a setear
	 */
	public void setEntrada(final ObjectInputStream entrada) {
		this.entrada = entrada;
	}
	/**Pide la salida
	 * @return Devuelve la salida
	 */
	public ObjectOutputStream getSalida() {
		return salida;
	}
	/**Setea la salida
	 * @param salida salida a setear
	 */
	public void setSalida(final ObjectOutputStream salida) {
		this.salida = salida;
	}
	/**Pide el paquete usuario
	 * @return Devuelve el paquete usuario
	 */
	public PaqueteUsuario getPaqueteUsuario() {
		return paqueteUsuario;
	}
	/**Pide el paquete personaje
	 * @return Devuelve el paquete personaje
	 */
	public PaquetePersonaje getPaquetePersonaje() {
		return paquetePersonaje;
	}
	/**Pide el juego
	 * @return Devuelve el juego
	 */
	public Juego getJuego() {
		return wome;
	}
	/**Pide el menu de carga
	 * @return Devuelve el menu de carga
	 */
	public MenuCarga getMenuCarga() {
		return menuCarga;
	}
	public void actualizarItems(PaquetePersonaje paqueteActualizado) {
		if(paquetePersonaje.getCantItems() != paqueteActualizado.getCantItems()) {
			paquetePersonaje.anadirItem(paqueteActualizado.getItems().get(paqueteActualizado.getItems().size() -1));
		}
		
	}
}