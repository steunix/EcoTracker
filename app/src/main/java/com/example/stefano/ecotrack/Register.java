package com.example.stefano.ecotrack;

        import android.content.Context;
        import android.database.Cursor;
        import android.database.sqlite.SQLiteDatabase;
        import android.database.sqlite.SQLiteException;
        import android.database.sqlite.SQLiteOpenHelper;
        import android.util.Log;

        import java.util.ArrayList;
        import java.util.Date;

/**
 * Class for the database
 */
public class Register extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;

    private SQLiteDatabase db;
    private Context context;

    public enum DB_SORT {
        SORT_DESCRIPTION,
        SORT_ID,
        SORT_USAGE,
        SORT_DATE,
        SORT_DATE_DESC
    }

    Register(Context ctx) {
        super(ctx, "ecotrack.db", null, DATABASE_VERSION);
        context = ctx;

        try {
            db = this.getWritableDatabase();
        } catch ( SQLiteException e ) {
            Log.d("error", e.getLocalizedMessage());
        }
    }

    public class ETExists extends Exception {
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table entities ( id integer primary key, description text, usage integer )");
        db.execSQL("create index entities_i1 on entities ( description )");

        db.execSQL("create table accounts ( id integer primary key, parent integer, type text, description text, usage integer )");
        db.execSQL("create index accounts_i1 on accounts ( description )");
        db.execSQL("create index accounts_i2 on accounts ( parent )");

        db.execSQL("create table register ( id integer primary key, date text, account integer, entity integer, amount real, description text )");
        db.execSQL("create index register_i1 on register ( date )");

        db.execSQL("create table usage ( account integer, entity number, usage number )");
        db.execSQL("create index usage_i1 on usage ( account )");
        db.execSQL("create index usage_i2 on usage ( entity )");

        // Default account and entity
        db.execSQL("insert into accounts ( parent, type, description, usage ) values ( 0, 'EXP', '" + context.getString(R.string.db_account_others) + "', 0)");
        db.execSQL("insert into entities ( description, usage ) values ( '" + context.getString(R.string.db_entity_others) + "', 0)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion ) {
    }

    public Account getAccount(Long id) {
        String sql = String.format("select id, parent, description, type from accounts where id=%d", id);
        Cursor cursor = db.rawQuery(sql, null);
        Account a = null;

        if (cursor.moveToFirst()) {
            a = new Account();
            a.id = cursor.getLong(0);
            a.parent = cursor.getLong(1);
            a.description = cursor.getString(2);
            a.type = cursor.getString(3);
        }

        cursor.close();
        return a;
    }

    public Entity getEntity(Long id) {
        String sql = String.format("select id, description from entities where id=%d", id);
        Cursor cursor = db.rawQuery(sql, null);
        Entity e = null;

        if (cursor.moveToFirst()) {
            e = new Entity();
            e.id = cursor.getLong(0);
            e.description = cursor.getString(1);
        }

        cursor.close();
        return e;
    }

    /**
     * Returns account list
     * @return Account list
     */
    public ArrayList<Account> getAccountsList(DB_SORT sort) {
        ArrayList<Account> list = new ArrayList<>();

        String sql = "select id, parent, description, type, usage from accounts ";
        switch ( sort ) {
            case SORT_DESCRIPTION:
                sql += "order by description, usage desc";
                break;
            case SORT_USAGE:
                sql += "order by usage desc, description";
                break;
            case SORT_ID:
                sql += "order by id";
                break;
        }

        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            do {
                Account a = new Account();
                a.id = cursor.getLong(0);
                a.parent = cursor.getLong(1);
                a.description = cursor.getString(2);
                a.type = cursor.getString(3);
                a.usage = cursor.getLong(4);
                list.add(a);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    /**
     * Returns account list
     * @return Account list
     */
    public ArrayList<Record> getRecordList(DB_SORT sort) {
        ArrayList<Record> list = new ArrayList<>();

        String sql = "select id, date, account, entity, amount, description from register ";
        switch ( sort ) {
            case SORT_DATE:
                sql += "order by date";
                break;
            case SORT_DATE_DESC:
                sql += "order by date desc";
                break;
        }

        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            do {
                Record r = new Record();
                r.id = cursor.getLong(0);
                r.date = Helper.isoToDate(cursor.getString(1));
                r.account = getAccount(cursor.getLong(2));
                r.entity = getEntity(cursor.getLong(3));
                r.amount = cursor.getFloat(4);
                r.description = cursor.getString(5);
                list.add(r);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public Record getRecord(Long id) {
        Record rec = null;

        String sql = String.format("select id, date, account, entity, amount, description from register where id=%d", id);
        Cursor cursor = db.rawQuery(sql, null);

        if (cursor.moveToFirst()) {
            rec = new Record();
            rec.id = cursor.getLong(0);
            rec.date = Helper.isoToDate(cursor.getString(1));
            rec.account = getAccount(cursor.getLong(2));
            rec.entity = getEntity(cursor.getLong(3));
            rec.amount = cursor.getFloat(4);
            rec.description = cursor.getString(5);
        }
        cursor.close();
        return rec;
    }

    public ArrayList<Record> getRecordList(Date from, Date to, DB_SORT sort) {
        String dtFrom = Helper.toIso(from);
        String dtTo = Helper.toIso(to);

        ArrayList<Record> list = new ArrayList<>();

        String sql = String.format("select id, date, account, entity, amount, description from register where date>='%s' and date<='%s' ", dtFrom, dtTo);
        switch ( sort ) {
            case SORT_DATE:
                sql += "order by date";
                break;
            case SORT_DATE_DESC:
                sql += "order by date desc";
                break;
        }

        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            do {
                Record r = new Record();
                r.id = cursor.getLong(0);
                r.date = Helper.isoToDate(cursor.getString(1));
                r.account = getAccount(cursor.getLong(2));
                r.entity = getEntity(cursor.getLong(3));
                r.amount = cursor.getFloat(4);
                r.description = cursor.getString(5);
                list.add(r);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    /**
     * Returns the entities list
     * @return Entities list
     */
    public ArrayList<Entity> getEntitiesList(DB_SORT sort) {
        ArrayList<Entity> list = new ArrayList<>();

        String sql = "select id, description, usage from entities ";
        switch ( sort ) {
            case SORT_DESCRIPTION:
                sql += "order by description, usage desc";
                break;
            case SORT_USAGE:
                sql += "order by usage desc, description";
                break;
            case SORT_ID:
                sql += "order by id";
                break;
        }

        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            do {
                Entity a = new Entity();
                a.id = cursor.getLong(0);
                a.description = cursor.getString(1);
                a.usage = cursor.getLong(2);
                list.add(a);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public float weekExpense(Date date) {
        String from = Helper.toIso(Helper.getWeekStart(date));
        String to   = Helper.toIso(Helper.getWeekEnd(date));
        float  sum  = 0;

        String sql = String.format(
                "select ifnull(sum(r.amount),0) from register r, accounts a where r.account = a.id and a.type = 'EXP' and r.date>='%s' and r.date<='%s'", from, to);

        Cursor cursor = db.rawQuery(sql,null);
        if (cursor.moveToFirst())
            sum = cursor.getFloat(0);

        cursor.close();
        return sum;
    }

    public float weekIncome(Date date) {
        String from = Helper.toIso(Helper.getWeekStart(date));
        String to   = Helper.toIso(Helper.getWeekEnd(date));
        float  sum  = 0;

        String sql = String.format(
                "select ifnull(sum(r.amount),0) from register r, accounts a where r.account = a.id and a.type = 'INC' and r.date>='%s' and r.date<='%s'", from, to);
        Cursor cursor = db.rawQuery(sql,null);
        if (cursor.moveToFirst())
            sum = cursor.getFloat(0);

        cursor.close();
        return sum;
    }

    public float dayExpense(Date date) {
        String sqlDate = Helper.toIso(date);
        float  sum  = 0;

        String sql = String.format(
                "select ifnull(sum(r.amount),0) from   register r, accounts a where  r.account = a.id and a.type = 'EXP' and r.date='%s'", sqlDate);

        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst())
            sum = cursor.getFloat(0);

        cursor.close();
        return sum;
    }

    public float dayIncome(Date date) {
        String sqlDate = Helper.toIso(date);
        float  sum  = 0;

        String sql = String.format(
                "select ifnull(sum(r.amount),0) from   register r, accounts a where  r.account = a.id and a.type = 'INC' and r.date='%s'", sqlDate);

        Cursor cursor = db.rawQuery(sql,null);
        if (cursor.moveToFirst())
            sum = cursor.getFloat(0);

        cursor.close();
        return sum;
    }

    /**
     * Returns the month expense
     * @param year Anno
     * @param month Mese
     * @return Month espense
     */
    public float monthExpense(int year, int month) {
        String m = String.format("%02d", month);
        String from = year+"-"+m+"-01";
        String to   = year+"-"+m+"-99";
        float  sum  = 0;

        String sql = String.format(
            "select ifnull(sum(r.amount),0) from register r, accounts a where r.account = a.id and a.type = 'EXP' and r.date>='%s' and r.date<='%s'", from, to);

        Cursor cursor = db.rawQuery(sql,null);
        if (cursor.moveToFirst())
            sum = cursor.getFloat(0);

        cursor.close();
        return sum;
    }

    /**
     * Returns month income
     * @param year Anno
     * @param month Mese
     * @return Month income
     */
    public float monthIncome(int year, int month) {
        String m = String.format("%02d", month);
        String from = year+"-"+m+"-01";
        String to   = year+"-"+m+"-99";
        float  sum  = 0;

        String sql = String.format(
                "select ifnull(sum(r.amount),0) from register r, accounts a where r.account = a.id and a.type = 'INC' and r.date>='%s' and r.date<='%s'", from, to);

        Cursor cursor = db.rawQuery(sql,null);
        if (cursor.moveToFirst())
            sum = cursor.getFloat(0);

        cursor.close();
        return sum;
    }

    public boolean deleteEntity (Entity entity) {
        try {
            db.execSQL("delete from register where entity=" + entity.id);
            db.execSQL("delete from usage where entity=" + entity.id);
            db.execSQL("delete from entities where id=" + entity.id);
        } catch ( Exception ex ) {
            return false;
        }
        return true;
    }

    public boolean saveRecord(Record record) {
        String sql;
        String sqlDate = Helper.toIso(record.date);
        String sqlAmount = record.amount.toString();
        String safedsc = Helper.sqlString(record.description);

        if ( record.id==null )
            sql = "insert into register (id,date,account,entity,amount,description) values "+
                    "(null, '"+sqlDate+"', "+record.account.id+", "+record.entity.id+", "+sqlAmount+",'"+safedsc+"')";
        else
            sql = "update register set date='"+sqlDate+"', account="+record.account.id+", entity="+record.entity.id+", "+
                    "amount="+sqlAmount+", description='"+safedsc+"' where id="+record.id;

        try {
            db.execSQL(sql);
        } catch ( Exception e ) {
            return false;
        }

        db.execSQL( "update accounts set usage = ifnull(usage,0)+1 where id="+record.account.id);
        db.execSQL( "update entities set usage = ifnull(usage,0)+1 where id="+record.entity.id);
        sql = "insert into usage ( account, entity, usage ) values ( "+record.account.id+", "+record.entity.id+",0 )";
        try {
            db.execSQL(sql);
        } catch ( Exception e ) {
        }

        sql = "update usage set usage=usage+1 where account="+record.account.id+" and entity="+record.entity.id;
        try {
            db.execSQL(sql);
        } catch ( Exception e ) {
        }

        return true;
    }

    public boolean deleteAccount(Account account) {
        try {
            db.execSQL("delete from register where account=" + account.id);
            db.execSQL("delete from usage where account=" + account.id);
            db.execSQL("delete from accounts where id=" + account.id);
            db.execSQL("update accounts set parent=0 where parent=" + account.id);
        } catch ( Exception ex ) {
            return false;
        }
        return true;
    }

    public boolean saveAccount(Account account) throws ETExists {
        String sql;
        String safedsc = Helper.sqlString(account.description);
        Long   cnt;

        Cursor cursor = db.rawQuery("select count(*) from accounts where description='"+safedsc+"'"+
                (account.id==null ? "" : " and id!="+account.id)
                , null);
        cursor.moveToFirst();
        cnt = cursor.getLong(0);

        cursor.close();

        if ( cnt>0 )
            throw new ETExists();

        cursor.close();

        if ( account.id==null )
            sql = String.format(
                    "insert into accounts ( id, parent, type, description, usage ) values ( null, %d, '%s', '%s',0)", account.parent, account.type, safedsc);
        else
            sql = String.format(
                    "update accounts set parent=%d, description='%s', type='%s' where id=%d", account.parent, safedsc, account.type, account.id);

        try {
            db.execSQL(sql);
        } catch ( Exception e ) {
            return false;
        }
        return true;
    }

    public boolean saveEntity(Entity entity) throws ETExists {
        String sql;
        String safedsc = Helper.sqlString(entity.description);
        Long   cnt;

        Cursor cursor = db.rawQuery("select count(*) from entities where description='"+safedsc+"'"+
                (entity.id==null ? "" : " and id!="+entity.id)
                , null);
        cursor.moveToFirst();
        cnt = cursor.getLong(0);

        cursor.close();

        if ( cnt>0 )
            throw new ETExists();

        if ( entity.id==null )
            sql = String.format("insert into entities ( id, description, usage ) values ( null, '%s', 0)", safedsc);
        else
            sql = String.format("update entities set description='%s' where id=%d", safedsc, entity.id);

        try {
            db.execSQL(sql);
        } catch ( Exception e ) {
            return false;
        }

        return true;
    }
}
