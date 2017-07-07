package mensajeria;

public class Comando {

	
	public static final String[] CLASESPOLIMORFISMO =  {"Conexion","CreacionPJ","Desconectar","InicioSesion",
			"MostrarMapas","Movimiento","Registro","Salir","Batalla","Atacar",
"FinalizarBatalla","ActualizarPersonaje", "ActualizarInventario" , "Mercado", "FinalizarMercado"};
	
	public static final String NOMBREPAQUETE = "PaqueteClaseComando";
	
	public static final int ACTUALIZARPERSONAJE = 11;
	public static final int ATACAR = 9;
	public static final int BATALLA = 8;
	public static final int CONEXION = 0;
	public static final int CREACIONPJ = 1;
	public static final int DESCONECTAR = 2;
	public static final int FINALIZARBATALLA = 10;
	public static final int INICIOSESION = 3;
	public static final int MOSTRARMAPAS = 4;
	public static final int MOVIMIENTO = 5;
	public static final int REGISTRO = 6;
	public static final int SALIR = 7;
	public static final int ACTUALIZARINVENTARIO = 12;
	public static final int MERCADO = 13;
	public static final int FINALIZARMERCADO = 14;
	
	


	
protected Paquete paquete;
	
	public void setPaquete(Paquete p){
		this.paquete = p;
}
	
	
	
}