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
import javax.swing.JOptionPane;

/**
 *
 * @author DARKCEUS
 */
public class ServidorBuscaminas {

    public static final Map<Integer, Sala> SALAS = new TreeMap<>();
    private int puerto;
    
    public static void main(String[] args) {
        ServidorBuscaminas sb = new ServidorBuscaminas();
        sb.iniciarServidor();
    }
    
    private void iniciarServidor() {
        validarPuerto(getPuerto());
        System.out.println("El Servidor de Buscaminas está en línea...");
        ExecutorService pool = Executors.newFixedThreadPool(500);
        try (ServerSocket listener = new ServerSocket(59001)) {
            while (true) {
                pool.execute(new Handler(listener.accept()));
            }
        } catch (IOException e) {
            System.out.println("Error al Abrir el Servidor");
        }
    }
    
    private String getPuerto() {
        return JOptionPane.showInputDialog(null, "Puerto", "Ingresa un puerto: ", JOptionPane.PLAIN_MESSAGE);
    }
    
    private void validarPuerto(String valor) {
        try {
            this.puerto = Integer.parseInt(valor);
            if (this.puerto < 0) {
                System.err.println("Debes de poner un puerto válido");
                System.exit(0);
            }
        } catch (NumberFormatException e) {
            System.err.println("Debes de poner un puerto válido");
            System.exit(0);
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

        public Handler(Socket socket) {
            this.socket = socket;
        }
        
        public int convertirInt(String num) {
            int num2 = -1;
            try {
                num2 = Integer.parseInt(num);
            } catch (NumberFormatException e) {}
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
                    if (nombre == null || nombre.isEmpty() || nombre.equals("") || !nombre.matches(PATRON_NOMBRE) || nombre.indexOf(' ') >= 0 || nombre.startsWith("/") || nombre.length() > 15) {
                        continue;
                    }
                    if (nombre.equals("null")) {
                        prueba = true;
                        return;
                    }
                    synchronized (SALAS) {
                        boolean entro = false;
                        jugador = new Jugador(nombre, Escritor);
                        for (Sala sala2 : SALAS.values()) {
                            if (sala2.checarDisponibilidad()) {
                                if (!sala2.checarJugador(jugador)) {
                                    this.sala = sala2;
                                    this.sala.agregarJugador(jugador);
                                    this.jugador.getPW().println("INFOMESSAGE Bienvenido " + nombre);
                                    entro = true;
                                    break;
                                }
                            }
                        }
                        if (!entro) {
                            int id = SALAS.size() + 1;
                            this.sala = new Sala(id, jugador);
                            SALAS.put(id, this.sala);
                            this.jugador.getPW().println("INFOMESSAGE Bienvenido " + nombre + ", eres el primero en entrar");
                        }
                        break;
                    }
                }
                Escritor.println("NAMEACCEPTED " + nombre + "," + sala.getID());
                sala.enviarInfo("MESSAGE [Servidor] " + jugador.getNombre() + " ha entrado");
                while (true) {
                    String input;
                    try {input = Entrada.nextLine();} catch (Exception e) {return;}
                    if (input != null && !input.equals("") && !input.isEmpty()) {
                        int espacio = input.indexOf(' ');
                        boolean prueba = espacio >= 0 && espacio < input.length();
                        if (input.toLowerCase().startsWith("/iniciarjuego")) {
                            if (sala.getJuego().validaciones()) {
                                sala.iniciarJuego(jugador);
                            } else {
                                sala.enviarInfo("MESSAGE [Servidor] Los datos en el servidor son erróneos.");
                            }
                        } else if (input.startsWith("CLICIZQUIERDO ")) {
                            if (prueba) {
                                String[] coordenadas = input.substring(espacio + 1).split(",");
                                if (coordenadas.length == 2) {
                                    sala.getJuego().descubrirCampo(jugador, convertirInt(coordenadas[0]), convertirInt(coordenadas[1]));
                                }
                            }
                        } else if (input.startsWith("CLICDERECHO ")) {
                            if (prueba) {
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
                        jugador.quitarBanderas();
                        sala.eliminarJugador(jugador);
                        if (sala.getTam() == 1 && sala.estaIniciado()) {
                            sala.enviarInfo("INFOMESSAGE Eres el único que queda, el juego va a terminar");
                            sala.getJuego().mostrarPuntos();
                        }
                        if (!sala.estaVacia()) {
                            sala.enviarInfo("MESSAGE [Servidor] " + jugador.getNombre() + " ha salido");
                            Jugador j = sala.getPrimerJugador();
                            if (j != sala.getAdmin()) {
                                sala.setAdmin(j);
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
