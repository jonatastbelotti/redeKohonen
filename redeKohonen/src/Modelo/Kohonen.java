package Modelo;

import Controle.Comunicador;
import Recursos.Arquivo;
import Recursos.Numero;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author Jônatas Trabuco Belotti [jonatas.t.belotti@hotmail.com]
 */
public class Kohonen {

  public static final int NUM_ENTRADAS = 3;
  private final int NUM_LINHAS = 4;
  private final int NUM_COLUNAS = 4;
  private final int NUM_NEURONIOS = NUM_LINHAS * NUM_COLUNAS;
  private final double MUDANCA_MINIMA = 0.01;
  private final int NUM_MAXIMO_EPOCAS = 10000;

  private final double TAXA_APRENDIZAGEM = 0.001;
  private final double RAIO_VIZINHANCA = 1D;

  private int mapaNeuronios[][];
  private List[] conjuntoVizinhos;
  private double pesos[][];
  private double entrada[];
  private HashMap<Integer, String> classeNeuronio;

  private int numEpocas;

  /**
   * Método construtor, responsável por instanciar os objetos, criar o mapa de neurônios e os conjuntos de vizinhos
   */
  public Kohonen() {
    int neuronio, linhaNeuronio, colunaNeuronio;
    double distancia;

    linhaNeuronio = colunaNeuronio = 0;

    mapaNeuronios = new int[NUM_LINHAS][NUM_COLUNAS];
    conjuntoVizinhos = new List[NUM_NEURONIOS];
    pesos = new double[NUM_NEURONIOS][NUM_ENTRADAS];
    entrada = new double[NUM_ENTRADAS];
    classeNeuronio = new HashMap<>();

    //Criando o mapa dos neurônios
    neuronio = 0;
    for (int linha = 0; linha < NUM_LINHAS; linha++) {
      for (int coluna = 0; coluna < NUM_COLUNAS; coluna++) {
        classeNeuronio.put(neuronio, "-");
        mapaNeuronios[linha][coluna] = neuronio++;
      }
    }

    //Montando conjuntos de vizinhos
    for (neuronio = 0; neuronio < NUM_NEURONIOS; neuronio++) {
      conjuntoVizinhos[neuronio] = new ArrayList();

      for (int linha = 0; linha < NUM_LINHAS; linha++) {
        for (int coluna = 0; coluna < NUM_COLUNAS; coluna++) {
          if (mapaNeuronios[linha][coluna] == neuronio) {
            linhaNeuronio = linha;
            colunaNeuronio = coluna;
            coluna = NUM_COLUNAS;
            linha = NUM_LINHAS;
          }
        }
      }

      for (int linha = 0; linha < NUM_LINHAS; linha++) {
        for (int coluna = 0; coluna < NUM_COLUNAS; coluna++) {
          if (mapaNeuronios[linha][coluna] != neuronio) {
            distancia = Math.sqrt(Math.pow((double) (linhaNeuronio - linha), 2D) + Math.pow((double) (colunaNeuronio - coluna), 2D));

            if (distancia <= RAIO_VIZINHANCA) {
              conjuntoVizinhos[neuronio].add(mapaNeuronios[linha][coluna]);
            }
          }
        }
      }
    }
  }

  /**
   * Método que realiza o treinamento da rede.
   * @param arquivoTreinamento arquivo contendo o conjunto de amostras de treinamento
   * @return retorna verdadeiro se o ttreinamento foi realizado com sucesso, falso caso contrário
   */
  public boolean treinar(Arquivo arquivoTreinamento) {
    String classe;
    double mudanca;
    int neuronioMenorDistancia;
    int numAmostra;

    Comunicador.iniciarLog("Iniciando treinamento da rede...");

    //Iniciando vetores de pesos já normalizados
    for (int neuronio = 0; neuronio < NUM_NEURONIOS; neuronio++) {
      separarEntradas(arquivoTreinamento.lerArquivo().split("\n")[neuronio]);
      normalizarVetor(entrada);
      copiarVetor(entrada, pesos[neuronio]);
    }

    //Iniciando número de epocas
    this.numEpocas = 0;

    //Atualizar pesos até que não haja mudança significativa
    do {
      mudanca = 0D;

      //Para cada amostra de treinamento do arquivo
      for (String linha : arquivoTreinamento.lerArquivo().split("\n")) {
        separarEntradas(linha);
        normalizarVetor(entrada);

        //Determinando qual o neurônio mais próximo da amostra
        neuronioMenorDistancia = calcNeuronioMenorDistancia();

        //Ajustando peso do neuronio vencedor
        for (int i = 0; i < NUM_ENTRADAS; i++) {
          pesos[neuronioMenorDistancia][i] += TAXA_APRENDIZAGEM * (entrada[i] - pesos[neuronioMenorDistancia][i]);
          mudanca += Math.abs(TAXA_APRENDIZAGEM * (entrada[i] - pesos[neuronioMenorDistancia][i]));
        }
        normalizarVetor(pesos[neuronioMenorDistancia]);

        //Ajustanndo pesos dos vizinhos do vencedor
        for (Object obj : conjuntoVizinhos[neuronioMenorDistancia]) {
          int neuronio = (int) obj;

          for (int i = 0; i < NUM_ENTRADAS; i++) {
            pesos[neuronio][i] += (TAXA_APRENDIZAGEM / 2D) * (entrada[i] - pesos[neuronioMenorDistancia][i]);
          }
          normalizarVetor(pesos[neuronio]);
        }
      }

      Comunicador.adicionarLog(String.format("%d %.8f", numEpocas, mudanca));

      numEpocas++;
    } while (mudanca >= MUDANCA_MINIMA && numEpocas <= NUM_MAXIMO_EPOCAS);
    
    Comunicador.adicionarLog("Fim do treinamento!");
    Comunicador.adicionarLog("Iniciando identificação da classe de cada neurônio...");
    
    //Identificando a que classe cada neurônio pertence
    numAmostra = 0;
    
    for (String linha : arquivoTreinamento.lerArquivo().split("\n")) {
      numAmostra++;
      separarEntradas(linha);
      normalizarVetor(entrada);
      
      neuronioMenorDistancia = calcNeuronioMenorDistancia();
      
      classe = "A";
      if (numAmostra >= 21 && numAmostra <= 60) {
        classe = "B";
      }
      if (numAmostra >= 61 && numAmostra <= 120) {
        classe = "C";
      }
      
      classeNeuronio.put(neuronioMenorDistancia, classe);
    }
    
    imprimirClasses();

    return true;
  }
  
