package com.overclockers.fetcher.service;

import com.overclockers.fetcher.Application;
import com.overclockers.fetcher.HibernateUtil;
import com.overclockers.fetcher.configuration.TestAppConfig;
import com.overclockers.fetcher.model.User;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestAppConfig.class)
public class FetchingServiceTest {

    @Autowired
    FetchingService fetchingService;

    @Test
    public void testConnectionDB() {
        Session session = HibernateUtil.getSession();
        Transaction transaction = session.beginTransaction();

        User userToSave = User.builder().username("TestUsername").profileLink("TestProfileLink").build();
        session.save(userToSave);
        transaction.commit();

        transaction = session.beginTransaction();
        User savedUser = session.get(User.class, userToSave.getUserId());

        assertEquals(savedUser, userToSave);

        session.delete(userToSave);
        transaction.commit();

        User userAfterDeleting = session.get(User.class, userToSave.getUserId());
        assertNull(userAfterDeleting);

        HibernateUtil.closeSession();
    }
}