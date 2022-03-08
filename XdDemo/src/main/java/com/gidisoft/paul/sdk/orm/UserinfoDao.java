package com.gidisoft.paul.sdk.orm;

import android.content.Context;
import android.database.SQLException;

import com.j256.ormlite.dao.Dao;

import java.util.ArrayList;

public class UserinfoDao implements UserinfoImpl{
    private XdDatabaseHelper mHelper;
    private Dao<UserBean, Integer> dao;
    private Context mContext;
    private static UserinfoDao instance;

    protected UserinfoDao(Context context) {
        this.mContext = context;
        try {
            mHelper = XdDatabaseHelper.getHelper(mContext);
            dao = mHelper.getDao(UserBean.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public static UserinfoDao getInstance(Context context) {
        if (instance == null) {
            synchronized (UserinfoDao.class) {
                if (instance == null) {
                    instance = new UserinfoDao(context);
                }
            }

        }
        return instance;
    }

    @Override
    public void insert(UserBean myBean) {
        try {
            //事务操作
           /* TransactionManager.callInTransaction(mHelper.getConnectionSource(), new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    return null;
                }
            });*/
            try {
                dao.create(myBean);
            } catch (java.sql.SQLException throwables) {
                throwables.printStackTrace();
            }
            //dao.createOrUpdate(myBean);//和上一行的方法效果一样
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(String name, String password) {
        ArrayList<UserBean> list = null;
        try {
            try {
                list = (ArrayList<UserBean>) dao.queryBuilder().where().eq("name", name).query();//上述相当与：select * from user where name = name ;
            } catch (java.sql.SQLException throwables) {
                throwables.printStackTrace();
            }
            if (list != null) {
                for (UserBean bean : list) {
                    bean.setPassword(password);
                    try {
                        dao.update(bean);
                    } catch (java.sql.SQLException throwables) {
                        throwables.printStackTrace();
                    }
                    //dao.createOrUpdate(bean);//和上一行的方法效果一样
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(String name) {
        ArrayList<UserBean> list = null;
        try {
            try {
                list = (ArrayList<UserBean>) dao.queryForEq("name", name);
            } catch (java.sql.SQLException throwables) {
                throwables.printStackTrace();
            }
            if (list != null) {
                for (UserBean bean : list) {
                    try {
                        dao.delete(bean);
                    } catch (java.sql.SQLException throwables) {
                        throwables.printStackTrace();
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public ArrayList<UserBean> query(String name) {
        ArrayList<UserBean> list = null;
        try {
            try {
                list = (ArrayList<UserBean>) dao.queryBuilder().where().eq("name", name).query();//上述相当与：select * from user where name = name ;
            } catch (java.sql.SQLException throwables) {
                throwables.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public ArrayList<UserBean> queryAll() {
        ArrayList<UserBean> list = null;
        try {
            list = (ArrayList<UserBean>) dao.queryForAll();
        } catch (java.sql.SQLException throwables) {
            throwables.printStackTrace();
        }
        if (list != null) {
            return list;
        }
        return list;
    }
}
