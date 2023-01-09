/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author rafae
 */
public class CodigoObjeto {
  private final ArrayList<String> errores;
  private final CodigoIntermedio intermedio;
  private final HashMap<String, Simbolo> tablaSimbolos;
  private final HashMap<String, ArrayList<Instruccion>> instrucciones;
  
  private final StringBuilder dataSection;
  private final StringBuilder bssSection;
  private final StringBuilder textSection;
  
  private final StringBuilder codigoEnsamblador;
  
  public CodigoObjeto(ArrayList<String> errores, CodigoIntermedio intermedio, HashMap<String, Simbolo> tablaSimbolos) {
    this.errores = errores;
    this.intermedio = intermedio;
    this.tablaSimbolos = tablaSimbolos;
    this.instrucciones = intermedio.getInstrucciones();
    
    this.dataSection = new StringBuilder();
    this.bssSection = new StringBuilder();
    this.textSection = new StringBuilder();
    
    this.codigoEnsamblador = new StringBuilder();
    
    this.setCodigoEnsamblador();
  }
  
  private void setCodigoEnsamblador() {
    this.codigoEnsamblador
      .append("SYS_EXIT  equ 1\n")
      .append("SYS_READ  equ 3\n")
      .append("SYS_WRITE equ 4\n")
      .append("STDIN     equ 0\n")
      .append("STDOUT    equ 1\n")
      .append("\n");
    
    // Se inicia la seccion data
    this.dataSection
      .append("SECTION .data\n");
    
    // Se inicia la seccion bss
    this.bssSection
      .append("SECTION .bss\n");
    
    // Se inicia la seccion text
    this.textSection
      .append("SECTION .text\n")
      .append("\tGLOBAL _start\n")
      .append("_start:\n");
  }
  
  public void compilar(URL urlFile) {
    // Agrego la informacion a las secciones
    convertirIntermedioAEnsamblador();
    // Agrego la seccion data al codigo ensamblador
    this.codigoEnsamblador
      .append(
        this.dataSection
      );
    // Agrego la seccion bss al codigo ensamblador
    this.codigoEnsamblador
      .append(
        this.bssSection
      );
    // Agrego la seccion text al codigo ensamblador
    this.codigoEnsamblador
      .append(
        this.textSection
      );
    // Agrego las funciones ya establecidas
    this.builtInFunctions();
    // Mando a guardar el archivo .asm y lo compilo
    this.generarCodigoMaquina(urlFile);
    System.out.println(this.codigoEnsamblador);
  }
  
