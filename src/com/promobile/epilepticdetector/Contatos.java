
package com.promobile.epilepticdetector;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;
import br.com.wcabralti.contatos.classes.Contato;
import br.com.wcabralti.contatos.classes.Email;
import br.com.wcabralti.contatos.classes.Endereco;
import br.com.wcabralti.contatos.classes.IM;
import br.com.wcabralti.contatos.classes.Organizacao;
import br.com.wcabralti.contatos.classes.Telefone;

public class Contatos {

	private ContentResolver mContentResolver;

	// Construtor da classe que recebe um ContentResolver
	public Contatos(ContentResolver contentResolver) {
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
		// Para saber quais colunas podem ser lidas acesse
		final String[] projection = { 
				ContactsContract.Contacts._ID, 
				ContactsContract.Contacts.DISPLAY_NAME			
		}; 
		
		// Consulta os contatos usando o ContentResolver
		Cursor c = mContentResolver.query(
				ContactsContract.Contacts.CONTENT_URI, projection, null, null, null);
				
		// Lê o cursor e retorna uma lista de Contatos
		return getContatosFromCursor(c);
	}

	// Este método recebe um cursor e retorna uma lista 
	// de contatos
	public List<Contato> getContatosFromCursor(Cursor c) {

		List<Contato> contatos = new ArrayList<Contato>();

		// Percorre todo o cursor
		if (c.getCount() > 0) {
			while (c.moveToNext()) {
				Contato contato = new Contato();
				
				// Id 
				contato.id = c.getString(c
						.getColumnIndex(ContactsContract.Contacts._ID));
						
				// Nome
				contato.nome = c
						.getString(c
								.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
				
				// Telefones
				contato.telefones = getTelefones(contato.id);				
				// Emails
				contato.emails = getEmails(contato.id);
				// IMs
				contato.ims = getIMs(contato.id);
				// Organização
				contato.organizacoes = getOrganizacoes(contato.id);
				// Endereços
				contato.enderecos = getEnderecos(contato.id);
				// Notas
				contato.notas = getNotas(contato.id);
				// Foto
				contato.foto = getFoto(contato.id);
				
				contatos.add(contato);
			}
		}

		return contatos;
	}

	// Este método retona a lista de Telefones do contato
	// http://developer.android.com/reference/android/provider/ContactsContract.CommonDataKinds.Phone.html
	public List<Telefone> getTelefones(String id) {
		List<Telefone> telefones = new ArrayList<Telefone>();
		
		// Colunas da consulta (otimização)
		final String[] projection = { 
				ContactsContract.CommonDataKinds.Phone.TYPE, 
				ContactsContract.CommonDataKinds.Phone.NUMBER			
		}; 

		// Consulta os telefones filtrando pelo Id do contato.
		Cursor c = mContentResolver.query(
				ContactsContract.CommonDataKinds.Phone.CONTENT_URI, projection,
				ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
				new String[] { id }, null);

		while (c.moveToNext()) {
			Telefone telefone = new Telefone();
			
			telefone.tipo = c
				.getString(c
					.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
			
			telefone.numero = c
				.getString(c
					.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

			telefones.add(telefone);

		}
		c.close();
		return (telefones);
	}

	// Este método retona a lista de E-mails do contato
	public List<Email> getEmails(String id) {
		List<Email> emails = new ArrayList<Email>();
		
		// Colunas da consulta (otimização)
		final String[] projection = { 
				ContactsContract.CommonDataKinds.Email.TYPE, 
				ContactsContract.CommonDataKinds.Email.DATA			
		}; 

		// Consulta os E-mails filtrando pelo Id do contato.
		Cursor c = mContentResolver.query(
				ContactsContract.CommonDataKinds.Email.CONTENT_URI, projection,
				ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
				new String[] { id }, null);

		while (c.moveToNext()) {
			Email email = new Email();
			email.tipo = c
					.getString(c
							.getColumnIndex(ContactsContract.CommonDataKinds.Email.TYPE));
			email.endereco = c
					.getString(c
							.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
			emails.add(email);
		}
		c.close();
		return (emails);
	}
	
	// Este método retona a lista de IM (mensagens instantâneas) do contato
	public List<IM> getIMs(String id) {
 		List<IM> ims = new ArrayList<IM>();
 		
		// Colunas da consulta (otimização)
 		final String[] projection = { 
 				ContactsContract.CommonDataKinds.Im.DATA, 
 				ContactsContract.CommonDataKinds.Im.TYPE			
		}; 
 		
 		String where = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?"; 
 		String[] whereParameters = new String[]{id, 
 				ContactsContract.CommonDataKinds.Im.CONTENT_ITEM_TYPE}; 
 		
 		Cursor c = mContentResolver.query(ContactsContract.Data.CONTENT_URI, projection, where, whereParameters, null); 
 		while (c.moveToNext()) {
 			IM im = new IM(); 			
 			im.nome = c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Im.DATA));
 			im.tipo = c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Im.TYPE));
 			ims.add(im);
 		} 
 		c.close();
 		return(ims);
 	}	
	
