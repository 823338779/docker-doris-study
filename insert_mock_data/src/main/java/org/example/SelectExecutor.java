package org.example;

import com.mysql.cj.Session;
import com.mysql.cj.jdbc.StatementImpl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class SelectExecutor {
    private static final String JDBC_URL = "jdbc:mysql://localhost:9031/cjl";
    private static final String JDBC_USER = "admin";
    private static final String JDBC_PASSWORD = "";

    private static final String INSERT_SQL = "INSERT INTO example_range_tbl " +
            "(user_id, date, timestamp, city, age, sex, last_visit_date, cost, max_dwell_time, min_dwell_time) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

//    private static final String[] CITIES = {"beijing", "shanghai", "new york"};
//    private static final Random RANDOM = new Random();

    static String sql = "select * from cjl.example_range_tbl";

    public static void main(String[] args) throws InterruptedException, SQLException {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Statement statement = null;
                try {
                    Connection conn = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
                    statement = conn.createStatement();

                    Session session = ((StatementImpl) statement).getSession();
                    System.out.println(session.getThreadId());
                    boolean execute = statement.execute(sql);
                    statement.close();
                    statement.close();
                    System.out.println("AAA");
                    execute = statement.execute(sql);

                    conn.close();
                    conn.close();
                    System.out.println("A");
                } catch (SQLException e) {
                    throw new RuntimeException(e);
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
