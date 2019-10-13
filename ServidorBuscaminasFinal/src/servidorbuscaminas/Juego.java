package servidorbuscaminas;

import java.util.ArrayList;
import java.util.Collections;

/**
 *
 * @author DARKCEUS
 */
public class Juego {

    private static final int FILAS = 30;
    private static final int COLUMNAS = 30;
    private static final int TAM_ALTO = 20;
    private static final int TAM_ANCHO = 20;
    private static final int NUMERO_MINAS = 200;
    private final Campo[][] TABLERO;
    private final ArrayList<Campo> LISTA_MINAS;
    //private final int PUNTOS = 10;
    //private int puntos;
    private boolean inicio;
    private ArrayList<Campo> LISTA_PERIMETRO;
    private final int[] NUM_X = {-1, 0, 1, -1, 1, -1, 0, 1};
    private final int[] NUM_Y = {-1, -1, -1, 0, 0, 1, 1, 1};
    private final Sala sala;

    public Juego(Sala sala) {
        //validaciones();
        this.sala = sala;
        this.TABLERO = new Campo[FILAS][COLUMNAS];
        LISTA_MINAS = new ArrayList<>();
        crearTablero();
        colocarMinas();
        checarPerimetro();
        imprimirInfo();
    }

    public void setInicio(boolean inicio) {
        this.inicio = inicio;
    }

    public boolean getInicio() {
        return this.inicio;
    }

    public static int getFILAS() {
        return Juego.FILAS;
    }

    public static int getCOLUMNAS() {
        return Juego.COLUMNAS;
    }

    public static int getTAM_ALTO() {
        return Juego.TAM_ALTO;
    }

    public static int getTAM_ANCHO() {
        return Juego.TAM_ANCHO;
    }
    
    public static int getNUMERO_MINAS(){
        return Juego.NUMERO_MINAS;
    }

    private void imprimirInfo() {
        for (int y = 0; y < COLUMNAS; y++) {
            for (int x = 0; x < FILAS; x++) {
                System.out.print(TABLERO[x][y].getValor());
            }
            System.out.println("");
        }
    }

    private void crearTablero() {
        for (int y = 0; y < COLUMNAS; y++) {
            for (int x = 0; x < FILAS; x++) {
                TABLERO[x][y] = new Campo(x, y);
            }
        }
    }

    private void colocarMinas() {
        boolean minasC;
        int fila;
        int columna;
        Campo campo;
        int contarMinas = 0;
        do {
            minasC = false;
            for (int i = 0; i < NUMERO_MINAS; i++) {
                fila = (int) (Math.random() * FILAS);
                columna = (int) (Math.random() * COLUMNAS);
                campo = TABLERO[fila][columna];
                if (campo.getValor() == Campo.VALOR_VACIO) {
                    campo.setValor(Campo.VALOR_MINA);
                    LISTA_MINAS.add(campo);
                    contarMinas++;
                } else {
                    i--;
                }
            }
            if (contarMinas == NUMERO_MINAS) {
                minasC = true;
            }
        } while (minasC == false);
        System.out.println(contarMinas);
    }

    private void checarPerimetro() {
        int x2;
        int y2;
        Campo campo;
        for (Campo minas : LISTA_MINAS) {
            for (int i = 0; i < 8; i++) {
                x2 = NUM_X[i] + minas.getX();
                y2 = NUM_Y[i] + minas.getY();
                if (x2 >= 0 && x2 < FILAS && y2 >= 0 && y2 < COLUMNAS) {
                    campo = TABLERO[x2][y2];
                    if (campo.getValor() != Campo.VALOR_MINA) {
                        campo.aumentarValor();
                    }
                }
            }
        }
    }
    
    private boolean checarEsquina(Jugador jugador, Campo campo) {
        if (jugador.getID() == 1 && campo.getX() == 0) {
            return true;
        } else if (jugador.getID() == 2 && campo.getX() == (Juego.getFILAS() - 1)) {
            return true;
        } else if (jugador.getID() == 3 && campo.getY() == 0) {
            return true;
        } else if (jugador.getID() == 4 && campo.getY() == (Juego.getCOLUMNAS() - 1)) {
            return true;
        }
        return false;
    }
    
