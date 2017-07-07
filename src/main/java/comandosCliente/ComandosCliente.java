package comandosCliente;

import cliente.Cliente;
import mensajeria.Comando;

public abstract class ComandosCliente extends Comando {

	public static final String COMANDOSCLIENTE = "COMANDOSCLIENTE";
	// protected Juego juego;
	protected Cliente cliente;

	// public void setJuego(Juego juego) {
	// this.juego = juego;
	// }

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}
	
	public abstract void ejecutar();
}