  private void convertirIntermedioAEnsamblador() {
    Set<String> variables = new HashSet(this.tablaSimbolos.keySet());
    // Se llena la seccion data (valores inicializados)
    this.dataSection
      .append("\ttrueVal: db \"true\",0\n")
      .append("\tfalseVal: db \"false\",0\n");
    // Se llena la seccion bss (variables)
    for(Map.Entry<String, Simbolo> variable : this.tablaSimbolos.entrySet()) {
      if(!variable.getValue().alcance.equals(AnalizadorSemantico.ALCANCE_FUNCION)) {
        this.bssSection
          .append("\t")
          .append(variable.getKey())
          .append(": resb 255\n");
      }
    }
    
    // Se busca que exista la funcion main
    if(this.instrucciones.containsKey(AnalizadorSemantico.ALCANCE_FUNCION+"_main:")) {
      this.textSection
        .append("\tcall    ")
        .append(AnalizadorSemantico.ALCANCE_FUNCION+"_main\n");
    }
    // Se agrega la salida del programa
    this.textSection
      .append("\tcall    quit\n\n");
    // Se agrega las funciones
    for(Map.Entry<String, ArrayList<Instruccion>> funcion : this.instrucciones.entrySet()) {
      this.textSection
        .append(funcion.getKey())
        .append("\n");
      
      for(Instruccion instruccion : funcion.getValue()) {
        switch(instruccion.getOperacion()) {
          case Instruccion.MOV -> {
            if(instruccion.getOperando1().startsWith("Temp")) {
              // Variable que indica si hay que agregar la variable Temp a .bss
              boolean agregarBSS = true;
              // Si mi segundo parametro no es una variable
              if(!variables.contains(instruccion.getOperando2())) {
                switch(instruccion.getOperando2().charAt(0)) {
                  case '"':
                  case '\'': {
                    this.dataSection
                      .append("\t")
                      .append(instruccion.getOperando1())
                      .append(": db ")
                      .append(instruccion.getOperando2())
                      .append(",0\n");
                    agregarBSS = false;
                    variables.add(instruccion.getOperando1());
                    break;
                  }
                  case 't': {
                    this.textSection
                      .append("\tmov     eax, trueVal\n")
                      .append("\tcall    slen\n")
                      .append("\tmov     ecx, eax\n")
                      .append("\tmov     esi, trueVal\n")
                      .append("\tmov     edi, ")
                      .append(instruccion.getOperando1())
                      .append("\n\tcld\n")
                      .append("\trep movsb\n")
                      .append("\tmov     eax, trueVal\n")
                      .append("\tcall    slen\n")
                      .append("\tmov     ebx, 0\n")
                      .append("\tmov     [")
                      .append(instruccion.getOperando1())
                      .append("+eax], ebx\n");
                    break;
                  }
                  case 'f': {
                    this.textSection
                      .append("\tmov     eax, falseVal\n")
                      .append("\tcall    slen\n")
                      .append("\tmov     ecx, eax\n")
                      .append("\tmov     esi, falseVal\n")
                      .append("\tmov     edi, ")
                      .append(instruccion.getOperando1())
                      .append("\n\tcld\n")
                      .append("\trep movsb\n")
                      .append("\tmov     eax, falseVal\n")
                      .append("\tcall    slen\n")
                      .append("\tmov     ebx, 0\n")
                      .append("\tmov     [")
                      .append(instruccion.getOperando1())
                      .append("+eax], ebx\n");
                    break;
                  }
                  default: // En caso de que sea un numero
                    this.textSection
                      .append("\tmov     eax, ")
                      .append(instruccion.getOperando2())
                      .append("\n")
                      .append("\tmov     [")
                      .append(instruccion.getOperando1())
                      .append("], eax\n");
                }
              } else {
                if(tablaSimbolos.get(instruccion.getOperando2()).tipo == AnalizadorSemantico.TIPO_INT) {
                  this.textSection
                    .append("\tmov     eax, [")
                    .append(instruccion.getOperando2())
                    .append("]\n")
                    .append("\tmov     [")
                    .append(instruccion.getOperando1())
                    .append("], eax\n");
                } else { // Char, string, boolean
                  this.textSection
                    .append("\tmov     eax, ")
                    .append(instruccion.getOperando2())
                    .append("\n\tcall    slen\n")
                    .append("\tmov     ecx, eax\n")
                    .append("\tmov     esi, ")
                    .append(instruccion.getOperando2())
                    .append("\n\tmov     edi, ")
                    .append(instruccion.getOperando1())
                    .append("\n\tcld\n")
                    .append("\trep movsb\n")
                    .append("\tmov     eax, ")
                    .append(instruccion.getOperando2())
                    .append("\n\tcall    slen\n")
                    .append("\tmov     ebx, 0\n")
                    .append("\tmov     [")
                    .append(instruccion.getOperando1())
                    .append("+eax], ebx\n");
                }
              }
              // Se verifica si se debe agregar a la section .bss
              if(agregarBSS) {
                // En caso de que no se haya declarado
                if(!variables.contains(instruccion.getOperando1())) {
                  // Se agrega a la section .bss
                  this.bssSection
                    .append("\t")
                    .append(instruccion.getOperando1())
                    .append(": resb 255\n");
                  // Se agrega a la lista de variables
                  variables.add(instruccion.getOperando1());
                }
              }
            } else {
              if(tablaSimbolos.get(instruccion.getOperando1()).tipo == AnalizadorSemantico.TIPO_INT) {
                this.textSection
                  .append("\tmov     eax, [")
                  .append(instruccion.getOperando2())
                  .append("]\n")
                  .append("\tmov     [")
                  .append(instruccion.getOperando1())
                  .append("], eax\n");
              } else { // String, char, boolean
                this.textSection
                  .append("\tmov     eax, ")
                  .append(instruccion.getOperando2())
                  .append("\n\tcall    slen\n")
                  .append("\tmov     ecx, eax\n")
                  .append("\tmov     esi, ")
                  .append(instruccion.getOperando2())
                  .append("\n\tmov     edi, ")
                  .append(instruccion.getOperando1())
                  .append("\n\tcld\n")
                  .append("\trep movsb\n")
                  .append("\tmov     eax, ")
                  .append(instruccion.getOperando2())
                  .append("\n\tcall    slen\n")
                  .append("\tmov     ebx, 0\n")
                  .append("\tmov     [")
                  .append(instruccion.getOperando1())
                  .append("+eax], ebx\n");
              }
            }
            break;
          }
          case Instruccion.ADD -> {
            this.textSection
              .append("\tmov     eax, [")
              .append(instruccion.getOperando1())
              .append("]\n")
              .append("\tmov     ebx, [")
              .append(instruccion.getOperando2())
              .append("]\n")
              .append("\tadd     eax, ebx\n")
              .append("\tmov     [")
              .append(instruccion.getOperando1())
              .append("], eax\n");
            break;
          }
          case Instruccion.SUB -> {
            this.textSection
              .append("\tmov     eax, [")
              .append(instruccion.getOperando1())
              .append("]\n")
              .append("\tmov     ebx, [")
              .append(instruccion.getOperando2())
              .append("]\n")
              .append("\tsub     eax, ebx\n")
              .append("\tmov     [")
              .append(instruccion.getOperando1())
              .append("], eax\n");
            break;
          }
          case Instruccion.MUL -> {
            this.textSection
              .append("\tmov     eax, [")
              .append(instruccion.getOperando1())
              .append("]\n")
              .append("\tmov     ebx, [")
              .append(instruccion.getOperando2())
              .append("]\n")
              .append("\tmul     ebx\n")
              .append("\tmov     [")
              .append(instruccion.getOperando1())
              .append("], eax\n");
            break;
          }
          case Instruccion.DIV -> {
            this.textSection
              .append("\tmov     eax, [")
              .append(instruccion.getOperando1())
              .append("]\n")
              .append("\tmov     ebx, [")
              .append(instruccion.getOperando2())
              .append("]\n")
              .append("\tdiv     ebx\n")
              .append("\tmov     [")
              .append(instruccion.getOperando1())
              .append("], eax\n");
            break;
          }
          case Instruccion.AND -> {
            int nTrue = ++Sentencia.nTrue;
            int nContinue = ++Sentencia.nContinue;
            // AND operando1, operando2
            // Convertir el operando1 a 1 o 0
            // Comparar el valor del operando1 con trueVal
            // Si son iguales asignar a eax = 1, si no, eax = 0
            this.textSection
              .append("\tmov     esi, ")
              .append(instruccion.getOperando1())
              .append("\n\tmov     edi, trueVal\n")
              .append("\tmov     eax, trueVal\n")
              .append("\tcall    slen\n")
              .append("\tmov     ecx, eax\n")
              .append("\tcld\n")
              .append("\trepe    cmpsb\n")
              .append("\tje      true")
              .append(nTrue)
              .append("\n\tmov     eax, 0\n")
              .append("\tjmp     continue")
              .append(nContinue)
              .append("\ntrue")
              .append(nTrue)
              .append(":\n\tmov eax, 1\n")
              .append("continue")
              .append(nContinue)
              .append(":\n\tpush    eax\n");
            // Convertir el operando2 a 1 o 0
            // Comparar el valor del operando2 con trueVal
            // Si son iguales asignar a ebx = 1, si no, ebx = 0
            nTrue++;
            nContinue++;
            this.textSection
              .append("\tmov     esi, ")
              .append(instruccion.getOperando2())
              .append("\n\tmov     edi, trueVal\n")
              .append("\tmov     eax, trueVal\n")
              .append("\tcall    slen\n")
              .append("\tmov     ecx, eax\n")
              .append("\tcld\n")
              .append("\trepe    cmpsb\n")
              .append("\tje      true")
              .append(nTrue)
              .append("\n\tmov     ebx, 0\n")
              .append("\tjmp     continue")
              .append(nContinue)
              .append("\ntrue")
              .append(nTrue)
              .append(":\n\tmov ebx, 1\n")
              .append("continue")
              .append(nContinue)
              .append(":\n\tpop     eax\n");
            // Comparar con AND eax, ebx
            // Comparar eax con 1
            // Si es igual asignar al operando1 el valor trueVal
            // Si no, asignar al operando1 el valor de falseVal
            nTrue++;
            nContinue++;
            this.textSection
              .append("\tand     eax, ebx\n")
              .append("\tcmp     eax, 1\n")
              .append("\tje      true")
              .append(nTrue)
              .append("\n\tmov     eax, falseVal\n")
              .append("\tcall    slen\n")
              .append("\tmov     ecx, eax\n")
              .append("\tmov     esi, falseVal\n")
              .append("\tmov     edi, ")
              .append(instruccion.getOperando1())
              .append("\n\tcld\n")
              .append("\trep movsb\n")
              .append("\tmov     eax, falseVal\n")
              .append("\tcall    slen\n")
              .append("\tmov     ebx, 0\n")
              .append("\tmov     [")
              .append(instruccion.getOperando1())
              .append("+eax], ebx\n")
              .append("\tjmp     continue")
              .append(nContinue)
              .append("\ntrue")
              .append(nTrue)
              .append(":\n\tmov     eax, trueVal\n")
              .append("\tcall    slen\n")
              .append("\tmov     ecx, eax\n")
              .append("\tmov     esi, trueVal\n")
              .append("\tmov     edi, ")
              .append(instruccion.getOperando1())
              .append("\n\tcld\n")
              .append("\trep movsb\n")
              .append("\tmov     eax, trueVal\n")
              .append("\tcall    slen\n")
              .append("\tmov     ebx, 0\n")
              .append("\tmov     [")
              .append(instruccion.getOperando1())
              .append("+eax], ebx\n")
              .append("continue")
              .append(nContinue)
              .append(":\n");
            break;
          }
          case Instruccion.OR -> {
            int nTrue = ++Sentencia.nTrue;
            int nContinue = ++Sentencia.nContinue;
            // OR operando1, operando2
            // Convertir el operando1 a 1 o 0
            // Comparar el valor del operando1 con trueVal
            // Si son iguales asignar a eax = 1, si no, eax = 0
            this.textSection
              .append("\tmov     esi, ")
              .append(instruccion.getOperando1())
              .append("\n\tmov     edi, trueVal\n")
              .append("\tmov     eax, trueVal\n")
              .append("\tcall    slen\n")
              .append("\tmov     ecx, eax\n")
              .append("\tcld\n")
              .append("\trepe    cmpsb\n")
              .append("\tje      true")
              .append(nTrue)
              .append("\n\tmov     eax, 0\n")
              .append("\tjmp     continue")
              .append(nContinue)
              .append("\ntrue")
              .append(nTrue)
              .append(":\n\tmov eax, 1\n")
              .append("continue")
              .append(nContinue)
              .append(":\n\tpush    eax\n");
            // Convertir el operando2 a 1 o 0
            // Comparar el valor del operando2 con trueVal
            // Si son iguales asignar a ebx = 1, si no, ebx = 0
            nTrue++;
            nContinue++;
            this.textSection
              .append("\tmov     esi, ")
              .append(instruccion.getOperando2())
              .append("\n\tmov     edi, trueVal\n")
              .append("\tmov     eax, trueVal\n")
              .append("\tcall    slen\n")
              .append("\tmov     ecx, eax\n")
              .append("\tcld\n")
              .append("\trepe    cmpsb\n")
              .append("\tje      true")
              .append(nTrue)
              .append("\n\tmov     ebx, 0\n")
              .append("\tjmp     continue")
              .append(nContinue)
              .append("\ntrue")
              .append(nTrue)
              .append(":\n\tmov ebx, 1\n")
              .append("continue")
              .append(nContinue)
              .append(":\n\tpop     eax\n");
            // Comparar con AND eax, ebx
            // Comparar eax con 1
            // Si es igual asignar al operando1 el valor trueVal
            // Si no, asignar al operando1 el valor de falseVal
            nTrue++;
            nContinue++;
            this.textSection
              .append("\tor      eax, ebx\n")
              .append("\tcmp     eax, 1\n")
              .append("\tje      true")
              .append(nTrue)
              .append("\n\tmov     eax, falseVal\n")
              .append("\tcall    slen\n")
              .append("\tmov     ecx, eax\n")
              .append("\tmov     esi, falseVal\n")
              .append("\tmov     edi, ")
              .append(instruccion.getOperando1())
              .append("\n\tcld\n")
              .append("\trep movsb\n")
              .append("\tmov     eax, falseVal\n")
              .append("\tcall    slen\n")
              .append("\tmov     ebx, 0\n")
              .append("\tmov     [")
              .append(instruccion.getOperando1())
              .append("+eax], ebx\n")
              .append("\tjmp     continue")
              .append(nContinue)
              .append("\ntrue")
              .append(nTrue)
              .append(":\n\tmov     eax, trueVal\n")
              .append("\tcall    slen\n")
              .append("\tmov     ecx, eax\n")
              .append("\tmov     esi, trueVal\n")
              .append("\tmov     edi, ")
              .append(instruccion.getOperando1())
              .append("\n\tcld\n")
              .append("\trep movsb\n")
              .append("\tmov     eax, trueVal\n")
              .append("\tcall    slen\n")
              .append("\tmov     ebx, 0\n")
              .append("\tmov     [")
              .append(instruccion.getOperando1())
              .append("+eax], ebx\n")
              .append("continue")
              .append(nContinue)
              .append(":\n");
            break;
          }
          case Instruccion.CMP -> {
            boolean numParam1, numParam2;
            String param1 = instruccion.getOperando1();
            String param2 = instruccion.getOperando2();
            
            if(this.tablaSimbolos.containsKey(instruccion.getOperando1())) {
              numParam1 = this.tablaSimbolos.get(instruccion.getOperando1()).tipo == AnalizadorSemantico.TIPO_INT;
              if(numParam1) {
                param1 = "["+param1+"]";
              }
            } else {
              // Checar si es valor numerico
              char c = instruccion.getOperando1().charAt(0);
              numParam1 = (c >= '0' && c <= '9');
              if(!numParam1) { // Si no es valor numerico
                if(c == 't' || c == 'f') { // Verifico si es valor booleano
                  param1 = (c == 't') ? "trueVal" : "falseVal";
                } else { // Si no, lo trato como cadena
                  // Creo una variable temporal en .data
                  int nTemp = ++Sentencia.nTemp;
                  this.dataSection
                    .append("\tTemp")
                    .append(nTemp)
                    .append(": db ")
                    .append(instruccion.getOperando1())
                    .append(",0\n");
                  // Asigno a param1 la referencia en memoria a la variable temporal
                  param1 = "Temp"+nTemp;
                }
              }
            }
            if(this.tablaSimbolos.containsKey(instruccion.getOperando2())) {
              numParam2 = this.tablaSimbolos.get(instruccion.getOperando2()).tipo == AnalizadorSemantico.TIPO_INT;
              if(numParam2) {
                param2 = "["+param2+"]";
              }
            } else {
              // Checar si es valor numerico
              char c = instruccion.getOperando1().charAt(0);
              numParam2 = (c >= '0' && c <= '9');
              if(!numParam2) { // Si no es valor numerico
                if(c == 't' || c == 'f') { // Verifico si es valor booleano
                  param2 = (c == 't') ? "trueVal" : "falseVal";
                } else { // Si no, lo trato como cadena
                  // Creo una variable temporal en .data
                  int nTemp = ++Sentencia.nTemp;
                  this.dataSection
                    .append("\tTemp")
                    .append(nTemp)
                    .append(": db ")
                    .append(instruccion.getOperando2())
                    .append(",0\n");
                  // Asigno a param1 la referencia en memoria a la variable temporal
                  param2 = "Temp"+nTemp;
                }
              }
            }
            
            this.textSection
                .append("\tmov     eax, ")
                .append(param1)
                .append("\n\tmov     ebx, ")
                .append(param2)
                .append("\n");
            
            if(numParam1 || numParam2) { // Compara entre dos valores numericos
              this.textSection
                .append("\tcmp     eax, ebx\n");
            } else { // Compara entre dos cadenas de caracteres
              this.textSection
                .append("\tmov     esi, eax\n")
                .append("\tmov     edi, ebx\n")
                .append("\tmov     eax, ebx\n")
                .append("\tcall    slen\n")
                .append("\tmov     ecx, eax\n")
                .append("\tcld\n")
                .append("\trepe    cmpsb\n");
            }
            break;
          }
          case Instruccion.CMPS -> {
            this.textSection
              .append("\tmov     esi, ")
              .append(instruccion.getOperando1())
              .append("\n\tmov     edi, ")
              .append(instruccion.getOperando2())
              .append("\n\tmov     eax, ")
              .append(instruccion.getOperando2())
              .append("\n\tcall    slen\n")
              .append("\tmov     ecx, eax\n")
              .append("\tcld\n")
              .append("\trepe    cmpsb\n");
            break;
          }
          case Instruccion.JE -> {
            this.textSection
                .append("\tje      ")
                .append(instruccion.getOperando1())
                .append(instruccion.getOperando2())
                .append("\n");
            break;
          }
          case Instruccion.JNE -> {
            this.textSection
              .append("\tjne     ")
              .append(instruccion.getOperando1())
              .append(instruccion.getOperando2())
              .append("\n");
            break;
          }
          case Instruccion.JG -> {
            this.textSection
              .append("\tjg      ")
              .append(instruccion.getOperando1())
              .append(instruccion.getOperando2())
              .append("\n");
            break;
          }
          case Instruccion.JGE -> {
            this.textSection
              .append("\tjge     ")
              .append(instruccion.getOperando1())
              .append(instruccion.getOperando2())
              .append("\n");
            break;
          }
          case Instruccion.JL -> {
            this.textSection
              .append("\tjl      ")
              .append(instruccion.getOperando1())
              .append(instruccion.getOperando2())
              .append("\n");
            break;
          }
          case Instruccion.JLE -> {
            this.textSection
              .append("\tjle     ")
              .append(instruccion.getOperando1())
              .append(instruccion.getOperando2())
              .append("\n");
            break;
          }
          case Instruccion.JMP -> {
            this.textSection
              .append("\tjmp     ")
              .append(instruccion.getOperando1())
              .append(instruccion.getOperando2())
              .append("\n");
            break;
          }
          case Instruccion.OUTPUT -> {
            int tipoDato = tablaSimbolos.get(instruccion.getOperando1()).tipo;
            String variable = instruccion.getOperando1();
            String funcImprime = "";
            switch(tipoDato) {
              case AnalizadorSemantico.TIPO_INT: {
                variable = "["+variable+"]";
                funcImprime = "iprintLF";
                break;
              }
              case AnalizadorSemantico.TIPO_FLOAT: {
                // crear funcion que imprime flotantes
                break;
              }
              case AnalizadorSemantico.TIPO_CHAR:
              case AnalizadorSemantico.TIPO_STRING:
              case AnalizadorSemantico.TIPO_BOOL: {
                funcImprime = "sprintLF";
                break;
              }
            }
            this.textSection
              .append("\tmov     eax, ")
              .append(variable)
              .append("\n\tcall    ")
              .append(funcImprime)
              .append("\n");
            break;
          }
          case Instruccion.INPUT -> {
            // Instrucciones para leer como caracter
            this.textSection
              .append("\tmov     edx, 255\n")
              .append("\tmov     ecx, ")
              .append(instruccion.getOperando1())
              .append("\n\tmov     ebx, STDIN\n")
              .append("\tmov     eax, SYS_READ\n")
              .append("\tint 0x80\n");
            // Se convierte a numero en caso de que ese sea su tipo de dato
            if(tablaSimbolos.get(instruccion.getOperando1()).tipo == AnalizadorSemantico.TIPO_INT) {
              this.textSection
                .append("\tmov     eax, ")
                .append(instruccion.getOperando1())
                .append("\n\tcall    atoi\n")
                .append("\tmov     [")
                .append(instruccion.getOperando1())
                .append("], eax\n");
            }
            break;
          }
          case Instruccion.CALL -> {
            this.textSection
              .append("\tcall    ")
              .append(instruccion.getOperando1())
              .append("\n");
            break;
          }
          case Instruccion.TRUE -> {
            this.textSection
              .append(instruccion.getOperacion())
              .append(instruccion.getOperando1())
              .append(":\n");
            break;
          }
          case Instruccion.FALSE -> {
            this.textSection
              .append(instruccion.getOperacion())
              .append(instruccion.getOperando1())
              .append(":\n");
            break;
          }
          case Instruccion.CONTINUE -> {
            this.textSection
              .append(instruccion.getOperacion())
              .append(instruccion.getOperando1())
              .append(":\n");
            break;
          }
          case Instruccion.CONDICION -> {
            this.textSection
              .append(instruccion.getOperacion())
              .append(instruccion.getOperando1())
              .append(":\n");
            break;
          }
          case Instruccion.ELSE -> {
            this.textSection
              .append(instruccion.getOperacion())
              .append(instruccion.getOperando1())
              .append(":\n");
            break;
          }
        }
      }
      // Al final se agrega el return de la funcion
      this.textSection
        .append("\tret\n\n");
    }
  }
  
