package bibliotecagrafos;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import bibliotecagrafos.visao.JanelaPrincipal;

/**
 *
 * @author  Daniel Freitas Martins - 2304
 *          Naiara Cristiane dos Reis Diniz - 3005
 */
public class Grafo implements RequisitosGrafo{
    
    public static final String QUEBRA_DE_LINHA = System.getProperty("line.separator");

    private Boolean arquivo_foi_lido;
    private ArrayList<ArrayList<Double>> matriz_valores;
    private ArrayList<Integer> bipartido_conjunto_1;
    private ArrayList<Integer> bipartido_conjunto_2;
    private Integer qtd_componentes_conexas;
    private Integer qtd_articulacoes;
    private Integer qtd_pontes;    
    
    public Boolean getArquivoFoiLido(){
        return arquivo_foi_lido;
    }
    
    public Grafo(){
        matriz_valores = null;
        bipartido_conjunto_1 = null;
        bipartido_conjunto_2 = null;
        qtd_componentes_conexas = -1;
        qtd_articulacoes = -1;
        qtd_pontes = -1;
        arquivo_foi_lido = false;
    }
    
    public ArrayList<ArrayList<Double>> getMatriz_valores() {
        return matriz_valores;
    }

    public ArrayList<Integer> getBipartido_conjunto_1() {
        return bipartido_conjunto_1;
    }

    public ArrayList<Integer> getBipartido_conjunto_2() {
        return bipartido_conjunto_2;
    }
    
    /**
     * Lê um arquivo e armazena os dados lidos em uma matriz de pesos/valores.
     * @param nome_arq Nome do arquivo a ser lido acompanhado de sua extensão. Ex.: grafo1.txt
     */
    @Override
    public void lerArquivo(String nome_arq){
        
        File arquivo = new File(nome_arq);
        String linha;
        arquivo_foi_lido = true;
        if(arquivo.canRead()){
            try{
                FileInputStream fis = new FileInputStream(arquivo);
                InputStreamReader isr = new InputStreamReader(fis, "ISO-8859-1");
                BufferedReader br = new BufferedReader(isr);
                
                try{
                    linha = br.readLine(); // Lendo a primeira linha
                    int qtd_vertices = Integer.parseInt(linha);
                    if(qtd_vertices > 0){
                        matriz_valores = new ArrayList<>(qtd_vertices);
                        for(int i = 0; i < qtd_vertices; i++){
                            matriz_valores.add(new ArrayList<>(qtd_vertices));
                            for(int j = 0; j < qtd_vertices; j++){
                                matriz_valores.get(i).add(0.0);
                            }
                        }

                        while((linha = br.readLine()) != null){ // Lendo as demais linhas
                            StringTokenizer stz = new StringTokenizer(linha);

                            while(stz.hasMoreTokens()){
                                int x = Integer.parseInt(stz.nextToken()) - 1; // representa coluna
                                int y = Integer.parseInt(stz.nextToken()) - 1; // representa linha
                                double peso = Double.parseDouble(stz.nextToken());
                                matriz_valores.get(x).set(y, peso);
                                matriz_valores.get(y).set(x, peso);
                            }
                        }                    
                    }
                    
                    br.close();
                    isr.close();
                    fis.close();  
                } catch (IOException ex) {
                    Logger.getLogger(Grafo.class.getName()).log(Level.SEVERE, null, ex);
                }
                
            } catch (FileNotFoundException | UnsupportedEncodingException ex) {
                arquivo_foi_lido = false;
                Logger.getLogger(Grafo.class.getName()).log(Level.SEVERE, null, ex);                
            }
        } else{
            arquivo_foi_lido = false;
        }
        
    }
    
