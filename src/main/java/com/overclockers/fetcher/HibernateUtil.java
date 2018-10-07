package com.overclockers.fetcher;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {
    private static SessionFactory factory;
    private static ThreadLocal<Session> threadLocal = new ThreadLocal<>();

    static {
        factory = new Configuration().configure().buildSessionFactory();
    }

    private HibernateUtil() {

    }

    public static Session getSession() {
        Session session;
        if (threadLocal.get() == null) {
            session = factory.openSession();
            threadLocal.set(session);
        } else {
            session = threadLocal.get();
        }
        return session;

    }

    public static void closeSession() {
        if (threadLocal.get() != null) {
            threadLocal.get().close();
            threadLocal.remove();
        }
    }

    public static void closeSessionFactory() {
        factory.close();
    }


}