  private void generarCodigoMaquina(URL urlFile) {
    Path path = Paths.get(urlFile.getPath());
    // Se obtiene el nombre del archivo .cmm a .asm
    String nombreArchivo = path.getFileName().toString();
    nombreArchivo = nombreArchivo.substring(0, nombreArchivo.lastIndexOf('.'));
    nombreArchivo = nombreArchivo+".asm";
    // Se obtiene la ruta donde se va a compilar
    String rutaArchivo = path.getParent().toString();
    
    // Guardo el codigo ensamblador en el archivo ensamblador
    File archivo = new File(rutaArchivo+File.separator+nombreArchivo);
    try (FileWriter writer = new FileWriter(archivo)) {
      writer.write(
        this.codigoEnsamblador.toString()
      );
    } catch (IOException ex) {
      this.errores.add("Error: "+ex.getMessage());
    }
    
    // Compilo el archivo ensamblador
    Process p;
    nombreArchivo = nombreArchivo.substring(0, nombreArchivo.lastIndexOf('.'));
    nombreArchivo = rutaArchivo+File.separator+nombreArchivo;
    try {
      // Comando para compilar el .asm y generar un .o
      p = Runtime.getRuntime()
        .exec("nasm -f elf "+archivo.getPath()+" -o "+nombreArchivo+".o");
      p.waitFor();
      p.destroy();
      
      // Comando para compilar el .o y generar un ejecutable
      p = Runtime.getRuntime()
        .exec("ld -m  elf_i386 -s -o "+nombreArchivo+" "+nombreArchivo+".o");
      p.waitFor();
      p.destroy();
    } catch(IOException | InterruptedException ex) {
      this.errores.add("Error: "+ex.getMessage());
    }
  }
  
