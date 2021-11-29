package bibliotecagrafos;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

/**
 * 
 * @author  Daniel Freitas Martins - 2304
 *                  Naiara Cristiane dos Reis Diniz - 3005
 */
public interface RequisitosGrafo {
    // Arquivos - Leitura e impressão + funções auxiliares
    public void lerArquivo(String nome_arq);
    public String escreveResultadosEmArquivo(String nome_arq);
    public String escreveResultadosEmArquivo(String nome_arq, String conteudo);
    public String iteratorParaImpressao(Collection array);
    public String iteratorParaImpressao(Collection array, String separacao);
    ///////////////////        
    
    // Métodos adicionais
    public void setGrafo(Grafo grafo);
    public ArrayList<Integer> getArticulacoes();
    public ArrayList<Aresta<Integer, Integer>> getPontes();
    
    public String toStringMatrizValores();
    public String toStringOrdem();
    public String toStringTamanho();
    public String toStringGrauVerticeEVizinhos(Integer vertice);
    public String toStringIsBipartido();
    public String toStringIsArticulacao(Integer vertice);
    public String toStringIsPonte(Aresta<Integer, Integer> aresta);
    public String toStringBuscaEmProfundidade(Integer vertice_partida);
    public String toStringBuscaEmLargura(Integer vertice_partida);
    public String toStringComponentesConexas();
    public String toStringDistanciaECaminhoMinimo(Integer vertice_origem, Integer vertice_destino, Boolean para_todos_vertices);
    public String toStringArvoreGeradoraMinima();
    public String toStringCadeiaEulerianaFechada();
    public String toStringConjuntoIndependente(Boolean ordenacao_aleatoria);
    ///////////////////        
    
    // Requisitos
    public Integer getOrdem();
    public Integer getTamanho();
    public ArrayDeque<Integer> getVizinhos(Integer vertice);
    public Integer getGrau(Integer vertice);
    public Boolean isBipartido();
    public Boolean isArticulacao(Integer vertice);
    public Boolean isPonte(Aresta<Integer, Integer> aresta, Grafo grafo_copia);
    public ParDeElementos<ArrayList<Integer>, ArrayList<Aresta<Integer, Integer>>> buscaEmProfundidade(Integer vertice);
    public ParDeElementos<ArrayList<Integer>, ArrayList<Aresta<Integer, Integer>>> buscaEmLargura(Integer vertice);
    public ArrayList<ArrayList<Integer>> getComponentesConexas(ArrayList<Integer> vertices_desconsiderados);
    public Integer getQtdComponentesConexas(ArrayList<Integer> vertices_desconsiderados);
    public ParDeElementos<ArrayList<ArrayList<Double>>, ArrayList<ArrayList<Integer>>> algoritmoFloydWarshall(Integer vertice_origem); // Distância, caminho mínimos e se tem circuito negativo
    public ArrayDeque<Aresta<Integer, Integer>> algoritmoPrim(); // Encontra árvore geradora mínima
    public LinkedList<Integer> algoritmoHierholzer(); // Encontra cadeia euleriana
    public ParDeElementos<ArrayDeque<Integer>, Integer> getConjuntoIndependente(Boolean ordenacao_aleatoria);
    ///////////////////
}
