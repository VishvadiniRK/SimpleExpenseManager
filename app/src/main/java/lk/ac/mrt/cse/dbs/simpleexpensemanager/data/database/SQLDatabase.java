package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.database;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.Cursor;
import android.content.ContentValues;
import android.content.Context;
import android.support.annotation.Nullable;


// import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
public class SQLDatabase extends SQLiteOpenHelper {

        private static final String NAME = "200332X.db";
        private static final String ACCOUNT_TABLE = "account";
        private static final String TRANSACTION_TABLE = "transactions";
        private static final int LIMIT = 0;

        public SQLDatabase(@Nullable Context context) {
            super(context, NAME, null, 2);
        }

        //override the methods of onCreate and onUpgrade from SQLiteOpenHelper
        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            sqLiteDatabase.execSQL("CREATE TABLE "+ACCOUNT_TABLE+" (Account_no TEXT PRIMARY KEY ,"+
                    "Bank_name TEXT  ,"+ "AccountHolder_name TEXT, "+ "Account_balance REAL" +")");
            sqLiteDatabase.execSQL("CREATE TABLE "+TRANSACTION_TABLE+ " (Transaction_no INTEGER  PRIMARY KEY AUTOINCREMENT,"+
                    "Account_no TEXT  ,"+ "Date TEXT, "+ "ExpenseType TEXT ,"+ "Amount REAL" +")");
        }
        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
            // DROP TABLES IF EXISTS AND THE CREATE TABLES
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+ACCOUNT_TABLE);
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+TRANSACTION_TABLE);
            onCreate(sqLiteDatabase);
        }

        //A method to delete a data item    @deleteData
        public Integer deleteDataValue(String table_name, String attribute, String pk_val){
            SQLiteDatabase database = this.getWritableDatabase();
            return database.delete(table_name, attribute+" = ?", new String[] {pk_val});
        }

        //A method to delete all entries of a table
        public void deleteAllContent(String table_name){
            SQLiteDatabase database = this.getWritableDatabase();
            database.execSQL("delete from "+table_name);
        }

        //A method to input an entry
        public boolean inputData(String table_name,ContentValues content){
            SQLiteDatabase database = this.getWritableDatabase();
            long feedback_val;
            try{
                feedback_val = database.insertOrThrow(table_name, null,content);
            }
            catch(Exception e){
                feedback_val = -1;
                System.out.println("Data Insertion Error has Occurred");
            }
            if(feedback_val == -1){return false;}
            else{return true;}
        }

        //A method to update an entry
        public boolean updateEntry(String table_name,ContentValues content, String[ ] condition){
            SQLiteDatabase database = this.getWritableDatabase();
            String cond = condition[0]+" "+condition[1]+" ? ";
            String[] args = {condition[2]};

            long feedback_val;
            try{
                feedback_val = database.update(table_name, content,cond,args);
            }
            catch (Exception e){
                feedback_val = -1;
            }

            if(feedback_val == -1){return false;}
            else{return true;}
        }

        // A method to query data with limits
        public Cursor getFromQuerying(String table_name, String [] columns, String [][] conditions,int limit){
            SQLiteDatabase database = this.getWritableDatabase();

            String cols = "";
            if (columns.length != 0){
                for (int i = 0;i < columns.length ;i++){
                    cols += columns[i]+" , ";
                }
                cols = cols.substring(0,cols.length()-2);
            }
            String condition = "";
            String[] args = null;
            if(conditions.length != 0){
                args = new String[conditions.length];
                condition += " WHERE ";
                for (int i = 0;i < conditions.length ;i++){
                    if(conditions[i].length == 3){
                        String[] temp = conditions[i];
                        condition += temp[0] + " "+temp[1]+" ? AND ";
                        args[i] = temp[2];
                    }
                }
                condition = condition.substring(0,condition.length()-4);
            }else{
                condition = "";
            }
            String lim = "";
            if(limit != 0){
                lim = " LIMIT "+String.valueOf(limit);
            }

            String sql = "select "+cols+" from "+table_name+condition+lim;
            Cursor query_output = database.rawQuery(sql,args);
            return query_output;
        }

        public Cursor getData(String table_name, String [] attributes, String [][] conditions){
            return getFromQuerying(table_name, attributes, conditions,LIMIT);
        }
}
