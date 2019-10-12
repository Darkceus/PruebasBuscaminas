package clientebuscaminas;


import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;
import java.util.Scanner;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 *
 * @author DARKCEUS
 */
public class PrincipalClienteJugador extends JFrame {

    String direccion;
    int puerto;
    Scanner in;
    PrintWriter out;
    private TableroJuego juego;
    JFrame frame = new JFrame("Chatter");
    JTextField textField = new JTextField(50);
    JTextArea messageArea = new JTextArea(16, 50);
    private JLabel tiempo;
    private JLabel texto;
    //private JButton Comenzar;

    public PrincipalClienteJugador() {
        //this.direccion = direccionServer;
        validarDireccion(getDireccion());
        //this.puerto = convertirInt(puerto);
        validarPuerto(getPuerto());
        setTitle("Buscaminas");
        texto = new JLabel("Banderas Restantes: 0");
        add(texto, BorderLayout.NORTH);
        tiempo = new JLabel("Tiempo: 00:00");
        add(tiempo, BorderLayout.SOUTH);
        /*Comenzar = new JButton("Comenzar juego");
        add(Comenzar, BorderLayout.AFTER_LINE_ENDS);*/
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        textField.setEditable(false);
        messageArea.setEditable(false);
        frame.getContentPane().add(textField, BorderLayout.SOUTH);
        frame.getContentPane().add(new JScrollPane(messageArea), BorderLayout.CENTER);
        frame.pack();

        textField.addActionListener((ActionEvent e) -> {
            out.println(textField.getText());
            textField.setText("");
        });
    }
    
    private String getDireccion(){
        return JOptionPane.showInputDialog(this, "Dirección", "Ingresa una Dirección: ", JOptionPane.PLAIN_MESSAGE);
    }
    
    private String getPuerto(){
        return JOptionPane.showInputDialog(this, "Puerto", "Ingresa un puerto: ", JOptionPane.PLAIN_MESSAGE);
    }
    
