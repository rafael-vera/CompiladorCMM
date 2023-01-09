/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author rafae
 */
public class CodigoIntermedio {
  private final ArrayList<String> errores;
  private final ArrayList<Funcion> funciones;
  private final HashMap<String, ArrayList<Instruccion>> instrucciones;
  
  public CodigoIntermedio(ArrayList<String> errores) {
    this.errores = errores;
    this.funciones = new ArrayList();
    this.instrucciones = new HashMap();
  }
  
  public void agregarFuncion(Funcion funcion) {
    this.funciones.add(funcion);
  }
  
  public void crearInstrucciones() {
    Sentencia.nTemp = 0;
    
    for(Funcion funcion : funciones) {
      ArrayList<Instruccion> instruccionesFunc = new ArrayList();
      
      for(Sentencia sentencia : funcion.getSentencias()) {
        sentencia.crearInstrucciones();
        instruccionesFunc.addAll(
          sentencia.getInstrucciones()
        );
      }
      
      this.instrucciones.put(
        funcion.getNombre()+":",
        instruccionesFunc
      );
    }
    
    optimizar();
  }
  
  public void optimizar() {
    HashMap<String, ArrayList<String>> grafoFunciones = new HashMap();
    List<String> funcionesInutilizadas = new ArrayList(this.instrucciones.keySet());
    ArrayList<String> aux;
    
    for(String nombreFuncion : this.instrucciones.keySet()) {
      aux = new ArrayList();
      
      for(Instruccion instruccionFuncion : this.instrucciones.get(nombreFuncion)) {
        if(instruccionFuncion.getOperacion().equals(Instruccion.CALL)) {
          aux.add(
            instruccionFuncion.getOperando1()+":"
          );
        }
      }
      
      grafoFunciones.put(
        nombreFuncion,
        aux
      );
    }
    
    // Recorrer el grafo y quitar de la lista las funciones que no se quitan (en caso de que estén)
    String nombreMain = AnalizadorSemantico.ALCANCE_FUNCION+"_main:";
    if(grafoFunciones.keySet().contains(nombreMain)) {
      recorrerGrafo(nombreMain, grafoFunciones, funcionesInutilizadas);
    }
    
    // Quitar del HashMap de instrucciones las funciones a quitar
    for(String nombreFuncion : funcionesInutilizadas) {
      this.instrucciones.remove(
        nombreFuncion
      );
    }
  }
  
  private void recorrerGrafo(
    String nombreFuncion,
    HashMap<String, ArrayList<String>> grafoFunciones,
    List<String> funcionesInutilizadas
  ) {
    funcionesInutilizadas.remove(nombreFuncion);
    for(String nombre : grafoFunciones.get(nombreFuncion)) {
      if(!nombre.equals(nombreFuncion)) {
        recorrerGrafo(nombre, grafoFunciones, funcionesInutilizadas);
      }
    }
  }
  
  public HashMap<String, ArrayList<Instruccion>> getInstrucciones() {
    return this.instrucciones;
  }
  
  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder(
      this.instrucciones.size()
    );
    
    for(Map.Entry<String, ArrayList<Instruccion>> funcion : instrucciones.entrySet()) {
      sb.append(funcion.getKey())
        .append('\n');
      for(Instruccion instruccion : funcion.getValue()) {
        sb.append('\t')
          .append(instruccion)
          .append('\n');
      }
    }
    
    return sb.toString();
  }
}