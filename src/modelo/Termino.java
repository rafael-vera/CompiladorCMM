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
public class Termino {
  public static final int FUNCION = -1;
  public static final int VALOR = 0;
  public static final int IGUAL = 1;
  public static final int CONCATENACION_LOG = 2;
  public static final int OP_LOGICO = 3;
  public static final int SUMA_RESTA = 4;
  public static final int MUL_DIV = 5;
  
  private final String contenido;
  private final int precedencia;
  private final ArrayList<NotacionPolaca> parametros;
  
  public Termino(String contenido, int precedencia) {
    this.contenido = contenido;
    this.precedencia = precedencia;
    this.parametros = new ArrayList();
  }
  
  public void agregarParametro(NotacionPolaca parametro) {
    this.parametros.add(parametro);
  }
  
  public String getContenido() {
    return this.contenido;
  }
  
  public int getPrecedencia() {
    return this.precedencia;
  }
  
  public ArrayList<NotacionPolaca> getParametros() {
    return this.parametros;
  }
  
  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(this.contenido);
    if(!this.parametros.isEmpty()) {
      for(NotacionPolaca parametro : parametros) {
        sb.append(parametro.getPrefijo());
      }
    }
    return sb.toString();
  }
}