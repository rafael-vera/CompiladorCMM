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
public class SentenciaFor extends Sentencia {
  private final SentenciaOperacion asignacionInicial;
  private final SentenciaOperacion condicion;
  private final SentenciaOperacion asignacionFinal;
  private final ArrayList<Sentencia> sentencias;
  
  public SentenciaFor(
    SentenciaOperacion asignacionInicial,
    SentenciaOperacion condicion,
    SentenciaOperacion asignacionFinal,
    HashMap<String, Simbolo> tablaSimbolos
  ) {
    super(tablaSimbolos);
    this.asignacionInicial = asignacionInicial;
    this.condicion = condicion;
    this.asignacionFinal = asignacionFinal;
    this.sentencias = new ArrayList();
    //System.out.println("Se crea una sentencia for");
  }
  
  public void agregarSentencia(Sentencia sentencia) {
    this.sentencias.add(sentencia);
  }
  
  @Override
  public String crearInstrucciones() {
    int numCondicion = (++nCondicion);
    int numContinue = (++nContinue);
    
    this.asignacionInicial.crearInstrucciones();
    this.instrucciones.addAll(
      this.asignacionInicial.getInstrucciones()
    );
    
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
    
    this.asignacionFinal.crearInstrucciones();
    this.instrucciones.addAll(
      this.asignacionFinal.getInstrucciones()
    );
    
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