    private boolean checarClic(Jugador jugador, Campo campo) {
        if (jugador.checarClic()) {
            return true;
        }
        boolean esquina = checarEsquina(jugador, campo);
        if (esquina) {
            if (!jugador.checarClic()) {
                jugador.darClic();
                return true;
            }
        }
        if (jugador.getID() == 1 && !esquina) {
            jugador.getPW().println("INFOMESSAGE Debes iniciar por la Izquierda");
        } else if (jugador.getID() == 2 && !esquina) {
            jugador.getPW().println("INFOMESSAGE Debes iniciar por la Derecha");
        } else if (jugador.getID() == 3 && !esquina) {
            jugador.getPW().println("INFOMESSAGE Debes iniciar por la Arriba");
        } else if (jugador.getID() == 4 && !esquina) {
            jugador.getPW().println("INFOMESSAGE Debes iniciar por la Abajo");
        }
        return false;
    }
    
    public void descubrirCampo(Jugador jugador, int x, int y) {
        if (sala.estaIniciado() && jugador.getEstado() == Jugador.ESTADO_JUGANDO) {
            Campo campo = TABLERO[x][y];
            if (checarClic(jugador, campo)) {
                if (campo.getEstado() == Campo.ESTADO_INICIAL || (!campo.getAdmin().equals(jugador) && campo.getEstado() == Campo.ESTADO_BANDERA)) {
                    campo.setEstado(Campo.ESTADO_APLASTADO);
                    campo.setAdmin(jugador);
                    sala.enviarInfo("DESCUBRIRCAMPO " + x + "," + y + "," + campo.getValor());
                    if (campo.getValor() == Campo.VALOR_VACIO) {
                        revelarPerimetro(jugador, campo);
                    } else if (campo.getValor() == Campo.VALOR_MINA) {
                        jugador.setEstado(Jugador.ESTADO_ESPECTADOR);
                        campo.setEstado(Campo.ESTADO_APLASTADO);
                        sala.enviarInfo("HAYMINA " + x + "," + y + "," + jugador.getID());
                        sala.enviarInfo("MESSAGE " + jugador.getNombre() + " ha perdido");
                        sala.agregarPerdedor(jugador);
                    }
                }
            }
        }
    }

    private void revelarPerimetro(Jugador jugador, Campo campo) {
        int x2;
        int y2;
        Campo campo2;
        for (int i = 0; i < 8; i++) {
            x2 = NUM_X[i] + campo.getX();
            y2 = NUM_Y[i] + campo.getY();
            if ((x2 >= 0 && x2 < FILAS) && (y2 >= 0 && y2 < COLUMNAS)) {
                campo2 = TABLERO[x2][y2];
                if (campo2.getEstado() == Campo.ESTADO_INICIAL) {
                    campo2.setEstado(Campo.ESTADO_APLASTADO);
                    campo2.setAdmin(jugador);
                    sala.enviarInfo("DESCUBRIRCAMPO " + x2 + "," + y2 + "," + campo2.getValor());
                    if (campo2.getValor() == Campo.VALOR_VACIO) {
                        revelarPerimetro(jugador, campo2);
                    }
                }
            }
        }
    }
    
    public void gestionarBandera(Jugador jugador, int x, int y) {
        if (sala.estaIniciado() && jugador.getEstado() == Jugador.ESTADO_JUGANDO) {
            Campo campo = TABLERO[x][y];
            if (campo.getEstado() == Campo.ESTADO_INICIAL) {
                campo.setEstado(Campo.ESTADO_BANDERA);
                jugador.agregarBandera(campo);
                campo.setAdmin(jugador);
                sala.enviarInfo("PONERBANDERA " + x + "," + y + "," + jugador.getID());
            } else if (campo.getEstado() == Campo.ESTADO_BANDERA && campo.getAdmin().equals(jugador)) {
                campo.setEstado(Campo.ESTADO_INICIAL);
                jugador.quitarBandera(campo);
                campo.setAdmin(jugador);
                sala.enviarInfo("QUITARBANDERA " + x + "," + y + "," + jugador.getID());
            }
        }
    }

