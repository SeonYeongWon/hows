package kr.hs.emirim.dnj1981.testmemo1;

/*
 * 데이터베이스 제어용 클래스
 * 이 클래스는 DBHelper를 통해 엑티비티에서 요청한 데이터 검색, 수정,
 * 삭제 등을 진행하는 기능을 함
 */
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import java.util.Calendar;

public class DBHandler {
	DBHelper helper;    // DBHelper 객체용 변수
	SQLiteDatabase db;    // 사용 가능한 SQLiteDatabase 객체용 변수
	Context context;    // 액티비티 객체를 위한 변수

	// 데이터베이스를 오픈하는 함수
	public void openDB() {
		helper = new DBHelper(context);
		db = helper.getWritableDatabase();
	}

	// 데이터베이스를 닫는 함수
	public void closeDB() {
		if (helper != null && db != null) {
			helper.close();
			helper = null;
			db = null;
		}
	}

	// DBHandler 생성자 함수 엑티비티 객체의 번지를 저장하고
	// 데이터베이스를 오픈하는 함수
	public DBHandler(Context context) {
		this.context = context;
		openDB();
	}

	// 클래스르보투 DBHandler 객체를 생성하는 함수
	public static DBHandler open(Context context) throws SQLException {
		DBHandler handler = new DBHandler(context);
		return handler;
	}

	// DBHandler 객체를 닫는 함수
	// 데이터베이스를 닫는 기능을 함
	public void close() {
		closeDB();
	}

	// 엑티비티에서 전달된 메모의 내용을 데이터베이스에 사입하는 함수
	public long insert(String memo) {
		ContentValues val = new ContentValues();
		// 현재 날짜를 천분이 1초 단위로 변환하여 writedate에 저장하고
		// 전달된 메모의 값을 content에 저장하여 삽입함
		Calendar cal = Calendar.getInstance();
		val.put("writedate", cal.getTimeInMillis());
		val.put("content", memo);

		return db.insert("memo", null, val);
	}

	// 전달받은 id와 memo를 이용하여 데이터베이스의 해당 자료를 수정하는
	// 함수
	public long update(int id, String memo) {
		ContentValues val = new ContentValues();
		Calendar cal = Calendar.getInstance();
		val.put("writedate", cal.getTimeInMillis());
		val.put("content", memo);
		return db.update("memo", val, "id = ?", new String[]{String.valueOf(id)});
	}

	// 데이터베이스에 저장된 모든 메모를 읽어 그 커서를 되돌려 줌
	public Cursor selectAll() throws SQLiteException {
		// 데이터베이스로부터 모든 데이터를 검색함
		Cursor cursor = db.query("memo",
				new String[]{"id", "writedate", "content"},
				null, null, null, null, null);
		// 커서가 null(없음)이면 null 값을 되돌림
		if (cursor == null) return null;

		// 커서로 부터 검색된 레코드 수가 0이면
		// 커서를 닫고 null값을 되돌림
		if (cursor.getCount() == 0) {
			cursor.close();
			return null;
		}

		// 검색된 데이터가 있으면 데이터 읽을 위치를 처음 레코드로 옮긴
		// 후 커서를 되돌림
		cursor.moveToFirst();
		return cursor;
	}

	// 해당 ID에 대한 자료를 검색하여 되돌려 주는 함스
	public MemoInfo select(int id) {
		// 해당 ID의 자료 검색
		Cursor cursor = db.query("memo",
				new String[]{"id", "writedate", "content"},
				"id = ?", new String[]{Integer.toString(id)}, null, null, null);
		if (cursor == null) return null;
		if (cursor.getCount() == 0) {
			cursor.close();
			return null;
		}

		// 검색된 데이터가 있으면 읽을 위치를 처음오로 올김 후
		// 데이터를 읽어 memoInfo 객체에 저장한 후 되돌려줌
		cursor.moveToFirst();
		MemoInfo memo = new MemoInfo();
		memo.id = cursor.getInt(0);
		memo.writeDate = cursor.getLong(1);
		memo.memo = cursor.getString(2);
		return memo;
	}

	// 해당 ID의 자료를 삭제하는 함수
	public long delete(int id) {
		return db.delete("memo", "id = ?", new String[]{String.valueOf(id)});
	}
}