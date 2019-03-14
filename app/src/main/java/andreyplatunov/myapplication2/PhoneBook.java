package andreyplatunov.myapplication2;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;

public class PhoneBook extends MainActivity implements View.OnClickListener {

    String[] namesDataBase = BookManager.fillMyBook();
    EditText etName, etSurname;
    Button btnFind;
    final String LOG_TAG = "myLogs";
    int switchPosition;


    /**
     * Called when the activity is first created.
     */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_book);

        btnFind = (Button) findViewById(R.id.btnFindBook);
        btnFind.setOnClickListener(this);

        etName = (EditText) findViewById(R.id.etNameBook);
        etSurname = (EditText) findViewById(R.id.etSurnameBook);

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
                switchPosition = position;


            }
        });

    }

    @Override
    public void onClick(View v) {

        String name = etName.getText().toString().trim();
        String surname = etSurname.getText().toString().trim();

        switch (v.getId()) {
            case R.id.btnFindBook:
                namesDataBase = BookManager.fillMyBook();
                if (name.isEmpty() && surname.isEmpty()) rebuildBook();
                else {
                    namesDataBase = BookManager.findConsumer(namesDataBase, name, surname);
                    rebuildBook();
                }break;
        }


    }


}
