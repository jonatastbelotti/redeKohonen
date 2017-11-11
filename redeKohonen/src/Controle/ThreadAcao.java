package Controle;

import Modelo.Kohonen;
import Recursos.Arquivo;
import javax.swing.JOptionPane;

/**
 *
 * @author Jônatas Trabuco Belotti [jonatas.t.belotti@hotmail.com]
 */
public class ThreadAcao extends Thread {

  private Kohonen redeKohonen = null;
  private Arquivo arquivoTreinamento;

  public ThreadAcao(Kohonen rede) {
    this.redeKohonen = rede;
  }

  public void setArquivoTreinamento(Arquivo arquivoTreinamento) {
    this.arquivoTreinamento = arquivoTreinamento;
  }

  @Override
  public void run() {
    if (redeKohonen != null && arquivoTreinamento != null) {
      imprimirMensagem(redeKohonen.treinar(arquivoTreinamento));

      stop();
    }
  }

  private void imprimirMensagem(boolean val) {
    if (val) {
      JOptionPane.showMessageDialog(null, "Rede neural treinada com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
      Comunicador.setEnabledBotoes(true);
    } else {
      JOptionPane.showMessageDialog(null, "Houve um erro no treinamento da rede!", "Erro", JOptionPane.ERROR_MESSAGE);
    }
  }

}
