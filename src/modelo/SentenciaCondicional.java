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
public class SentenciaCondicional extends Sentencia {
  private final ArrayList<SentenciaIf> sentenciasIf;
  private final ArrayList<Sentencia> sentenciasElse;
  
  public SentenciaCondicional(HashMap<String, Simbolo> tablaSimbolos) {
    super(tablaSimbolos);
    this.sentenciasIf = new ArrayList();
    this.sentenciasElse = new ArrayList();
    //System.out.println("Se crea una sentencia condicional");
  }
  
  public void agregarCondicionSentenciaIf(SentenciaOperacion condicion) {
    this.sentenciasIf.add(
      new SentenciaIf(condicion)
    );
  }
  
  public void agregarSentenciaIf(Sentencia sentencia) {
    this.sentenciasIf.get(
      this.sentenciasIf.size()-1
    ).agregarSentencia(sentencia);
  }
  
  public void agregarSentenciaElse(Sentencia sentencia) {
    this.sentenciasElse.add(sentencia);
  }
  
  @Override
  public String crearInstrucciones() {
    String val1, val2;
    int numElse = 0;
    int numContinue = (++nContinue);
    
    for(SentenciaIf sentenciaIf : this.sentenciasIf) {
      numElse = (++nElse);
      
      this.instrucciones.add(
        new Instruccion(
          Instruccion.CONDICION,
          ""+(++nCondicion),
          "-"
        )
      );
      
      val1 = sentenciaIf.condicion.crearCondicion();
      this.instrucciones.addAll(
        sentenciaIf.condicion.getInstrucciones()
      );
      
      val2 = "Temp"+(++nTemp);
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
          Instruccion.ELSE,
          ""+numElse
        )
      );
      
      for(Sentencia sentencia : sentenciaIf.sentencias) {
        sentencia.crearInstrucciones();
        this.instrucciones.addAll(
          sentencia.getInstrucciones()
        );
      }
      
      this.instrucciones.add(
        new Instruccion(
          Instruccion.JMP,
          Instruccion.CONTINUE,
          ""+numContinue
        )
      );
      
      this.instrucciones.add(
        new Instruccion(
          Instruccion.ELSE,
          ""+numElse,
          "-"
        )
      );
    }
    
    for(Sentencia sentencia : this.sentenciasElse) {
      sentencia.crearInstrucciones();
      this.instrucciones.addAll(
        sentencia.getInstrucciones()
      );
    }
    
    this.instrucciones.add(
      new Instruccion(
        Instruccion.CONTINUE,
        ""+numContinue,
        "-"
      )
    );
    
    return "";
  }
  
  class SentenciaIf {
    private final SentenciaOperacion condicion;
    private final ArrayList<Sentencia> sentencias;
    
    public SentenciaIf(SentenciaOperacion condicion) {
      this.condicion = condicion;
      this.sentencias = new ArrayList();
    }
    
    public void agregarSentencia(Sentencia sentencia) {
      this.sentencias.add(sentencia);
    }
  }
}