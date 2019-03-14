package andreyplatunov.myapplication2;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

public class BookManager {
    final static String LOG_TAG = "myLogs";


    public static String[] fillMyBook() {
        ArrayList<String> book = new ArrayList<>();

        // создаем объект для создания и управления версиями БД
        MainActivity.DBHelper dbHelper = MainActivity.getDbHelper();
        // подключаемся к БД
        SQLiteDatabase db = dbHelper.getWritableDatabase();


        Log.d(LOG_TAG, "--- Rows in mytable: ---");
        // делаем запрос всех данных из таблицы mytable, получаем Cursor
        Cursor c = db.query("mytable", null, null, null,
                null, null, null);

        // ставим позицию курсора на первую строку выборки
        // если в выборке нет строк, вернется false
        if (c.moveToFirst()) {

            // определяем номера столбцов по имени в выборке
            int idColIndex = c.getColumnIndex("id");
            int nameColIndex = c.getColumnIndex("name");
            int surnameColIndex = c.getColumnIndex("surname");
            int emailColIndex = c.getColumnIndex("email");
            int telColIndex = c.getColumnIndex("tel");

            do {
                // получаем значения по номерам столбцов и пишем все в лог
                Log.d(LOG_TAG,
                        "ID = " + c.getInt(idColIndex) + ", name = "
                                + c.getString(nameColIndex) + ", surname = "
                                + c.getString(surnameColIndex) + ", email = "
                                + c.getString(emailColIndex) + ", tel = "
                                + c.getString(telColIndex));
                // заполняем список
                book.add("ID:" + c.getInt(idColIndex) + "\t"
                        + c.getString(nameColIndex) + "\t" + c.getString(surnameColIndex)
                        + "\t\n\t"
                        + c.getString(emailColIndex) + "\t" + c.getString(telColIndex));

                // переход на следующую строку
                // а если следующей нет (текущая - последняя), то false -
                // выходим из цикла
            } while (c.moveToNext());
        } else
            Log.d(LOG_TAG, "0 rows");
        c.close();
        dbHelper.close();
        System.out.println(book);
        return book.toArray(new String[book.size()]);
    }

    public static String[] findConsumer(String[] namesDataBase, String name, String surname) {

        Log.d(LOG_TAG, "--- Find from mytable: ---");
        ArrayList<String> contactArr = new ArrayList<>();
        for (int i = 0; i < namesDataBase.length; i++) {
            try {
                String[] parts = namesDataBase[i].split("\t");
                if (name.isEmpty() && surname.equals(parts[2].trim())) {
                    contactArr.add(namesDataBase[i]);
                } else if (name.equals(parts[1].trim()) && surname.isEmpty()) {
                    contactArr.add(namesDataBase[i]);
                } else if (name.equals(parts[1]) && surname.equals(parts[2])) {
                    contactArr.add(namesDataBase[i]);
                }
            }catch (Exception e){e.getMessage();}
        }
        namesDataBase = contactArr.toArray(new String[contactArr.size()]);
        return namesDataBase;


    }
}
