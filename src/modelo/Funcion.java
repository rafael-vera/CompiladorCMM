/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

import java.util.ArrayList;

/**
 *
 * @author rafae
 */
public class Funcion {
  private final ArrayList<Sentencia> sentencias;
  private final String nombre;
  
  public Funcion(String nombre) {
    this.sentencias = new ArrayList();
    this.nombre = nombre;
  }
  
  public void agregarSentencia(Sentencia sentencia) {
    this.sentencias.add(sentencia);
  }
  
  public ArrayList<Sentencia> getSentencias() {
    return this.sentencias;
  }
  
  public String getNombre() {
    return this.nombre;
  }
  
  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    
    sb.append(nombre)
      .append(":\n");
    for(Sentencia sent : sentencias) {
      sb.append(sent);
    }
    
    return sb.toString();
  }
}
