package servidorbuscaminas;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author DARKCEUS
 */
public class ServidorBuscaminas {

    public static final Map<Integer, Sala> SALAS = new TreeMap<>();
    //public static final ArrayList<Jugador> jugadores = new ArrayList<>();
    
    public static void main(String[] args) throws IOException {
        System.out.println("El Servidor de Buscaminas está en línea...");
        ExecutorService pool = Executors.newFixedThreadPool(500);
        try (ServerSocket listener = new ServerSocket(59001)) {
            while (true) {
                pool.execute(new Handler(listener.accept()));
            }
        }
    }

    public static class Handler implements Runnable {

        public Jugador jugador;
        public String nombre;
        public Sala sala;
        public Socket socket;
        public PrintWriter Escritor;
        public int numerosala;
        public Scanner Entrada;
        private boolean prueba = false;
        private final String SIN_REPETICIONES = "[a-zA-Z0-9]{1}";
        private final String ALFANUMERICO = "[a-zA-Z0-9]+";
        private final String PATRON_NOMBRE = ALFANUMERICO;
        private final String AYUDA = "ayuda";
        private final String AYUDA_C = "Comandos:,/nombre_usuario mensaje       Envías mensaje privado,/salir     Sales del Servidor,"
                + "/bloquear nombre_usuario      Bloqueas al Usuario,/desbloquear "
                + "nombre_usuario       Desbloqueas al Usuario,/listau     Muestra la lista de Usuarios,/listab       "
                + "Muestra tu lista de Usuarios Bloqueados,/cnombre nuevo_nombre       Cambias tu nombre";
        private final String SALIR = "salir";
        private final String BLOQUEAR = "bloquear";
        private final String DESBLOQUEAR = "desbloquear";
        private final String MOSTRAR = "listau";
        private final String MOSTRAR_B = "listab";
        private final String CAMBIAR_NOMBRE = "cnombre";
        private final String SEPARADOR = ",";
        private final String INFO_NOMBRE = "El nombre debe estar en Alfanumérico.,No debe estar vacío,No debe tener espacios vacíos,No debe tener más de 15 caracteres, Y no debe ser igual a cualquiera de estos:," + AYUDA + " - " + SALIR + " - " + BLOQUEAR + " - " + DESBLOQUEAR + " - " + MOSTRAR + " - " + MOSTRAR_B + " - " + CAMBIAR_NOMBRE;
        private String nombres = "";
        private String bloqueados = "";

        public Handler(Socket socket) {
            this.socket = socket;
        }
        
        /*public void EnviarMensaje(String input) {
            if (!input.equals("")) {
                for (Jugador informacionJugador : jugadores) {
                    if (informacionJugador.getSala() == numerosala) {
                        informacionJugador.getPW().println("MENSAJE " + informacionJugador.getNombre() + ": " + input);
                        System.out.println("este vato esta en sala: " + numerosala);
                        System.out.println("sala del vato a enviar: " + informacionJugador.getSala());
                        System.out.println("sala del vato a que envia : " + numerosala);
                    }

                }
            }
        }*/
        
        public int convertirInt(String num){
            int num2 = -1;
            try{
                num2 = Integer.parseInt(num);
            }catch(NumberFormatException e){}
            return num2;
        }

        @Override
        public void run() {
            try {
                Entrada = new Scanner(socket.getInputStream());
                Escritor = new PrintWriter(socket.getOutputStream(), true);
                while (true) {
                    Escritor.println("NOMBREDEENVIO");
                    nombre = Entrada.nextLine();
                    if (nombre == null || nombre.isEmpty() || nombre.equals("") || !nombre.matches(PATRON_NOMBRE) || nombre.indexOf(' ') >= 0 || nombre.equalsIgnoreCase(AYUDA) || nombre.equalsIgnoreCase(SALIR) || nombre.equalsIgnoreCase(BLOQUEAR) || nombre.equalsIgnoreCase(DESBLOQUEAR) || nombre.equalsIgnoreCase(MOSTRAR) || nombre.equalsIgnoreCase(MOSTRAR_B) || nombre.equalsIgnoreCase(CAMBIAR_NOMBRE) || nombre.startsWith("/") || nombre.length() > 15) {
                        //Escritor.println("INFO2MESSAGE " + INFO_NOMBRE);
                        continue;
                    }
                    if (nombre.equals("null")) {
                        prueba = true;
                        return;
                    }
                    synchronized (SALAS) {
                        jugador = new Jugador(nombre, Escritor);
                        for (Sala sala2 : SALAS.values()) {
                            if (sala2.checarDisponibilidad()) {
                                if (!sala2.checarJugador(jugador)) {
                                    this.sala = sala2;
                                    this.sala.agregarJugador(jugador);
                                    this.jugador.getPW().println("INFOMESSAGE Bienvenido " + nombre);
                                    break;
                                }
                            }
                        }
                        if (this.sala == null) {
                            int id = SALAS.size() + 1;
                            this.sala = new Sala(id, jugador);
                            SALAS.put(id, this.sala);
                            this.jugador.getPW().println("INFOMESSAGE Bienvenido " + nombre + ", eres el primero en entrar");
                        }
                        break;
                    }
                }
                Escritor.println("NAMEACCEPTED " + nombre);
                sala.enviarInfo("MESSAGE " + jugador.getNombre() + " ha entrado");
                while (true) {
                    String input;
                    try {input = Entrada.nextLine();} catch (Exception e) {return;}
                    if (input != null && !input.equals("") && !input.isEmpty()) {
                        int espacio = input.indexOf(' ');
                        boolean cap = espacio >= 0 && espacio < input.length();
                        if (input.toLowerCase().startsWith("/"+SALIR)) {
                            return;
                        } else if (input.toLowerCase().startsWith("/iniciarjuego")) {
                            sala.iniciarJuego(jugador);
                        } else if (input.startsWith("ABRIRJUEGO ")) {
                            
                        } else if (input.startsWith("CLICIZQUIERDO ")) {
                            if (cap) {
                                String[] coordenadas = input.substring(espacio + 1).split(",");
                                if (coordenadas.length == 2) {
                                    sala.getJuego().descubrirCampo(jugador, convertirInt(coordenadas[0]), convertirInt(coordenadas[1]));
                                }
                            }
                        } else if (input.startsWith("CLICDERECHO ")) {
                            if (cap) {
                                String[] coordenadas = input.substring(espacio + 1).split(",");
                                if (coordenadas.length == 2) {
                                    sala.getJuego().gestionarBandera(jugador, convertirInt(coordenadas[0]), convertirInt(coordenadas[1]));
                                }
                            }
                        } else {
                            sala.enviarInfo("MESSAGE " + jugador.getNombre() + ": " + input);
                        }
                    }
                }
            } catch (IOException e) {
                System.out.println(e);
            } finally {
                if (Escritor != null || sala != null || jugador != null) {
                    if (!prueba) {
                        sala.eliminarJugador(jugador);
                        if (!sala.estaVacia()) {
                            sala.enviarInfo("MESSAGE " + jugador.getNombre() + " ha salido");
                            if (sala.getPrimerJugador() != sala.getAdmin()) {
                                sala.setAdmin(sala.getPrimerJugador());
                                sala.enviarInfo("MESSAGE " + sala.getJugador(0).getNombre() + " es el nuevo Administrador de la Sala");
                            }
                        } else {
                            SALAS.remove(sala.getID());
                        }
                    }
                }
                try {
                    socket.close();
                } catch (IOException e) {
                }
            }
        }
    }
}