    private void getMensaje(String info) {
        JOptionPane.showMessageDialog(this, info, "Información", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void getMensaje2(String info){
        JOptionPane.showMessageDialog(this, info, "Información", JOptionPane.INFORMATION_MESSAGE);
    }

    private String getNombre() {
        return JOptionPane.showInputDialog(frame, "Nombre", "Ingresa un Nombre: ", JOptionPane.PLAIN_MESSAGE);
    }
    
    private void mensaje(String line) {
        String info = line.substring(12);
        getMensaje(info);
    }

    private void run() throws IOException {
        try {
            Socket socket = new Socket(direccion, puerto);
            in = new Scanner(socket.getInputStream());
            out = new PrintWriter(socket.getOutputStream(), true);
            String[] tam;
            while (in.hasNextLine()) {                
                String linea = in.nextLine();
                if(linea.startsWith("NOMBREDEENVIO")){
                    out.println(getNombre());
                } else if (linea.startsWith("INFOMESSAGE")) {
                    mensaje(linea);
                } else if (linea.startsWith("INFO2MESSAGE")) {
                    
                } else if (linea.startsWith("INFO3MESSAGE")) {
                    
                } else if (linea.startsWith("PONERBANDERA ")) {
                    tam = linea.substring(13).split(",");
                    if (tam.length == 3) {
                        juego.ponerBandera(convertirInt(tam[0]), convertirInt(tam[1]), convertirInt(tam[2]));
                    } else {
                        getMensaje("Error al poner bandera");
                    }
                } else if (linea.startsWith("QUITARBANDERA ")) {
                    tam = linea.substring(14).split(",");
                    if (tam.length == 3) {
                        juego.quitarBandera(convertirInt(tam[0]), convertirInt(tam[1]), convertirInt(tam[2]));
                    } else {
                        getMensaje("Error al quitar bandera");
                    }
                } else if (linea.startsWith("DATOS ")) {
                    tam = linea.substring(6).split(",");
                    if (tam.length == 7) {
                        System.out.println(Arrays.toString(tam));
                        juego = new TableroJuego(texto, tiempo, out, convertirInt(tam[0]), convertirInt(tam[1]), convertirInt(tam[2]), convertirInt(tam[3]), convertirInt(tam[4]), convertirInt(tam[5]), convertirInt(tam[6]));
                        add(juego, BorderLayout.CENTER);
                        pack();
                        this.setVisible(true);
                    } else {
                        getMensaje2("Error al recibir datos");
                    }
                } else if (linea.startsWith("HAYMINA ")) {
                    tam = linea.substring(8).split(",");
                    if (tam.length == 3) {
                        juego.hayMina(convertirInt(tam[0]), convertirInt(tam[1]), convertirInt(tam[2]));
                    } else {
                        getMensaje("Error al poner mina");
                    }
                } else if (linea.startsWith("DESCUBRIRCAMPO ")) {
                    tam = linea.substring(15).split(",");
                    if (tam.length == 4) {
                        System.out.println("X: " + convertirInt(tam[0]) + ", Y: " + convertirInt(tam[1]) + ", Valor: " + convertirInt(tam[2]));
                        juego.descubrirCampo(convertirInt(tam[0]), convertirInt(tam[1]), convertirInt(tam[2]), convertirInt(tam[3]));
                    } else {
                        getMensaje("Error al decubrir campo");
                    }
                } else if (linea.startsWith("INICIARJUEGO ")) {
                    
                } else if (linea.startsWith("NAMEACCEPTED ")) {
                    this.setTitle("Buscaminas - " + linea.substring(13));
                    textField.setEditable(true);
                } else if (linea.startsWith("PUNTOS ")) {
                    tam = linea.substring(7).split("\\.");
                    if (tam.length > 0 && tam.length < 5) {
                        String jugadores = "";
                        for (String tam1 : tam) {
                            jugadores += tam1 + "\n";
                        }
                        getMensaje(jugadores);
                    } else {
                        getMensaje("Error al recibir datos");
                    }
                } else if (linea.startsWith("GANAR")) {
                    
                } else if (linea.startsWith("MESSAGE ")) {
                    messageArea.append(linea.substring(8) + "\n");
                }
            }
        } finally {
            frame.setVisible(false);
            frame.dispose();
            setVisible(false);
            dispose();
        }
    }
    
    private int convertirInt(String num) {
        int num2 = -1;
        try {
            num2 = Integer.parseInt(num);
        } catch (NumberFormatException e) {
            System.err.println("Debes de poner datos válidos");
            System.exit(0);
        }
        return num2;
    }

    public void Abrir() throws IOException {
        run();
        this.setVisible(true);
    }

    private void validarDireccion(String valor) {
        if (valor.equalsIgnoreCase("localhost")) {
            this.direccion = "127.0.0.1";
            return;
        }
        char[] algo = valor.toCharArray();
        int cont = 0;
        for (int i = 0; i < algo.length; i++) {
            if (algo[i] == '.') {
                cont++;
            }
        }
        String[] dir = valor.split("\\.");
        boolean bol = (convertirInt(dir[0]) <= 255) && (convertirInt(dir[1]) <= 255) && (convertirInt(dir[2]) <= 255) && (convertirInt(dir[3]) <= 254);
        if ((cont == 3) && (bol)) {
            this.direccion = valor;
        } else {
            System.err.println("Debes de poner una dirección válida");
            System.exit(0);
        }
    }

    private void validarPuerto(String valor) {
        try {
            this.puerto = Integer.parseInt(valor);
        } catch (NumberFormatException e) {
            System.err.println("Debes de poner un puerto válido");
            System.exit(0);
        }
    }

    public static void main(String[] args) throws IOException {
        PrincipalClienteJugador cliente = new PrincipalClienteJugador();
        cliente.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        cliente.frame.setVisible(true);
        cliente.run();

    }

    /*public class TableroJuego extends JPanel { //ActionListener, MouseListener

        private final int FILAS;
        private final int COLUMNAS;
        private final int TAM_ALTO;
        private final int TAM_ANCHO;
        private final int NUM_JUGADOR;
        private int X;
        private int Y;
        private final JButton[][] BOTONES;

        public TableroJuego(int filas, int columnas, int AltoCampo, int AnchoCampo, int NumJugador) {
            FILAS = filas;
            COLUMNAS = columnas;
            TAM_ALTO = AltoCampo;
            TAM_ANCHO = AnchoCampo;
            NUM_JUGADOR = NumJugador;
            BOTONES = new JButton[FILAS][COLUMNAS];
            iniciarTablero();
        }
        
        private void iniciarTablero(){
            this.setLayout(new GridLayout(FILAS, COLUMNAS));
            for (int y = 0; y < COLUMNAS; y++) {
                for (int x = 0; x < FILAS; x++) {
                    BOTONES[x][y] = new JButton();
                    BOTONES[x][y].setPreferredSize(new Dimension(TAM_ANCHO, TAM_ALTO));
                    BOTONES[x][y].addMouseListener(new EventoClic());
                    this.add(BOTONES[x][y]);
                    BOTONES[x][y].setEnabled(true);
                }
            }
        }

        private String clic(JButton boton) {
            JButton boton2;
            for (int y = 0; y < COLUMNAS; y++) {
                for (int x = 0; x < FILAS; x++) {
                    boton2 = BOTONES[x][y];
                    if (boton2 == boton) {
                        X = x;
                        Y = y;
                        out.println(X + "," + Y);
                        return X + "," + Y;
                    }
                }
            }
            return "";
        }

        private class EventoClic extends MouseAdapter {
            
            public EventoClic(){
                
            }

            @Override
            public void mousePressed(MouseEvent e) {
                JButton boton = (JButton) e.getSource();
                String clic = "CLIC";
                if (e.getButton() == MouseEvent.BUTTON1) {
                    clic += "IZQUIERDO ";
                } else if (e.getButton() == MouseEvent.BUTTON3) {
                    clic += "DERECHO ";
                }
                clic += clic(boton);
                out.print(clic);
            }
        }
    }*/
}
