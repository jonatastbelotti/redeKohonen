package Recursos;

import Modelo.Kohonen;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Jônatas Trabuco Belotti [jonatas.t.belotti@hotmail.com]
 */
public class Arquivo {

  protected String caminhoArquivo;
  protected String nomeArquivo;

  public Arquivo() {
  }

  public Arquivo(String caminhoArquivo, String nomeArquivo) {
    this.caminhoArquivo = caminhoArquivo;
    this.nomeArquivo = nomeArquivo;
  }

  public boolean validarArquivo() {
    String primeiraLinha;
    FileReader arq;

    if (caminhoArquivo == null || nomeArquivo == null) {
      return false;
    }

    if (caminhoArquivo.equals("") || nomeArquivo.equals("")) {
      return false;
    }

    try {
      arq = new FileReader(caminhoArquivo + nomeArquivo);
      BufferedReader lerArq = new BufferedReader(arq);

      primeiraLinha = lerArq.readLine();
      arq.close();

      if (primeiraLinha == null) {
        return false;
      }
    } catch (FileNotFoundException ex) {
      return false;
    } catch (IOException ex) {
      return false;
    }

    return true;
  }

  public boolean validarDados() {
    FileReader arq;
    BufferedReader lerArq;
    String linha;
    String[] vetor;
    boolean resposta;
    int i;

    if (validarArquivo() == false) {
      return false;
    }

    try {
      resposta = true;
      arq = new FileReader(caminhoArquivo + nomeArquivo);
      lerArq = new BufferedReader(arq);

      linha = lerArq.readLine();
      if (linha.contains("x1")) {
        linha = lerArq.readLine();
      }

      while (linha != null) {
        vetor = linha.split("\\s+");
        i = 0;

        if (vetor[0].equals("")) {
          i = 1;
        }

        for (int j = i; j < vetor.length; j++) {
          try {
            Double.parseDouble(vetor[j].replace(",", "."));
          } catch (NumberFormatException e) {
            resposta = false;
          }
        }

        if ((i == 0 && vetor.length != (Kohonen.NUM_ENTRADAS)) || (i == 1 && vetor.length != (Kohonen.NUM_ENTRADAS + 1))) {
          resposta = false;
        }

        linha = lerArq.readLine();
      }

      arq.close();
      return resposta;
    } catch (FileNotFoundException ex) {
      return false;
    } catch (IOException ex) {
      return false;
    }
  }

  public String lerArquivo() {
    String resultado = null;
    FileReader arq;
    BufferedReader lerArq;
    String linha;

    try {
      arq = new FileReader(getCaminhoCompleto());
      lerArq = new BufferedReader(arq);

      linha = lerArq.readLine();
      if (linha.contains("x1")) {
        linha = lerArq.readLine();
      }

      resultado = linha;
      linha = lerArq.readLine();
      while (linha != null) {
        resultado += "\n" + linha;

        linha = lerArq.readLine();
      }

      arq.close();
    } catch (FileNotFoundException ex) {
      return null;
    } catch (IOException ex) {
      return null;
    }

    return resultado;
  }

  public boolean salvarArquivo(String conteudo) {
    File pasta;
    PrintWriter saida;

    pasta = new File(caminhoArquivo);

    if (!pasta.exists()) {
      pasta.mkdirs();
    }

    try {
      saida = new PrintWriter(new File(pasta, nomeArquivo));
      saida.print(conteudo);
      saida.close();
    } catch (FileNotFoundException ex) {
      Logger.getLogger(Arquivo.class.getName()).log(Level.SEVERE, null, ex);
      return false;
    }

    return true;
  }

  public String getCaminhoArquivo() {
    return caminhoArquivo;
  }

  public void setCaminhoArquivo(String caminhoArquivo) {
    this.caminhoArquivo = caminhoArquivo;
  }

  public String getNomeArquivo() {
    return nomeArquivo;
  }

  public void setNomeArquivo(String nomeArquivo) {
    this.nomeArquivo = nomeArquivo;
  }

  public String getCaminhoCompleto() {
    return (caminhoArquivo + nomeArquivo);
  }

}
