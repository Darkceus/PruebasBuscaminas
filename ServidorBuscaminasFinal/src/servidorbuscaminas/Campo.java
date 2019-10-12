package servidorbuscaminas;

/**
 *
 * @author DARKCEUS
 */
public class Campo {
    public static int VALOR_VACIO = 0;
    public static int VALOR_MINA = 9;
    public static int ESTADO_INICIAL = 0;
    public static int ESTADO_APLASTADO = 1;
    public static int ESTADO_BANDERA = 2;
    public static int ESTADO_BANDERA_NO_MINA = 3;
    private Jugador Admin;
    private int X = 0;
    private int y = 0;
    private int Valor = 0;
    private int Estado = 0;

    public Campo(int x, int y) {
        this.X = x;
        this.y = y;
    }

    public int getX() {
        return X;
    }

    public void setX(int X) {
        this.X = X;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getValor() {
        return Valor;
    }

    public void setValor(int Valor) {
        this.Valor = Valor;
    }
    
    public void aumentarValor() {
        this.Valor++;
    }

    public int getEstado() {
        return Estado;
    }

    public void setEstado(int Estado) {
        this.Estado = Estado;
    }

    public Jugador getAdmin() {
        return Admin;
    }

    public void setAdmin(Jugador Admin) {
        this.Admin = Admin;
    }
    
    
}