  private void builtInFunctions() {
    // Funcion que convierte una cadena de caracteres a su correspondiente numerico
    this.codigoEnsamblador
      .append("atoi:\n")
      .append("\tpush    ebx\n")
      .append("\tpush    ecx\n")
      .append("\tpush    edx\n")
      .append("\tpush    esi\n")
      .append("\tmov     esi, eax\n")
      .append("\tmov     eax, 0\n")
      .append("\tmov     ecx, 0\n")
      .append("\n")
      .append(".multiplyLoop:\n")
      .append("\txor     ebx, ebx\n")
      .append("\tmov     bl, [esi+ecx]\n")
      .append("\tcmp     bl, 48\n")
      .append("\tjl      .finished\n")
      .append("\tcmp     bl, 57\n")
      .append("\tjg      .finished\n")
      .append("\n")
      .append("\tsub     bl, 48\n")
      .append("\tadd     eax, ebx\n")
      .append("\tmov     ebx, 10\n")
      .append("\tmul     ebx\n")
      .append("\tinc     ecx\n")
      .append("\tjmp     .multiplyLoop\n")
      .append("\n")
      .append(".finished:\n")
      .append("\tcmp     ecx, 0\n")
      .append("\tje      .restore\n")
      .append("\tmov     ebx, 10\n")
      .append("\tdiv     ebx\n")
      .append("\n")
      .append(".restore:\n")
      .append("\tpop     esi\n")
      .append("\tpop     edx\n")
      .append("\tpop     ecx\n")
      .append("\tpop     ebx\n")
      .append("\tret\n");
    // Funcion que imprime un valor numerico
    this.codigoEnsamblador
      .append("iprint:\n")
      .append("\tpush    eax\n")
      .append("\tpush    ecx\n")
      .append("\tpush    edx\n")
      .append("\tpush    esi\n")
      .append("\tmov     ecx, 0\n")
      .append("\n")
      .append("divideLoop:\n")
      .append("\tinc     ecx\n")
      .append("\tmov     edx, 0\n")
      .append("\tmov     esi, 10\n")
      .append("\tidiv    esi\n")
      .append("\tadd     edx, 48\n")
      .append("\tpush    edx\n")
      .append("\tcmp     eax, 0\n")
      .append("\tjnz     divideLoop\n")
      .append("\n")
      .append("printLoop:\n")
      .append("\tdec     ecx\n")
      .append("\tmov     eax, esp\n")
      .append("\tcall    sprint\n")
      .append("\tpop     eax\n")
      .append("\tcmp     ecx, 0\n")
      .append("\tjnz     printLoop\n")
      .append("\t\n")
      .append("\tpop     esi\n")
      .append("\tpop     edx\n")
      .append("\tpop     ecx\n")
      .append("\tpop     eax\n")
      .append("\tret\n");
    // Funcion que un valor numerico con salto de linea al final
    this.codigoEnsamblador
      .append("iprintLF:\n")
      .append("\tcall    iprint\n")
      .append("\t\n")
      .append("\tpush    eax\n")
      .append("\tmov     eax, 0xA\n")
      .append("\tpush    eax\n")
      .append("\tmov     eax, esp\n")
      .append("\tcall    sprint\n")
      .append("\tpop     eax\n")
      .append("\tpop     eax\n")
      .append("\tret\n");
    // Funcion que obtiene la longitud de una cadena
    this.codigoEnsamblador
      .append("slen:\n")
      .append("\tpush    ebx\n")
      .append("\tmov     ebx, eax\n")
      .append("\t\n")
      .append("nextchar:\n")
      .append("\tcmp     byte [eax], 0\n")
      .append("\tjz      finished\n")
      .append("\tinc     eax\n")
      .append("\tjmp     nextchar\n")
      .append("\t\n")
      .append("finished:\n")
      .append("\tsub     eax, ebx\n")
      .append("\tpop     ebx\n")
      .append("\tret\n");
    // Funcion que imprime una cadena de caracteres
    this.codigoEnsamblador
      .append("sprint:\n")
      .append("\tpush    edx\n")
      .append("\tpush    ecx\n")
      .append("\tpush    ebx\n")
      .append("\tpush    eax\n")
      .append("\tcall    slen\n")
      .append("\t\n")
      .append("\tmov     edx, eax\n")
      .append("\tpop     eax\n")
      .append("\t\n")
      .append("\tmov     ecx, eax\n")
      .append("\tmov     ebx, STDOUT\n")
      .append("\tmov     eax, SYS_WRITE\n")
      .append("\tint     0x80\n")
      .append("\t\n")
      .append("\tpop     ebx\n")
      .append("\tpop     ecx\n")
      .append("\tpop     edx\n")
      .append("\tret\n");
    // Funcion que imprime una cadena de caracteres con salto de linea al final
    this.codigoEnsamblador
      .append("sprintLF:\n")
      .append("\tcall    sprint\n")
      .append("\t\n")
      .append("\tpush    eax\n")
      .append("\tmov     eax, 0xA\n")
      .append("\tpush    eax\n")
      .append("\tmov     eax, esp\n")
      .append("\tcall    sprint\n")
      .append("\tpop     eax\n")
      .append("\tpop     eax\n")
      .append("\tret\n");
    // Funcion que termina la ejecucion del programa
    this.codigoEnsamblador
      .append("quit:\n")
      .append("\tmov     ebx, STDIN\n")
      .append("\tmov     eax, SYS_EXIT\n")
      .append("\tint     0x80\n")
      .append("\tret\n");
  }
}