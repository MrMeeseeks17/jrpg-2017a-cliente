package juego;

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.JOptionPane;

import cliente.Cliente;
import mensajeria.Comando;
import recursos.Recursos;

public class CargarRecursos extends Thread {

	private Cliente cliente;

	public CargarRecursos(Cliente cliente) {
		this.cliente = cliente;
	}

	@Override
	public void run() {
		synchronized (cliente) {
			try {
				Recursos.cargar(cliente.getMenuCarga());
			} catch (FileNotFoundException e) {
				JOptionPane.showMessageDialog(null, "Error de archivo al cargar Recursos");
			} catch (NumberFormatException e) {
				JOptionPane.showMessageDialog(null, "Error de formato al cargar Recursos");
			} catch (IOException e) {
				JOptionPane.showMessageDialog(null, "Error de IO al cargar Recursos");
			}

			cliente.setAccion(Comando.SALIR);
			cliente.notify();
		}
	}

}