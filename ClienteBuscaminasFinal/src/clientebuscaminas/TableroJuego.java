package clientebuscaminas;

/**
 *
 * @author DARKCEUS
 */
import java.awt.*;
import java.awt.event.*;
import java.io.PrintWriter;
import javax.swing.*;

public class TableroJuego extends JPanel {
    
    /*private final int BANDERA_J1 = 11;
    private final int BANDERA_J2 = 13;
    private final int BANDERA_J3 = 14;
    private final int BANDERA_J4 = 15;*/
    private final Color COLOR_J1 = Color.YELLOW;
    private final Color COLOR_J2 = Color.BLUE;
    private final Color COLOR_J3 = Color.GREEN;
    private final Color COLOR_J4 = Color.ORANGE;
    private final Color COLOR_MINA = Color.RED;
    private final Color NO_COLOR = null;
    private final String TEXTO_BANDERA = "X";
    private final String TEXTO_MINA = "O";
    private final Color COLOR_APLASTADO = Color.WHITE;
    //private final int MINA = 9;
    //private final int BASE_2 = 10;
    //private final int NUM_IMAGENES = 16;
    //private final ImageIcon[] IMAGENES;
    private final int FILAS;
    private final int COLUMNAS;
    private final int TAM_ALTO;
    private final int TAM_ANCHO;
    private final int NUMERO_MINAS;
    private int minasRes;
    private int X;
    private int Y;
    private int minutos = 0;
    private int segundos = 0;
    private String minutos2 = "";
    private String segundos2 = "";
    private Thread t;
    private final JButton[][] BOTONES;
    private final JLabel texto;
    private final JLabel tiempo;
    //private final JButton comenzarJuego;
    private boolean inicio;
    private boolean inicio2;
    private final int NUM_JUGADOR;
    //private final boolean imagenes;
    private final PrintWriter out;
    private final int ADMIN;

    public TableroJuego(JLabel texto, JLabel tiempo, PrintWriter out, int filas, int columnas, int AltoCampo, int AnchoCampo, int NumMinas, int NumJugador, int NumAdmin) {
        //this.imagenes = true;
        this.FILAS = filas;
        this.COLUMNAS = columnas;
        this.TAM_ALTO = AltoCampo;
        this.TAM_ANCHO = AnchoCampo;
        this.NUMERO_MINAS = NumMinas;
        this.NUM_JUGADOR = NumJugador;
        this.ADMIN = NumAdmin;
        validaciones();
        this.minasRes = NUMERO_MINAS;
        this.out = out;
        this.texto = texto;
        this.texto.setText("Banderas Restantes: " + minasRes);
        this.tiempo = tiempo;
        this.setLayout(new GridLayout(FILAS, COLUMNAS));
        //this.IMAGENES = new ImageIcon[NUM_IMAGENES];
        //cargarImagenes();
        BOTONES = new JButton[FILAS][COLUMNAS];
        crearBotones();
        inicio = true;
        inicio2 = false;
        Tiempo();
    }
    
    /*private void cargarImagenes(){
        for (int i = 0; i < NUM_IMAGENES; i++) {
            String ruta = "src/imagenes/" + i + ".png";
            IMAGENES[i] = new ImageIcon((new ImageIcon(ruta)).getImage().getScaledInstance(TAM_ANCHO, TAM_ALTO, java.awt.Image.SCALE_DEFAULT));
        }
    }*/
    
    private void validaciones(){
        if (this.FILAS != this.COLUMNAS) {
            System.out.println("El número de Filas y Columnas deben ser iguales");
            System.exit(1);
        }
        if ((this.FILAS > 30 || this.FILAS < 0) || (this.COLUMNAS > 30 || this.COLUMNAS < 0)) {
            System.out.println("El número de Filas y Columnas deben ser menores o iguales a 30 y mayor a 0");
            System.exit(2);
        }
        if (this.TAM_ALTO != this.TAM_ANCHO) {
            System.out.println("El tamaño de los campos deben ser iguales");
            System.exit(3);
        }
        if ((this.TAM_ALTO > 20 || this.TAM_ALTO < 0) || (this.TAM_ANCHO > 20 || this.TAM_ANCHO < 0)) {
            System.out.println("El tamaño de los campos deben ser menores o iguales a 20 y mayor a 0");
            System.exit(2);
        }
        if (this.NUMERO_MINAS > (this.FILAS * this.COLUMNAS)) {
            System.out.println("El número de minas debe ser menor al tamaño total del tablero.");
            System.exit(2);
        }
    }
    
    public void ponerBandera(int x, int y, int numJugador) {
        //BOTONES[x][y].setIcon(obtenerIcono(numJugador));
        BOTONES[x][y].setBackground(obtenerColor(numJugador));
        BOTONES[x][y].setText(TEXTO_BANDERA);
        if (numJugador == NUM_JUGADOR) {
            minasRes--;
            texto.setText("Banderas Restantes: " + minasRes);
        }
    }
    
    public void quitarBandera(int x, int y, int numJugador){
        //BOTONES[x][y].setIcon(IMAGENES[BASE_2]);
        BOTONES[x][y].setBackground(NO_COLOR);
        BOTONES[x][y].setText(null);
        if (numJugador == NUM_JUGADOR) {
            minasRes++;
            texto.setText("Banderas Restantes: " + minasRes);
        }
    }
    
