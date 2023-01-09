/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modelo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Rafael Vera
 */
public class AnalizadorSemantico {

  public final static int SIGNO_PLUS = 7;
  public final static int SIGNO_MINUS = 8;
  public final static int SIGNO_MULTIPLY = 9;
  public final static int SIGNO_DIVISION = 10;
  public final static int SIGNO_MOD = 11;

  public final static int SIGNO_EQUALS = 13;
  public final static int SIGNO_NOT_EQUALS = 14;
  public final static int SIGNO_AND = 15;
  public final static int SIGNO_OR = 16;
  public final static int SIGNO_GREATER = 17;
  public final static int SIGNO_GREATER_EQUAL = 18;
  public final static int SIGNO_LESS = 19;
  public final static int SIGNO_LESS_EQUAL = 20;

  public final static int TIPO_VOID = 27;
  public final static int TIPO_INT = 32;
  public final static int TIPO_FLOAT = 33;
  public final static int TIPO_CHAR = 34;
  public final static int TIPO_STRING = 35;
  public final static int TIPO_BOOL = 36;

  public final static int VALOR_INT = 37;
  public final static int VALOR_FLOAT = 38;
  public final static int VALOR_CHAR = 39;
  public final static int VALOR_STRING = 40;
  public final static int VALOR_BOOL = 41;

  public final static int IDENTIFICADOR = 42;

  public final static String ALCANCE_FUNCION = "global";

  private final ArrayList<String> errores;
  private final HashMap<String, Simbolo> tablaSimbolos;

  public AnalizadorSemantico(ArrayList<String> errores, HashMap<String, Simbolo> tablaSimbolos) {
    this.errores = errores;
    this.tablaSimbolos = tablaSimbolos;
  }

  public void addFuncion(Token func, int tipo) {
    if (tablaSimbolos.containsKey(ALCANCE_FUNCION + "_" + func.image)) {
      errores.add("Error en la línea " + func.beginLine + ", columna " + func.beginColumn + ":"
        + "\n\tLa función \'" + func.image + "\' ya está declarada.");
      return;
    }

    tablaSimbolos.put(
      ALCANCE_FUNCION + "_" + func.image,
      new Simbolo(tipo, func.beginLine, func.beginColumn)
    );
  }

  public void addVariable(Token var, int tipo, String alcance, int argumento) {
    if (tablaSimbolos.containsKey(alcance + "_" + var.image)) {
      errores.add("Error en la línea " + var.beginLine + ", columna " + var.beginColumn + ":"
        + "\n\tLa variable \'" + var.image + "\' ya está declarada.");
      return;
    }

    tablaSimbolos.put(
      alcance + "_" + var.image,
      new Simbolo(tipo, alcance, argumento, var.beginLine, var.beginColumn)
    );
  }

  public int obtenerTipo(Token token, String alcance) {
    if (!tablaSimbolos.containsKey(alcance + "_" + token.image)) {
      errores.add("Error en la línea " + token.beginLine + ", columna " + token.beginColumn + ":"
        + "\n\tLa variable o funcion \'" + token.image + "\' no está declarada.");
      return -1;
    }

    return tablaSimbolos.get(alcance + "_" + token.image).tipo;
  }

  public void checarTipoValor(int tipo, Token valor) {
    switch(tipo) {
      case TIPO_INT: {
        if( valor.kind == VALOR_INT )
          return;
        break;
      }
      case TIPO_FLOAT: {
        if( valor.kind == VALOR_FLOAT )
          return;
        break;
      }
      case TIPO_CHAR: {
        if( valor.kind == VALOR_CHAR )
          return;
        break;
      }
      case TIPO_STRING: {
        if( valor.kind == VALOR_STRING )
          return;
        break;
      }
    }
    
    errores.add("Error en la línea " + valor.beginLine + ", columna " + valor.beginColumn + ":"
      + "\n\tEl valor no corresponde con el tipo de dato.");
  }

