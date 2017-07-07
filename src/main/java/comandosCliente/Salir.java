package comandosCliente;

import java.io.IOException;

import javax.swing.JOptionPane;

import mensajeria.Comando;
import mensajeria.Paquete;

public class Salir extends ComandosCliente {

	@Override
	public void ejecutar() {
		cliente.getPaqueteUsuario().setInicioSesion(false);
		try {
			cliente.getSalida().writeObject(new Paquete(Comando.SALIR).getJson());
			cliente.getSocket().close();
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Error producido al Salir");
			System.exit(1);
		}

}

}
