package com.example.administrator.envsystem.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2017/8/12.
 */

public class SQLFuntion {

    private static DbHelper dbManager;
    private static SQLiteDatabase db;

    public static void initTable(Context context) {
        dbManager = new DbHelper(context);
        db = dbManager.getReadableDatabase();
        // dbManager.getWritableDatabase();
    }

    public static void insert(String[] str){
        ContentValues values=new ContentValues();
        values.put(TableName.JYLSH,str[0]);
        values.put(TableName.V0101,str[1]);
        values.put(TableName.V0102,str[2]);
        values.put(TableName.V0103,str[3]);
        values.put(TableName.V0104,str[4]);
        values.put(TableName.V02,str[5]);
        values.put(TableName.V0401,str[6]);
        values.put(TableName.V0402,str[7]);
        values.put(TableName.V0403,str[8]);
        values.put(TableName.V0501,str[9]);
        values.put(TableName.V0502,str[10]);
        values.put(TableName.C01,str[11]);
        values.put(TableName.C02,str[12]);
        values.put(TableName.PHOTO01,str[13]);
        values.put(TableName.PHOTO02,str[14]);
        values.put(TableName.PHOTO03,str[15]);
        values.put(TableName.PHOTO04,str[16]);
        values.put(TableName.TIME,str[17]);
        long insert = db.insert(DBconf.TABLE_NAME, null, values);
        Log.i("TAG","insert="+insert);
    }

    /**
     * 插入数据
     * @param map
     * @return
     */
    public static boolean insertData(HashMap<String,String> map){
        ContentValues values=new ContentValues();
        if (map!=null){
            for (Map.Entry<String,String> entry:map.entrySet()){
                values.put(entry.getKey(),entry.getValue());
            }
        }
        long insert = db.insert(DBconf.TABLE_NAME, null, values);
        return insert>0;
    }

    public static boolean insertData2(String column,String value){
        ContentValues values=new ContentValues();
        values.put(column, value);
        long insert = db.insert(DBconf.TABLE_NAME, null, values);
        return insert>0;
    }

    /**
     * 更新数据
     * @param businessid
     * @param map
     * @return
     */
    public static boolean updateData(String businessid,HashMap<String,String> map){
        ContentValues values=new ContentValues();
        if (map!=null){
            for (Map.Entry<String,String> entry:map.entrySet()){
                values.put(entry.getKey(),entry.getValue());
            }
        }
        int update = db.update(DBconf.TABLE_NAME, values, "jylsh like ?", new String[]{businessid});
        return update>0;
    }

    /*public static ArrayList<String> queryAll(){
        ArrayList<String> list=new ArrayList<>();
        Cursor cursor = db.query("waiguan", null, null, null, null, null, null);
        if (cursor.moveToFirst()){
            do {
                String jylsh = cursor.getString(cursor.getColumnIndex(TableName.JYLSH));
                String v0101 = cursor.getString(cursor.getColumnIndex(TableName.V0101));
                String v0102 = cursor.getString(cursor.getColumnIndex(TableName.V0102));
                String v0103 = cursor.getString(cursor.getColumnIndex(TableName.V0103));
                String v0104 = cursor.getString(cursor.getColumnIndex(TableName.V0104));
                String v02 = cursor.getString(cursor.getColumnIndex(TableName.V02));
                String v0401 = cursor.getString(cursor.getColumnIndex(TableName.V0401));
                String v0402 = cursor.getString(cursor.getColumnIndex(TableName.V0402));
                String v0403 = cursor.getString(cursor.getColumnIndex(TableName.V0403));
                String v0501 = cursor.getString(cursor.getColumnIndex(TableName.V0501));
                String v0502 = cursor.getString(cursor.getColumnIndex(TableName.V0502));
                String c01 = cursor.getString(cursor.getColumnIndex(TableName.C01));
                String c02 = cursor.getString(cursor.getColumnIndex(TableName.C02));
                String photo01 = cursor.getString(cursor.getColumnIndex(TableName.PHOTO01));
                String photo02 = cursor.getString(cursor.getColumnIndex(TableName.PHOTO02));
                String photo03 = cursor.getString(cursor.getColumnIndex(TableName.PHOTO03));
                String photo04 = cursor.getString(cursor.getColumnIndex(TableName.PHOTO04));
                String time = cursor.getString(cursor.getColumnIndex(TableName.TIME));
                list.add(jylsh);list.add(v0101);list.add(v0102);list.add(v0103);list.add(v0104);list.add(v02);
                list.add(v0401);
                list.add(v0402);
                list.add(v0403);
                list.add(v0501);
                list.add(v0502);
                list.add(c01);
                list.add(c02);
                list.add(photo01);
                list.add(photo02);
                list.add(photo03);
                list.add(photo04);
                list.add(time);
            }while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }*/

    public static String[] queryBySelection(String businessid){
        Cursor cursor = db.query(DBconf.TABLE_NAME, null, "jylsh like ?", new String[]{businessid}, null, null, null);
        String[] str=new String[5];
        if (cursor.moveToFirst()){
            do {
                str[0] = cursor.getString(cursor.getColumnIndex(TableName.JYLSH));
                str[1] = cursor.getString(cursor.getColumnIndex(TableName.PHOTO01));
                str[2] = cursor.getString(cursor.getColumnIndex(TableName.PHOTO02));
                str[3] = cursor.getString(cursor.getColumnIndex(TableName.PHOTO03));
                str[4] = cursor.getString(cursor.getColumnIndex(TableName.PHOTO04));
                String time = cursor.getString(cursor.getColumnIndex(TableName.TIME));
            }while (cursor.moveToNext());
        }else {
            cursor.close();
            return null;
        }
        cursor.close();
        return str;
    }

    public static String queryBySelection2(String[] columns,String businessid){
        String s;
        Cursor cursor = db.query(DBconf.TABLE_NAME, columns, "jylsh like ?", new String[]{businessid}, null, null, null);
        if (cursor.moveToFirst()){
            do{
                s = cursor.getString(cursor.getColumnIndex(columns[0]));
            }while (cursor.moveToNext());
        }else {
            cursor.close();
            return null;
        }
        cursor.close();
        return s;
    }

    public static boolean queryAll2(String businessid){
        Cursor cursor = db.query(DBconf.TABLE_NAME, null, "jylsh like ?", new String[]{businessid}, null, null, null);
        boolean b=false;
        if (cursor.moveToFirst()){
            b=true;
        }
        cursor.close();
        return b;
    }
}