    @Override
    public String escreveResultadosEmArquivo(String nome_arq, String conteudo){
        File arquivo = new File(nome_arq);
        StringBuilder escrita = new StringBuilder(100000);
        if(arquivo_foi_lido == false){
            return "";
        }
            
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(arquivo);
            OutputStreamWriter osw = new OutputStreamWriter(fos, "ISO-8859-1");
            BufferedWriter bw = new BufferedWriter(osw);
            escrita.append("A escrita no arquivo \"" + nome_arq + "\" foi realizada com sucesso!");
            try {                
                bw.write(conteudo);                
                
                bw.close();
                osw.close();
                fos.close();
            } catch (IOException ex) {
                Logger.getLogger(Grafo.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        } catch (FileNotFoundException | UnsupportedEncodingException ex) {
            Logger.getLogger(Grafo.class.getName()).log(Level.SEVERE, null, ex);
        }
        return escrita.toString();
    }
    
    @Override
    public String escreveResultadosEmArquivo(String nome_arq){
        File arquivo = new File(nome_arq);
        StringBuilder escrita = new StringBuilder(100000);
        if(arquivo_foi_lido == false){
            return "";
        }
            
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(arquivo);
            OutputStreamWriter osw = new OutputStreamWriter(fos, "ISO-8859-1");
            BufferedWriter bw = new BufferedWriter(osw);
            
            try {
                Integer qtd_vertices = this.getOrdem();                
                
                escrita.append(toStringOrdem()).append(toStringTamanho()).append(toStringIsBipartido());                
                
                
                
                ArrayList<ArrayList<Integer>> componentes_conexas = this.getComponentesConexas(null);
                if(componentes_conexas != null){
                    qtd_componentes_conexas = componentes_conexas.size();
                    escrita.append("Componentes conexas: ").append(qtd_componentes_conexas).append(QUEBRA_DE_LINHA);
                    for (ArrayList<Integer> componente_conexa : componentes_conexas) {
                        escrita.append("   {").append(iteratorParaImpressao(componente_conexa)).append("}").append(QUEBRA_DE_LINHA);
                    }
                } else{
                    escrita.append("O grafo não possui componentes conexas").append(QUEBRA_DE_LINHA);
                }
                
                ArrayList<Integer> articulacoes = this.getArticulacoes();
                if(articulacoes != null){
                    qtd_articulacoes = articulacoes.size();
                    escrita.append("Articulações: ").append(qtd_articulacoes).append(QUEBRA_DE_LINHA).append("   {").append(
                                iteratorParaImpressao(articulacoes)).append("}");
                } else{
                    escrita.append("Não há articulações!");
                }
                escrita.append(QUEBRA_DE_LINHA);
                
                ArrayList<Aresta<Integer, Integer>> pontes = this.getPontes();
                if(pontes != null){
                    qtd_pontes = pontes.size();
                    escrita.append("Pontes: ").append(qtd_pontes).append(QUEBRA_DE_LINHA).append("   {").append(
                            iteratorParaImpressao(pontes)).append("}");   
                } else{
                    escrita.append("Não há pontes!");
                }
                escrita.append(QUEBRA_DE_LINHA);
                
                ParDeElementos<ArrayList<Integer>, ArrayList<Aresta<Integer, Integer>>> resultado_busca_profundidade;
                
                for(int i = 1; i <= qtd_vertices; i++){
                    resultado_busca_profundidade = buscaEmProfundidade(i);
                    escrita.append("Busca em profundidade para ").append(i).append(":").append(QUEBRA_DE_LINHA);
                    if(resultado_busca_profundidade.getElemento_1().isEmpty() == false){
                        escrita.append("   Vértices visitados: {").append(
                                iteratorParaImpressao(resultado_busca_profundidade.getElemento_1())).append("}").append(QUEBRA_DE_LINHA);
                    }
                    if(resultado_busca_profundidade.getElemento_2().isEmpty() == false){
                        escrita.append("   Arestas de retorno: {").append(
                                iteratorParaImpressao(resultado_busca_profundidade.getElemento_2())).append("}").append(QUEBRA_DE_LINHA);
                    }
                }
                
                
                bw.write(escrita.toString());                
                
                bw.close();
                osw.close();
                fos.close();
            } catch (IOException ex) {
                Logger.getLogger(Grafo.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        } catch (FileNotFoundException | UnsupportedEncodingException ex) {
            Logger.getLogger(Grafo.class.getName()).log(Level.SEVERE, null, ex);
        }
        return escrita.toString();
    }
    
    @Override
    public String toStringMatrizValores(){
        int qtd_vertices = this.getOrdem();
        StringBuilder escrita = new StringBuilder(qtd_vertices*qtd_vertices);
        int i, j;
        
        escrita.append("<html><body><h1>--- Matriz de valores ---</h1><table><tr><th> </th>");
        
        for(i = 1; i <= qtd_vertices; i++){
            escrita.append("<th>").append(i).append("</th>");
        }
        escrita.append("</tr>");
        
        for(i = 1; i <= qtd_vertices; i++){
            if(i % 2 == 0){
                escrita.append("<tr><td>").append(i).append("</td>");
            } else{
                escrita.append("<tr style='background-color: #ebebe0'><td>").append(i).append("</td>");
            }
            for(j = 1; j <= qtd_vertices; j++){
                if(matriz_valores.get(i-1).get(j-1).compareTo(0.0) == 0){
                    escrita.append("<td>").append(matriz_valores.get(i-1).get(j-1)).append("</td>");
                } else{
                    escrita.append("<td style='color: red;'>").append(matriz_valores.get(i-1).get(j-1)).append("</td>");
                }
            }
            escrita.append("</tr>");
        }
        escrita.append("</table></body></html>");
        return escrita.toString();
    }
    
    @Override
    public String toStringOrdem(){
        StringBuilder escrita = new StringBuilder(1000);
        Integer qtd_vertices = this.getOrdem();                
        escrita.append("Ordem: ").append(qtd_vertices).append(QUEBRA_DE_LINHA);
        
        return escrita.toString();
    }
    
    @Override
    public String toStringTamanho(){
        StringBuilder escrita = new StringBuilder(1000);
        Integer tamanho = this.getTamanho();                
        escrita.append("Tamanho: ").append(tamanho).append(QUEBRA_DE_LINHA);
        
        return escrita.toString();
    }
    
    @Override
    public String toStringGrauVerticeEVizinhos(Integer vertice){
        StringBuilder escrita = new StringBuilder(1000);   
        
        if(this.getOrdem() == 0){
            escrita.append("(*) O grafo é vazio.").append(QUEBRA_DE_LINHA);
            return escrita.toString();
        }
        
        ArrayDeque<Integer> vizinhanca = this.getVizinhos(vertice);
        
        escrita.append("Grau do vértice ").append(vertice).append(": ").append(this.getGrau(vertice)).append(
                        QUEBRA_DE_LINHA).append("   Vizinhança do vértice ").append(vertice).append(": {").append(
                         iteratorParaImpressao(vizinhanca)).append("}").append(QUEBRA_DE_LINHA);
        
        return escrita.toString();
    }
    
    @Override
    public String toStringIsBipartido(){
        StringBuilder escrita = new StringBuilder();
        
        if(this.getOrdem() == 0){
            escrita.append("(*) O grafo é vazio.").append(QUEBRA_DE_LINHA);
            return escrita.toString();
        }
        
        Boolean is_bipartido = this.isBipartido();                
        if(is_bipartido){
            escrita.append("O grafo é bipartido: ").append(QUEBRA_DE_LINHA).append(
                            "   Conjunto 1: {").append(iteratorParaImpressao(bipartido_conjunto_1)).append(
                            "}").append(QUEBRA_DE_LINHA).append("   Conjunto 2: {").append(
                                    iteratorParaImpressao(bipartido_conjunto_2)).append("}");
        } else{
            escrita.append("O grafo não é bipartido");
        }
        return escrita.append(QUEBRA_DE_LINHA).toString();
    }
    
    @Override
    public String toStringIsArticulacao(Integer vertice){
        StringBuilder escrita = new StringBuilder();
        
        if(this.getOrdem() == 0){
            escrita.append("(*) O grafo é vazio.").append(QUEBRA_DE_LINHA);
            return escrita.toString();
        }
        
        Boolean is_articulacao = this.isArticulacao(vertice);
        if(is_articulacao){
            escrita.append("Sim, ").append(vertice).append(" é uma articulação!");
        } else{
            escrita.append("Não, ").append(vertice).append(" não é uma articulação!");
        }
        return escrita.append(QUEBRA_DE_LINHA).toString();
    }
    
    @Override
    public String toStringIsPonte(Aresta<Integer, Integer> aresta){
        StringBuilder escrita = new StringBuilder();
        
        if(this.getOrdem() == 0){
            escrita.append("(*) O grafo é vazio.").append(QUEBRA_DE_LINHA);
            return escrita.toString();
        }
        
        Boolean is_ponte = this.isPonte(aresta, null);
        if(matriz_valores.get(aresta.getOrigem() - 1).get(aresta.getDestino() - 1).compareTo(0.0) == 0){
            escrita.append("Não, a aresta (").append(aresta.getOrigem()).append(", ").append(aresta.getDestino()).append(") não existe");
        } else{
            if(is_ponte){
                escrita.append("Sim, a aresta (").append(aresta.getOrigem()).append(", ").append(aresta.getDestino()).append(") é uma ponte");
            } else{
                escrita.append("Não, a aresta (").append(aresta.getOrigem()).append(", ").append(aresta.getDestino()).append(") não é uma ponte");
            }
        }
        return escrita.append(QUEBRA_DE_LINHA).toString();
    }
    
    @Override
    public String toStringBuscaEmProfundidade(Integer vertice_partida){
        StringBuilder escrita = new StringBuilder();
        
        if(this.getOrdem() == 0){
            escrita.append("(*) O grafo é vazio.").append(QUEBRA_DE_LINHA);
            return escrita.toString();
        }
        
        ParDeElementos<ArrayList<Integer>, ArrayList<Aresta<Integer, Integer>>> resultado_busca_profundidade;
        resultado_busca_profundidade = buscaEmProfundidade(vertice_partida);
        ArrayList<Integer> vertices_visitados = resultado_busca_profundidade.getElemento_1();
        ArrayList<Aresta<Integer, Integer>> arestas_retorno = resultado_busca_profundidade.getElemento_2();        
        
        escrita.append("Busca em profundidade para ").append(vertice_partida).append(":").append(QUEBRA_DE_LINHA);
        if(vertices_visitados.isEmpty() == false){
            escrita.append("   Vértices visitados: {").append(
                    iteratorParaImpressao(vertices_visitados)).append("}").append(QUEBRA_DE_LINHA);
        }
        if(arestas_retorno.isEmpty() == false){
            escrita.append("   Arestas de retorno: {").append(
                    iteratorParaImpressao(arestas_retorno)).append("}").append(QUEBRA_DE_LINHA);
        }
        
        return escrita.toString();
    }
    
    @Override
    public String toStringBuscaEmLargura(Integer vertice_partida){
        StringBuilder escrita = new StringBuilder();
        
        if(this.getOrdem() == 0){
            escrita.append("(*) O grafo é vazio.").append(QUEBRA_DE_LINHA);
            return escrita.toString();
        }
        
        ParDeElementos<ArrayList<Integer>, ArrayList<Aresta<Integer, Integer>>> resultado_busca_largura;

        resultado_busca_largura = buscaEmLargura(vertice_partida);
        escrita.append("Busca em largura para ").append(vertice_partida).append(":").append(QUEBRA_DE_LINHA);
        if(resultado_busca_largura.getElemento_1().isEmpty() == false){
            escrita.append("   Vértices visitados: {").append(
                    iteratorParaImpressao(resultado_busca_largura.getElemento_1())).append("}").append(QUEBRA_DE_LINHA);
        }
        if(resultado_busca_largura.getElemento_2().isEmpty() == false){
            escrita.append("   Arestas que não pertencem à busca: {").append(
                    iteratorParaImpressao(resultado_busca_largura.getElemento_2())).append("}").append(QUEBRA_DE_LINHA);
        }
        
        return escrita.toString();
    }
    
    @Override
    public String toStringComponentesConexas(){
        StringBuilder escrita = new StringBuilder();
        
        if(this.getOrdem() == 0){
            escrita.append("(*) O grafo é vazio.").append(QUEBRA_DE_LINHA);
            return escrita.toString();
        }
        
        ArrayList<ArrayList<Integer>> componentes_conexas = this.getComponentesConexas(null);
        
        if(componentes_conexas != null){            
            qtd_componentes_conexas = componentes_conexas.size();
            escrita.append("Componentes conexas: ").append(qtd_componentes_conexas).append(QUEBRA_DE_LINHA);
            for (ArrayList<Integer> componente_conexa : componentes_conexas) {
                componente_conexa.sort(null);
                escrita.append("   {").append(iteratorParaImpressao(componente_conexa)).append("}").append(QUEBRA_DE_LINHA);
            }
        } else{
            escrita.append("O grafo não possui componentes conexas").append(QUEBRA_DE_LINHA);
        }
        return escrita.toString();
    }
    
    @Override
    public String toStringDistanciaECaminhoMinimo(Integer vertice_origem, Integer vertice_destino, Boolean para_todos_vertices){
        StringBuilder escrita = new StringBuilder();
        
        if(this.getOrdem() == 0){
            escrita.append("(*) O grafo é vazio.").append(QUEBRA_DE_LINHA);
            return escrita.toString();
        }
        
        ParDeElementos<ArrayList<ArrayList<Double>>, ArrayList<ArrayList<Integer>>> resposta_floyd = this.algoritmoFloydWarshall(vertice_origem);
        ArrayList<ArrayList<Double>> L = resposta_floyd.getElemento_1(); // pesos
        ArrayList<ArrayList<Integer>> R = resposta_floyd.getElemento_2();
        int h, i = vertice_origem - 1, j = vertice_destino - 1, qtd_vertices = this.getOrdem();
        boolean acabou_arestas;
        
        for(h = 0; h < qtd_vertices; h++){
            if(L.get(h).get(h).compareTo(0.0) != 0){
                escrita.append("O grafo possui circuito negativo!").append(QUEBRA_DE_LINHA);                
                //break;
                return escrita.append("Não há como mostrar distância/caminho mínimo, neste caso.").append(QUEBRA_DE_LINHA).toString();
            }
        }
        if(h == qtd_vertices){
            escrita.append("O grafo não possui circuito negativo.").append(QUEBRA_DE_LINHA);
        }
        if(para_todos_vertices){
            for(vertice_destino = 1; vertice_destino <= qtd_vertices; vertice_destino++){
                acabou_arestas = false;
                j = vertice_destino - 1;
                

                ArrayDeque<Integer> monta_caminho = new ArrayDeque<>();
                //monta_caminho.addFirst(vertice_destino);
                do{            
                    monta_caminho.addFirst(j + 1);
                    j = R.get(i).get(j);
                    if(j == -1){ // aresta não existe
                        acabou_arestas = true;
                        break;
                    }                        
                    if(L.get(j).get(j).compareTo(0.0) != 0){
                        monta_caminho.addFirst(j + 1);
                        //j = R.get(i).get(j);
                        break;
                    } 
                    if(R.get(i).get(j).compareTo(i) == 0 && i != j){
                        monta_caminho.addFirst(j + 1);
                        break;
                    }
                } while(R.get(i).get(j).compareTo(i) != 0);
                
                if(acabou_arestas || L.get(j).get(j).compareTo(0.0) != 0){
                    if(!acabou_arestas && L.get(j).get(j).compareTo(0.0) != 0)
                        escrita.append("CIRCUITO NEGATIVO ENCONTRADO!").append(QUEBRA_DE_LINHA);
                    escrita.append("<Não foi encontrado caminho mínimo para se chegar da origem ").append(vertice_origem).append(" ao vértice de destino ").append(vertice_destino).append(">").append(QUEBRA_DE_LINHA);
                    escrita.append("Tentativa falha:   {").append(iteratorParaImpressao(monta_caminho)).append("}").append(QUEBRA_DE_LINHA);

                } else{
                    escrita.append("Distância mínima para se chegar da origem ").append(vertice_origem).append(" ao vértice de destino ").append(vertice_destino).append(": ");
                    escrita.append(L.get(i).get(vertice_destino - 1)).append(QUEBRA_DE_LINHA);
                    escrita.append("Caminho mínimo para se chegar da origem ").append(vertice_origem).append(" ao vértice de destino ").append(vertice_destino).append(": ").append(QUEBRA_DE_LINHA);
                    monta_caminho.addFirst(vertice_origem);
                    escrita.append("   {").append(iteratorParaImpressao(monta_caminho)).append("}").append(QUEBRA_DE_LINHA);
                }
                escrita.append(QUEBRA_DE_LINHA);
            }
        } else{
            acabou_arestas = false;
            escrita.append("Distância mínima para se chegar da origem ").append(vertice_origem).append(" ao vértice de destino ").append(vertice_destino).append(": ");
            escrita.append(L.get(i).get(j)).append(QUEBRA_DE_LINHA);
            escrita.append("Caminho mínimo para se chegar da origem ").append(vertice_origem).append(" ao vértice de destino ").append(vertice_destino).append(": ").append(QUEBRA_DE_LINHA);

            ArrayDeque<Integer> monta_caminho = new ArrayDeque<>();
            //monta_caminho.addFirst(vertice_destino);
            do{            
                monta_caminho.addFirst(j + 1);
                j = R.get(i).get(j);
                if(j == -1){
                    acabou_arestas = true;
                    break;
                }
                if(L.get(j).get(j).compareTo(0.0) != 0){
                    monta_caminho.addFirst(j + 1);
                    //j = R.get(i).get(j);
                    break;
                }     
                if(R.get(i).get(j).compareTo(i) == 0 && i != j){
                    monta_caminho.addFirst(j + 1);
                    break;
                }
            } while(R.get(i).get(j).compareTo(i) != 0);
            
            if(acabou_arestas || L.get(j).get(j).compareTo(0.0) != 0){
                escrita = new StringBuilder();
                if(!acabou_arestas && L.get(j).get(j).compareTo(0.0) != 0)
                        escrita.append("CIRCUITO NEGATIVO ENCONTRADO!").append(QUEBRA_DE_LINHA);
                escrita.append("<Não foi encontrado caminho mínimo para se chegar da origem ").append(vertice_origem).append(" ao vértice de destino ").append(vertice_destino).append(">").append(QUEBRA_DE_LINHA);
                escrita.append("Tentativa falha:   {").append(iteratorParaImpressao(monta_caminho)).append("}").append(QUEBRA_DE_LINHA);
            } else{
                monta_caminho.addFirst(vertice_origem);
                escrita.append("   {").append(iteratorParaImpressao(monta_caminho)).append("}").append(QUEBRA_DE_LINHA);
            }
        }
        
        
        return escrita.toString();
    }
    
    @Override
    public String toStringArvoreGeradoraMinima(){
        StringBuilder escrita = new StringBuilder();
        
        if(this.getOrdem() == 0){
            escrita.append("(*) O grafo é vazio.").append(QUEBRA_DE_LINHA);
            return escrita.toString();
        }
        int qtd_componentes_conexas = this.getQtdComponentesConexas(null);
        if(qtd_componentes_conexas > 1){
            escrita.append("(*) Este grafo não é conexo... Possui ").append(qtd_componentes_conexas).append(
                    " componentes conexas.").append(QUEBRA_DE_LINHA);
            return escrita.toString();
        }
        
        ArrayDeque<Aresta<Integer, Integer>> arestas_arvore = this.algoritmoPrim();
        
        Double peso_total = 0.0;
        StringBuilder arvoreMinima = new StringBuilder();
        arvoreMinima.append(this.getOrdem()).append(QUEBRA_DE_LINHA);
        for(Aresta<Integer, Integer> aresta : arestas_arvore){
            if(aresta.getOrigem() != -1 && aresta.getDestino() != -1){
                arvoreMinima.append(aresta.getOrigem()).append(" ").append(aresta.getDestino()).append(" ").append(matriz_valores.get(aresta.getOrigem() - 1).get(aresta.getDestino() - 1)).append(QUEBRA_DE_LINHA);
                peso_total += matriz_valores.get(aresta.getOrigem() - 1).get(aresta.getDestino() - 1);
            }
        }
        escrita.append("Peso total da árvore geradora mínima: ").append(peso_total).append(QUEBRA_DE_LINHA);
        escrita.append("Arestas que a formam: ").append(QUEBRA_DE_LINHA).append("   {").append(
                    iteratorParaImpressao(arestas_arvore)).append("}").append(QUEBRA_DE_LINHA);
        escreveArvoreMinimaEmArquivo(JanelaPrincipal.OUTPUT_PATH + JanelaPrincipal.FILE_SEPARATOR + "arvore_geradora_minima.txt", arvoreMinima.toString());
        
        return escrita.toString();
    }
    
    public String escreveArvoreMinimaEmArquivo(String nome_arq, String conteudo){
        File arquivo = new File(nome_arq);
        StringBuilder escrita = new StringBuilder(100000);
        if(arquivo_foi_lido == false){
            return "";
        }
            
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(arquivo);
            OutputStreamWriter osw = new OutputStreamWriter(fos, "ISO-8859-1");
            BufferedWriter bw = new BufferedWriter(osw);
            escrita.append("A escrita no arquivo \"" + nome_arq + "\" foi realizada com sucesso!");
            try {                
                bw.write(conteudo);                
                
                bw.close();
                osw.close();
                fos.close();
            } catch (IOException ex) {
                Logger.getLogger(Grafo.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        } catch (FileNotFoundException | UnsupportedEncodingException ex) {
            Logger.getLogger(Grafo.class.getName()).log(Level.SEVERE, null, ex);
        }
        return escrita.toString();
    }
    
    @Override
    public String toStringCadeiaEulerianaFechada(){
        StringBuilder escrita = new StringBuilder();
        
        if(this.getOrdem() == 0){
            escrita.append("(*) O grafo é vazio.").append(QUEBRA_DE_LINHA);
            return escrita.toString();
        }
        
        LinkedList<Integer> cadeia_euleriana = this.algoritmoHierholzer();
        
        if(cadeia_euleriana == null){
            escrita.append("O grafo não é euleriano.");
        } else{
            escrita.append("O grafo é euleriano.").append(QUEBRA_DE_LINHA).append(
                    "   Cadeia euleriana: {").append(iteratorParaImpressao(cadeia_euleriana)).append("}");
        }
        escrita.append(QUEBRA_DE_LINHA);
        
        
        return escrita.toString();
    }
    
    @Override
    public String toStringConjuntoIndependente(Boolean ordenacao_aleatoria){
        StringBuilder escrita = new StringBuilder();
        
        if(this.getOrdem() == 0){
            escrita.append("(*) O grafo é vazio.").append(QUEBRA_DE_LINHA);
            return escrita.toString();
        }
        
        ParDeElementos<ArrayDeque<Integer>, Integer> resposta = this.getConjuntoIndependente(ordenacao_aleatoria);
        ArrayDeque<Integer> conjunto_independente = resposta.getElemento_1();
        Integer numero_independencia = resposta.getElemento_2();
        
        escrita.append("Número de independência: ").append(numero_independencia).append(QUEBRA_DE_LINHA);
        escrita.append("Conjunto independente encontrado: ").append(QUEBRA_DE_LINHA).append("   {").append(
                    iteratorParaImpressao(conjunto_independente)).append("}").append(QUEBRA_DE_LINHA);        
        
        
        return escrita.toString();
    }
    
    /**
     * Coloca os dados do array em uma String, separados por vírgula e um espaço em branco.
     * @param array Conjunto de dados a ser iterado.
     * @return String contendo os dados do array separados por vírgula e um espaço em branco.
     */
    @Override
    public String iteratorParaImpressao(Collection array){
        if(array != null){
            StringBuilder escrita = new StringBuilder(3*array.size());
            Iterator it = array.iterator();
            if(it.hasNext()){
                escrita.append(it.next().toString());
                while(it.hasNext()){
                    escrita.append(", ").append(it.next().toString());
                }            
            }
            return escrita.toString();
        }
        return "";
    }
    
    /**
     * Coloca os dados do array em uma String, separados de acordo com o parâmetro de separação.
     * @param array Conjunto de dados a ser iterado.
     * @param separacao String que estará entre dois valores do array na String resultante.
     * @return String contendo os dados do array separados de acordo com o parâmetro de separação.
     */
    @Override
    public String iteratorParaImpressao(Collection array, String separacao){
        if(array != null){
            StringBuilder escrita = new StringBuilder(3*array.size());
            Iterator it = array.iterator();
            if(it.hasNext()){
                escrita.append(it.next().toString());
                while(it.hasNext()){
                    escrita.append(separacao).append(it.next().toString());
                }
            }
            return escrita.toString();
        }
        return "";
    }
    
    /**
     * Calcula a cardinalidade do conjunto de vértices deste grafo.
     * @return Quantos vértices este grafo possui.
     */
    @Override
    public Integer getOrdem(){
        return (matriz_valores == null ? 0 : matriz_valores.size());
    }
    
    /**
     * Calcula a cardinalidade do conjunto de pontes deste grafo.
     * @return Quantas pontes este grafo possui.
     */
    @Override
    public Integer getTamanho(){
        if(matriz_valores == null){
            return 0;
        }
        Integer qtd_vertices = this.getOrdem();
        Integer qtd_arestas = 0;
        
        for(int i = 0; i < qtd_vertices; i++){
            for(int j = 0; j < i+1; j++){
                if(matriz_valores.get(i).get(j) != 0.0){
                    qtd_arestas++;
                }
            }
        }
        return qtd_arestas;
    }
    
    /**
     * Retorna os vizinhos de um vértice.
     * @param vertice Vértice a ter seus vizinhos procurados (1 &lt= vertice &lt= N)
     * @return Vizinhança do vértice passado como referência; null caso não possua vizinhos.
     */
    @Override
    public ArrayDeque<Integer> getVizinhos(Integer vertice){
        Integer qtd_vertices = this.getOrdem();
        ArrayDeque<Integer> vizinhanca = null;
        int i;
        if(vertice < 1 || vertice > qtd_vertices){
            return vizinhanca;
        }
        vertice = vertice - 1;
        ArrayList<Double> linha = matriz_valores.get(vertice);
        
        for(i = 0; i < qtd_vertices; i++){
            if(linha.get(i) != 0.0){
                if(i != vertice){ // é necessário considerar o laço? Pela definição, parece que não.
                    if(vizinhanca == null){
                        vizinhanca = new ArrayDeque<>();
                    }
                    vizinhanca.add(i+1);
                }
            }
        }
        return vizinhanca;
    }
    
    /**
     * Calcula o grau de um vértice deste grafo.
     * @param vertice Vértice a ter seu grau calculado (1 &lt= vertice &lt= N).
     * @return Grau do vértice passado como parâmetro.
     */
    @Override
    public Integer getGrau(Integer vertice){
        Integer qtd_vertices = getOrdem();
        Integer grau = 0;
        if(vertice < 1 || vertice > qtd_vertices){
            return 0;
        }
        vertice = vertice - 1;
        
        ArrayList<Double> linha = matriz_valores.get(vertice);
        
        for(int i = 0; i < qtd_vertices; i++){            
            if(linha.get(i) != 0){
                if(i == vertice){
                    grau += 2;
                } else{
                    grau++;
                }
            }
        }
            
        return grau;
    }
    
    /**
     * Checa se o grafo é bipartido.
     * @return true se o grafo é bipartido; false caso contrário.
     */
    @Override
    public Boolean isBipartido(){                
        int qtd_vertices = this.getOrdem();
        HashMap<Integer, Integer> c_1, c_2; // declaração de conjunto 1 e conjunto 2.
        Queue<Integer> vertices_nao_marcados = new ArrayDeque<>(); // fila de vértices não marcados.
        
        if(qtd_vertices == 0){
            return true;
        }
        
        c_1 = new HashMap<>(qtd_vertices);
        c_2 = new HashMap<>(qtd_vertices);
        
        for(int i = 1; i <= qtd_vertices; i++){
            // Adicionando todos os vértices como não marcados.
            vertices_nao_marcados.add(i);
        }
        
        c_1.put(1, vertices_nao_marcados.poll()); // adicionando o vértice 1 ao conjunto 1 e marcando (removendo da fila)
        
        while(vertices_nao_marcados.isEmpty() == false){ // enquanto houver elementos não marcados (vértices não avaliados)...
            Integer vertice_avaliado = vertices_nao_marcados.poll(); // retirando da fila...
            ArrayDeque<Integer> vizinhanca = this.getVizinhos(vertice_avaliado);
            if(vizinhanca != null){
                // inicialmente vertice_avaliado está apto para ir para qualquer um dos conjuntos.
                boolean pode_ir_para_c1 = true, pode_ir_para_c2 = true; 
                for (Integer vizinho : vizinhanca) {
                    // Se o conjunto 1 contém algum vizinho do vértice avaliado, então este vértice não pode ir para 
                    // o conjunto 1.
                    if(c_1.containsKey(vizinho)){ 
                        pode_ir_para_c1 = false;
                    } else{
                        // Se não existe vizinho no conjunto 1, avalie se existe no conjunto 2. Se existir, então o vértice não
                        // pode ir para o conjunto 2.
                        if(c_2.containsKey(vizinho)){
                            pode_ir_para_c2 = false;
                        }
                    }
                    
                    // Se ambos os conjuntos não podem receber o vértice avaliado, então retorne false.
                    if(pode_ir_para_c1 == false && pode_ir_para_c2 == false){
                        return false;
                    }
                }
                if(pode_ir_para_c1){
                    c_1.put(vertice_avaliado, vertice_avaliado);
                } else{
                    c_2.put(vertice_avaliado, vertice_avaliado);
                }
            } else{ // tanto faz para qual conjunto irá, pois não possui vizinhos...
                c_1.put(vertice_avaliado, vertice_avaliado);
            }
        }
        
        bipartido_conjunto_1 = new ArrayList<>(c_1.values());
        bipartido_conjunto_2 = new ArrayList<>(c_2.values());
        
        bipartido_conjunto_1.sort(null);
        bipartido_conjunto_2.sort(null);
        
        return true;
    }
    
    /**
     * Função responsável por encontrar as componentes conexas deste grafo.
     * @param vertices_desconsiderados Array contendo os vértices que devem ser considerados como 
     * removidos do grafo. Use null para considerar todos os vértices.
     * @return Array contendo os conjuntos que formam as componentes conexas deste grafo; null caso o grafo não tenha vértices.
     */
    @Override
    public ArrayList<ArrayList<Integer>> getComponentesConexas(ArrayList<Integer> vertices_desconsiderados){
        Integer qtd_vertices, vertice_avaliado;
        ArrayList<ArrayList<Integer>> conjunto_componentes_conexas;
        ArrayDeque<Integer> vertices_nao_marcados, vizinhanca, vizinhanca_temporaria;
        ArrayList<Integer> conjunto_atual;
        HashSet<Integer> vertices_avaliados;
        
        
        qtd_vertices = this.getOrdem();
        vertices_nao_marcados = new ArrayDeque<>(qtd_vertices); // fila de vértices não marcados.    
        vertices_avaliados = new HashSet<>(qtd_vertices/2 + 1); // devido ao load factor...
        if(qtd_vertices == 0){
            return null;
        }
        
        if(vertices_desconsiderados == null){
            // Criando array vazio apenas para tratar operações mais adiante...
            vertices_desconsiderados = new ArrayList<>();
        }
        
        for(int i = 1; i <= qtd_vertices; i++){
            if(vertices_desconsiderados.contains(i) == false){
                // Adicionando todos os vértices como não marcados.
                vertices_nao_marcados.add(i);
            }
        }
        
        conjunto_componentes_conexas = new ArrayList<>();
        
        while((vertice_avaliado = vertices_nao_marcados.poll()) != null){ // enquanto houver elementos não marcados (vértices não avaliados)...
            
            vizinhanca = this.getVizinhos(vertice_avaliado);
            conjunto_atual = new ArrayList<>(qtd_vertices/2 + 1); // criando novo conjunto de componentes conexas...     
            // adicionando o vértice avaliado ao conjunto atual.
            conjunto_atual.add(vertice_avaliado);
            vertices_avaliados.add(vertice_avaliado);
            conjunto_componentes_conexas.add(conjunto_atual);
            if(vizinhanca == null){
                continue;
            } // else            
            // Pegando um elemento da vizinhança
            while((vertice_avaliado = vizinhanca.poll()) != null){
                
                if(vertices_avaliados.contains(vertice_avaliado) == false
                        && vertices_desconsiderados.contains(vertice_avaliado) == false){ // Se este elemento da vizinhança ainda não foi considerado...
                    
                    vertices_avaliados.add(vertice_avaliado);
                    conjunto_atual.add(vertice_avaliado); // Este elemento entra no conjunto atual.
                    vertices_nao_marcados.remove(vertice_avaliado); // Remova o elemento, pois ele será considerado.                    
                    vizinhanca_temporaria = this.getVizinhos(vertice_avaliado); // Pegando os vizinhos deste elemento
                    
                    for(Integer vizinho : vizinhanca_temporaria){
                        if(vizinhanca.contains(vizinho) == false
                                && vertices_avaliados.contains(vizinho) == false 
                                && vertices_desconsiderados.contains(vizinho) == false){
                            vizinhanca.add(vizinho); // Adicionando seus vizinhos à vizinhança.
                        }                        
                    }
                }                
            }
        }
        
        return conjunto_componentes_conexas;
    }
    
    /**
     * Calcula a quantidade de componentes conexas deste grafo.
     * @param vertices_desconsiderados Array contendo os vértices que devem ser considerados como 
     * removidos do grafo. Use null para considerar todos os vértices.
     * @return A quantidade de componentes conexas deste grafo.
     */
    @Override
    public Integer getQtdComponentesConexas(ArrayList<Integer> vertices_desconsiderados){
        ArrayList<ArrayList<Integer>> componentes_conexas = this.getComponentesConexas(vertices_desconsiderados);
        if(componentes_conexas == null){
            return 0;
        }
        // else
        return componentes_conexas.size();
    }
    
    /**
     * Este método verifica se um vértice é uma articulação.
     * @param vertice Vértice avaliado.
     * @return true se é uma articulação; false caso contrário.
     */
    @Override
    public Boolean isArticulacao(Integer vertice){
        Integer qtd_componentes_conexas_depois; // ...antes e depois da remoção...
        
        if(qtd_componentes_conexas == -1){
            qtd_componentes_conexas = this.getQtdComponentesConexas(null);
        }
        
        ArrayList<Integer> vertice_desconsiderado = new ArrayList<>();
        vertice_desconsiderado.add(vertice);
        qtd_componentes_conexas_depois = this.getQtdComponentesConexas(vertice_desconsiderado);
        
        return (qtd_componentes_conexas_depois > qtd_componentes_conexas);        
    }
    
    /**
     * Agrupa todos os vértices que são articulações em um array.
     * @return Array contendo as articulações deste grafo; null caso o grafo não possua nenhuma articulação.
     */
    @Override
    public ArrayList<Integer> getArticulacoes(){
        Integer qtd_vertices = this.getOrdem();
        ArrayList<Integer> articulacoes = null;
        for(int i = 1; i <= qtd_vertices; i++){
            if(this.isArticulacao(i)){
                if(articulacoes == null){
                    articulacoes = new ArrayList<>();
                }
                articulacoes.add(i);
            }
        }
        return articulacoes;
    }
    
    /**
     * Agrupa todos as pontes que são pontes em um array.
     * @return Array contendo as pontes deste grafo; null caso o grafo não possua nenhuma ponte.
     */
    @Override
    public ArrayList<Aresta<Integer, Integer>> getPontes(){
        ArrayList<Aresta<Integer, Integer>> pontes = null;
        Integer qtd_vertices = this.getOrdem();
        Double guarda_peso;
        //Aresta aresta = new Aresta<>(1, 1);
        ArrayList<Double> linha;
        Grafo grafo_copia;
        
        grafo_copia = new Grafo();
        grafo_copia.setGrafo(this);        
        ArrayList<ArrayList<Double>> matriz_valores_copiado = grafo_copia.getMatriz_valores();        
        for(int i = 0; i < qtd_vertices; i++){
            linha = matriz_valores.get(i);
            for(int j = 0; j < i+1; j++){
                if(linha.get(j) != 0.0){
                    //aresta.setOrigem(i+1);
                    //aresta.setDestino(j+1);
                    guarda_peso = matriz_valores_copiado.get(i).get(j);
                    matriz_valores_copiado.get(i).set(j, 0.0);
                    matriz_valores_copiado.get(j).set(i, 0.0);
                    if(this.isPonte(null, grafo_copia)){
                        if(pontes == null){
                            pontes = new ArrayList<>();
                        }
                        pontes.add(new Aresta<>(i+1, j+1));
                    }
                    matriz_valores_copiado.get(i).set(j, guarda_peso);
                    matriz_valores_copiado.get(j).set(i, guarda_peso);
                }
            }
        }
        
        return pontes;
    }
    
    /**
     * Copia o conteúdo do grafo passado como parâmetro para este grafo.
     * @param grafo Grafo a ter o conteúdo copiado.
     */
    @Override
    public void setGrafo(Grafo grafo){
        Integer qtd_vertices = grafo.getOrdem();
        ArrayList<ArrayList<Double>> matriz_valores_fonte = grafo.getMatriz_valores();
        matriz_valores = new ArrayList<>(qtd_vertices);
        for(int i = 0; i < qtd_vertices; i++){
            ArrayList<Double> linha = new ArrayList<>(qtd_vertices);
            matriz_valores.add(linha);
            
            for(Double peso : matriz_valores_fonte.get(i)){
                linha.add(peso);
            }
        }
        ArrayList<Integer> bipartido_conjunto_1_fonte = grafo.getBipartido_conjunto_1();
        ArrayList<Integer> bipartido_conjunto_2_fonte = grafo.getBipartido_conjunto_2();
        if(bipartido_conjunto_1_fonte != null && bipartido_conjunto_2_fonte != null){
            bipartido_conjunto_1 = new ArrayList<>(bipartido_conjunto_1_fonte.size());
            bipartido_conjunto_2 = new ArrayList<>(bipartido_conjunto_2_fonte.size());
            for(Integer c_1 : bipartido_conjunto_1_fonte){
                bipartido_conjunto_1.add(c_1);
            }
            for(Integer c_2 : bipartido_conjunto_2_fonte){
                bipartido_conjunto_2.add(c_2);
            }
        }
    }
    
    /**
     * Este método verifica se uma aresta é uma ponte.
     * @param aresta aresta avaliada. Este parâmetro pode ser null caso o segundo nao seja.
     * @param grafo_copia cópia do grafo a ser verificado; null caso queira realizar a cópia deste grafo neste método.
     * @return true se a aresta é uma ponte; false caso contrário.
     */
    @Override
    public Boolean isPonte(Aresta<Integer, Integer> aresta, Grafo grafo_copia){
        Integer qtd_componentes_conexas_antes, qtd_componentes_conexas_depois;
        Integer vertice_origem, vertice_destino;
        
        qtd_componentes_conexas_antes = this.getQtdComponentesConexas(null);
        
        if(getOrdem() == 0){
            return false;
        }
        
        ////// Criando novo grafo sem a aresta...
        if(grafo_copia == null){
            grafo_copia = new Grafo();
            grafo_copia.setGrafo(this);
            vertice_origem = aresta.getOrigem() - 1; // tratando índice do vetor...
            vertice_destino = aresta.getDestino() - 1;
            ArrayList<ArrayList<Double>> matriz_valores_copiado = grafo_copia.getMatriz_valores();
            matriz_valores_copiado.get(vertice_origem).set(vertice_destino, 0.0);
            matriz_valores_copiado.get(vertice_destino).set(vertice_origem, 0.0);
        }
        /////////////////////////////
        
        qtd_componentes_conexas_depois = grafo_copia.getQtdComponentesConexas(null);
        
        return (qtd_componentes_conexas_depois > qtd_componentes_conexas_antes);        
    }
    
    /**
     * Realiza a busca em profundidade a partir do vértice passado como parâmetro.
     * @param vertice - Vértice de partida.
     * @return Um par de elementos contendo a sequência de vértices vertices_avaliados e as arestas de retorno, respectivamente;
     */
    @Override
    public ParDeElementos<ArrayList<Integer>, ArrayList<Aresta<Integer, Integer>>> buscaEmProfundidade(Integer vertice){
        ParDeElementos<ArrayList<Integer>, ArrayList<Aresta<Integer, Integer>>> resposta;
        resposta = new ParDeElementos<>(new ArrayList<>(), new ArrayList<>());
        ArrayDeque<Aresta<Integer, Integer>> arestas_exploradas = new ArrayDeque<>();
        
        int qtd_vertices = this.getOrdem();
        if(qtd_vertices > 0){
            ArrayDeque<Integer> vertices_nao_marcados = new ArrayDeque<>();
            for(int i = 1; i <= qtd_vertices; i++){
                vertices_nao_marcados.add(i);
            }
            buscaEmProfundidadeAuxiliar(vertices_nao_marcados, resposta, arestas_exploradas, vertice);
        }
        return resposta;
    }
    
    /**
     * Procedimento recursivo que realiza de fato a busca em profundidade.
     * @param vertices_nao_marcados Lista de vértices não marcados.
     * @param resposta Para de elementos que irá conter a sequência de vértices vertices_avaliados e as arestas de retorno, respectivamente.
     * @param arestas_exploradas Lista das arestas exploradas
     * @param vertice Vértice a ser avaliado
     */
    private void buscaEmProfundidadeAuxiliar(ArrayDeque<Integer> vertices_nao_marcados, 
            ParDeElementos<ArrayList<Integer>, ArrayList<Aresta<Integer, Integer>>> resposta, 
            ArrayDeque<Aresta<Integer, Integer>> arestas_exploradas, Integer vertice){
        // pág. 4 aula 06
        vertices_nao_marcados.remove(vertice);
        ArrayDeque<Integer> vizinhos = this.getVizinhos(vertice);        
        resposta.getElemento_1().add(vertice);
        if(vizinhos == null){
            return;
        }
        while(vizinhos.size() > 0){
            Integer vertice_avaliado = vizinhos.poll();
            Aresta aresta_explorada;
            if(vertices_nao_marcados.contains(vertice_avaliado)){
                aresta_explorada = new Aresta(vertice, vertice_avaliado);
                arestas_exploradas.add(aresta_explorada);
                buscaEmProfundidadeAuxiliar(vertices_nao_marcados, resposta, arestas_exploradas, vertice_avaliado);
            } else{
                Boolean aresta_nao_explorada = true;
                for(Aresta<Integer, Integer> aresta : arestas_exploradas){
                    if(aresta.verificaAresta(vertice, vertice_avaliado)){
                        aresta_nao_explorada = false;
                        break;
                    }
                }
                if(aresta_nao_explorada){
                    aresta_explorada = new Aresta(vertice, vertice_avaliado);
                    resposta.getElemento_2().add(aresta_explorada);
                    arestas_exploradas.add(aresta_explorada);
                }
            }
        }
    }
    
    /**
     * Procedimento não recursivo que realiza a busca em largura no grafo a partir do vértice passado como parâmetro.
     * @param vertice Vértice de origem para a busca em largura.
     * @return Par de elementos contendo os vértices que compõem a busca em largura e as arestas não visitadas.
     */
    @Override
    public ParDeElementos<ArrayList<Integer>, ArrayList<Aresta<Integer, Integer>>> buscaEmLargura(Integer vertice){
        // pág. 33 aula 06
        ParDeElementos<ArrayList<Integer>, ArrayList<Aresta<Integer, Integer>>> resposta;
        resposta = new ParDeElementos<>(new ArrayList<>(), new ArrayList<>());
        ArrayDeque<Integer> Q = new ArrayDeque<>(), vertices_marcados = new ArrayDeque<>();
        ArrayDeque<Aresta<Integer, Integer>> arestas_exploradas = new ArrayDeque<>();
        Aresta aresta_explorada;
        
        vertices_marcados.add(vertice);
        Q.add(vertice);
        resposta.getElemento_1().add(vertice);
        while(!Q.isEmpty()){            
            vertice = Q.poll();
            ArrayDeque<Integer> vizinhanca = this.getVizinhos(vertice);
            Integer w;            
            while(!vizinhanca.isEmpty()){
                w = vizinhanca.pollFirst();                                
                if(!vertices_marcados.contains(w)){
                    aresta_explorada = new Aresta(vertice, w);
                    arestas_exploradas.add(aresta_explorada);
                    Q.add(w);
                    resposta.getElemento_1().add(w);
                    vertices_marcados.add(w);
                } else{
                    Boolean aresta_nao_explorada = true;
                    for(Aresta<Integer, Integer> aresta : arestas_exploradas){
                        if(aresta.verificaAresta(vertice, w)){
                            aresta_nao_explorada = false;
                            break;
                        }
                    }
                    if(aresta_nao_explorada){
                        aresta_explorada = new Aresta(vertice, w);
                        resposta.getElemento_2().add(aresta_explorada);
                        arestas_exploradas.add(aresta_explorada);
                    }
                }
            }
        }
        return resposta;
    }
    
    /**
     * Algoritmo que calcula a distância e o caminho mínimo. Também prevê existência de circuito negativo caso passe null como parâmetro.
     * @param vertice_origem Vértice de origem a ser considerado para o menor caminho; null caso queira calcular para todos os vértices.
     * @return Par de elemento contendo a matriz L (pesos) e a matriz R (caminho mínimo), respectivamente.
     */
    @Override
    public ParDeElementos<ArrayList<ArrayList<Double>>, ArrayList<ArrayList<Integer>>> algoritmoFloydWarshall(Integer vertice_origem){
        // pág. 16 aula 10
        ParDeElementos<ArrayList<ArrayList<Double>>, ArrayList<ArrayList<Integer>>> resposta;
        Integer qtd_vertices = this.getOrdem();
        ArrayList<ArrayList<Double>> L = new ArrayList<>(qtd_vertices);
        ArrayList<ArrayList<Integer>> R = new ArrayList<>(qtd_vertices);
        for(int i = 0; i < qtd_vertices; i++){
            L.add(new ArrayList<>(qtd_vertices));
            R.add(new ArrayList<>(qtd_vertices));
            for(int j = 0; j < qtd_vertices; j++){
                
                if(i == j){
                    L.get(i).add(0.0);
                } else{
                    if(matriz_valores.get(i).get(j).compareTo(0.0) != 0){
                        L.get(i).add(matriz_valores.get(i).get(j));
                    } else{
                        L.get(i).add(Double.POSITIVE_INFINITY);
                    }
                }
                
                if(L.get(i).get(j).compareTo(Double.POSITIVE_INFINITY) == 0){
                    R.get(i).add(-1);
                } else{
                    R.get(i).add(i);
                }
            }
        }        
        resposta = new ParDeElementos<>(L, R);
        Double soma;
        for(int k = 0; k < qtd_vertices; k++){
            for(int i = 0; i < qtd_vertices; i++){
                for(int j = 0; j < qtd_vertices; j++){
                    soma = (L.get(i).get(k) + L.get(k).get(j));
                    if(L.get(i).get(j).compareTo(soma) > 0){
                        L.get(i).set(j, soma);
                        R.get(i).set(j, R.get(k).get(j));
                    }
                }
            }
        }
        
        
        return resposta;
    }
    
    /**
     * Encontra a árvore geradora mínima do grafo.
     * @return Conjunto de arestas da árvore geradora mínima.
     */
    @Override
    public ArrayDeque<Aresta<Integer, Integer>> algoritmoPrim(){
        int vertice = 0, qtd_vertices = this.getOrdem();
        
        ArrayDeque<Integer> vertices_selecionados = new ArrayDeque<>(qtd_vertices);
        vertices_selecionados.add(vertice);
        ArrayDeque<Integer> vertices_nao_selecionados = new ArrayDeque<>(qtd_vertices);
        for(int i = 0; i < qtd_vertices; i++){
            if(vertice != i)
                vertices_nao_selecionados.add(i);
        }
        ArrayDeque<Aresta<Integer, Integer>> T_min = new ArrayDeque<>(qtd_vertices);
        while(vertices_selecionados.size() != qtd_vertices){
            Aresta<Integer, Integer> melhor_aresta = new Aresta<>(-1, -1);
            int guarda_k = 0;
            // Encontrando a melhor aresta...
            for(Integer j : vertices_selecionados){
                for(Integer k : vertices_nao_selecionados){
                    if(matriz_valores.get(j).get(k).compareTo(0.0) != 0){
                        if(melhor_aresta.getDestino().compareTo(-1) == 0){
                            melhor_aresta.setOrigem(j+1);
                            melhor_aresta.setDestino(k+1);
                            guarda_k = k;
                        }
                        if(matriz_valores.get(j).get(k).compareTo(matriz_valores.get(melhor_aresta.getOrigem()-1).get(melhor_aresta.getDestino()-1)) < 0){
                            melhor_aresta.setOrigem(j+1);
                            melhor_aresta.setDestino(k+1);
                            guarda_k = k;
                        }
                    }
                }
            }
            ///////////////////////////////////
            vertices_selecionados.add(guarda_k);
            vertices_nao_selecionados.remove(guarda_k);
            T_min.add(melhor_aresta);
        }
        return T_min;
    }
    
    /**
     * Encontra uma cadeia euleriana.
     * @return A cadeira euleirana; null caso o grafo não seja euleriano
     */
    @Override
    public LinkedList<Integer> algoritmoHierholzer(){
        // pág. 17 aula 11
        int qtd_vertices = this.getOrdem();
        int vertice_anterior_da_cadeia = 1, vertice_da_cadeia;
        LinkedList cadeia_euleriana_final = null;
        ArrayDeque<Integer> cadeia_temp;
        Grafo K = new Grafo();
        ArrayList<ArrayList<Double>> copia_matriz_valores;
        
        for(int i = 1; i <= qtd_vertices; i++){
            if(this.getGrau(i) % 2 == 1){
                return cadeia_euleriana_final;
            }
        }
        cadeia_euleriana_final = new LinkedList<>();
              
        cadeia_temp = this.getCadeiaFechada(1, null);
        if(cadeia_temp == null){
            return null;
        }
        
        K.setGrafo(this);
        copia_matriz_valores = K.getMatriz_valores();
        
        cadeia_euleriana_final.addAll(cadeia_temp);
        Iterator<Integer> it = cadeia_temp.iterator();
        if(it.hasNext()){
            vertice_anterior_da_cadeia = it.next() - 1;
        }
        
        while(it.hasNext()){
            vertice_da_cadeia = it.next() - 1;
            copia_matriz_valores.get(vertice_anterior_da_cadeia).set(vertice_da_cadeia, 0.0);
            copia_matriz_valores.get(vertice_da_cadeia).set(vertice_anterior_da_cadeia, 0.0);
            vertice_anterior_da_cadeia = vertice_da_cadeia;
        }
        int v = 1;
        while(K.getTamanho() != 0){
            it = cadeia_euleriana_final.iterator();
            while(it.hasNext()){
                v = it.next();
                if(K.getGrau(v) > 0){
                    break;
                }
            }
            
            cadeia_temp = K.getCadeiaFechada(v, null); // H = ...
            if(cadeia_temp == null){
                return null;
            }
            int index_v = cadeia_euleriana_final.indexOf(v);
            cadeia_euleriana_final.remove(index_v);
            cadeia_euleriana_final.addAll(index_v, cadeia_temp);
            
            it = cadeia_temp.iterator();            
            if(it.hasNext()){
                vertice_anterior_da_cadeia = it.next() - 1;
            }
            while(it.hasNext()){
                vertice_da_cadeia = it.next() - 1;
                copia_matriz_valores.get(vertice_anterior_da_cadeia).set(vertice_da_cadeia, 0.0);
                copia_matriz_valores.get(vertice_da_cadeia).set(vertice_anterior_da_cadeia, 0.0);
                vertice_anterior_da_cadeia = vertice_da_cadeia;
            }
        }
        
        return cadeia_euleriana_final;
    }
    
    private ArrayDeque<Integer> getCadeiaFechada(Integer vertice_inicial, ArrayDeque<Aresta<Integer, Integer>> arestas_ignoradas){        
        ArrayDeque<Integer> cadeia, cadeia_reconstruida, vizinhanca, vertices_marcados;
        int vertice, vertice_anterior, i;
        boolean encontrou_cadeia;
        
        cadeia = new ArrayDeque<>();        
        vertices_marcados = new ArrayDeque<>();
        
        i = vertice_inicial;
        vertice = i;
        vertice_anterior = vertice;
        vertices_marcados.add(vertice);    
        cadeia.add(vertice);
        
        encontrou_cadeia = false;        
        while(encontrou_cadeia == false){            
            vizinhanca = this.getVizinhos(vertice);
            do{
                if(vizinhanca == null || vizinhanca.isEmpty())
                    return null;
                i = vizinhanca.pollFirst();
                // vertice anterior == i OU a aresta (vertice_anterior, i) é para ser ignorada? Se sim, repita...
            } while(vertice_anterior == i || (arestas_ignoradas == null ? false : (arestas_ignoradas.contains(new Aresta<>(vertice, i))))); 
            
            vertices_marcados.add(i);
            if(cadeia.contains(i)){
                encontrou_cadeia = true;
            }
            cadeia.add(i);
            vertice_anterior = vertice;
            vertice = i;
        }
        
        // Reconstruindo a cadeia encontrada...
        cadeia_reconstruida = new ArrayDeque<>();
        // i está guardando o vértice que é tanto origem como destino...
        i = cadeia.poll(); // removendo o último elemento da fila que é igual ao valor de i
        cadeia_reconstruida.addFirst(i);
        do{
            vertice = cadeia.poll();
            cadeia_reconstruida.addFirst(vertice);            
        } while(vertice != i);
        
        return cadeia_reconstruida;
    }
    
    /**
     * Encontra um conjunto independente por meio de uma heurística gulosa.
     * @param ordenacao_aleatoria false caso a ordenação seja de forma decrescente; true caso seja aleatória
     * @return Par de elementos contendo um conjunto independente do grafo e o número de indepêndencia.
     */
    @Override
    public ParDeElementos<ArrayDeque<Integer>, Integer> getConjuntoIndependente(Boolean ordenacao_aleatoria){
        // pág. 6 aula 14
        ParDeElementos<ArrayDeque<Integer>, Integer> resposta;
        ArrayDeque<Integer> conjunto_independente = new ArrayDeque<>();
        Integer numero_independencia = 0;
        int indice_vertice_maior_grau;        
        int qtd_vertices = this.getOrdem();
        ArrayList<Integer> vertices_ordenados_decrescentemente = new ArrayList<>(qtd_vertices);
        ArrayDeque<Integer> vizinhanca;
        
        for(int i = 1; i <= qtd_vertices; i++){
            vertices_ordenados_decrescentemente.add(i);
        }
        
        if(ordenacao_aleatoria){
            Collections.shuffle(vertices_ordenados_decrescentemente);
        }
        
        vertices_ordenados_decrescentemente.sort(new OrdenarDecrescentemente(this));
        indice_vertice_maior_grau = 0;
        while(vertices_ordenados_decrescentemente.isEmpty() == false){            
            vizinhanca = getVizinhos(vertices_ordenados_decrescentemente.get(indice_vertice_maior_grau));   
            if(vizinhanca != null){
                vertices_ordenados_decrescentemente.removeAll(vizinhanca);
            }
            conjunto_independente.add(vertices_ordenados_decrescentemente.get(indice_vertice_maior_grau));            
            vertices_ordenados_decrescentemente.remove(indice_vertice_maior_grau);
            numero_independencia += 1;
        }
        
        resposta = new ParDeElementos<>(conjunto_independente, numero_independencia);
        
        return resposta;
    }
    
    private class OrdenarDecrescentemente implements Comparator<Integer>{

        private Grafo grafo;
        
        public OrdenarDecrescentemente() {
            this.grafo = null;
        }
        
        public OrdenarDecrescentemente(Grafo grafo) {
            this.grafo = grafo;
        }
        
        @Override
        public int compare(Integer o1, Integer o2) {
            if(this.grafo != null){
                // ordem decrescente de acordo com o grau...
                return grafo.getGrau(o2) - grafo.getGrau(o1);
            } // else
            // ordem decrescente...
            return o2 - o1;
        }
        
    }
    
    
    
}