  /**
   * Método que realiza a execução da rede, classifica um conjunto de amostras nas classes estabelecidas (A, B e C)
   * @param arquivoTeste arquivo contendo o conjunto de amostra a serem classificadas
   */
  public void testar(Arquivo arquivoTeste) {
    String texto;
    int numAmostra;
    int neuronio;
    
    Comunicador.iniciarLog("Iniciando execução da rede");
    numAmostra = 0;
    
    for (String linha : arquivoTeste.lerArquivo().split("\n")) {
      numAmostra++;
      separarEntradas(linha);
      normalizarVetor(entrada);
      
      neuronio = calcNeuronioMenorDistancia();
      
      texto = "" + numAmostra + " ";
      
      for (int i = 0; i < NUM_ENTRADAS; i++) {
        texto += String.format("%f ", entrada[i]);
      }
      
      texto += String.format("-> %s", classeNeuronio.get(neuronio));
      
      Comunicador.adicionarLog(texto);
    }
    
  }

  /**
   * Método que recura os valores da entrada da rede de uma linha de texto.
   * @param linha String (texto) contendo os valores das entradas (x1, x2, x3, ...)
   */
  private void separarEntradas(String linha) {
    String[] vetor;
    int i;

    vetor = linha.split("\\s+");
    i = 0;

    if (vetor[0].equals("")) {
      i++;
    }

    //preenche o vetor de entradas a partir da linha lida do arquivo
    for (int j = 0; j < NUM_ENTRADAS; j++) {
      entrada[j] = Numero.parseDouble(vetor[i++]);
    }
  }

  /**
   * Método que normaliza um vetor.
   * @param vetor vetor a ser normalizado.
   */
  private void normalizarVetor(double vetor[]) {
    double modulo;

    //Calculando modulo do vetor
    modulo = 0D;
    for (int i = 0; i < vetor.length; i++) {
      modulo += Math.pow(vetor[i], 2D);
    }
    modulo = Math.sqrt(modulo);

    //Normalizando cada elemento do vetor
    for (int i = 0; i < vetor.length; i++) {
      vetor[i] /= modulo;
    }
  }

  /**
   * Calcula a distância euclidiana entre dois vetores.
   * @param vetor1 vetor da origem do cálculo da distância euclidiana.
   * @param vetor2 vetor destino do cálculo da distância euclidiana.
   * @return Retorna o valor (double) da distância eucliadiana entre os dois vetores.
   */
  private double distanciaEuclidiana(double vetor1[], double vetor2[]) {
    double distancia;

    distancia = 0D;

    for (int i = 0; i < vetor1.length; i++) {
      distancia += Math.pow(vetor1[i] - vetor2[i], 2D);
    }

    distancia = Math.sqrt(distancia);

    return distancia;
  }

  /**
   * Realiza a copia dos valores de um vetor para outro.
   * @param origem vetor de onde os valores são copiados.
   * @param destino vetor para onde os valores são copiados.
   */
  private void copiarVetor(double[] origem, double[] destino) {
    for (int i = 0; i < origem.length && i < destino.length; i++) {
      destino[i] = origem[i];
    }
  }

  /**
   * Calcula qual o neurônio com menor distância euclidiana até a entrada da rede.
   * @return retorna o neurônio (de 0 até n-1) mais próximo do vetor de entrada da rede.
   */
  private int calcNeuronioMenorDistancia() {
    double distancia;
    double menorDistancia;
    int neuronioMenorDistancia;

    menorDistancia = Double.MAX_VALUE;
    neuronioMenorDistancia = 0;

    for (int neuronio = 0; neuronio < NUM_NEURONIOS; neuronio++) {
      distancia = distanciaEuclidiana(pesos[neuronio], entrada);

      if (distancia < menorDistancia) {
        menorDistancia = distancia;
        neuronioMenorDistancia = neuronio;
      }
    }

    return neuronioMenorDistancia;
  }

  /**
   * Imprime o mapa de contexto e a classe de cada neurônio.
   */
  private void imprimirClasses() {
    String texto;
    
    Comunicador.adicionarLog("Mapa de contexto:");
    
    for (int linha = 0; linha < NUM_LINHAS; linha++) {
      texto = "";
      
      for (int coluna = 0; coluna < NUM_COLUNAS; coluna++) {
        texto += classeNeuronio.get(mapaNeuronios[linha][coluna]) + "  ";
      }
      
      Comunicador.adicionarLog(texto);
    }
    
    Comunicador.adicionarLog("Classe de cada neurônio:");
    for (int neuronio = 0; neuronio < NUM_NEURONIOS; neuronio++) {
      Comunicador.adicionarLog(String.format("%d -> %s", neuronio+1, classeNeuronio.get(neuronio)));
    }
  }
}
