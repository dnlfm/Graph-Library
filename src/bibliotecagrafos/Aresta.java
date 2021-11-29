package bibliotecagrafos;

/**
 *
 * @author  Daniel Freitas Martins - 2304
 *                  Naiara Cristiane dos Reis Diniz - 3005
 * @param <Origem> Vértice de origem.
 * @param <Destino> Vértice de destino.
 */
public class Aresta<Origem, Destino> {
    private Origem origem;
    private Destino destino;
    
    public Aresta(Origem origem, Destino destino){
        this.origem = origem;
        this.destino = destino;
    }

    public Origem getOrigem() {
        return origem;
    }
    
    public Destino getDestino() {
        return destino;
    }
    
    public void setOrigem(Origem origem) {
        this.origem = origem;
    }
    
    public void setDestino(Destino destino) {
        this.destino = destino;
    }
    
    public Boolean verificaAresta(Integer vertice_1, Integer vertice_2){
        return (vertice_1 == origem && vertice_2 == destino) || (vertice_1 == destino && vertice_2 == origem);            
    }
    
    @Override
    public String toString(){
        return "(" + origem + ", " + destino + ")";
    }

}
