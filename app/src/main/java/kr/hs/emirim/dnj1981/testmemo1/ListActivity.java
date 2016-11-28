package kr.hs.emirim.dnj1981.testmemo1;

/**
 * 메모의 목록을 ListView에 표시해주는 엑티비티로
 * 데이터베이스에 저장된 메모를  ListView에 표시하고
 * 새로운 메모를 등록할 수 있도록 하는 기능을 함
 */

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

public class ListActivity extends Activity implements OnItemClickListener, OnClickListener {
	// 데이터베이스를 제어하기 위한 Handler 객체용 변수
	DBHandler handler;

	// 데이터베이스에 저장된 메모를 관리하기 위한 ArrayList 객체용 변수
	ArrayList<MemoInfo> memoList;

	// 메모리스트를 표시할  ListVew용  변수
	ListView view;

	// 리수트 뷰에 사용자가 지정한 레이아웃으로 표시하기 위한 ListAdapter를 위한
	// 변수
	ListAdapter listAdapter;

	// 매인 엑티비트를 실행하기 위한 requestCode용 상수
	private static final int EDIT_ACTIVITY = 1;

	// 새 메모를 작성하기 위한 버튼 객체를 위한 변수
	Button newMemoBtn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list);

		// 새 메모 버튼을 클릭했을 때 이벤트 리스너 등록
		newMemoBtn = (Button)findViewById( R.id.newMemoBtn );
		newMemoBtn.setOnClickListener( this );

		// 데이버에스  제어를 위한 Handler 객체 생성
		handler = new DBHandler( this );
		// 메모 목록을 관리하는 ArrayList 객체 생성
		memoList = new ArrayList<MemoInfo>();

		// ListView에 사용자가 지정한 레이아웃으로 표시할 수 있도록
		// ListAdapter 객체를 생성하여 ListView에 등록함
		listAdapter = new ListAdapter( this, memoList );
		view = (ListView)findViewById( R.id.list);
		view.setAdapter( listAdapter );
		// 리스트 아이템을 클릭했을 때 리스너 객체를 현재 객체로 등록함
		view.setOnItemClickListener( this );
		// 리스트  뷰를 2초 정도 누르고 있을 때 큰텐스트 메뉴가 표시되도록
		// 설정
		registerForContextMenu( view );

		// 데이터베이스로부터 메모를 읽어 목록에 등록하고 표시함
		displayMemoList();
	}

	protected void displayMemoList() {
		// handler 객체로부터 모든 메모를 읽어 옴
		Cursor cursor = handler.selectAll();
		// 읽어 들인 데이터가 없으면 함수 종료
		if( cursor == null ) return;
		//memoList에 저장된 목록을 모두 삭제함
		removeAllList();
		// 커서를 통해 읽어 온 데이터를 memoList에 추가함
		do {
			MemoInfo memo = new MemoInfo();
			memo.id = cursor.getInt( 0 );
			memo.writeDate = cursor.getLong( 1 );
			memo.memo = cursor.getString( 2 );
			memoList.add( memo );
		} while( cursor.moveToNext() );
		// 커서를 닫고 listAdapter 객체에게 알려 갱신된 정보가
		// 화면에 표시되도록 함
		cursor.close();
		listAdapter.notifyDataSetChanged();
	}

	// memoList에서 모든 목록을 삭제하는 함수
	protected void removeAllList() {
		memoList.removeAll( memoList );
	}

	// 데이터가 수정되었을 때 특정 위치의 목록을 삭제하고
	// 변경된 내용이 하면에 표시되도록 하는 함수
	protected void removeList( int position ) {
		MemoInfo memo = memoList.get( position );
		handler.delete( memo.id );
		memoList.remove( position );
		listAdapter.notifyDataSetChanged();
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
									ContextMenuInfo menuInfo) {
		// TODO Auto-generated method stub
		super.onCreateContextMenu(menu, v, menuInfo);

		// 리스트 아이템을 약 2초 정도 누르고 있을 경우 표시되는 컨텍스트
		// 메뉴를 읽어와 객체를 생성함
		getMenuInflater().inflate(R.menu.pop, menu );
		menu.setHeaderTitle( R.string.context_menu_title );
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		// 컨텍스트 메뉴 중 특정 메뉴가 선택되었을 때
		// 메뉴 정보를 읽어 옴
		AdapterContextMenuInfo info = (AdapterContextMenuInfo)item.getMenuInfo();

		if( info != null ) {
			// 메뉴 정보가 있으면
			// ListView의 어느 위치의 목록이 선택되었는지 저장함
			int position = info.position;
			if( item.getItemId() == R.id.view_content ) {
				// 메뉴의 ID가 view_content이면
				// 인텐트를 만들고 메모의 id, 목록의 position isCalled를
				// 설정하여 매인(편집용) 엑티비티를 실행함
				// 이 때 실행 결과를 받아 목록에 반영해야 하므로
				// startActivity가 아니라 startActivityForResult 함수를
				// 이용함
				Intent intent = new Intent( this, MainActivity.class );
				intent.putExtra("id", memoList.get( position ).id );
				intent.putExtra("position",  position );
				intent.putExtra( "isCalled", true );
				startActivityForResult( intent, EDIT_ACTIVITY );
			}
			// 메뉴의 ID가 remove_content(삭제)이면 해당 목록을 삭제함
			else if( item.getItemId() == R.id.remove_content ) removeList( position );
		}
		return super.onContextItemSelected(item);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		// 리스트 아이템을 클릭했을 때
		// 인텐트를 생성하여 메모 ID와 position, isCalled를 설정하여
		// 매인 액티비티를 실행함
		Intent intent = new Intent( this, MainActivity.class );
		intent.putExtra("id", memoList.get( arg2 ).id );
		intent.putExtra("position",  arg2 );
		intent.putExtra( "isCalled", true );
		startActivityForResult( intent, EDIT_ACTIVITY );
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		// 엑티비티가 종료되면 handler 객체의 close 함수를 이용하여
		// 데이터베이스를 닫음
		handler.close();
		super.onDestroy();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		int edit_id, edit_position;
		boolean added;

		super.onActivityResult(requestCode, resultCode, data);

		// 매인 액티비티에 의해 실행 결과 정보가 전달 된 경우
		// requestCode가 EDIT_ACTIVITY와 같은 경우
		if( requestCode == EDIT_ACTIVITY ) {
			// edit_id, edit_position isadded 등의 정보를 전달 반아
			edit_id = data.getIntExtra( "edit_id", 0 );
			edit_position = data.getIntExtra( "edit_position", -1 );
			added = data.getBooleanExtra("isadded",  false);
			// 유효한 edit_id와 edit_position이면
			// 메모를 읽어 memoList의 해당 position의 목록을 변경
			// 내용으로 수정함
			if( edit_id != 0 && edit_position >= 0 ) {
				MemoInfo edit_memo = handler.select( edit_id );
				memoList.set( edit_position,  edit_memo );
				listAdapter.notifyDataSetChanged();
			}
			else if( added ) {
				// 그렇지 않고 목록이 추가되었으면 목록 전체를 새로 읽어
				// 화면에 표시함
				displayMemoList();
			}
		}
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		// "새 목록" 버튼을 누른 경우 인텐트 객체를 생성하여
		// 매인 엑티비티를 실행함
		Intent intent = new Intent( this, MainActivity.class );
		intent.putExtra("id", 0);
		intent.putExtra("position",  -1 );
		intent.putExtra( "isCalled", true );
		startActivityForResult( intent, EDIT_ACTIVITY );
	}
}
