/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author rafae
 */
public class SentenciaWhile extends Sentencia {
  private final SentenciaOperacion condicion;
  private final ArrayList<Sentencia> sentencias;
  
  public SentenciaWhile(SentenciaOperacion condicion, HashMap<String, Simbolo> tablaSimbolos) {
    super(tablaSimbolos);
    this.condicion = condicion;
    this.sentencias = new ArrayList();
    //System.out.println("Se crea una sentencia while");
  }
  
  public void agregarSentencia(Sentencia sentencia) {
    this.sentencias.add(sentencia);
  }
  
  @Override
  public String crearInstrucciones() {
    int numCondicion = (++nCondicion);
    int numContinue = (++nContinue);
    
    this.instrucciones.add(
      new Instruccion(
        Instruccion.CONDICION,
        ""+numCondicion,
        "-"
      )
    );
    
    String val1 = this.condicion.crearCondicion();
    this.instrucciones.addAll(
      this.condicion.getInstrucciones()
    );
    
    String val2 = "Temp"+(++nTemp);
    this.instrucciones.add(
      new Instruccion(
        Instruccion.MOV, val2, "false"
      )
    );
    
    this.instrucciones.add(
      new Instruccion(
        Instruccion.CMPS, val1, val2
      )
    );
    
    this.instrucciones.add(
      new Instruccion(
        Instruccion.JE,
        Instruccion.CONTINUE,
        ""+numContinue
      )
    );
    
    for(Sentencia sentencia : this.sentencias) {
      sentencia.crearInstrucciones();
      this.instrucciones.addAll(
        sentencia.getInstrucciones()
      );
    }
    
    this.instrucciones.add(
      new Instruccion(
        Instruccion.JMP,
        Instruccion.CONDICION,
        ""+numCondicion
      )
    );
    
    this.instrucciones.add(
      new Instruccion(
        Instruccion.CONTINUE, ""+numContinue, "-"
      )
    );
    
    return "";
  }
}