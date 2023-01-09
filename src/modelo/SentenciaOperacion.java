/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

/**
 *
 * @author rafae
 */
public class SentenciaOperacion extends Sentencia {
  private final NotacionPolaca expresion;
  private final Stack<Termino> prefijo;
  private int pos;
  
  public SentenciaOperacion(NotacionPolaca expresion, HashMap<String, Simbolo> tablaSimbolos) {
    super(tablaSimbolos);
    this.expresion = expresion;
    this.expresion.crearPrefijo();
    this.prefijo = this.expresion.getPrefijo();
    //System.out.println(this.prefijo);
  }
  
  @Override
  public String crearInstrucciones() {
    this.pos = this.prefijo.size() - 1;
    System.out.println(this.prefijo);
    return crearInstrucciones(false);
  }
  
  public String crearCondicion() {
    this.pos = this.prefijo.size() - 1;
    System.out.println(this.prefijo);
    return crearInstrucciones(true);
  }
  
  public String crearInstrucciones(boolean hasTemp) {
    String val1 = "";
    String val2 = "";
    
    Termino actual = this.prefijo.get(pos);
    switch(actual.getPrecedencia()) {
      case Termino.FUNCION -> {
        SentenciaOperacion aux;
        
        if(actual.getContenido().equals("print") || actual.getContenido().equals("input")) {
          aux = new SentenciaOperacion(actual.getParametros().get(0), tablaSimbolos);
          
          String temp = aux.crearInstrucciones(false);
          
          this.instrucciones.add(
            new Instruccion(
              actual.getContenido().equals("print") ? Instruccion.OUTPUT : Instruccion.INPUT,
              temp,
              "-"
            )
          );
        } else {
          ArrayList<Instruccion> asignaParametros = new ArrayList();
          
          for(NotacionPolaca parametro : actual.getParametros()) {
            aux = new SentenciaOperacion(parametro, this.tablaSimbolos);
            aux.crearInstrucciones();
            
            asignaParametros.add(
              aux.getInstrucciones()
                .get(
                  aux.getInstrucciones().size()-1
                )
            );
            
            this.instrucciones.addAll(
              aux.getInstrucciones()
            );
            
            this.instrucciones.remove(
              this.instrucciones.size()-1
            );
          }
          
          this.instrucciones.addAll(asignaParametros);
          
          this.instrucciones.add(
            new Instruccion(
              Instruccion.CALL,
              actual.getContenido(),
              "-"
            )
          );
          
          val2 = actual
            .getContenido()
            .substring(
              AnalizadorSemantico
                .ALCANCE_FUNCION
                .length()+1
            ) + "_return";
          
          if(hasTemp) {
            val1 = "Temp"+(++nTemp);
            
            this.instrucciones.add(
              new Instruccion(
                Instruccion.MOV,
                val1,
                val2
              )
            );
          } else {
            val1 = val2;
          }
        }
        
        break;
      }
      case Termino.VALOR -> {
        if(hasTemp) {
          val1 = "Temp"+(++nTemp);
          
          this.instrucciones.add(
            new Instruccion(
              Instruccion.MOV,
              val1,
              actual.getContenido()
            )
          );
        } else {
          val1 = actual.getContenido();
        }
        
        break;
      }
      case Termino.IGUAL -> {
        pos--;
        val1 = crearInstrucciones(false);
        
        pos--;
        val2 = crearInstrucciones(true);
        
        this.instrucciones.add(
          new Instruccion(
            Instruccion.MOV,
            val1,
            val2
          )
        );
        break;
      }
      case Termino.CONCATENACION_LOG -> {
        pos--;
        val1 = crearInstrucciones(true);
        
        pos--;
        val2 = crearInstrucciones(true);
        
        this.instrucciones.add(
          new Instruccion(
            actual.getContenido().equals("&&") ? Instruccion.AND : Instruccion.OR,
            val1,
            val2
          )
        );
        
        break;
      }
      case Termino.OP_LOGICO -> {
        pos--;
        val1 = crearInstrucciones(false);
        
        pos--;
        val2 = crearInstrucciones(false);
        
        this.instrucciones.add(
          new Instruccion(
            Instruccion.CMP,
            val1,
            val2
          )
        );
        
        String op = "";
        
        switch(actual.getContenido()) {
          case "==" -> {
            op = Instruccion.JE;
            break;
          }
          case "!=" -> {
            op = Instruccion.JNE;
            break;
          }
          case "<" -> {
            op = Instruccion.JL;
            break;
          }
          case "<=" -> {
            op = Instruccion.JLE;
            break;
          }
          case ">" -> {
            op = Instruccion.JG;
            break;
          }
          case ">=" -> {
            op = Instruccion.JGE;
            break;
          }
        }
        
        this.instrucciones.add(
          new Instruccion(
            op, Instruccion.TRUE, ""+(++nTrue)
          )
        );
        
        val1 = "Temp"+(++nTemp);
        
        this.instrucciones.add(
          new Instruccion(
            Instruccion.MOV, val1, "false"
          )
        );
        
        this.instrucciones.add(
          new Instruccion(
            Instruccion.JMP, Instruccion.CONTINUE, ""+(++nContinue)
          )
        );
        
        this.instrucciones.add(
          new Instruccion(
            Instruccion.TRUE, ""+(nTrue), "-"
          )
        );
        
        this.instrucciones.add(
          new Instruccion(
            Instruccion.MOV, val1, "true"
          )
        );
        
        this.instrucciones.add(
          new Instruccion(
            Instruccion.CONTINUE, ""+(nContinue), "-"
          )
        );
        
        break;
      }
      case Termino.SUMA_RESTA -> {
        pos--;
        val1 = crearInstrucciones(true);
        
        pos--;
        val2 = crearInstrucciones(true);
        
        this.instrucciones.add(
          new Instruccion(
            actual.getContenido().equals("+") ? Instruccion.ADD : Instruccion.SUB,
            val1,
            val2
          )
        );
        break;
      }
      case Termino.MUL_DIV -> {
        pos--;
        val1 = crearInstrucciones(true);
        
        pos--;
        val2 = crearInstrucciones(true);
        
        this.instrucciones.add(
          new Instruccion(
            actual.getContenido().equals("*") ? Instruccion.MUL : Instruccion.DIV,
            val1,
            val2
          )
        );
        break;
      }
    }
    
    return val1;
  }
}