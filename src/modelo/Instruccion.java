/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

/**
 *
 * @author rafae
 */
public class Instruccion {
  // Operacion aritmetica
  public static final String MOV       = "ASIGNA";
  public static final String ADD       = "SUMA";
  public static final String SUB       = "RESTA";
  public static final String MUL       = "MULTIPLICA";
  public static final String DIV       = "DIVIDE";
  // Operacion logica
  public static final String AND       = "AND";
  public static final String OR        = "OR";
  // Operacion condicion
  public static final String CMP       = "COMPARA";
  public static final String CMPS      = "COMPARA_STR";
  public static final String JE        = "IGUAL";
  public static final String JNE       = "DIFERENTE";
  public static final String JG        = "MAYOR";
  public static final String JGE       = "MAYOR_IGUAL";
  public static final String JL        = "MENOR";
  public static final String JLE       = "MENOR_IGUAL";
  // Operacion saltar (condicion o ciclos)
  public static final String JMP       = "SALTA_A";
  // Funciones integradas
  public static final String OUTPUT    = "PRINT";
  public static final String INPUT     = "INPUT";
  // Funciones externas
  public static final String CALL      = "LLAMA";
  // Etiquetas condicionales
  public static final String TRUE      = "_TRUE";
  public static final String FALSE     = "_FALSE";
  public static final String CONTINUE  = "_CONTINUE";
  public static final String CONDICION = "_CONDICION";
  public static final String ELSE      = "_SI_NO";
  
  private final String operacion;
  private final String operando1;
  private final String operando2;
  
  public Instruccion(String operacion, String operando1, String operando2) {
    this.operacion = operacion;
    this.operando1 = operando1;
    this.operando2 = operando2;
  }
  
  public String getOperacion() {
    return this.operacion;
  }
  
  public String getOperando1() {
    return this.operando1;
  }
  
  public String getOperando2() {
    return this.operando2;
  }
  
  @Override
  public String toString() {
    return operacion+" "+operando1+", "+operando2;
  }
}