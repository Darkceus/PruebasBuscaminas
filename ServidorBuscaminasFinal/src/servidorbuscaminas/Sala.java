package servidorbuscaminas;

import java.util.ArrayList;

/**
 *
 * @author DARKCEUS
 */
public class Sala {

    private int id;
    private Jugador Admin;
    private final ArrayList<Jugador> listaJugadores;
    private boolean disponible;
    private boolean iniciado;
    private Juego juego;
    private final ArrayList<Jugador> listaPerdedores;

    public Sala(int id, Jugador jugador) {
        this.id = id;
        this.listaJugadores = new ArrayList<>();
        this.listaPerdedores = new ArrayList<>();
        this.Admin = null;
        this.disponible = true;
        this.iniciado = false;
        this.agregarAdmin(jugador);
        //juego = new Juego(this);
    }

    public ArrayList<Jugador> getLista() {
        return this.listaJugadores;
    }
    
    public void agregarPerdedor(Jugador jugador){
        this.listaPerdedores.add(jugador);
        if(this.listaPerdedores.size() == this.getTam()){
            this.juego.mostrarPuntos();
        }
    }

    public Juego getJuego() {
        return this.juego;
    }

    public boolean checarJugador(Jugador jugador) {
        return this.listaJugadores.indexOf(jugador) >= 0;
    }

    public void setAdmin(Jugador admin) {
        this.Admin = admin;
    }

    public Jugador getAdmin() {
        return Admin;
    }

    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
    }

    public boolean estaDisponible() {
        return disponible;
    }

    public boolean estaIniciado() {
        return this.iniciado;
    }

    public void setID(int id) {
        this.id = id;
    }

    public int getID() {
        return this.id;
    }

    public Jugador getJugador(int indice) {
        return this.listaJugadores.get(indice);
    }
    
    public Jugador getPrimerJugador(){
        return this.listaJugadores.get(0);
    }

    public void enviarInfo(String mensaje) {
        listaJugadores.forEach((jugador) -> {
            jugador.getPW().println(mensaje);
        });
    }

    public void enviarDatosJuego() {
        listaJugadores.forEach((jugador) -> {
            jugador.getPW().println("DATOS " + Juego.getFILAS() + "," + Juego.getCOLUMNAS() + "," + Juego.getTAM_ALTO() + "," + Juego.getTAM_ANCHO()
                    + ","  + Juego.getNUMERO_MINAS() + "," + jugador.getID() + "," + this.getAdmin().getID());
        });
    }

    public int getTam() {
        return this.listaJugadores.size();
    }

    public boolean estaVacia() {
        return this.listaJugadores.isEmpty();
    }

    private void agregarAdmin(Jugador jugador) {
        this.Admin = jugador;
        this.agregarJugador(jugador);
    }

    public boolean agregarJugador(Jugador jugador) {
        jugador.setID(listaJugadores.size() + 1);
        jugador.setSala(this);
        return this.listaJugadores.add(jugador);
    }

    public boolean eliminarJugador(Jugador jugador) {
        if(this.Admin == jugador){
            this.Admin = null;
        }
        return this.listaJugadores.remove(jugador);
    }

    public boolean checarDisponibilidad() {
        return (disponible = (this.getTam() < 4)) && !iniciado;
    }

    public void iniciarJuego(Jugador jugador) {
        if (getTam() > 1 && !iniciado && disponible && this.Admin.equals(jugador)) {
            juego = new Juego(this);
            this.iniciado = true;
            this.disponible = false;
            this.enviarDatosJuego();
        }
        if(!this.Admin.equals(jugador)){
            jugador.getPW().println("INFOMESSAGE No eres el Admin");
        }
    }
}