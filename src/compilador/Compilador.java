/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compilador;

import com.formdev.flatlaf.FlatIntelliJLaf;
import controlador.OyenteBarra;
import javax.swing.UIManager;
import vista.PanelCentro;
import vista.Ventana;

/**
 *
 * @author Rafael Vera
 */
public class Compilador {
  /**
   * @param args the command line arguments
   */
  public static void main(String[] args) {
    try {
      UIManager.setLookAndFeel(
        new FlatIntelliJLaf()
      );
    } catch(Exception e) {
      System.out.println("Error Look and Feel: "+e.getMessage());
    }
    
    PanelCentro panelCentro = new PanelCentro();
    Ventana ventana = new Ventana(panelCentro);
    OyenteBarra oyente = new OyenteBarra(ventana, panelCentro);
    ventana.addEvents(oyente);
    
    ventana.setVisible(true);
  }
}