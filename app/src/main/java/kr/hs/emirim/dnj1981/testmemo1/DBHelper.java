package kr.hs.emirim.dnj1981.testmemo1;

/*
 * DBHandler를 통해 데이터베이스를 제어할 수 있도록 도와주는 클래스
 * 이 클래스를 통해 사용 가능 데이터베이스 객체를 얻고 제어할 수 있으며
 * 데이터베이스가 최초를 열릴 때 테이블을 생성하고 업그레이드를 할 수 있
 * 도록 하는 함수
 */
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
	// DBHelper 셍성자 함수
	// 최초 오픈시 데이터베이스를 만듦
	public DBHelper( Context context) {
		super( context, "textmemo.db", null, 1);
	}

	// 데이터베이스가 최초 만들어질 때 호출되는 함수로
	// 테이블을 생성하는 기능을 수행함
	public void onCreate( SQLiteDatabase db ) {
		String table = "CREATE TABLE memo ( "
				+ "id INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ "writedate INGEGER, content TEXT )";
		db.execSQL(table);
	}

	// 테이블이 있으나 업그레이드가 필요할 때 호출되는 함수로
	// 기존 테이블을 제거한 후 새로 만들거나, 테이블을 수정하는 기능
	// 을 함
	public void onUpgrade( SQLiteDatabase db, int oldVersion, int newVersion ) {
		db.execSQL( "DROP TABLE IF EXISTS memo" );
		onCreate( db );
	}
}