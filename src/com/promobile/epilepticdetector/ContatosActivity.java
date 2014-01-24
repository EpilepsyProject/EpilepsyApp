package com.promobile.epilepticdetector;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import br.com.promobile.epilepticdetector.banco.ContatoBanco;
import br.com.promobile.epilepticdetector.banco.ContatoDao;
import br.com.wcabralti.contatos.ContatosUtil;
import br.com.wcabralti.contatos.classes.Contato;

public class ContatosActivity extends Activity {

	static final int INTENT_CONTACT  = 1;
	static final int QUERY_CONTACT = 2;

	private ContatosUtil mContatos;
	private String id_contato;
	private TextView mNome;
	private TextView mTelefone;
	private TextView mEmail;
	private TextView mIM;
	private TextView mOrganizacao;
	private TextView mEndereco;
	private TextView mNota;
	private ImageView mFoto;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_contatos);

		mNome        = (TextView) findViewById(R.id.tvNome);
		mTelefone    = (TextView) findViewById(R.id.tvTelefone);
		mEmail       = (TextView) findViewById(R.id.tvEmail);
		mIM          = (TextView) findViewById(R.id.tvIM);
		mOrganizacao = (TextView) findViewById(R.id.tvOrganizacao);
		mEndereco    = (TextView) findViewById(R.id.tvEndereco);
		mNota        = (TextView) findViewById(R.id.tvNotas);
		mFoto        = (ImageView) findViewById(R.id.ivFoto);

		// Cria um objeto ContatosUtil passando
		// o ContentResolver
		mContatos = new ContatosUtil(getContentResolver());
		
		Button btnSalvarContato = (Button) findViewById(R.id.btnSalvarContato);
		btnSalvarContato.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				ContatoBanco  contatoBanco = new ContatoBanco();
				contatoBanco.setId_android(id_contato);
				contatoBanco.setNome(mNome.getText().toString());
				contatoBanco.setTelefone(mTelefone.getText().toString());
				
				ContatoDao dao = new ContatoDao(ContatosActivity.this);
				dao.inserir(contatoBanco);
				dao.close();
				Toast.makeText(getApplicationContext(), "Novo contato salvo!",
						Toast.LENGTH_SHORT).show();		
				
			}
		});
	} 

	public void contatosIntentClick(View v) {
		// Pega os contatos via Intent
		Intent it = mContatos.getContatoFromIntent();
		startActivityForResult(it, INTENT_CONTACT);
	}
	
	public void contatosQuery() {
		// Pega os contatos via Query
		//List<Contato> contatos = mContatos.getContatosFromQuery();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
		case (INTENT_CONTACT):
			if (resultCode == Activity.RESULT_OK) {

				// Recupera o retorna da Intent
				Uri contactData = data.getData();
				
				// Cria um Cursor para o contato selecionado
				Cursor c = getContentResolver().query(contactData, null, null,
						null, null);

				// Pega os contatos via Cursor
				List<Contato> contatos = mContatos.getContatosFromCursor(c);

				// Caso retorne mais de um contato
				// voc� pode criar uma estrurura de repeti��o
				if (contatos.size() > 0) {
					id_contato = contatos.get(0).id;
					mNome.setText(contatos.get(0).nome);
					
					// Tem telefone?
					if (contatos.get(0).telefones.size() > 0) {					
						mTelefone.setText(contatos.get(0).telefones.get(0).numero);
					}					
					// Tem Email?
					if (contatos.get(0).emails.size() > 0) {
						mEmail.setText(contatos.get(0).emails.get(0).endereco);	
					}					
					// Tem IM?
					if (contatos.get(0).ims.size() > 0) {
						mIM.setText(contatos.get(0).ims.get(0).nome);	
					}
					// Tem Organizacao?
					if (contatos.get(0).organizacoes.size() > 0) {
						mOrganizacao.setText(contatos.get(0).organizacoes.get(0).empresa);	
					}					
					// Tem Endereco?
					if (contatos.get(0).enderecos.size() > 0) {
						mEndereco.setText(
								contatos.get(0).enderecos.get(0).rua + " - " +
								contatos.get(0).enderecos.get(0).cidade + " - " +
								contatos.get(0).enderecos.get(0).estado
						);	
					}
					// Tem Nota?
					if (contatos.get(0).notas.size() > 0) {
						mNota.setText(contatos.get(0).notas.get(0));
					}					
					// Tem Foto?
					if (contatos.get(0).foto != null) {
						mFoto.setImageBitmap(contatos.get(0).foto);
					}
					
				}
			}		
			break;
		}
	}

}
