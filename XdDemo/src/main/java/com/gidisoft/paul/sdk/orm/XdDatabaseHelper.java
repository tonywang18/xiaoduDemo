package com.gidisoft.paul.sdk.orm;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.util.HashMap;
import java.util.Map;

public class XdDatabaseHelper extends OrmLiteSqliteOpenHelper {
    public static final String DB_NAME = "XdDemo.db";
    public static final int DB_VERSION = 1;
    public static final String DB_PATH = Environment.getExternalStorageDirectory().getAbsolutePath()+"/XdDemo";

    public XdDatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource) {
        try {
            try {
                TableUtils.createTable(connectionSource, UserBean.class);
            } catch (java.sql.SQLException throwables) {
                throwables.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        System.out.println("MyDatabaseHelper.onUpgrade oldVersion=" + oldVersion + "  newVersion=" + newVersion);
        try {

            switch (oldVersion) {
                case 1:
//                    getDao(UserBean.class).executeRaw("alter table Book add column book_type varchar(20)");
                    //在数据库版本1的下一版本，Book表中新添加了 book_type 字段

                case 2:
                    // TableUtils.createTable(connectionSource, MyBean2.class);
                    //在数据库版本2的下一版本，新增加了一张表
                default:
                    break;
            }
            //显然这样处理比较暴力
            //TableUtils.dropTable(connectionSource, MyBean.class, true);
            //onCreate(sqLiteDatabase, connectionSource);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static XdDatabaseHelper instance;

    /**
     * 单例获取该Helper
     *
     * @param context
     * @return
     */
    public static XdDatabaseHelper getHelper(Context context) {
        if (instance == null) {
            synchronized (XdDatabaseHelper.class) {
                if (instance == null)
                    instance = new XdDatabaseHelper(new DatabaseContext(context, DB_PATH));
//                    instance = new XdDatabaseHelper(context);
            }
        }
        return instance;
    }


    private Map<String, Dao> daos = new HashMap<>();

    public synchronized Dao getDao(Class clazz) throws SQLException {
        Dao dao = null;
        String className = clazz.getSimpleName();
        if (daos.containsKey(className)) {
            dao = daos.get(clazz);
        }
        if (dao == null) {
            try {
                dao = super.getDao(clazz);
            } catch (java.sql.SQLException throwables) {
                throwables.printStackTrace();
            }
            daos.put(className, dao);
        }
        return dao;
    }


    @Override
    public void close() {
        super.close();
        for (String key : daos.keySet()) {
            Dao dao = daos.get(key);
            dao = null;
        }
    }


}

