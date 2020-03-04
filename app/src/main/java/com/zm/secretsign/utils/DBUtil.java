package com.zm.secretsign.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.zm.secretsign.bean.AddressKey;
import com.zm.secretsign.bean.AddressKeyDao;
import com.zm.secretsign.bean.DaoMaster;
import com.zm.secretsign.bean.DaoSession;
import com.zm.secretsign.bean.Password;
import com.zm.secretsign.bean.PasswordDao;

import java.util.List;

/**
 * author : Zhouzhou
 * e-mail : 553419781@qq.com
 * date   : 2019/6/21 10:31
 */
public class DBUtil {

    public static final int LIMIT = 10;

    private static DaoSession mDaoSession;

    public static void initDatabase(Context context) {
        if (mDaoSession != null) {
            return;
        }

        //创建数据库.db"
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(context, context.getPackageName() + "secret_sign.db", null);
        //获取可写数据库
        SQLiteDatabase db = helper.getWritableDatabase();
        //获取数据库对象
        DaoMaster daoMaster = new DaoMaster(db);
        //获取Dao对象管理者
        mDaoSession = daoMaster.newSession();
    }

    ////////////////Password/////////////////////////////////////////////////////////////////////////////////////////////////////////
    public static void insertPwd(Password item) {
        mDaoSession.getPasswordDao().insertOrReplace(item);
    }

    public static Password getPwdFirst() {
        List<Password> items = mDaoSession.getPasswordDao().queryBuilder().orderDesc(PasswordDao.Properties.Id).list();
        return items.size() > 0 ? items.get(0) : null;
    }

    ////////////////AddressKey/////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * 添加项目数据，如果有重复则覆盖
     */
    public static void insertKey(AddressKey item) {
        mDaoSession.getAddressKeyDao().insertOrReplace(item);
    }

    public static void updateKey(AddressKey item) {
        mDaoSession.getAddressKeyDao().update(item);
    }

    public static void updateKeys(List<AddressKey> item) {
        mDaoSession.getAddressKeyDao().updateInTx(item);
    }

    /**
     * 查询全部项目数据
     */
    public static List<AddressKey> queryAllKeyItems(int page, String coinType) {
        return mDaoSession.getAddressKeyDao().queryBuilder()
                .where(AddressKeyDao.Properties.CoinType.eq(coinType))
                .orderAsc(AddressKeyDao.Properties.Order)
                .offset((page - 1) * LIMIT)
                .limit(LIMIT)
                .list();
    }

    /**
     * 查询全部项目数据
     */
    public static List<AddressKey> queryAllKeyItems() {
        return mDaoSession.getAddressKeyDao().queryBuilder().list();
    }

    /**
     * 时间上面一条记录(可能经过上移、下移操作,不一定按时间排序)
     */
    public static AddressKey getKeyFirst(String coinType) {
        List<AddressKey> items = mDaoSession.getAddressKeyDao().queryBuilder()
                .where(AddressKeyDao.Properties.CoinType.eq(coinType))
                .orderAsc(AddressKeyDao.Properties.Order).list();
        return items.size() > 0 ? items.get(0) : null;
    }

    public static AddressKey getKeyByAddress(String address) {
        List<AddressKey> items = mDaoSession.getAddressKeyDao().queryBuilder()
                .where(AddressKeyDao.Properties.Address.eq(address)).list();
        return items.size() > 0 ? items.get(0) : null;
    }

    public static AddressKey getKeyByAddressAndCoinType(String address, String coinType) {
        List<AddressKey> items = mDaoSession.getAddressKeyDao().queryBuilder()
                .where(AddressKeyDao.Properties.Address.eq(address)).where(AddressKeyDao.Properties.CoinType.eq(coinType)).list();
        return items.size() > 0 ? items.get(0) : null;
    }

    public static Long getKeyMaxId() {
        List<AddressKey> items = mDaoSession.getAddressKeyDao().queryBuilder().orderDesc(AddressKeyDao.Properties.Id).list();
        return items.size() > 0 ? items.get(0).getId() : 0;
    }

    /**
     * 删除某条记录
     */
    public static void deleteKey(AddressKey item) {
        mDaoSession.getAddressKeyDao().delete(item);
    }
//
//    /**
//     * 按名称(编号)查询项目数据
//     */
//    public static ProjectItem queryProjectItemByName(String name) {
//        try {
//            QueryBuilder<ProjectItem> qb = mDaoSession.getProjectItemDao().queryBuilder();
//            qb.where(ProjectItemDao.Properties.Name.eq(name));//
//            return qb.list().get(0);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }
//    }

}
