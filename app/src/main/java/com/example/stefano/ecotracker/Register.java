package com.example.stefano.ecotracker;

        import android.content.Context;
        import android.database.Cursor;
        import android.database.sqlite.SQLiteDatabase;
        import android.database.sqlite.SQLiteOpenHelper;

        import java.util.ArrayList;
        import java.util.Date;

/**
 * Class for the database
 */
public class Register extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 2;

    private SQLiteDatabase db;

    public enum DB_SORT {
        SORT_DESCRIPTION,
        SORT_ID,
        SORT_USAGE,
        SORT_DATE,
        SORT_DATE_DESC
    }

    Register(Context context) {
        super(context, "ecotracker.db", null, DATABASE_VERSION);

        db = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table entities ( id integer primary key, description text, usage integer )");
        db.execSQL("create index entities_i1 on entities ( description )");

        db.execSQL("create table accounts ( id integer primary key, parent integer, type text, description text, usage integer )");
        db.execSQL("create index accounts_i1 on accounts ( description )");
        db.execSQL("create index accounts_i2 on accounts ( parent )");

        db.execSQL("create table register ( id integer primary key, date text, account integer, entity integer, amount real )");
        db.execSQL("create index register_i1 on register ( date )");

        db.execSQL("create table usage ( account integer, entity number, usage number )");
        db.execSQL("create index usage_i1 on usage ( account )");
        db.execSQL("create index usage_i2 on usage ( entity )");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion ) {
        if ( newVersion==2 )
            db.execSQL("alter table register add column description text");
    }

    public Account getAccount(Long id) {
        Cursor cursor = db.rawQuery("select id, parent, description, type from accounts where id="+id, null);
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
        Cursor cursor = db.rawQuery("select id, description from entities where id='"+id+"'", null);
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

        String sql = "select id, date, account, entity, amount, description from register where id="+id;

        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            rec = new Record();
            rec.id = cursor.getLong(0);
            rec.date = Helper.isoToDate(cursor.getString(1));
            rec.account = getAccount(cursor.getLong(2));
            rec.entity = getEntity(cursor.getLong(3));
            rec.amount = cursor.getFloat(4);
            rec.description = cursor.getString(5);
            return rec;
        }
        cursor.close();
        return rec;
    }

    public ArrayList<Record> getRecordList(Date from, Date to, DB_SORT sort) {
        String dtFrom = Helper.toIso(from);
        String dtTo = Helper.toIso(to);

        ArrayList<Record> list = new ArrayList<>();

        String sql = "select id, date, account, entity, amount, description from register "+
                "where date>='"+dtFrom+"' and date<='"+dtTo+"' ";
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

        Cursor cursor = db.rawQuery(
                "select ifnull(sum(r.amount),0) "+
                        "from   register r, accounts a "+
                        "where  r.account = a.id "+
                        "and    a.type = 'EXP' "+
                        "and    r.date>='"+from+"' and r.date<='"+to+"'", null);

        if (cursor.moveToFirst())
            sum = cursor.getFloat(0);

        cursor.close();
        return sum;
    }

    public float weekIncome(Date date) {
        String from = Helper.toIso(Helper.getWeekStart(date));
        String to   = Helper.toIso(Helper.getWeekEnd(date));
        float  sum  = 0;

        Cursor cursor = db.rawQuery(
                "select ifnull(sum(r.amount),0) "+
                        "from   register r, accounts a "+
                        "where  r.account = a.id "+
                        "and    a.type = 'INC' "+
                        "and    r.date>='"+from+"' and r.date<='"+to+"'", null);

        if (cursor.moveToFirst())
            sum = cursor.getFloat(0);

        cursor.close();
        return sum;
    }

    public float dayExpense(Date date) {
        String sqlDate = Helper.toIso(date);
        float  sum  = 0;

        Cursor cursor = db.rawQuery(
                "select ifnull(sum(r.amount),0) "+
                        "from   register r, accounts a "+
                        "where  r.account = a.id "+
                        "and    a.type = 'EXP' "+
                        "and    r.date='"+sqlDate+"'", null);

        if (cursor.moveToFirst())
            sum = cursor.getFloat(0);

        cursor.close();
        return sum;
    }

    public float dayIncome(Date date) {
        String sqlDate = Helper.toIso(date);
        float  sum  = 0;

        Cursor cursor = db.rawQuery(
                "select ifnull(sum(r.amount),0) "+
                        "from   register r, accounts a "+
                        "where  r.account = a.id "+
                        "and    a.type = 'INC' "+
                        "and    r.date='"+sqlDate+"'", null);

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

        Cursor cursor = db.rawQuery(
            "select ifnull(sum(r.amount),0) "+
            "from   register r, accounts a "+
            "where  r.account = a.id "+
            "and    a.type = 'EXP' "+
            "and    r.date>='"+from+"' and r.date<='"+to+"'", null);

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

        Cursor cursor = db.rawQuery(
            "select ifnull(sum(r.amount),0) "+
            "from   register r, accounts a "+
            "where  r.account = a.id "+
            "and    a.type = 'INC' "+
            "and    r.date>='"+from+"' and r.date<='"+to+"'",null);

        if (cursor.moveToFirst())
            sum = cursor.getFloat(0);

        cursor.close();
        return sum;
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

    public boolean saveAccount(Account account) {
        String sql;
        String safedsc = Helper.sqlString(account.description);

        if ( account.id==null )
            sql = "insert into accounts ( id, parent, type, description, usage ) values ( null, "+account.parent+", '"+account.type+"', '"+safedsc+"',0)";
        else
            sql = "update accounts set parent="+account.parent+", description='"+safedsc+"', type='"+account.type+"' where id="+account.id;

        try {
            db.execSQL(sql);
        } catch ( Exception e ) {
            return false;
        }
        return true;
    }

    public boolean saveEntity(Entity entity) {
        String sql;
        String safedsc = Helper.sqlString(entity.description);

        if ( entity.id==null )
            sql = "insert into entities ( id, description, usage ) values ( null, '"+safedsc+"',0)";
        else
            sql = "update entities set description='"+safedsc+"' where id="+entity.id;

        try {
            db.execSQL(sql);
        } catch ( Exception e ) {
            return false;
        }

        return true;
    }
}
