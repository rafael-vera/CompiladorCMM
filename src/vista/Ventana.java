/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vista;

import controlador.OyenteBarra;
import java.awt.BorderLayout;
import java.awt.Color;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;

/**
 *
 * @author Rafael Vera
 */
public class Ventana extends JFrame {
  private final JMenuItem nuevoArchivo;
  private final JMenuItem abrirArchivo;
  private final JMenuItem guardarArchivo;
  private final JMenuItem cerrarArchivo;
  private final JMenuItem salir;
  private final JMenuItem copiar;
  private final JMenuItem cortar;
  private final JMenuItem pegar;
  private final JMenuItem compilar;
  private final JMenuItem acercaDe;
  
  private final PanelCentro panelCentro;
  
  public Ventana(String title, PanelCentro panelCentro) {
    super(title);
    this.nuevoArchivo = new JMenuItem("Nuevo archivo");
    this.abrirArchivo = new JMenuItem("Abrir archivo");
    this.guardarArchivo = new JMenuItem("Guardar archivo");
    this.cerrarArchivo = new JMenuItem("Cerrar archivo");
    this.salir = new JMenuItem("Salir");
    this.copiar = new JMenuItem("Copiar");
    this.cortar = new JMenuItem("Cortar");
    this.pegar = new JMenuItem("Pegar");
    this.compilar = new JMenuItem("Compilar");
    this.acercaDe = new JMenuItem("Acerca de");
    this.panelCentro = panelCentro;
    initComponents();
    setSize(750, 600);
    setLocationRelativeTo(null);
    setDefaultCloseOperation(EXIT_ON_CLOSE);
  }
  public Ventana(PanelCentro panelCentro) {
    this("Compilador CMM", panelCentro);
  }
  
  public void addEvents(OyenteBarra oyente) {
    this.nuevoArchivo.setName("nuevoArchivo");
    this.nuevoArchivo.addActionListener(oyente);
    this.abrirArchivo.setName("abrirArchivo");
    this.abrirArchivo.addActionListener(oyente);
    this.guardarArchivo.setName("guardarArchivo");
    this.guardarArchivo.addActionListener(oyente);
    this.cerrarArchivo.setName("cerrarArchivo");
    this.cerrarArchivo.addActionListener(oyente);
    this.salir.setName("salir");
    this.salir.addActionListener(oyente);
    this.copiar.setName("copiar");
    this.copiar.addActionListener(oyente);
    this.cortar.setName("cortar");
    this.cortar.addActionListener(oyente);
    this.pegar.setName("pegar");
    this.pegar.addActionListener(oyente);
    this.compilar.setName("compilar");
    this.compilar.addActionListener(oyente);
    this.acercaDe.setName("acercaDe");
    this.acercaDe.addActionListener(oyente);
  }
  
  private void initComponents() {
    JMenuBar barra = new JMenuBar();
    JMenu menuArchivo = new JMenu("Archivo");
    JMenu menuEditar = new JMenu("Editar");
    JMenu menuCompilador = new JMenu("Compilador");
    JMenu menuAyuda = new JMenu("Ayuda");
    
    menuArchivo.add(this.nuevoArchivo);
    menuArchivo.add(this.abrirArchivo);
    menuArchivo.add(this.guardarArchivo);
    menuArchivo.add(this.cerrarArchivo);
    menuArchivo.add(
      new JSeparator()
    );
    menuArchivo.add(this.salir);
    barra.add(menuArchivo);
    menuEditar.add(this.copiar);
    menuEditar.add(this.cortar);
    menuEditar.add(this.pegar);
    barra.add(menuEditar);
    menuCompilador.add(this.compilar);
    barra.add(menuCompilador);
    menuAyuda.add(this.acercaDe);
    barra.add(menuAyuda);
    setJMenuBar(barra);
    
    getContentPane().setBackground(
      new Color(90, 90, 100)
    );
    getContentPane().add(this.panelCentro, BorderLayout.CENTER);
    
    pack();
  }
}