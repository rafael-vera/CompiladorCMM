/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

import java.util.Stack;

/**
 * Clase que define la creación y estructura
 * de la notacion Polaca (Prefija).
 * 
 * @author rafae
 */
public class NotacionPolaca {
  private final Stack<Termino> infija;
  private final Stack<Termino> prefija;
  
  public NotacionPolaca() {
    this.infija = new Stack();
    this.prefija = new Stack();
  }
  
  public void agregarTermino(Termino termino) {
    this.infija.add(termino);
  }
  
  public void mostrarInfija() {
    for(Termino termino : this.infija) {
      System.out.print(termino.getContenido());
      if(termino.getPrecedencia() == Termino.FUNCION) {
        System.out.print("(");
        for(NotacionPolaca parametro : termino.getParametros()) {
          parametro.mostrarInfija();
        }
        System.out.print(")");
      }
      System.out.print(" ");
    }
    System.out.println("");
  }
  
  public void mostrarPrefija() {
    Termino aux;
    for(int i = this.prefija.size()-1; i >= 0; i--) {
      aux = this.prefija.get(i);
      System.out.print(aux.getContenido());
      if(aux.getPrecedencia() == Termino.FUNCION) {
        System.out.print("(");
        for(NotacionPolaca parametro : aux.getParametros()) {
          parametro.mostrarPrefija();
        }
        System.out.print(")");
      }
      System.out.print(" ");
    }
  }
  
  public void crearPrefijo() {
    Stack<Termino> aux = new Stack();
    Termino ter;
    for(int i = this.infija.size()-1; i >= 0; i--) {
      ter = this.infija.get(i);
      
      switch(ter.getPrecedencia()) {
        case Termino.FUNCION -> {
          for(NotacionPolaca parametro : ter.getParametros()) {
            parametro.crearPrefijo();
          }
          this.prefija.add(ter);
        }
        case Termino.VALOR -> {
          this.prefija.add(ter);
        }
        default -> {
          while( !aux.empty() && ter.getPrecedencia() < aux.peek().getPrecedencia() ) {
            this.prefija.add(aux.pop());
          }
          aux.add(ter);
        }
      }
    }
    
    while(!aux.empty()) {
      this.prefija.add(aux.pop());
    }
  }
  
  public Stack<Termino> getPrefijo() {
    return this.prefija;
  }
}