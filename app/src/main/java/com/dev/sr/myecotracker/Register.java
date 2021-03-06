package com.dev.sr.myecotracker;

        import android.content.Context;
        import android.database.Cursor;
        import android.database.sqlite.SQLiteDatabase;
        import android.database.sqlite.SQLiteException;
        import android.database.sqlite.SQLiteOpenHelper;
        import android.location.Location;
        import android.os.Environment;
        import android.util.Log;
        import android.widget.Toast;

        import java.io.File;
        import java.io.FileInputStream;
        import java.io.FileOutputStream;
        import java.nio.channels.FileChannel;
        import java.util.ArrayList;
        import java.util.Date;

/**
 * Class for the database
 */
public class Register extends SQLiteOpenHelper {

    private static Register sInstance;

    private static final int DATABASE_VERSION = 3;

    private SQLiteDatabase db;
    private Context context;

    public enum DB_SORT {
        SORT_DESCRIPTION,
        SORT_ID,
        SORT_USAGE,
        SORT_DATE,
        SORT_DATE_DESC,
        SORT_USAGE_COMBINED
    }

    public static synchronized Register getInstance(Context context) {
        if ( sInstance==null )
            sInstance = new Register(context);
        return sInstance;
    }

    private Register(Context ctx) {
        super(ctx, "myecotracker.db", null, DATABASE_VERSION);
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

        db.execSQL("create table register ( id integer primary key, date text, account integer, entity integer, amount real, description text, latitude real, longitude real )");
        db.execSQL("create index register_i1 on register ( date )");
        db.execSQL("create index register_i2 on register ( account )");
        db.execSQL("create index register_i3 on register ( entity )");

        db.execSQL("create table usage ( account integer, entity number, usage number )");
        db.execSQL("create index usage_i1 on usage ( account )");
        db.execSQL("create index usage_i2 on usage ( entity )");

        db.execSQL("create table categories ( id integer primary key, description text )");
        db.execSQL("create table account_categories ( account integer, category integer )");
        db.execSQL("create index account_categories_i1 on account_categories ( account )");
        db.execSQL("create index account_categories_i2 on account_categories ( category )");

        // Default account and entity
        db.execSQL("insert into accounts ( id, parent, type, description, usage ) values ( 0, null, 'EXP', '" + context.getString(R.string.db_account_others) + "', 0)");
        db.execSQL("insert into entities ( id, description, usage ) values ( 0, '" + context.getString(R.string.db_entity_others) + "', 0)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion ) {
        if ( oldVersion<2 ) {
            db.execSQL("create table categories ( id integer primary key, description text )");
            db.execSQL("create table account_categories ( account integer, category integer )");
            db.execSQL("create index account_categories_i1 on account_categories ( account )");
            db.execSQL("create index account_categories_i2 on account_categories ( category )");
        }

        if ( oldVersion<3 ) {
            db.execSQL("alter table register add column latitude real");
            db.execSQL("alter table register add column longitude real");
        }
    }

    public boolean execSQL(String sql) throws Exception {
        try {
            db.rawQuery(sql, null);
        } catch ( Exception e ) {
            throw ( e );
        }
        return true;
    }

    // Categories
    public Category getCategory(String description) {
        String safedsc = Helper.sqlString(description);
        String sql = String.format("select id, description from categories where description='%s'", safedsc);
        Cursor cursor = db.rawQuery(sql, null);
        Category c = null;

        if (cursor.moveToFirst()) {
            c = new Category();
            c.id = cursor.getLong(0);
            c.description = cursor.getString(1);
        }

        cursor.close();
        return c;
    }

    public Category getCategory(Long id) {
        String sql = String.format("select id, description from categories where id=%d", id);
        Cursor cursor = db.rawQuery(sql, null);
        Category c = null;

        if (cursor.moveToFirst()) {
            c = new Category();
            c.id = cursor.getLong(0);
            c.description = cursor.getString(1);
        }

        cursor.close();
        return c;
    }

    public boolean deleteCategory (Category category) {
        try {
            db.execSQL("delete from account_categories where category=" + category.id);
            db.execSQL("delete from categories where id=" + category.id);
        } catch ( Exception ex ) {
            return false;
        }
        return true;
    }

    public ArrayList<Category> getAccountCategories ( Account account ) {
        ArrayList<Category> cat = new ArrayList<>();

        String sql = String.format("select c.id, c.description from categories c, account_categories a where a.category=c.id and a.account=%d order by c.description",
                account.id);
        Cursor cursor = db.rawQuery(sql, null);

        if (cursor.moveToFirst()) {
            do {
                Category c = new Category();
                c.id = cursor.getLong(0);
                c.description = cursor.getString(1);

                cat.add(c);
            } while ( cursor.moveToNext() );
        }

        cursor.close();
        return cat;
    }

    public ArrayList<CategorySelect> getAccountCategoriesSelect( Account account ) {
        ArrayList<CategorySelect> cat = new ArrayList<>();

        String sql = String.format(
                "select c.id, c.description, (select ifnull(count(*),0) from account_categories where category=c.id and account=%d) sel from categories c order by sel desc, c.description",
                (account.id == null ? -1 : account.id));
        Cursor cursor = db.rawQuery(sql, null);

        if (cursor.moveToFirst()) {
            do {
                CategorySelect c = new CategorySelect();
                c.category = new Category();
                c.category.id = cursor.getLong(0);
                c.category.description = cursor.getString(1);
                c.selected = (cursor.getLong(2) > 0);
                cat.add(c);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return cat;
    }

    public ArrayList<Category> getCategoriesList() {
        ArrayList<Category> list = new ArrayList<>();

        String sql = "select id, description from categories order by description ";

        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            do {
                Category a = new Category();
                a.id = cursor.getLong(0);
                a.description = cursor.getString(1);
                list.add(a);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public boolean saveCategory(Category category) throws ETExists {
        String sql;
        String safedsc = Helper.sqlString(category.description);
        Long   cnt;

        Cursor cursor = db.rawQuery("select count(*) from categories where description='"+safedsc+"'"+
                (category.id==null ? "" : " and id!="+category.id)
                , null);
        cursor.moveToFirst();
        cnt = cursor.getLong(0);

        cursor.close();

        if ( cnt>0 )
            throw new ETExists();

        if ( category.id==null )
            sql = String.format("insert into categories ( id, description ) values ( null, '%s')", safedsc);
        else
            sql = String.format("update categories set description='%s' where id=%d", safedsc, category.id);

        try {
            db.execSQL(sql);
        } catch ( Exception e ) {
            return false;
        }

        return true;
    }

    // Accounts
    public Long getNextAccountId() {
        String sql = String.format("select ifnull(max(id),0)+1 from accounts");
        Cursor cursor = db.rawQuery(sql, null);
        long next;

        if (cursor.moveToFirst())
            next = cursor.getLong(0);
        else
            next = 0;

        cursor.close();
        return next;
    }

    public Account getAccount(Long id) {
        String sql = String.format("select id, ifnull(parent,-1), description, type from accounts where id=%d", id);
        Cursor cursor = db.rawQuery(sql, null);
        Account a = null;

        if (cursor.moveToFirst()) {
            a = new Account();
            a.id = cursor.getLong(0);
            a.description = cursor.getString(2);
            a.type = cursor.getString(3);
            a.categories = getAccountCategories(a);
        }

        cursor.close();
        return a;
    }

    public Account getAccount(String description) {
        String safedsc = Helper.sqlString(description);
        String sql = String.format("select id, ifnull(parent,-1), description, type from accounts where description='%s'", safedsc);
        Cursor cursor = db.rawQuery(sql, null);
        Account a = null;

        if (cursor.moveToFirst()) {
            a = new Account();
            a.id = cursor.getLong(0);
            a.description = cursor.getString(2);
            a.type = cursor.getString(3);
            a.categories = getAccountCategories(a);
        }

        cursor.close();
        return a;
    }

    public ArrayList<Account> getAccountsList(DB_SORT sort) {
        ArrayList<Account> list = new ArrayList<>();

        String sql = "select id, ifnull(parent,-1), description, type, usage from accounts ";
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
                a.description = cursor.getString(2);
                a.type = cursor.getString(3);
                a.usage = cursor.getLong(4);
                a.categories = getAccountCategories(a);
                list.add(a);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
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

    public Record getRecord(Long id) {
        Record rec = null;

        String sql = String.format("select id, date, account, entity, amount, description, ifnull(latitude,10000), ifnull(longitude,10000) from register where id=%d", id);
        Cursor cursor = db.rawQuery(sql, null);

        if (cursor.moveToFirst()) {
            rec = new Record();
            rec.id = cursor.getLong(0);
            rec.date = Helper.isoToDate(cursor.getString(1));
            rec.account = getAccount(cursor.getLong(2));
            rec.entity = getEntity(cursor.getLong(3));
            rec.amount = cursor.getFloat(4);
            rec.description = cursor.getString(5);

            if ( cursor.getFloat(6)!=10000 && cursor.getFloat(7)!=10000 ) {
                Location location = new Location("");
                location.setLatitude(cursor.getFloat(6));
                location.setLongitude(cursor.getFloat(7));
                rec.location = location;
            } else {
                rec.location = null;
            }
        }
        cursor.close();
        return rec;
    }

    public ArrayList<Record> getRecordList(Date from, Date to, DB_SORT sort) {
        Account a = new Account();
        a.id = null;
        Entity e = new Entity();
        e.id = null;
        return getRecordList(a, e, from, to, null, null, sort);
    }

    public ArrayList<Record> getRecordList(Account account, Entity entity, Date dateFrom, Date dateTo, Float amtFrom, Float amtTo, DB_SORT sort) {
        ArrayList<Record> list = new ArrayList<>();

        String sql = String.format("select id, date, account, entity, amount, description, ifnull(latitude,10000), ifnull(longitude,10000) from register where 1=1 ");

        if ( account.id != null ) {
            sql += " and account="+account.id+" ";
        }

        if ( entity.id != null ) {
            sql += " and entity="+entity.id+" ";
        }

        if ( dateFrom!= null ) {
            String dtFrom = Helper.toIso(dateFrom);
            sql += " and date>='"+dtFrom+"' ";
        }

        if ( dateTo!= null ) {
            String dtTo = Helper.toIso(dateTo);
            sql += " and date<='"+dtTo+"' ";
        }

        if ( amtFrom != null ) {
            sql += String.format(" and amount>="+Helper.sqlFloat(amtFrom)+" " );
        }

        if ( amtTo != null ) {
            sql += String.format(" and amount<="+Helper.sqlFloat(amtTo)+" ");
        }

        switch ( sort ) {
            case SORT_DATE:
                sql += " order by date";
                break;
            case SORT_DATE_DESC:
                sql += " order by date desc";
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

                if ( cursor.getFloat(6)!=10000 && cursor.getFloat(7)!=10000 ) {
                    Location location = new Location("");
                    location.setLatitude(cursor.getFloat(6));
                    location.setLongitude(cursor.getFloat(7));
                    r.location = location;
                } else {
                    r.location = null;
                }

                list.add(r);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public ArrayList<AccountBreakdown> getAccountBreakdown(Date from, Date to) {
        String dtFrom = Helper.toIso(from);
        String dtTo = Helper.toIso(to);

        ArrayList<AccountBreakdown> list = new ArrayList<>();

        String sql = String.format(
                "select r.account, a.type, a.description, sum(r.amount) from register r, accounts a " +
                        "where r.account=a.id and r.date>='%s' and r.date<='%s' " +
                        "group by r.account, a.type, a.description order by a.type desc, sum(r.amount) desc, a.description ", dtFrom, dtTo);

        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            do {
                AccountBreakdown a = new AccountBreakdown();
                a.account = getAccount(cursor.getLong(0));
                a.amount = cursor.getFloat(3);
                list.add(a);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public ArrayList<Entity> getEntitiesList(DB_SORT sort, Account account) {
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
            case SORT_USAGE_COMBINED:
                if ( account!=null ) {
                    sql = "select e.id, e.description, (select ifnull(sum(usage),0) from usage where entity=e.id and account="+account.id+") usg "+
                            "from entities e "+
                            "order by usg desc, e.usage desc, e.description";
                }
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

    // Records
    public Long getNextRecordId() {
        String sql = String.format("select ifnull(max(id),0)+1 from register");
        Cursor cursor = db.rawQuery(sql, null);
        long next;

        if (cursor.moveToFirst())
            next = cursor.getLong(0);
        else
            next = 0;

        cursor.close();
        return next;
    }

    public boolean deleteRecord (Record record) {
        try {
            db.execSQL("delete from register where id=" + record.id);
        } catch ( Exception ex ) {
            return false;
        }
        return true;
    }

    public boolean saveRecord(Record record) {
        String sql;
        String sqlDate = Helper.toIso(record.date);
        String sqlAmount = Helper.sqlFloat(record.amount);
        String safedsc = Helper.sqlString(record.description);
        Long id;

        if ( record.id==null ) {
            id = getNextRecordId();
            sql = String.format("insert into register (id,date,account,entity,amount,description) values (%d,'%s',%d,%d,%s,'%s')",
                    id, sqlDate, record.account.id, record.entity.id, sqlAmount, safedsc);
        } else {
            id = record.id;
            sql = String.format("update register set date='%s', account=%d, entity=%d, amount=%s, description='%s' where id=%d",
                    sqlDate, record.account.id, record.entity.id, sqlAmount, safedsc, id);
        }

        try {
            db.execSQL(sql);
        } catch ( Exception e ) {
            return false;
        }

        // Stores location
        if ( record.location!=null ) {
            sql = String.format("update register set latitude=%s, longitude=%s where id=%d",
                    Helper.sqlDouble(record.location.getLatitude()), Helper.sqlDouble(record.location.getLongitude()), id);
            db.execSQL(sql);
        }

        // Updates usages
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
        int    i;

        Cursor cursor = db.rawQuery("select count(*) from accounts where description='"+safedsc+"'"+
                (account.id==null ? "" : " and id!="+account.id)
                , null);
        cursor.moveToFirst();
        cnt = cursor.getLong(0);

        cursor.close();

        if ( cnt>0 )
            throw new ETExists();

        cursor.close();

        if ( account.id==null ) {
            account.id = getNextAccountId();
            sql = String.format(
                    "insert into accounts ( id, type, description, usage ) values ( %d, '%s', '%s', 0)", account.id, account.type, safedsc);
        } else {
            sql = String.format(
                    "update accounts set parent=null, description='%s', type='%s' where id=%d", safedsc, account.type, account.id);
        }

        try {
            db.execSQL(sql);
        } catch (Exception e) {
            return false;
        }

        // Apply categories
        db.execSQL( "delete from account_categories where account="+account.id);
        if ( account.categories!=null ) {
            for (i = 0; i < account.categories.size(); i++) {
                db.execSQL("insert into account_categories ( account, category ) values ( " + account.id + ", " + account.categories.get(i).id + ")");
            }
        }
        return true;
    }

    public Long getNextEntityId() {
        String sql = String.format("select ifnull(max(id),0)+1 from entities");
        Cursor cursor = db.rawQuery(sql, null);
        long next;

        if (cursor.moveToFirst())
            next = cursor.getLong(0);
        else
            next = 0;

        cursor.close();
        return next;
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

        if ( entity.id==null ) {
            entity.id = getNextEntityId();
            sql = String.format("insert into entities ( id, description, usage ) values ( %d, '%s', 0)", entity.id, safedsc);
        } else {
            sql = String.format("update entities set description='%s' where id=%d", safedsc, entity.id);
        }

        try {
            db.execSQL(sql);
        } catch ( Exception e ) {
            return false;
        }

        return true;
    }

    public void backup()
    {
        db.close();

        try
        {
            File sd = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            File data = Environment.getDataDirectory();

            if (sd.canWrite())
            {
                String currentDBPath = "//data//com.dev.sr.myecotracker//databases//myecotracker.db";
                String backupDBPath = "myecotracker-backup.db";
                File currentDB = new File(data, currentDBPath);
                File backupDB = new File(sd, backupDBPath);

                if (currentDB.exists()) {
                    FileChannel src = new FileInputStream(currentDB).getChannel();
                    FileChannel dst = new FileOutputStream(backupDB).getChannel();
                    long l = dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();

                    Toast.makeText(context, context.getString(R.string.backup_complete), Toast.LENGTH_SHORT).show();
                }
            }
        }
        catch (Exception e) {
            Log.w("Settings Backup", e);
        }

        db = this.getWritableDatabase();
    }

    public void restore()
    {
        db.close();

        try
        {
            File sd = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            File data = Environment.getDataDirectory();

            String currentDBPath = "//data//com.dev.sr.myecotracker//databases//myecotracker.db";
            String backupDBPath = "myecotracker-backup.db";
            File currentDB = new File(data, currentDBPath);
            File backupDB = new File(sd, backupDBPath);

            if (backupDB.exists()) {
                FileChannel src = new FileInputStream(backupDB).getChannel();
                FileChannel dst = new FileOutputStream(currentDB).getChannel();
                Long l = dst.transferFrom(src, 0, src.size());
                src.close();
                dst.close();

                Toast.makeText(context, context.getString(R.string.restore_complete), Toast.LENGTH_SHORT).show();
            }

        }
        catch (Exception e) {
            Log.w("Settings Backup", e);
        }

        db = this.getWritableDatabase();
    }
}
