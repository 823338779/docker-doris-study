package org.example;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mysql.cj.NativeSession;
import com.mysql.cj.Query;
import com.mysql.cj.Session;
import com.mysql.cj.TransactionEventHandler;
import com.mysql.cj.conf.HostInfo;
import com.mysql.cj.exceptions.CJException;
import com.mysql.cj.jdbc.StatementImpl;
import com.mysql.cj.jdbc.exceptions.SQLExceptionsMapping;
import com.mysql.cj.protocol.a.NativeMessageBuilder;

import java.io.IOException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Random;

public class DorisStopper {
    private static final String JDBC_URL = "jdbc:mysql://localhost:9031/cjl";
    private static final String JDBC_USER = "admin";
    private static final String JDBC_PASSWORD = "";


    public static void main(String[] args) throws InterruptedException, SQLException {
        final Statement[] statement = {null};
        final long[] sessionStr = {0L};
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try (Connection conn = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD)) {
                    statement[0] = conn.createStatement();

//                    sql = "select * from cjl.example_range_tbl";
                    Session session = ((StatementImpl) statement[0]).getSession();
                    sessionStr[0] = session.getThreadId();
                    System.out.println(session.getThreadId());
                    statement[0].cancel();
                    statement[0].execute("kill query 132");

                } catch (SQLException e) {
                    e.printStackTrace();
//                    e.getSQLState()

                }finally {
                    if (statement[0] != null) {
                        try {

                            statement[0].close();
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }
}
