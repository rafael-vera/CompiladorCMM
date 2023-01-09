/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author rafael-vera
 */
public class Sentencia {
  protected final HashMap<String, Simbolo> tablaSimbolos;
  protected final ArrayList<Instruccion> instrucciones;
  
  public static int nTemp = 0;
  public static int nTrue = 0;
  public static int nFalse = 0;
  public static int nContinue = 0;
  public static int nCondicion = 0;
  public static int nElse = 0;
  
  public Sentencia(HashMap<String, Simbolo> tablaSimbolos) {
    this.tablaSimbolos = tablaSimbolos;
    this.instrucciones = new ArrayList();
  }
  
  public String crearInstrucciones() {
    return "";
  }
  
  public ArrayList<Instruccion> getInstrucciones() {
    return this.instrucciones;
  }
  
  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    
    for(Instruccion instruccion : instrucciones) {
      sb.append('\t')
        .append(instruccion)
        .append('\n');
    }
    
    return sb.toString();
  }
}