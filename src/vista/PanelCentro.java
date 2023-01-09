/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vista;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.net.MalformedURLException;
import javax.swing.JTabbedPane;
import modelo.Analizador;
import modelo.ParseException;
import modelo.TokenMgrError;

/**
 *
 * @author Rafael Vera
 */
public class PanelCentro extends JTabbedPane {
  public PanelCentro() {
    super(JTabbedPane.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);
  }
  
  public void nuevoArchivo() {
    addTab(
      "Sin Titulo",
      new PanelDividido()
    );
  }
  
  public void abrirArchivo(File archivo) {
    addTab(
      archivo.getName(),
      new PanelDividido(archivo)
    );
  }
  
  public void guardarArchivo() {
    if(getTabCount() > 0) {
      int index = getSelectedIndex();
      PanelDividido panelDividido = (PanelDividido) getComponentAt(index);
      panelDividido.saveContent();
      setTitleAt(index, panelDividido.getTitle());
    }
  }
  
  public void cerrarArchivo() {
    if(getTabCount() > 0) {
      removeTabAt(
        getSelectedIndex()
      );
    }
  }
  
  public void copiar() {
    if(getTabCount() > 0) {
      PanelDividido panelDividido = (PanelDividido) getSelectedComponent();
      panelDividido.copiar();
    }
  }
  
  public void cortar() {
    if(getTabCount() > 0) {
      PanelDividido panelDividido = (PanelDividido) getSelectedComponent();
      panelDividido.cortar();
    }
  }
  
  public void pegar() {
    if(getTabCount() > 0) {
      PanelDividido panelDividido = (PanelDividido) getSelectedComponent();
      panelDividido.pegar();
    }
  }
  
  public void clearInfo() {
    if(getTabCount() > 0) {
      PanelDividido panelDividido = (PanelDividido) getSelectedComponent();
      panelDividido.clearInfo();
    }
  }
  
  public void addMessage(String message) {
    if(getTabCount() > 0) {
      PanelDividido panelDividido = (PanelDividido) getSelectedComponent();
      panelDividido.addMessage(message);
    }
  }
  
  public void compilar() {
    if(!(getTabCount() > 0)) {
      return;
    }
    this.guardarArchivo();
    
    PanelDividido panelDividido = (PanelDividido) getSelectedComponent();
    File archivo = panelDividido.getArchivo();
    if(archivo == null) {
      return;
    }
    
    // /*
    Analizador analizador = new Analizador(
      new ByteArrayInputStream(
        panelDividido.getText().getBytes()
      )
    );
    
    try {
      analizador.setUrlFile(
        archivo.toURI().toURL()
      );
    } catch (MalformedURLException ex) {
      clearInfo();
      panelDividido.addMessage(ex.getMessage());
      return;
    }
    
    clearInfo();
    
    try {
      analizador.analizar();
    } catch(ParseException | TokenMgrError ex) {
      analizador.errores.add(ex.getMessage());
    }
    
    int numErrores = analizador.errores.size();
    if(numErrores > 0) {
      panelDividido.addMessage("Se encontraron "+numErrores+" errores:");
      for(String error : analizador.errores) {
        panelDividido.addMessage(error);
      }
    }
    panelDividido.addMessage("Analisis finalizado.");
    
    // */
  }
}