    public void checarMinas() {
        int checar = 0;
        Campo campo;
        for (int y = 0; y < COLUMNAS; y++) {
            for (int x = 0; x < FILAS; x++) {
                campo = TABLERO[x][y];
                if (campo.getEstado() == Campo.ESTADO_BANDERA && campo.getValor() == Campo.VALOR_MINA) {
                    checar++;
                }
            }
        }
        if (checar == NUMERO_MINAS) {
            sala.enviarInfo("GANAR");
        }
    }
    
    private void sacarPuntaje(){
       Campo campo;
        for (int y = 0; y < COLUMNAS; y++) {
            for (int x = 0; x < FILAS; x++) {
                campo = TABLERO[x][y];
                if (campo.getValor() != Campo.VALOR_MINA) {
                    if (campo.getEstado() == Campo.ESTADO_BANDERA) {
                        campo.setEstado(Campo.ESTADO_BANDERA_NO_MINA);
                        sala.enviarInfo("NOMINA " + x + "," + y);
                        for (Jugador jugador : sala.getLista()) {
                            if (campo.getAdmin().equals(jugador)) {
                                jugador.quitarPuntos();
                            }
                        }
                    } else if (campo.getEstado() == Campo.ESTADO_INICIAL || campo.getEstado() == Campo.ESTADO_APLASTADO) {
                        campo.setEstado(Campo.ESTADO_APLASTADO);
                        sala.enviarInfo("2DESCUBRIRCAMPO " + x + "," + y);
                    }
                } else {
                    if (campo.getEstado() == Campo.ESTADO_BANDERA) {
                        for (Jugador jugador : sala.getLista()) {
                            if(campo.getAdmin().equals(jugador)){
                                jugador.aumentarPuntos();
                            }
                        }
                    } else if (campo.getEstado() == Campo.ESTADO_INICIAL) {
                        campo.setEstado(Campo.ESTADO_APLASTADO);
                        sala.enviarInfo("2HAYMINA " + x + "," + y);
                    }
                }
            }
        } 
    }

    public void mostrarPuntos() {
        /*Campo campo;
        for (int y = 0; y < COLUMNAS; y++) {
            for (int x = 0; x < FILAS; x++) {
                campo = TABLERO[x][y];
                if (campo.getValor() == Campo.VALOR_VACIO) {
                    if (campo.getEstado() == Campo.ESTADO_BANDERA) {
                        campo.setEstado(Campo.ESTADO_BANDERA_NO_MINA);
                        sala.enviarInfo("NOMINA " + x + "," + y);
                        for (Jugador jugador : sala.getLista()) {
                            if (campo.getAdmin().equals(jugador)) {
                                jugador.quitarPuntos();
                            }
                        }
                    } else if (campo.getEstado() == Campo.ESTADO_INICIAL) {
                        campo.setEstado(Campo.ESTADO_APLASTADO);
                        sala.enviarInfo("2DESCUBRIRCAMPO " + x + "," + y);
                    }
                } else if (campo.getValor() == Campo.VALOR_MINA) {
                    if (campo.getEstado() == Campo.ESTADO_BANDERA) {
                        for (Jugador jugador : sala.getLista()) {
                            if(campo.getAdmin().equals(jugador)){
                                jugador.aumentarPuntos();
                            }
                        }
                    } else if (campo.getEstado() == Campo.ESTADO_INICIAL) {
                        campo.setEstado(Campo.ESTADO_APLASTADO);
                        sala.enviarInfo("2HAYMINA " + x + "," + y);
                    }
                }
            }
        }*/
        sacarPuntaje();
        inicio = false;
        //Collections.sort(sala.getLista(), (Jugador j1, Jugador j2) -> new Integer(j1.getPuntos()).compareTo(new Integer(j2.getPuntos())));
        sala.enviarInfo("MESSAGE El juego ha terminado");
        String algo = "";
        for (Jugador jugador : sala.getLista()) {
            algo += "Número: " + jugador.getID() + ", Nombre: " + jugador.getNombre() + ", Puntos: " + jugador.getPuntos() + ".";
        }
        sala.enviarInfo("PUNTOS " + algo);
        sala.reiniciarDatos();
    }
}