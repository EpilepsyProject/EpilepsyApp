package com.promobile.contatos;

import java.util.List;

public class Contato {    
  public String id;
  public String nome;
  public List<Telefone> telefones;
  private  String telefone;
  
  public void setNome(String nome) {
	this.nome = nome;
  }
  
  public void setTelefone(String telefone){
	this.telefone = telefone;
  }
  public String getTelefone() {
	return telefone;
}
 
  
  
}