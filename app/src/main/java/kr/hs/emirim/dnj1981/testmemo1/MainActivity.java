package kr.hs.emirim.dnj1981.testmemo1;

/*
 * 앱 실행시 처음에 표시되는 액티비티입니다.
 * 이 액티비티는 EditText를 이용하여 사용자의 입력을 받아 들이고
 * SQLite 데이터베이스를 이용하여  데이터를 삽입하거나 수정합니다.
 *
 *  각 버튼의 기능
 *  목록보기 :	목록을 표시하는 액티비티에 의해  실행된 경우
 *  			현재 액티비티를 종료합니다. 이 때 새로운 내용이
 *  			추가된 경우 호출한 엑티비티에 이를 알려 줍니다.
 *
 *  새로 입력 :	입력된 내용을 지우고 새로운 데이터를 입력할 수 있도록
 *  			합니다.
 *
 *  메모 등록 :	입력된 메모를 수정하거나 삽입합니다.
 *  			목록을 표시하는 엑티비티에 의해 수정할 레코드가 전달
 *  			되었으면 입력한 데이터를 데이터베이스에 수정하고
 *  			종료합니다.
 *  			새로운 레코드를 삽입하는 경우(edit_id가 0임) 입력된
 *  			내용을 데이터베이스에 삽입하고 입력란을 깨끗이 지웁니다.
 */

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener {
    // 데이터베이스 제어용 객체
    DBHandler handler;

    // 수정을 위한 edit_id, edit_position(목록에서의 항목 위치)
    // 지정을 위한 변수
    int edit_id = 0, edit_position = 0;

    // 목록표시 엑티비티에서 호출되었는지(isCalled),
    // 새로운 데이터가 삽입되었는지(isadded) 판단하기 위한 변수
    boolean isCalled = false, isadded = false;

    // 뷰 컨트를을 위한 객체용 변수
    Button resetBtn, listBtn;
    Button submitBtn;
    EditText memo;

    // 메모 내용을 저장하기 위한 객체용 변수
    MemoInfo memoinfo;

    // 호출한 엑티비티로 실행결과를 들려주기 위한 인텐트 객체의 변수
    Intent result;
    private static final int PICK_FROM_GALLERY = 2;
    private ImageView imgview;
    Bundle extras2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imgview = (ImageView) findViewById(R.id.imageView1);
        Button buttonGallery = (Button) findViewById(R.id.btn_select_gallery);

        // 데이터베이스 헨들러 객체 생성
        handler = new DBHandler( this );

        // 뷰 컨트롤 객체를 가져옴
        resetBtn = (Button)findViewById( R.id.resetBtn );
        resetBtn.setOnClickListener( this );
        submitBtn = (Button)findViewById( R.id.submitBtn );
        submitBtn.setOnClickListener( this );
        listBtn = (Button)findViewById( R.id.listBtn );
        listBtn.setOnClickListener( this );
        memo = (EditText)findViewById( R.id.memo );
        memo.setSelection(memo.getText().length());

        // 목록 표시용 엑티비티에서 전달된 값을 읽어 옴
        Intent intent = getIntent();
        edit_id = intent.getIntExtra( "id", 0 );
        edit_position = intent.getIntExtra("position",  -1 );
        isCalled = intent.getBooleanExtra("isCalled", false );

        // 전달된 값 중 edit_id와 edit_position 값이 유효한 값이면
        // edit_id에 해당하는 자료를 검색하여 가져옴
        if( edit_id > 0 && edit_position >= 0 ) {
            memoinfo = handler.select( edit_id );
            memo.setText( memoinfo.memo );
        }

        // 실행 결과 정보를 되돌리기 위한 인텐트 객체 생성
        result = new Intent();
        buttonGallery.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Intent intent = new Intent();
                // Gallery 호출
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                // 잘라내기 셋팅
                intent.putExtra("crop", "true");
                intent.putExtra("aspectX", 0);
                intent.putExtra("aspectY", 0);
                intent.putExtra("outputX", 200);
                intent.putExtra("outputY", 150);
                try {
                    intent.putExtra("return-data", true);
                    startActivityForResult(Intent.createChooser(intent,
                            "사진 선택"), PICK_FROM_GALLERY);
                } catch (ActivityNotFoundException e) {
                    // Do nothing for now
                }
            }
        });
    }

    protected void onActivityResult(int requestCode,int resultCode,Intent data) {

        try {
            if (requestCode == PICK_FROM_GALLERY) {
                Bundle extras2 = data.getExtras();
                if (extras2 != null) {
                    Bitmap photo = extras2.getParcelable("data");
                    imgview.setImageBitmap(photo);
                }/*else if(extras2==null){
                Intent intent = null;
                intent = new Intent(MainActivity.this, kr.hs.emirim.dnj1981.testmemo1.MainActivity.class);
                startActivity(intent);           }*/
            }
        }catch (Exception e){
            //finish();
            //this.finish();
            Intent intent=new Intent(this,MainActivity.class);
            startActivityForResult(intent,0);
            //setResult(resultCode, data);
            finish();

        }
    }
    @Override
    public void onClick(View v ) {
        // TODO Auto-generated method stub

        // 클릭된 뷰 컨트롤이 "새로 입력" 이면
        if( v == resetBtn ) {
            resetMemo();	// 입력 화면 초기화
            resetImg();
        }
        else if( v == submitBtn ) {	// 등록 버턴이 눌려진 경우

            // 내용이 입력되었는지 검사하고 내용이 입력되지 않았으면
            // 입력 안내 메시지 출력 후 함수 실행 종료
            if( !inputCheck() ) {
                Toast.makeText(this,  "내용을 입력하세요.", Toast.LENGTH_LONG).show();
                return;
            }

            // edit_id 가 0보다 크고 입력란의 내용과 원본과 내용이 다르면
            // 입력 내용으로 수정함
            if( edit_id > 0 &&  memo.getText().toString() != memoinfo.memo ) {
                if( handler.update( edit_id,  memo.getText().toString() ) == 0 ) Toast.makeText( this, "수정할 수 없어요!",  Toast.LENGTH_LONG).show();
                else {
                    // 수정 후 결과 정보를 result 인텐트에 등록하고
                    // setResult 함수를 이용하여 내용을 되돌린 후 종료
                    Toast.makeText( this, "수정 완료!",  Toast.LENGTH_LONG).show();
                    result.putExtra( "edit_id", edit_id );
                    result.putExtra( "edit_position", edit_position );
                    result.putExtra( "isadded", isadded );
                    setResult( RESULT_OK, result );
                    finish();
                }
            }
            else {
                // 그렇지 않으면 입력된 내용을 데이터베이스에 삽입홤
                if( handler.insert( memo.getText().toString() ) == 0 ) Toast.makeText( this, "업로드 할 수 없어요!",  Toast.LENGTH_LONG).show();
                else {
                    Toast.makeText( this, "생각 업로드 완료!",  Toast.LENGTH_LONG).show();
                    resetMemo();
                    resetImg();
                    isadded = true;
                }
            }
        }
        else if( v == listBtn ) {	// 목록보기 버튼인 경우
            if( !isCalled ) {
                // 목룍 표시 엑티비티로붙 호출된 것이 아니면
                // 인텐트를 이용하여 목록 표시용 엑티비티를 실행한 후
                // 종료
                Intent intent = new Intent( this, ListActivity.class );
                startActivity( intent );
                finish();
            }
            else {
                // 호출 경우 결과 정보를 초기화한  후(추가된 데이터가 있으면
                // isadded를 true로 설정) 되돌리고 종료함
                result.putExtra( "edit_id", 0 );
                result.putExtra( "edit_position", -1 );
                result.putExtra( "isadded", isadded );
                setResult( RESULT_OK, result );
                finish();
            }
        }
    }

    // 내용이 입력되었는지 검사하여 true(입력됨), false(입력되지 않음)
    // 을 되돌림
    protected boolean inputCheck() {
        if( memo.getText().toString().length() == 0 ) return false;
        else return true;
    }

    // 입력 내용 초기화
    private void resetMemo() {
        edit_id = 0;
        memo.setText( "" );
    }
    private void resetImg() {
        //imgview=null;
        imgview.setImageDrawable(null);
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        // 뒤로 버튼을 누른 경우
        // 목록 표시용 액티비티에서 호출된 경우  실행 결과 정보를 초기화한 후
        // isadded를 true로 설정) 되돌리고 종료함
        // 그렇지 않으면 액티비티 종료함
        if( isCalled ) {
            result.putExtra( "edit_id", 0 );
            result.putExtra( "edit_position", -1 );
            result.putExtra( "isadded", isadded );
            setResult( RESULT_OK, result );
        }
        finish();
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        // 엑티비티가 종료되면 handler 객체의 close 함수를 이용하여
        // 데이터베이스를 닫아 줌
        super.onDestroy();

        handler.close();
    }
}