	// Este método retona a lista de Organizações do contato
	public List<Organizacao> getOrganizacoes(String id) {
 		List<Organizacao> organizacoes = new ArrayList<Organizacao>();
 		
		// Colunas da consulta (otimização)
 		final String[] projection = { 
				ContactsContract.CommonDataKinds.Organization.COMPANY, 
				ContactsContract.CommonDataKinds.Organization.TITLE			
		}; 
 		
 		String where = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?"; 
 		String[] whereParameters = new String[]{id, 
 				ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE}; 
 		
 		Cursor c = mContentResolver.query(ContactsContract.Data.CONTENT_URI, projection, where, whereParameters, null); 
 		while (c.moveToNext()) {
 			Organizacao organizacao = new Organizacao(); 			
 			organizacao.empresa = c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Organization.DATA));
 			organizacao.titulo  = c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Organization.TITLE));
 			organizacoes.add(organizacao);
 		} 
 		c.close();
 		return(organizacoes);
 	}
	
	// Este método retona a lista de endereços do contato
	public List<Endereco> getEnderecos(String id) {
 		List<Endereco> enderecos = new ArrayList<Endereco>();
 		
		// Colunas da consulta (otimização)
 		final String[] projection = { 
 				ContactsContract.CommonDataKinds.StructuredPostal.POBOX, 
 				ContactsContract.CommonDataKinds.StructuredPostal.STREET,
 				ContactsContract.CommonDataKinds.StructuredPostal.CITY,
 				ContactsContract.CommonDataKinds.StructuredPostal.REGION,
 				ContactsContract.CommonDataKinds.StructuredPostal.POSTCODE,
 				ContactsContract.CommonDataKinds.StructuredPostal.COUNTRY,
 				ContactsContract.CommonDataKinds.StructuredPostal.TYPE
		}; 
 		
 		String where = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?"; 
 		String[] whereParameters = new String[]{id, 
 				ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE}; 
 		
 		Cursor c = mContentResolver.query(ContactsContract.Data.CONTENT_URI, projection, where, whereParameters, null); 
 		while (c.moveToNext()) {
 			Endereco endereco = new Endereco(); 			
 			endereco.poBox = c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.POBOX));
 			endereco.rua = c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.STREET));
 			endereco.cidade = c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.CITY));
 			endereco.estado = c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.REGION));
 			endereco.cep = c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.POSTCODE));
 			endereco.pais = c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.COUNTRY));
 			endereco.tipo = c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.TYPE));
 			enderecos.add(endereco);
 		} 
 		c.close();
 		return(enderecos);
 	}	
	
	// Este método retona a de notas do contato
	public List<String> getNotas(String id) {
 		List<String> notas = new ArrayList<String>();
 		
 		String where = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?"; 
 		String[] whereParameters = new String[]{id, 
 				ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE}; 
 		
 		Cursor c = mContentResolver.query(ContactsContract.Data.CONTENT_URI, null, where, whereParameters, null); 
 		while (c.moveToNext()) {
 			notas.add(c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Note.NOTE)));
 		} 
 		c.close();
 		return(notas);
 	}
	
			
	// Retorna a Foto do contato
	public Bitmap getFoto(String id) {
		Uri uri = ContentUris.withAppendedId(
				ContactsContract.Contacts.CONTENT_URI, Long.valueOf(id));

		InputStream input = ContactsContract.Contacts
				.openContactPhotoInputStream(mContentResolver, uri);

		if (input == null) {
			return null;
		}
		return BitmapFactory.decodeStream(input);
	}
}
