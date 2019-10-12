package servidorbuscaminas;

import java.io.PrintWriter;

/**
 *
 * @author DARKCEUS
 */
public class Jugador {

    public final static boolean ESTADO_JUGANDO = true;
    public final static boolean ESTADO_ESPECTADOR = false;
    private int id;
    private boolean estado;
    private Sala sala;
    private String nombre;
    private PrintWriter pw;
    private int puntos;

    public Jugador(String nombre, PrintWriter pw) {
        this.nombre = nombre;
        this.pw = pw;
        this.estado = Jugador.ESTADO_JUGANDO;
        this.puntos = 0;
    }

    public void setSala(Sala sala) {
        this.sala = sala;
    }

    public Sala getSala() {
        return this.sala;
    }
    
    public void aumentarPuntos(){
        this.puntos++;
    }
    
    public void quitarPuntos(){
        this.puntos--;
    }
    
    public int getPuntos(){
        return this.puntos;
    }

    public void setID(int id) {
        this.id = id;
    }

    public int getID() {
        return this.id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String id) {
        this.nombre = id;
    }

    public PrintWriter getPW() {
        return pw;
    }

    public void setPW(PrintWriter pw) {
        this.pw = pw;
    }

    public boolean getEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }
}
