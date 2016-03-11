package com.yaohuola.ormlite.db;


import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.yaohuola.ormlite.bean.ShoppingCartBean;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;  
public class DatabaseHelper extends OrmLiteSqliteOpenHelper  
{  
  
    private static final String TABLE_NAME = "sqlite-shopping_cart.db";  
    /** 
     * shoppingCartDao ，每张表对于一个 
     */  
    @SuppressWarnings("unused")
	private Dao<ShoppingCartBean, Integer> shoppingCartDao;  
    @SuppressWarnings("rawtypes")
	private Map<String, Dao> daos = new HashMap<String, Dao>();  
    private DatabaseHelper(Context context)  
    {  
        super(context, TABLE_NAME, null, 2);  
    }  
  
    @Override  
    public void onCreate(SQLiteDatabase database,  
            ConnectionSource connectionSource)  
    {  
        try  
        {  
            TableUtils.createTable(connectionSource, ShoppingCartBean.class);  
        } catch (SQLException e)  
        {  
            e.printStackTrace();  
        }  
    }  
  
    @Override  
    public void onUpgrade(SQLiteDatabase database,  
            ConnectionSource connectionSource, int oldVersion, int newVersion)  
    {  
        try  
        {  
            TableUtils.dropTable(connectionSource, ShoppingCartBean.class, true);  
            onCreate(database, connectionSource);  
        } catch (SQLException e)  
        {  
            e.printStackTrace();  
        }  
    }  
  
    private static DatabaseHelper instance;  
  
    /** 
     * 单例获取该Helper 
     *  
     * @param context 
     * @return 
     */  
    public static synchronized DatabaseHelper getHelper(Context context)  
    {  
        if (instance == null)  
        {  
            synchronized (DatabaseHelper.class)  
            {  
                if (instance == null)  
                    instance = new DatabaseHelper(context);  
            }  
        }  
  
        return instance;  
    }  
  
    @SuppressWarnings({ "unchecked", "rawtypes" })
	public synchronized Dao getDao(Class clazz) throws SQLException  
    {  
        Dao dao = null;  
        String className = clazz.getSimpleName();  
  
        if (daos.containsKey(className))  
        {  
            dao = daos.get(className);  
        }  
        if (dao == null)  
        {  
            dao = super.getDao(clazz);  
            daos.put(className, dao);  
        }  
        return dao;  
    }  
//    /** 
//     * 获得shoppingCartDao 
//     *  
//     * @return 
//     * @throws SQLException 
//     */  
//    public Dao<ShoppingCartBean, Integer> getUserDao() throws SQLException  
//    {  
//        if (shoppingCartDao == null)  
//        {  
//        	shoppingCartDao = getDao(ShoppingCartBean.class);  
//        }  
//        return shoppingCartDao;  
//    }  
  
    /** 
     * 释放资源 
     */  
    @Override  
    public void close()  
    {  
        super.close();  
        shoppingCartDao = null;  
    }  
  
}  


