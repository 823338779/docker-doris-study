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

public class OverwriteExecutor {
    private static final String JDBC_URL = "jdbc:mysql://localhost:9031/cjl";
    private static final String JDBC_USER = "admin";
    private static final String JDBC_PASSWORD = "";

    private static final String INSERT_SQL = "INSERT INTO example_range_tbl " +
            "(user_id, date, timestamp, city, age, sex, last_visit_date, cost, max_dwell_time, min_dwell_time) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

//    private static final String[] CITIES = {"beijing", "shanghai", "new york"};
//    private static final Random RANDOM = new Random();

    static String sql = "INSERT OVERWRITE TABLE cjl.example_range_tmp SELECT\n" +
            "    t1.user_id,\n" +
            "    COUNT(*) AS visit_cnt,\n" +
            "    MAX(t1.cost) AS max_cost,\n" +
            "    MIN(t2.min_dwell_time) AS min_dwell,\n" +
            "    AVG(t2.age) AS avg_age\n" +
            "FROM\n" +
            "    (\n" +
            "        SELECT user_id, cost\n" +
            "        FROM example_range_tbl\n" +
            "        WHERE age BETWEEN 20 AND 60\n" +
            "    ) t1\n" +
            "CROSS JOIN\n" +
            "    (\n" +
            "        SELECT min_dwell_time, age\n" +
            "        FROM example_range_tbl\n" +
            "        WHERE age IS NOT NULL\n" +
            "    ) t2\n" +
            "GROUP BY\n" +
            "    t1.user_id;";

    public static void main(String[] args) throws InterruptedException, SQLException {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Statement statement = null;
                try (Connection conn = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD)) {
                    statement = conn.createStatement();

                    Session session = ((StatementImpl) statement).getSession();
                    System.out.println(session.getThreadId());
                    boolean execute = statement.execute(sql);

                } catch (SQLException e) {
//                    throw new RuntimeException(e);
                    e.printStackTrace();

                } finally {
                    if (statement != null) {
                        try {
                            statement.close();
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
//        Thread.sleep(5000L);

//        NativeSession nativeSession = JSON.parseObject(sessionStr[0], NativeSession.class);
//        cancel(nativeSession);
//        System.out.println("canceled");
    }

//    public static void  cancel(HostInfo hostInfo,  session) throws SQLException {
//            NativeSession newSession = null;
//
//            try {
//                HostInfo hostInfo = session.getHostInfo();
//                String database = hostInfo.getDatabase();
//                String user = hostInfo.getUser();
//                String password = hostInfo.getPassword();
//                newSession = new NativeSession(session.getHostInfo(), session.getPropertySet());
//                newSession.connect(hostInfo, user, password, database, 30000, new TransactionEventHandler() {
//                    public void transactionCompleted() {
//                    }
//
//                    public void transactionBegun() {
//                    }
//                });
//                newSession.getProtocol().sendCommand((new NativeMessageBuilder(newSession.getServerSession().supportsQueryAttributes())).buildComQuery(newSession.getSharedSendPacket(), "KILL QUERY " + session.getThreadId()), false, 0);
////                        this.setCancelStatus(Query.CancelStatus.CANCELED_BY_USER);
//            } catch (IOException e) {
//
//            } finally {
//                if (newSession != null) {
//                    newSession.forceClose();
//                }
//            }
//    }
}
