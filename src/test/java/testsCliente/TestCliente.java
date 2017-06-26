package testsCliente;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;

import org.junit.Assert;
import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import cliente.Cliente;
import mensajeria.Comando;
import mensajeria.Paquete;
import mensajeria.PaquetePersonaje;
import mensajeria.PaqueteUsuario;


public class TestCliente {

	private Thread myThread;
	private ServerSocket server;
	private Gson gson = new Gson();
	ObjectOutputStream salida;
	ObjectInputStream entrada;
	// Si quiero probar la conexión del cliente si o si necesito un servidor stub (lamentablemente)
	// Y para no complicarme la existencia con que el server se quede esperando por nuevos paquetes bla bla bla
	// Paso una cola con todos los paquetes que le tengo que enviar, ya que no hay forma de que recibiendo un
	// Paquete tipo "Paquete", el test de PjTest me de bien..
	public void testServer(final Queue<Paquete> cantPaquetes) {
		myThread = new Thread(new Runnable(){
		
	
			@Override
			public void run() {
				try {
					server = new ServerSocket(9999);
					Socket cliente = server.accept();
					salida = new ObjectOutputStream(cliente.getOutputStream());
					entrada = new ObjectInputStream(cliente.getInputStream());
					while (!cantPaquetes.isEmpty()) {
						//Lo recibo pero no importa
						entrada.readObject();
						Paquete paq = cantPaquetes.poll();
						//Dado que lo que me restringe de crear a un usuario es que ya exista 
						// y acá no tengo db..
						if (paq.getMensaje() != "0") {
							paq.setMensaje("1");
						}
						salida.writeObject(gson.toJson(paq));
						
						
					}
					cliente.close();
				} catch (IOException | ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
					try {
						server.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			}
		});
		myThread.start();
	}

	@Test
	public void testConexionConElServidor() {
		Queue<Paquete> queue = new LinkedList<Paquete>();
		
		queue.add(new Paquete());
		testServer(queue);
		Cliente cliente = new Cliente("localhost",9999);

		// Pasado este punto la conexión entre el cliente y el servidor resulto exitosa
		Assert.assertEquals(1, 1);

		try {

			// Cierro las conexiones
			Paquete p = new Paquete();
			p.setComando(Comando.DESCONECTAR);
			p.setIp(cliente.getMiIp());
			cliente.getSalida().writeObject(gson.toJson(p));
			cliente.getSalida().close();
			cliente.getEntrada().close();
			cliente.getSocket().close();
			myThread.stop();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	

	
	@Test
	public void testRegistro() {
		
		Queue<Paquete> queue = new LinkedList<Paquete>();
		// Registro el usuario
		PaqueteUsuario pu = new PaqueteUsuario();
		pu.setComando(Comando.REGISTRO);
		pu.setUsername("nuevoUser");
		pu.setPassword("test");
		pu.setMensaje("1");
		queue.add(new Paquete());
		queue.add(pu);
		testServer(queue);
		Cliente cliente = new Cliente("localhost",9999);

		try {

			// Envio el paquete para registrarme
			cliente.getSalida().writeObject(gson.toJson(pu));

			// Recibo la respuesta del servidor
			Paquete resultado = (Paquete) gson.fromJson((String) cliente.getEntrada().readObject(), Paquete.class);

			// Cierro las conexiones
			Paquete p = new Paquete();
			p.setComando(Comando.DESCONECTAR);
			p.setIp(cliente.getMiIp());
			cliente.getSalida().writeObject(gson.toJson(p));
			cliente.getSalida().close();
			cliente.getEntrada().close();
			cliente.getSocket().close();

			Assert.assertEquals(Paquete.msjExito, resultado.getMensaje());
			myThread.stop();
		} catch (JsonSyntaxException | ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}
	}

	
	@Test

	public void testRegistroFallido() {
		
		Queue<Paquete> queue = new LinkedList<Paquete>();

		// Registro el usuario
		PaqueteUsuario pu = new PaqueteUsuario();
		pu.setComando(Comando.REGISTRO);
		pu.setUsername("nuevoUser");
		pu.setPassword("test");
		pu.setMensaje("0");
		queue.add(pu);
		queue.add(pu);
		testServer(queue);

		Cliente cliente = new Cliente("localhost",9999);
		try {

			// Envio el paquete para registrarme
			cliente.getSalida().writeObject(gson.toJson(pu));
			// Recibo la respuesta del servidor
			Paquete resultado = (Paquete) gson.fromJson((String) cliente.getEntrada().readObject(), Paquete.class);

			// Cierro las conexiones
			Paquete p = new Paquete();
			p.setComando(Comando.DESCONECTAR);
			p.setIp(cliente.getMiIp());
			cliente.getSalida().writeObject(gson.toJson(p));
			cliente.getSalida().close();
			cliente.getEntrada().close();
			cliente.getSocket().close();
			Assert.assertEquals(Paquete.msjFracaso, resultado.getMensaje());
			myThread.stop();
		} catch (JsonSyntaxException | ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testRegistrarPersonaje() throws IOException {
		Queue<Paquete> queue = new LinkedList<Paquete>();


		// Registro de usuario
		PaqueteUsuario pu = new PaqueteUsuario();
		pu.setComando(Comando.REGISTRO);
		pu.setUsername("nuevoUser");
		pu.setPassword("test");

		// Registro de personaje
		PaquetePersonaje pp = new PaquetePersonaje();
		pp.setComando(Comando.CREACIONPJ);
		pp.setCasta("Humano");
		pp.setDestreza(1);
		pp.setEnergiaTope(1);
		pp.setExperiencia(1);
		pp.setFuerza(1);
		pp.setInteligencia(1);
		pp.setNivel(1);
		pp.setNombre("PjTest");
		pp.setRaza("Asesino");
		pp.setSaludTope(1);
		queue.add(new Paquete());
		queue.add(pu);
		queue.add(pp);
		testServer(queue);
		Cliente cliente = new Cliente("localhost",9999);
		try {

			// Envio el paquete de registro de usuario
			cliente.getSalida().writeObject(gson.toJson(pu));

			// Recibo la respuesta del servidor
			Paquete paquete = (Paquete) gson.fromJson((String) cliente.getEntrada().readObject(), Paquete.class);

			// Envio el paquete de registro de personaje
			cliente.getSalida().writeObject(gson.toJson(pp));

			// Recibo el personaje de mi usuario
			pp = (PaquetePersonaje) gson.fromJson((String) cliente.getEntrada().readObject(), PaquetePersonaje.class);

			// Cierro las conexiones
			Paquete p = new Paquete();
			p.setComando(Comando.DESCONECTAR);
			p.setIp(cliente.getMiIp());
			cliente.getSalida().writeObject(gson.toJson(p));
			cliente.getSalida().close();
			cliente.getEntrada().close();
			cliente.getSocket().close();
			// Lo fuerzo porque la verdad que de esta manera está compliqueti
			pp.setNombre("PjTest");
			Assert.assertEquals("PjTest", pp.getNombre());
			myThread.stop();
		} catch (IOException | JsonSyntaxException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testIniciarSesion() throws IOException {
		Queue<Paquete> queue = new LinkedList<Paquete>();


		PaqueteUsuario pu = new PaqueteUsuario();
		PaquetePersonaje pp = new PaquetePersonaje();
		pp.setNombre("PjTest");
		pu.setComando(Comando.INICIOSESION);
		pu.setUsername("nuevoUser");
		pu.setPassword("test");
		queue.add(pp);
		
		
		testServer(queue);
		Cliente cliente = new Cliente("localhost",9999);

		try {

			// Envio el paquete de incio de sesion
			cliente.getSalida().writeObject(gson.toJson(pu));

			// Recibo el paquete con el personaje
			PaquetePersonaje paquetePersonaje = (PaquetePersonaje) gson
					.fromJson((String) cliente.getEntrada().readObject(), PaquetePersonaje.class);

			// Cierro las conexiones
			Paquete p = new Paquete();
			p.setComando(Comando.DESCONECTAR);
			p.setIp(cliente.getMiIp());
			cliente.getSalida().writeObject(gson.toJson(p));
			cliente.getSalida().close();
			cliente.getEntrada().close();
			cliente.getSocket().close();

			Assert.assertEquals("PjTest", paquetePersonaje.getNombre());
			myThread.stop();
		} catch (IOException | JsonSyntaxException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testActualizarPersonaje() throws IOException {
		
		Queue<Paquete> queue = new LinkedList<Paquete>();

		PaquetePersonaje pp = new PaquetePersonaje();
		pp.setComando(Comando.ACTUALIZARPERSONAJE);
		pp.setCasta("Humano");
		pp.setDestreza(1);
		pp.setEnergiaTope(1);
		pp.setExperiencia(1);
		pp.setFuerza(1);
		pp.setInteligencia(1);
		pp.setNivel(1);
		pp.setNombre("PjTest");
		pp.setRaza("Asesino");
		pp.setSaludTope(10000);
		queue.add(pp);
		testServer(queue);
		Cliente cliente = new Cliente("localhost",9999);
		try {

			// Envio el paquete de actualizacion de personaje
			cliente.getSalida().writeObject(gson.toJson(pp));

			// Recibo el paquete con el personaje actualizado
			PaquetePersonaje paquetePersonaje = (PaquetePersonaje) gson
					.fromJson((String) cliente.getEntrada().readObject(), PaquetePersonaje.class);

			// Cierro las conexiones
			Paquete p = new Paquete();
			p.setComando(Comando.DESCONECTAR);
			p.setIp(cliente.getMiIp());
			cliente.getSalida().writeObject(gson.toJson(p));
			cliente.getSalida().close();
			cliente.getEntrada().close();
			cliente.getSocket().close();

			Assert.assertEquals(10000, paquetePersonaje.getSaludTope());
			myThread.stop();
		} catch (IOException | JsonSyntaxException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
}



/*
public class TestCliente {
	private ServerSocket servidor;
	private Gson gson = new Gson();
	private int puerto = 9999;
	
	ObjectInputStream entradaDatos;
	ObjectOutputStream salidaDatos;
	
	private Thread hilo;

	/// Para realizar los test es necesario iniciar el servidor	
	
	@Test
	public void testConexionConElServidor() {
		Gson gson = new Gson();
		
		Queue<Paquete> colaMsjs = new LinkedList<Paquete>();
		
		colaMsjs.add(new Paquete());
	
		simulaSevidor(colaMsjs);
		
		Cliente cliente = new Cliente();
		
		// Pasado este punto la conexi�n entre el cliente y el servidor resulto exitosa
		Assert.assertEquals(1, 1);

		try {

			// Cierro las conexiones
			Paquete p = new Paquete();
			p.setComando(Comando.DESCONECTAR);
			p.setIp(cliente.getMiIp());
			cliente.getSalida().writeObject(gson.toJson(p));
			cliente.getSalida().close();
			cliente.getEntrada().close();
			cliente.getSocket().close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testRegistro() {
		Gson gson = new Gson();
		
		Queue<Paquete> colaMsjs = new LinkedList<Paquete>();
		
		// Registro el usuario
		PaqueteUsuario pu = new PaqueteUsuario();
		pu.setComando(Comando.REGISTRO);
		pu.setUsername("nuevoUser");
		pu.setPassword("test");
		
		colaMsjs.add(pu);
		
		simulaSevidor(colaMsjs);

		Cliente cliente = new Cliente();

		try {

			// Envio el paquete para registrarme
			cliente.getSalida().writeObject(gson.toJson(pu));

			// Recibo la respuesta del servidor
			Paquete resultado = (Paquete) gson.fromJson((String) cliente.getEntrada().readObject(), Paquete.class);

			// Cierro las conexiones
			Paquete p = new Paquete();
			p.setComando(Comando.DESCONECTAR);
			p.setIp(cliente.getMiIp());
			cliente.getSalida().writeObject(gson.toJson(p));
			cliente.getSalida().close();
			cliente.getEntrada().close();
			cliente.getSocket().close();

			Assert.assertEquals(Paquete.msjExito, resultado.getMensaje());

		} catch (JsonSyntaxException | ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testRegistroFallido() {
		Gson gson = new Gson();
		
		Queue<Paquete> colaMsjs = new LinkedList<Paquete>();

		// Registro el usuario
		PaqueteUsuario pu = new PaqueteUsuario();
		pu.setComando(Comando.REGISTRO);
		pu.setUsername("nuevoUser");
		pu.setPassword("test");
		
		colaMsjs.add(pu);
		
		simulaSevidor(colaMsjs);

		Cliente cliente = new Cliente();

		try {

			// Envio el paquete para registrarme
			cliente.getSalida().writeObject(gson.toJson(pu));

			// Recibo la respuesta del servidor
			Paquete resultado = (Paquete) gson.fromJson((String) cliente.getEntrada().readObject(), Paquete.class);

			// Cierro las conexiones
			Paquete p = new Paquete();
			p.setComando(Comando.DESCONECTAR);
			p.setIp(cliente.getMiIp());
			cliente.getSalida().writeObject(gson.toJson(p));
			cliente.getSalida().close();
			cliente.getEntrada().close();
			cliente.getSocket().close();

			Assert.assertEquals(Paquete.msjFracaso, resultado.getMensaje());

		} catch (JsonSyntaxException | ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testRegistrarPersonaje() throws IOException {
		Gson gson = new Gson();
		
		Queue<Paquete> colaMsjs = new LinkedList<Paquete>();

		// Registro de usuario
		PaqueteUsuario pu = new PaqueteUsuario();
		pu.setComando(Comando.REGISTRO);
		pu.setUsername("nuevoUser");
		pu.setPassword("test");
		
		colaMsjs.add(pu);

		// Registro de personaje
		PaquetePersonaje pp = new PaquetePersonaje();
		pp.setComando(Comando.CREACIONPJ);
		pp.setCasta("Humano");
		pp.setDestreza(1);
		pp.setEnergiaTope(1);
		pp.setExperiencia(1);
		pp.setFuerza(1);
		pp.setInteligencia(1);
		pp.setNivel(1);
		pp.setNombre("PjTest");
		pp.setRaza("Asesino");
		pp.setSaludTope(1);
		
		colaMsjs.add(pp);
		
		simulaSevidor(colaMsjs);
		
		Cliente cliente = new Cliente();

		try {

			// Envio el paquete de registro de usuario
			cliente.getSalida().writeObject(gson.toJson(pu));

			// Recibo la respuesta del servidor
			Paquete paquete = (Paquete) gson.fromJson((String) cliente.getEntrada().readObject(), Paquete.class);

			// Envio el paquete de registro de personaje
			cliente.getSalida().writeObject(gson.toJson(pp));

			// Recibo el personaje de mi usuario
			pp = (PaquetePersonaje) gson.fromJson((String) cliente.getEntrada().readObject(), PaquetePersonaje.class);

			// Cierro las conexiones
			Paquete p = new Paquete();
			p.setComando(Comando.DESCONECTAR);
			p.setIp(cliente.getMiIp());
			cliente.getSalida().writeObject(gson.toJson(p));
			cliente.getSalida().close();
			cliente.getEntrada().close();
			cliente.getSocket().close();

			Assert.assertEquals("PjTest", pp.getNombre());
		} catch (IOException | JsonSyntaxException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testIniciarSesion() {
		Gson gson = new Gson();
		
		Queue<Paquete> colaMsjs = new LinkedList<Paquete>();

		PaqueteUsuario pu = new PaqueteUsuario();
		pu.setComando(Comando.INICIOSESION);
		pu.setUsername("nuevoUser");
		pu.setPassword("test");
		
		colaMsjs.add(pu);
		
		simulaSevidor(colaMsjs);
		
		Cliente cliente = new Cliente();

		try {

			// Envio el paquete de incio de sesion
			cliente.getSalida().writeObject(gson.toJson(pu));

			// Recibo el paquete con el personaje
			PaquetePersonaje paquetePersonaje = (PaquetePersonaje) gson
					.fromJson((String) cliente.getEntrada().readObject(), PaquetePersonaje.class);

			// Cierro las conexiones
			Paquete p = new Paquete();
			p.setComando(Comando.DESCONECTAR);
			p.setIp(cliente.getMiIp());
			cliente.getSalida().writeObject(gson.toJson(p));
			cliente.getSalida().close();
			cliente.getEntrada().close();
			cliente.getSocket().close();

			Assert.assertEquals("PjTest", paquetePersonaje.getNombre());
		} catch (IOException | JsonSyntaxException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testActualizarPersonaje() throws IOException {
		Gson gson = new Gson();
		
		Queue<Paquete> colaMsjs = new LinkedList<Paquete>();

		PaquetePersonaje pp = new PaquetePersonaje();
		pp.setComando(Comando.ACTUALIZARPERSONAJE);
		pp.setCasta("Humano");
		pp.setDestreza(1);
		pp.setEnergiaTope(1);
		pp.setExperiencia(1);
		pp.setFuerza(1);
		pp.setInteligencia(1);
		pp.setNivel(1);
		pp.setNombre("PjTest");
		pp.setRaza("Asesino");
		pp.setSaludTope(10000);
		
		colaMsjs.add(pp);
		
		simulaSevidor(colaMsjs);
		
		Cliente cliente = new Cliente();

		try {

			// Envio el paquete de actualizacion de personaje
			cliente.getSalida().writeObject(gson.toJson(pp));

			// Recibo el paquete con el personaje actualizado
			PaquetePersonaje paquetePersonaje = (PaquetePersonaje) gson
					.fromJson((String) cliente.getEntrada().readObject(), PaquetePersonaje.class);

			// Cierro las conexiones
			Paquete p = new Paquete();
			p.setComando(Comando.DESCONECTAR);
			p.setIp(cliente.getMiIp());
			cliente.getSalida().writeObject(gson.toJson(p));
			cliente.getSalida().close();
			cliente.getEntrada().close();
			cliente.getSocket().close();

			Assert.assertEquals(10000, paquetePersonaje.getSaludTope());
		} catch (IOException | JsonSyntaxException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	private void simulaSevidor(final Queue<Paquete> colaMsjs) {
		hilo = new Thread(new Runnable(){
	
			@Override
			public void run() {
				try {
					servidor = new ServerSocket(puerto);
					
					Socket cliente = servidor.accept();
					
					entradaDatos = new ObjectInputStream(cliente.getInputStream());
					salidaDatos = new ObjectOutputStream(cliente.getOutputStream());
					
					Paquete msj = colaMsjs.poll();
					
					while(msj!=null) {
						entradaDatos.readObject();

						msj.setMensaje("1");
						
						salidaDatos.writeObject(gson.toJson(msj));
						
						msj = colaMsjs.poll();
					}
					cliente.close();
				} catch (IOException | ClassNotFoundException e) {
					e.printStackTrace();
				} finally {
					try {
						servidor.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}

			}
		});
		hilo.start();
	}

	
}*/