  public void checarTipoOperacion(int tipo, Token operacion) {
    switch (tipo) {
      case TIPO_INT:
      case TIPO_FLOAT: {
        if (!(operacion.kind == SIGNO_PLUS
          || operacion.kind == SIGNO_MINUS
          || operacion.kind == SIGNO_MULTIPLY
          || operacion.kind == SIGNO_DIVISION
          || operacion.kind == SIGNO_MOD)) {
          errores.add("Error en la línea " + operacion.beginLine + ", columna " + operacion.beginColumn + ":"
            + "\n\tLa operación no corresponde con el tipo de dato.");
        }
        break;
      }
      case TIPO_CHAR: {
        errores.add("Error en la línea " + operacion.beginLine + ", columna " + operacion.beginColumn + ":"
          + "\n\tEl tipo de dato char no tiene operaciones aplicables.");
        break;
      }
      case TIPO_STRING: {
        //if (!(operacion.kind == SIGNO_PLUS)) {
          errores.add("Error en la línea " + operacion.beginLine + ", columna " + operacion.beginColumn + ":"
            + "\n\tLa operación no corresponde con el tipo de dato.");
        //}
        break;
      }
    }
  }

  public void checarTipoFuncVar(String nombre, Integer[] tipos, int tipoFuncVar, int beginLine, int beginColumn, boolean isVar) {
    if( !(Arrays.asList(tipos).contains(tipoFuncVar)) ) {
      String mensaje = "Error en la línea " + beginLine + ", columna " + beginColumn + ":\n\tEl valor de ";
      mensaje += isVar? "la variable" : "retorno de la función";
      mensaje += " \'" + nombre + "\' no corresponde con el tipo de dato.";
      errores.add(mensaje);
    }
  }
  
  public int tieneParametros(Token func) {
    if( !(tablaSimbolos.containsKey(ALCANCE_FUNCION + "_" + func.image)) ) {
      errores.add("Error en la línea " + func.beginLine + ", columna " + func.beginColumn + ":"
        + "\n\tLa función \'" + func.image + "\' no existe.");
      return -1;
    }
    
    int total = 0;
    for( Map.Entry<String, Simbolo> simbolo : tablaSimbolos.entrySet() ) {
      if( func.image.equals( simbolo.getValue().alcance ) && simbolo.getValue().argumento > total ) {
        total = simbolo.getValue().argumento;
      }
    }
    
    return total;
  }
  
  public String obtenerNombreArgumento(String func, int nArg) {
    for( Map.Entry<String, Simbolo> simbolo : tablaSimbolos.entrySet() ) {
      if( func.equals( simbolo.getValue().alcance ) && simbolo.getValue().argumento == nArg ) {
        return simbolo.getKey();
      }
    }
    
    return "";
  }
  
  public int obtenerTipoArgumento(String func, int nArg) {
    for( Map.Entry<String, Simbolo> simbolo : tablaSimbolos.entrySet() ) {
      if( func.equals( simbolo.getValue().alcance ) && simbolo.getValue().argumento == nArg ) {
        return simbolo.getValue().tipo;
      }
    }
    
    return -1;
  }
  
  public void comprobarTotalArgumentos(Token token, int totalArgs, int argsIngresados) {
    int diferencia = Math.abs( totalArgs - argsIngresados );
    if( argsIngresados < totalArgs ) {
      errores.add("Error en la línea " + token.beginLine + ", columna " + token.beginColumn + ":"
        + "\n\tFalta " + diferencia + " argumento(s).");
    } else if( argsIngresados > totalArgs ) {
      while( argsIngresados > totalArgs ) {
        errores.remove(errores.size() - 1);
        argsIngresados--;
      }
      errores.add("Error en la línea " + token.beginLine + ", columna " + token.beginColumn + ":"
        + "\n\tSobran " + diferencia + " argumento(s).");
    }
  }

  public void mostrarSimbolos() {
    System.out.println("Identificador | Tipo | Alcance | argumento");
    for (Map.Entry<String, Simbolo> simbolo : tablaSimbolos.entrySet()) {
      System.out.println(simbolo.getKey() + " | " + simbolo.getValue().tipo + " | " + simbolo.getValue().alcance + " | " + simbolo.getValue().argumento);
    }
  }
}