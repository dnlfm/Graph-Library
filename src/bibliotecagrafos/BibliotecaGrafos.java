package bibliotecagrafos;

import bibliotecagrafos.visao.JanelaPrincipal;

/**
 *
 * @author  Daniel Freitas Martins - 2304
 *                  Naiara Cristiane dos Reis Diniz - 3005
 */
public class BibliotecaGrafos {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        /*
        Grafo grafo = new Grafo();
        String nome_arquivo_lido, nome_arquivo_escrito;
        
        nome_arquivo_lido = javax.swing.JOptionPane.showInputDialog(null, "Informe o nome do arquivo a ser lido:");
        if(nome_arquivo_lido != null){
            grafo.lerArquivo(nome_arquivo_lido);
            
            nome_arquivo_escrito = javax.swing.JOptionPane.showInputDialog(null, "Informe o nome do arquivo a ser escrito:");
            if(nome_arquivo_escrito != null){
                grafo.escreveResultadosEmArquivo(nome_arquivo_escrito);
            }
        }*/
        JanelaPrincipal janela = new JanelaPrincipal();
        janela.setVisible(true);
    }
    
}
