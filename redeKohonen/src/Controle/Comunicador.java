package Controle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JTextArea;

/**
 *
 * @author Jônatas Trabuco Belotti [jonatas.t.belotti@hotmail.com]
 */
public abstract class Comunicador {

  private static JTextArea campoLog = null;
  private static List<JButton> listaBotoes = null;

  public static void setCampo(JTextArea campoLog) {
    Comunicador.campoLog = campoLog;
  }
  
  public static void addBotao(JButton botao) {
    if (listaBotoes == null) {
      listaBotoes = new ArrayList<>();
    }
    
    listaBotoes.add(botao);
  }
  
  public static void addBotoes(JButton... botoes) {
    if (listaBotoes == null) {
      listaBotoes = new ArrayList<>();
    }
    
    listaBotoes.addAll(Arrays.asList(botoes));
  }
  
  public static void removerTodosBotoes() {
    listaBotoes.clear();
  }
  
  public static void iniciarLog(String texto) {
    if (campoLog != null) {
      campoLog.setText(texto);
    }
  }
  
  public static void adicionarLog(String texto) {
    if (campoLog != null) {
      campoLog.append(String.format("\n%s", texto));
      campoLog.setCaretPosition(campoLog.getText().length());
    }
  }
  
  public static void setEnabledBotoes(boolean val) {
    if (listaBotoes == null) {
      return;
    }
    
    for (JButton botao : listaBotoes) {
      botao.setEnabled(val);
    }
  }

}
