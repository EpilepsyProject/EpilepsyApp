package br.com.wcabralti.contatos.classes;

import java.util.List;

import android.graphics.Bitmap;

public class Contato {
				
	public String id;
	public String nome;
	public List<Email> emails;
	public List<Telefone> telefones;
	public List<IM> ims;
	public List<Organizacao> organizacoes;
	public List<Endereco> enderecos;
	public List<String> notas;
	public Bitmap foto;
	
}
