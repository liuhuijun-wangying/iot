package com.iot.dao.util;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by zc on 16-8-9.
 */
public class DbConnProxy {

    private static final SqlSessionFactory sqlSessionFactory;
    static{
        String resource = "mybatis-config.xml";
        try {
            InputStream inputStream = Resources.getResourceAsStream(resource);
            sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("load mybatis-config.xml failed");
        }
    }

    public static final ThreadLocal<SqlSession> session = new ThreadLocal<>();
    public static SqlSession getSession(){
        SqlSession s = session.get();
        if (s == null) {
            s = sqlSessionFactory.openSession();
            session.set(s);
        }
        return s;
    }

    public static void closeSession() {
        SqlSession s = session.get();
        session.set(null);
        if (s != null)
            s.close();
    }
}
