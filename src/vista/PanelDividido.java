/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vista;

import java.awt.Color;
import java.awt.Font;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import javax.swing.JFileChooser;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.Element;

/**
 *
 * @author Rafael Vera
 */
public class PanelDividido extends JSplitPane {
  private final JTextArea textoArchivo;
  private final JTextArea textoInfo;
  
  private File archivo;
  
  public PanelDividido(File archivo) {
    super(JSplitPane.VERTICAL_SPLIT);
    this.textoArchivo = new JTextArea();
    this.textoInfo = new JTextArea();
    this.archivo = archivo;
    
    initComponents();
    setOneTouchExpandable(true);
    setContinuousLayout(true);
    setResizeWeight(0.8);
  }
  
  public PanelDividido() {
    this(null);
  }
  
  private void initComponents() {
    Font fuente = new Font("Consolas", Font.PLAIN, 16);
    
    JTextArea lineas = new JTextArea("1 ");
    lineas.setFont(fuente);
    lineas.setBackground(Color.LIGHT_GRAY);
    lineas.setEditable(false);
    setLines(lineas);
    
    this.textoArchivo.setEditable(true);
    this.textoArchivo.setFont(fuente);
    this.textoArchivo.setBackground(Color.DARK_GRAY);
    this.textoArchivo.setForeground(Color.WHITE);
    this.textoArchivo.setCaretColor(Color.WHITE);
    this.textoArchivo.setTabSize(4);
    this.textoArchivo.setText(
      getContent()
    );
    
    JScrollPane jsp = new JScrollPane(
      this.textoArchivo
    );
    jsp.setRowHeaderView(lineas);
    setTopComponent(jsp);
    
    this.textoInfo.setEditable(false);
    this.textoInfo.setFont(fuente);
    this.textoInfo.setBackground(Color.BLACK);
    this.textoInfo.setForeground(Color.WHITE);
    this.textoInfo.setTabSize(4);
    setBottomComponent(
      new JScrollPane(textoInfo)
    );
  }
  
  private void setLines(JTextArea lineas) {
    this.textoArchivo.getDocument().addDocumentListener(
      new DocumentListener() {
        public String getText() {
          int caretPosition = textoArchivo.getDocument().getLength();
          Element root = textoArchivo.getDocument().getDefaultRootElement();
          String text = "1 " + System.getProperty("line.separator");
          for(int i = 2; i < root.getElementIndex(caretPosition) + 2; i++) {
            text += i + " " + System.getProperty("line.separator");
          }
          return text;
        }
        
        @Override
        public void insertUpdate(DocumentEvent e) {
          lineas.setText(getText());
        }
        
        @Override
        public void removeUpdate(DocumentEvent e) {
          lineas.setText(getText());
        }
        
        @Override
        public void changedUpdate(DocumentEvent e) {
          lineas.setText(getText());
        }
      }
    );
  }
  
  private String getContent() {
    if(this.archivo == null)
      return "";
    StringBuilder builder = new StringBuilder();
    try (FileReader fr = new FileReader(archivo)) {
      BufferedReader buffer = new BufferedReader(fr);
      String linea;
      while( (linea = buffer.readLine()) != null ) {
        builder.append(linea).append("\n");
      }
      builder.deleteCharAt( builder.length()-1 );
    } catch (FileNotFoundException ex) {
      System.err.println("Error: "+ex.getMessage());
      return "";
    } catch (IOException ex) {
      System.err.println("Error: "+ex.getMessage());
      return "";
    }
    return builder.toString();
  }
  
  public void saveContent() {
    if(archivo == null)
      archivo = createFile();
    
    if(archivo == null)
      return;
    
    try (FileWriter writer = new FileWriter(archivo)) {
      writer.write(textoArchivo.getText());
    } catch (IOException ex) {
      System.err.println("Error: "+ex.getMessage());
    }
  }
  
  private File createFile() {
    JFileChooser chooser = new JFileChooser();
    FileNameExtensionFilter extension = new FileNameExtensionFilter(
      "Archivo C--",
      "cmm"
    );
    chooser.setFileFilter(extension);
    int valor = chooser.showSaveDialog(this);
    if(valor == JFileChooser.APPROVE_OPTION)
      return chooser.getSelectedFile();
    return null;
  }
  
  protected String getTitle() {
    return archivo == null ?
      "Sin Titulo" : archivo.getName();
  }
  
  protected String getText() {
    return textoArchivo.getText();
  }
  
  protected void copiar() {
    this.textoArchivo.copy();
  }
  
  protected void cortar() {
    this.textoArchivo.cut();
  }
  
  protected void pegar() {
    this.textoArchivo.paste();
  }
  
  protected void clearInfo() {
    this.textoInfo.setText("");
  }
  
  protected void addMessage(String message) {
    this.textoInfo.append(message+"\n");
  }
  
  protected File getArchivo() {
    return this.archivo;
  }
}