/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vista;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.WindowConstants;

/**
 *
 * @author Rafael Vera
 */
public class DialogoAcercaDe extends JDialog {
  public DialogoAcercaDe(Frame parent, boolean modal) {
    super(parent, modal);
    initComponents();
  }
  
  private void initComponents() {
    JScrollPane scroll = new JScrollPane();
    JEditorPane panelEditor = new JEditorPane();
    JPanel panelSur = new JPanel();
    JButton botonAceptar = new JButton();
    
    setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    setTitle("Acerca del programa");
    setResizable(false);
    
    scroll.setBorder(
      BorderFactory.createLineBorder(
        getBackground(), 10
      )
    );
    panelEditor.setEditable(false);
    try {
      panelEditor.setPage(
        getClass().getResource("/info/AcercaDe.html")
      );
    } catch(IOException ex) {
      System.err.println(ex.getMessage());
    }
    scroll.setViewportView(panelEditor);
    getContentPane().add(scroll, BorderLayout.CENTER);
    
    botonAceptar.setText("Aceptar");
    botonAceptar.addActionListener(
      new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          botonAceptarActionPerformed(e);
        }
      }
    );
    panelSur.add(botonAceptar);
    
    getContentPane().add(panelSur, BorderLayout.SOUTH);
    
    pack();
  }
  
  private void botonAceptarActionPerformed(ActionEvent evt) {
    setVisible(false);
  }
}