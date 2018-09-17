package com.feamorx86.boardgame.dao;

import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.dialect.function.SQLFunctionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Home on 30.09.2017.
 */
@Transactional
@Repository

public class SQLHelper {
    @Autowired
    private SessionFactory sessionFactory;

    public boolean executeList(String request, List<Object> requsetParqameters, Integer maxResults, ArrayList<Object> results) {
        boolean result = false;
        try {
            SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery(request);
            if (requsetParqameters != null && requsetParqameters.size() > 0) {
                for (int i = 0; i < requsetParqameters.size(); i++) {
                    Object parameter = requsetParqameters.get(i);
                    query.setParameter(i, parameter);
                }
            }
            if (maxResults != null) {
                query.setMaxResults(maxResults);
            }
            results.addAll(query.list());
            result = true;
        } catch (Throwable throwable) {
            result = false;
            printError(throwable, results);
        }
        return result;
    }

    private void printError(Throwable error, List<Object> results) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        error.printStackTrace(pw);
        String text = sw.toString();

        results.add(text);
        System.err.print(text);
    }

    public boolean executeUpdate(String request, List<Object> requsetParqameters, List<Object> results) {
        boolean result = false;
        try {
            SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery(request);
            if (requsetParqameters != null && requsetParqameters.size() > 0) {
                for (int i = 0; i < requsetParqameters.size(); i++) {
                    Object parameter = requsetParqameters.get(i);
                    query.setParameter(i, parameter);
                }
            }
            results.add(Integer.valueOf(query.executeUpdate()));
            result = true;
        } catch (Throwable throwable) {
            printError(throwable, results);

            result = false;
        }
        return result;
    }
}
