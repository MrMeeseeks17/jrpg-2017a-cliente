package cliente;

import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JOptionPane;

import com.google.gson.Gson;

import estados.Estado;
import estados.EstadoBatalla;
import juego.Juego;
import mensajeria.Comando;
import mensajeria.Paquete;
import mensajeria.PaqueteAtacar;
import mensajeria.PaqueteBatalla;
import mensajeria.PaqueteDeMovimientos;
import mensajeria.PaqueteDePersonajes;
import mensajeria.PaqueteFinalizarBatalla;
import mensajeria.PaqueteMovimiento;
import mensajeria.PaquetePersonaje;
/**La clase EscuchaMensajes tiene como función  
 * esuchar los mensajes que se enviaran
 * al servidor.
 */
public class EscuchaMensajes extends Thread {

	private Juego juego;
	private Cliente cliente;
	private ObjectInputStream entrada;
	private final Gson gson = new Gson();

	private Map<Integer, PaqueteMovimiento> ubicacionPersonajes;
	private Map<Integer, PaquetePersonaje> personajesConectados;
	/**Constructor de EsuchaMensaje
	 * @param juego juego del que se escucha el mensaje
	 */
	public EscuchaMensajes(final Juego juego) {
		this.juego = juego;
		cliente = juego.getCliente();
		entrada = cliente.getEntrada();
	}

	@Override
	public void run() {

		try {

			Paquete paquete;
			PaquetePersonaje paquetePersonaje;
			PaqueteMovimiento personaje;
			PaqueteBatalla paqueteBatalla;
			PaqueteAtacar paqueteAtacar;
			PaqueteFinalizarBatalla paqueteFinalizarBatalla;
			personajesConectados = new HashMap<>();
			ubicacionPersonajes = new HashMap<>();

			while (true) {

				String objetoLeido = (String) entrada.readObject();

				paquete = gson.fromJson(objetoLeido , Paquete.class);

				switch (paquete.getComando()) {

				case Comando.CONEXION:
					personajesConectados = gson.fromJson(objetoLeido, PaqueteDePersonajes.class).getPersonajes();
					break;

				case Comando.MOVIMIENTO:
					ubicacionPersonajes = gson.fromJson(objetoLeido, PaqueteDeMovimientos.class).getPersonajes();
					break;

				case Comando.BATALLA:
					paqueteBatalla = gson.fromJson(objetoLeido, PaqueteBatalla.class);
					juego.getPersonaje().setEstado(Estado.estadoBatalla);
					Estado.setEstado(null);
					juego.setEstadoBatalla(new EstadoBatalla(juego, paqueteBatalla));
					Estado.setEstado(juego.getEstadoBatalla());
					break;

				case Comando.ATACAR:
					paqueteAtacar = gson.fromJson(objetoLeido, PaqueteAtacar.class);
					juego.getEstadoBatalla().getEnemigo().actualizarAtributos(paqueteAtacar.getMapPersonaje());
					juego.getEstadoBatalla().getPersonaje().actualizarAtributos(paqueteAtacar.getMapEnemigo());
					juego.getEstadoBatalla().setMiTurno(true);
					break;

				case Comando.FINALIZARBATALLA:
					paqueteFinalizarBatalla = gson.fromJson(objetoLeido, PaqueteFinalizarBatalla.class);
					juego.getPersonaje().setEstado(Estado.estadoJuego);
					Estado.setEstado(juego.getEstadoJuego());
					break;

				case Comando.ACTUALIZARPERSONAJE:
					paquetePersonaje = gson.fromJson(objetoLeido, PaquetePersonaje.class);

					personajesConectados.remove(paquetePersonaje.getId());
					personajesConectados.put(paquetePersonaje.getId(), paquetePersonaje);
					
					if (juego.getPersonaje().getId() == paquetePersonaje.getId()) {
						juego.actualizarPersonaje();
						juego.getEstadoJuego().actualizarPersonaje();
						cliente.actualizarItems(paquetePersonaje);
					}
				}
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Fallo la conexión con el servidor.");
		}
	}
	/**Pide la ubicacion de los personajes
	 * @return devuelve el mapa con la ubicacion de los personajes
	 */
	public Map<Integer, PaqueteMovimiento> getUbicacionPersonajes() {
		return ubicacionPersonajes;
	}
	/**Pide los personajes conectados
	 * @return devuelve el mapa con los personajes conectados
	 */
	public Map<Integer, PaquetePersonaje> getPersonajesConectados() {
		return personajesConectados;
	}
}