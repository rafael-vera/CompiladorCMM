/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controlador;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.filechooser.FileNameExtensionFilter;
import vista.DialogoAcercaDe;
import vista.PanelCentro;
import vista.Ventana;

/**
 *
 * @author Rafael Vera
 */
public class OyenteBarra implements ActionListener {
  private final Ventana ventana;
  private final PanelCentro panelCentro;
  
  public OyenteBarra(Ventana ventana, PanelCentro panelCentro) {
    this.ventana = ventana;
    this.panelCentro = panelCentro;
  }
  
  @Override
  public void actionPerformed(ActionEvent e) {
    JMenuItem opcion = (JMenuItem) e.getSource();
    String nombre = opcion.getName();
    switch (nombre) {
      case "nuevoArchivo":
        panelCentro.nuevoArchivo();
        break;
      case "abrirArchivo":
        abrirArchivo();
        break;
      case "guardarArchivo":
        panelCentro.guardarArchivo();
        break;
      case "cerrarArchivo":
        panelCentro.cerrarArchivo();
        break;
      case "salir":
        System.exit(0);
      case "copiar":
        panelCentro.copiar();
        break;
      case "cortar":
        panelCentro.cortar();
        break;
      case "pegar":
        panelCentro.pegar();
        break;
      case "compilar":
        panelCentro.compilar();
        break;
      case "acercaDe":
        acercaDe();
        break;
      default:
        break;
    }
  }
  
  private void acercaDe() {
    DialogoAcercaDe dialogo = new DialogoAcercaDe(ventana, true);
    dialogo.setSize(500, 400);
    dialogo.setLocationRelativeTo(panelCentro);
    dialogo.setVisible(true);
  }
  
  private void abrirArchivo() {
    File archivo = getFile();
    if(archivo != null) {
      panelCentro.abrirArchivo(archivo);
    }
  }
  
  private File getFile() {
    JFileChooser chooser = new JFileChooser();
    FileNameExtensionFilter extension = new FileNameExtensionFilter(
      "Archivo C--",
      "cmm"
    );
    chooser.setFileFilter(extension);
    int valor = chooser.showOpenDialog(panelCentro);
    if(valor == JFileChooser.APPROVE_OPTION)
      return chooser.getSelectedFile();
    return null;
  }
}