    public void hayMina(int x, int y, int numJugador){
        //BOTONES[x][y].setIcon(IMAGENES[MINA]);
        BOTONES[x][y].setBackground(COLOR_MINA);
        BOTONES[x][y].setText(TEXTO_MINA);
        if(numJugador == NUM_JUGADOR){
            inicio = false;
            t.stop();
            JOptionPane.showMessageDialog(this, "Jajaja perdiste xdxdxd");
        }
    }
    
    public void descubrirCampo(int x, int y, int valorCampo, int numJugador){
        //System.out.println("Valor: " + valorCampo);
        BOTONES[x][y].setBackground(COLOR_APLASTADO);
        BOTONES[x][y].setText(""+valorCampo);
        //BOTONES[x][y].setIcon(IMAGENES[valorCampo]);
    }
    
    private Color obtenerColor(int valor){
        switch (valor) {
            case 1:
                return COLOR_J1;
            case 2:
                return COLOR_J2;
            case 3:
                return COLOR_J3;
            case 4:
                return COLOR_J4;
        }
        return null;
    }
    
    /*private Icon obtenerIcono(int valor){
        switch (valor) {
            case 1:
                return IMAGENES[BANDERA_J1];
            case 2:
                return IMAGENES[BANDERA_J2];
            case 3:
                return IMAGENES[BANDERA_J3];
            case 4:
                return IMAGENES[BANDERA_J4];
        }
        return null;
    }*/
    
    private void crearBotones() {
        for (int y = 0; y < COLUMNAS; y++) {
            for (int x = 0; x < FILAS; x++) {
                BOTONES[x][y] = new JButton();
                //BOTONES[x][y].setIcon(IMAGENES[BASE_2]);
                BOTONES[x][y].setPreferredSize(new Dimension(TAM_ANCHO, TAM_ALTO));
                BOTONES[x][y].addMouseListener(new EventoClic());
                this.add(BOTONES[x][y]);
                BOTONES[x][y].setEnabled(true);
            }
        }
    }
    
    private void Tiempo() {
        minutos = 0;
        segundos = 0;
        tiempo.setText("Tiempo: 00:00");
        minutos2 = "";
        segundos2 = "";
        t = new Thread(){
            @Override
            public void run() {
                while (true) {
                    try {
                        sleep(1000);
                        segundos++;
                        if (segundos > 60) {
                            segundos = 0;
                            minutos++;
                        }
                        minutos2 = (minutos < 10) ? ("0" + minutos) : ("" + minutos);
                        segundos2 = (segundos < 10) ? ("0" + segundos) : ("" + segundos);
                        tiempo.setText("Tiempo: " + minutos2 + ":" + segundos2);
                    } catch (InterruptedException e) {
                    }
                }
            }
        };
        t.start();
    }
    
    private String checar(){
        if (NUM_JUGADOR == 1 && X == 0 && !inicio2) {
            inicio2 = true;
            return X + "," + Y;
        } else if (NUM_JUGADOR == 2 && X == (FILAS - 1)  && !inicio2) {
            inicio2 = true;
            return X + "," + Y;
        } else if (NUM_JUGADOR == 3 && Y == 0  && !inicio2) {
            inicio2 = true;
            return X + "," + Y;
        } else if (NUM_JUGADOR == 4 && Y == (COLUMNAS - 1)  && !inicio2) {
            inicio2 = true;
            return X + "," + Y;
        } else {
            if (!inicio2) {
                String texto2 = "";
                switch (NUM_JUGADOR) {
                    case 1: {
                        texto2 = "Debes comenzar por Izquierda";
                        break;
                    }
                    case 2: {
                        texto2 = "Debes comenzar por Derecha";
                        break;
                    }
                    case 3: {
                        texto2 = "Debes comenzar por Arriba";
                        break;
                    }
                    case 4: {
                        texto2 = "Debes comenzar por Abajo";
                        break;
                    }
                }
                JOptionPane.showMessageDialog(this, texto2);
            }
        }
        return "";
    }
    
    private String clic(JButton boton) {
        JButton boton2;
        hola:
        for (int y = 0; y < COLUMNAS; y++) {
            for (int x = 0; x < FILAS; x++) {
                boton2 = BOTONES[x][y];
                if (boton2 == boton) {
                    X = x;
                    Y = y;
                    if (!inicio2) {
                        return checar();
                    } else {
                        return X + "," + Y;
                    }
                }
            }
        }
        return "";
    }

    private class EventoClic extends MouseAdapter {

        public EventoClic() {

        }

        @Override
        public void mousePressed(MouseEvent e) {
            if (inicio) {
                JButton boton = (JButton) e.getSource();
                String clic = "CLIC";
                if (e.getButton() == MouseEvent.BUTTON1) {
                    clic += "IZQUIERDO ";
                } else if (e.getButton() == MouseEvent.BUTTON3) {
                    clic += "DERECHO ";
                }
                String clic2 = clic(boton);
                if (!clic2.equals("")) {
                    clic += clic2;
                    System.out.println(clic);
                    out.println(clic);
                }
            }
        }
    }
}
