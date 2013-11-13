package br.com.promobile.epilepticdetector.banco;
 
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ContatoDao extends SQLiteOpenHelper{
	private static final String TABELA = "tb_contato";
	private static final int VERSION = 1;
	private static String[] COLS = {"id", "id_android", "nome", "telefone", "status"};
	
	public ContatoDao(Context contexto){
		super(contexto, TABELA, null, VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		String sql = "CREATE TABLE " + TABELA + 
				"( id UNIQUEIDENTIFIER PRIMARY KEY," +
				" id_android TEXT," + 
				" nome TEXT," +
				" telefone TEXT," +
				" status TEXT " + 
				");";
		db.execSQL(sql);
	}
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersao, int newVersao) {
		db.execSQL("DROP TABLE IF EXISTS " + TABELA);
		this.onCreate(db);
	}
	
	public void inserir(ContatoBanco contato){
		ContentValues valores = new ContentValues();
		//valores.put("id", contato.getId());
		valores.put("id_android", contato.getId_android());
		valores.put("nome", contato.getNome());
		valores.put("telefone", contato.getTelefone());
		valores.put("status", contato.getStatus());
		getWritableDatabase().insert(TABELA, null, valores);
	}
	
	public List<ContatoBanco> getLista(){
		Cursor cursor = getWritableDatabase().query(TABELA, COLS, null, null, null, null, null);
		List<ContatoBanco> lista = new ArrayList<ContatoBanco>();
		while (cursor.moveToNext()) {
			ContatoBanco contato =  new ContatoBanco();
			contato.setId(cursor.getInt(0));
			contato.setId_android(cursor.getString(1));
			contato.setNome(cursor.getString(2));
			contato.setTelefone(cursor.getString(3));
			contato.setStatus(cursor.getString(4));
			lista.add(contato);
		}
		cursor.close();
		return lista;
	}
	
	public ContatoBanco getContatoById(int id){
		Cursor cursor = getWritableDatabase().query(TABELA, COLS, "id=?", new String[]{""+id}, null, null, null);
		cursor.moveToFirst();
		
		ContatoBanco contato = new ContatoBanco();
		contato.setId(cursor.getInt(0));
		contato.setId_android(cursor.getString(1));
		contato.setNome(cursor.getString(2));
		contato.setTelefone(cursor.getString(3));
		contato.setStatus(cursor.getString(4));
		
		cursor.close();
		return contato;
	}
	
	public void alterar(ContatoBanco contato){
		ContentValues valores = new ContentValues();
		//valores.put("id", contato.getId());
		valores.put("id_android", contato.getId_android());
		valores.put("nome", contato.getNome());
		valores.put("telefone", contato.getTelefone());
		valores.put("status", contato.getStatus());
		getWritableDatabase().update(TABELA, valores, "id=?", new String[]{""+contato.getId()});
	}
	
	public boolean isContato(String telefone){
		String sql = "SELECT telefone FROM "+ TABELA + "WHERE  telefone = "+ telefone;
		Cursor consulta = getWritableDatabase().rawQuery(sql, null);
		int cont = consulta.getCount();
		return cont > 0;
	}
}
