package lk.ac.mrt.cse.dbs.simpleexpensemanager.DataBase;

import java.util.Date;
import java.util.ArrayList;
import java.util.List;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

public class DBHelper extends SQLiteOpenHelper {

    public static final int version = 1;
    public static final String name = "db";

    public static final String table_accounts = "accounts";
    public static final String table_transactions = "transactions";

    //common columns
//    private static final String PRIMARY_KEY = "id";
    public static final String acc_number = "accountNo";

    //accounts table columns
    public static final String bank_name = "bankName";
    public static final String acc_owner_name = "accountHolderName";
    public static final String balance = "balance";

    //transaction table columns
    public static final String trans_id = "id";
    public static final String type = "expenseType";
    public static final String amount = "amount";
    public static final String date = "Date";

    private static final String create_acc_table ="CREATE TABLE " + table_accounts +"(" + acc_number +" VARCHAR(20) PRIMARY KEY," + bank_name +" VARCHAR(20)," + acc_owner_name +" VARCHAR(100)," + balance +" NUMERIC(15,2)" +")";
    private static final String create_trans_table ="CREATE TABLE " + table_transactions +"(" + trans_id +" INTEGER PRIMARY KEY AUTOINCREMENT,"  + acc_number +" VARCHAR(20) REFERENCES " + table_accounts + "(" + acc_number + "),"  + type +" VARCHAR(20)," + amount +" NUMERIC(12,2)," + date +" TEXT" +")";
    public DBHelper(Context context, SQLiteDatabase.CursorFactory factory) {
        super(context, name,factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(create_acc_table);
        db.execSQL(create_trans_table);
    }

    @Override
    public void onUpgrade (SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + table_accounts);
        db.execSQL("DROP TABLE IF EXISTS "+ table_transactions);
        onCreate(db);
    }

    //Closing the database.
    public void closeDB() {
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null && db.isOpen())
            db.close();
    }

    //Getting transactions up to date.
    public List<Transaction> getTransactions() {
        List<Transaction> transactions = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + table_transactions;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery,     null);

        if (c.moveToFirst()) {
            do {
                Transaction td = new Transaction();
                td.setAccountNo(c.getString(c.getColumnIndex(acc_number)));
                td.setDate(new Date(c.getString(c.getColumnIndex(date))));
                td.setAmount(c.getDouble(c.getColumnIndex(amount)));
                td.setExpenseType(ExpenseType.valueOf(c.getString(c.getColumnIndex(type))));

                transactions.add(td);
            } while (c.moveToNext());
        }
        c.close();

        return transactions;
    }

    //Adding transactions to the database.
    public long add_transaction_to_DB(Transaction transaction){
        SQLiteDatabase db =  this.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put(acc_number, transaction.getAccountNo());
        values.put(date, transaction.getDate().toString());
        values.put(amount, transaction.getAmount());
        values.put(type, transaction.getExpenseType().toString());

        long transaction_id = db.insert(table_transactions, null, values);

        return transaction_id;
    }

    //Adding new accounts to acc-database.
    public long add_acc_to_DB(Account account) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(acc_number, account.getAccountNo());
        values.put(acc_owner_name, account.getAccountHolderName());
        values.put(balance, account.getBalance());
        values.put(bank_name, account.getBankName());

        // insert row
        long account_id = db.insert(table_accounts, null, values);

        return account_id;
    }

    //Deleting accounts from the database.
    public void delete_acc_from_DB(String accountNo){

        SQLiteDatabase db = this.getReadableDatabase();
        db.delete(table_accounts, acc_number +" = ?", new String[] {accountNo});
    }

    //Updating accounts in the database.
    public int update_acc_in_DB(Account account){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(balance, account.getBalance());

        // update rows
        return db.update(table_accounts, values, acc_number + " = ?",
                new String[] { account.getAccountNo() });
    }

    //Getting account details from the database.
    public List<Account> get_accounts_from_DB(){
        List<Account> accounts = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + table_accounts;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery,     null);

        if (c.moveToFirst()) {
            do {
                Account td = new Account();
                td.setAccountNo(c.getString(c.getColumnIndex(acc_number)));
                td.setBalance(c.getDouble(c.getColumnIndex(balance)));
                td.setAccountHolderName(c.getString(c.getColumnIndex(acc_owner_name)));
                td.setBankName(c.getString(c.getColumnIndex(bank_name)));

                accounts.add(td);
            } while (c.moveToNext());
        }
        c.close();
        return accounts;
    }

}