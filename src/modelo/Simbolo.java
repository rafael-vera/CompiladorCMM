/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modelo;

/**
 *
 * @author Rafael Vera
 */
public class Simbolo {
  public final int tipo;
  public final String alcance;
  public final int argumento;
  public final int beginLine;
  public final int beginColumn;
  
  public Simbolo(int tipo, String alcance, int argumento, int beginLine, int beginColumn) {
    this.tipo = tipo;
    this.alcance = alcance;
    this.argumento = argumento;
    this.beginLine = beginLine;
    this.beginColumn = beginColumn;
  }
  
  public Simbolo(int tipo, int beginLine, int beginColumn) {
    this(tipo, "global", 0, beginLine, beginColumn);
  }
}