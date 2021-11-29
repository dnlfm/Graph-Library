package bibliotecagrafos;

/**
 *
 * @author  Daniel Freitas Martins - 2304
 *                  Naiara Cristiane dos Reis Diniz - 3005
 * @param <Elemento_1>
 * @param <Elemento_2>
 */
public class ParDeElementos<Elemento_1, Elemento_2> {
    private Elemento_1 elemento_1;
    private Elemento_2 elemento_2;    
    
    public ParDeElementos(Elemento_1 elemento_1, Elemento_2 elemento_2){
        this.elemento_1 = elemento_1;
        this.elemento_2 = elemento_2;
    }
    
    public Elemento_1 getElemento_1() {
        return elemento_1;
    }

    public Elemento_2 getElemento_2() {
        return elemento_2;
    }

    public void setElemento_1(Elemento_1 elemento_1) {
        this.elemento_1 = elemento_1;
    }

    public void setElemento_2(Elemento_2 elemento_2) {
        this.elemento_2 = elemento_2;
    }    
    
}
