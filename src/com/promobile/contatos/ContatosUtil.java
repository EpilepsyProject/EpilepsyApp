package com.promobile.contatos;

import java.util.ArrayList;
import java.util.List;

import com.promobile.contatos.Contato;
import com.promobile.contatos.Telefone;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.provider.ContactsContract;
/**
 * Classe reponsável por manipular a lista de contados dos dispositivo, com o objetivo de pegar
 *  um contato e salvar na aplicação. 
 * @author eribeiro
 *
 */
public class ContatosUtil {

  private ContentResolver mContentResolver;

  // Construtor da classe que recebe um ContentResolver
  public ContatosUtil(ContentResolver contentResolver) {
    mContentResolver = contentResolver;
  }

  // Chama a Intent nativa da agenda
  public Intent getContatoFromIntent() {
    return (new Intent(Intent.ACTION_PICK,
        ContactsContract.Contacts.CONTENT_URI));
  }

  // Pega os contatos via Query
  public List<Contato> getContatosFromQuery() {
  
    // Para melhorar o desempenho,
    // informamos apenas as colunas
    // que irão ser lidas.
    final String[] projection = { 
        ContactsContract.Contacts._ID, 
        ContactsContract.Contacts.DISPLAY_NAME   
    }; 
  
    // Consulta os contatos usando o ContentResolver
    Cursor c = mContentResolver.query(
        ContactsContract.Contacts.CONTENT_URI, 
        projection, null, null, null);
    
    // Lê o cursor e retorna uma lista de Contatos
    return getContatosFromCursor(c);
  }

  // Este método recebe um cursor e retorna uma lista 
  // de contatos
  public List<Contato> getContatosFromCursor(
      Cursor c) {

    List<Contato> contatos = new ArrayList<Contato>();

    // Percorre todo o cursor
    if (c.getCount() > 0) {
      while (c.moveToNext()) {
        Contato contato = new Contato();
    
        // Id 
        contato.id = c.getString(c.getColumnIndex(
            ContactsContract.Contacts._ID));
      
        // Nome
        contato.nome = c.getString(c.getColumnIndex(
            ContactsContract.Contacts.DISPLAY_NAME));
    
        // Telefones
        contato.telefones = getTelefones(
            contato.id);   
    
        contatos.add(contato);
      }
    }

    return contatos;
  }

  // Este método retona a lista de Telefones do contato
  public List<Telefone> getTelefones(String id) {
    List<Telefone> telefones = new ArrayList<Telefone>();
  
    // Colunas da consulta (otimização)
    final String[] projection = { 
        ContactsContract.CommonDataKinds.Phone.TYPE, 
        ContactsContract.CommonDataKinds.Phone.NUMBER   
    }; 

    // Consulta os telefones filtrando pelo Id do contato.
    Cursor c = mContentResolver.query(
        ContactsContract.CommonDataKinds.Phone.CONTENT_URI, 
        projection,
        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + 
        " = ?",
        new String[] { id }, null);

    while (c.moveToNext()) {
      Telefone telefone = new Telefone();
   
      telefone.tipo = c.getString(c.getColumnIndex(
          ContactsContract.CommonDataKinds.Phone.TYPE));
   
      telefone.numero = c.getString(c.getColumnIndex(
          ContactsContract.CommonDataKinds.Phone.NUMBER));

      telefones.add(telefone);

    }
    c.close();
    return (telefones);
  }
}