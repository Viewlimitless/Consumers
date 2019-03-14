package andreyplatunov.myapplication2;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener {

    final String LOG_TAG = "myLogs";
    String[] namesDataBase;

    Button btnAdd, btnUpd, btnDel, btnBook, btnFind;
    EditText etName, etSurname, etEmail, etTel, etID;

    private static DBHelper dbHelper;






    /** Called when the activity is first created. */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnAdd = (Button) findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(this);

        btnUpd = (Button) findViewById(R.id.btnUpd);
        btnUpd.setOnClickListener(this);

        btnDel = (Button) findViewById(R.id.btnDel);
        btnDel.setOnClickListener(this);

        btnBook = (Button) findViewById(R.id.btnBook);
        btnBook.setOnClickListener(this);

        btnFind = (Button) findViewById(R.id.btnFind);
        btnFind.setOnClickListener(this);

        etName = (EditText) findViewById(R.id.etName);
        etSurname = (EditText)findViewById(R.id.etSurname);
        etEmail = (EditText) findViewById(R.id.etEmail);
        etTel = (EditText) findViewById(R.id.etTel);
        etID = (EditText) findViewById(R.id.etID);


        // создаем объект для создания и управления версиями БД
        dbHelper = new DBHelper(this);
        namesDataBase = BookManager.fillMyBook();
        rebuildBook();
    }
    public void rebuildBook() {

        // находим список
        ListView lvMain = (ListView) findViewById(R.id.lvMain);

        // создаем адаптер
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, namesDataBase);

        // присваиваем адаптер списку
        lvMain.setAdapter(adapter);

        lvMain.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(LOG_TAG, "item Click: position = " + position + ", id = " + id);
                try {
                    String[] contact = namesDataBase[position].split("\t");
                    etID.setText("");
                    etName.setText("");
                    etSurname.setText("");
                    etEmail.setText("");
                    etTel.setText("");
                    etID.setText(contact[0].replaceAll("ID:",""));
                    etName.setText(contact[1]);
                    etSurname.setText(contact[2]);
                    etEmail.setText(contact[4]);
                    etTel.setText(contact[5]);
                }catch (Exception e){
                    System.out.println(e.getMessage());}

            }
        });

    }

    public static DBHelper getDbHelper() {
        return dbHelper;
    }

    public void onClick(View v) {

        // создаем объект для данных
        ContentValues cv = new ContentValues();

        // получаем данные из полей ввода
        String name = etName.getText().toString();
        String surname = etSurname.getText().toString();
        String email = etEmail.getText().toString();
        String tel = etTel.getText().toString();
        String id = etID.getText().toString();

        // подключаемся к БД
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        switch (v.getId()) {
            case R.id.btnAdd:
                Log.d(LOG_TAG, "--- Insert in mytable: ---");
                // подготовим данные для вставки в виде пар: наименование столбца -
                // значение
                cv.put("name", name);
                cv.put("surname", surname);
                cv.put("email", email);
                cv.put("tel", tel);
                // вставляем запись и получаем ее ID
                long rowID = db.insert("mytable", null, cv);
                Log.d(LOG_TAG, "row inserted, ID = " + rowID);
                namesDataBase = BookManager.fillMyBook();
                rebuildBook();
                break;
            case R.id.btnUpd:
                if (id.equalsIgnoreCase("")) {
                    break;
                }
                Log.d(LOG_TAG, "--- Update mytable: ---");
                // подготовим значения для обновления
                cv.put("name", name);
                cv.put("surname", surname);
                cv.put("email", email);
                cv.put("tel", tel);
                // обновляем по id
                int updCount = db.update("mytable", cv, "id = ?",
                        new String[] { id });
                Log.d(LOG_TAG, "updated rows count = " + updCount);
                namesDataBase = BookManager.fillMyBook();
                rebuildBook();
                break;
            case R.id.btnDel:
                if (id.equalsIgnoreCase("")) {
                    break;
                }else createMyPinDialog();
                break;
            case R.id.btnBook:
                Intent intent = new Intent(this, PhoneBook.class);
                startActivity(intent);
                break;
            case R.id.btnFind:
                namesDataBase = BookManager.fillMyBook();
                if (name.isEmpty() && surname.isEmpty()) rebuildBook();
                else {
                    namesDataBase = BookManager.findConsumer(namesDataBase, name, surname);
                    rebuildBook();
                }break;
        }
        // закрываем подключение к БД
        dbHelper.close();
    }



    public void createMyPinDialog(){
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
        View mView = getLayoutInflater().inflate(R.layout.dialog_login, null);
        final EditText mPassword = (EditText) mView.findViewById(R.id.etPassword);
        Button mLogin = (Button) mView.findViewById(R.id.btnLogin);

            mBuilder.setView(mView);
            final AlertDialog dialog = mBuilder.create();
            dialog.show();
            mLogin.setOnClickListener(new View.OnClickListener() {


                @Override
                public void onClick(View view) {

                    if (mPassword.getText().toString().equals("1234")) {
                        Toast.makeText(MainActivity.this,
                                R.string.success_pin_msg,
                                Toast.LENGTH_SHORT).show();
                        String id = etID.getText().toString();
                        deliteFromMyTable(id);
                        dialog.dismiss();
                        rebuildBook();
                    } else {
                        Toast.makeText(MainActivity.this,
                                R.string.error_pin_msg,
                                Toast.LENGTH_SHORT).show();
                        if (!(mPassword.getText().toString()).isEmpty()) {
                            dialog.dismiss();
                        }
                    }
                }
            });

    }
    public void deliteFromMyTable(String id){

            SQLiteDatabase db = dbHelper.getWritableDatabase();
            Log.d(LOG_TAG, "--- Delete from mytable: ---");
            // удаляем по id
            int delCount = db.delete("mytable", "id = " + id, null);
            Log.d(LOG_TAG, "deleted rows count = " + delCount);
            namesDataBase = BookManager.fillMyBook();
            rebuildBook();
            db.close();


    }


    class DBHelper extends SQLiteOpenHelper {

        public DBHelper(Context context) {
            // конструктор суперкласса
            super(context, "myDB", null, 1);
        }

        public void onCreate(SQLiteDatabase db) {
            Log.d(LOG_TAG, "--- onCreate database ---");
            // создаем таблицу с полями
            db.execSQL("create table mytable ("
                    + "id integer primary key autoincrement,"
                    + "name text,"
                    + "surname text,"
                    + "email text,"
                    + "tel text"
                    + ");");
        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }




}