package org.example;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class DorisDataGenerator {

    private static final String JDBC_URL = "jdbc:mysql://localhost:9031/cjl";
    private static final String JDBC_USER = "admin";
    private static final String JDBC_PASSWORD = "";

    private static final String INSERT_SQL = "INSERT INTO example_range_tbl " +
            "(user_id, date, timestamp, city, age, sex, last_visit_date, cost, max_dwell_time, min_dwell_time) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private static final String[] CITIES = {"beijing", "shanghai", "new york"};
    private static final Random RANDOM = new Random();

    public static void main(String[] args) throws Exception {

        Runnable runnable = new Runnable() {
            @Override
            public void run() {

                try (Connection conn = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
                     PreparedStatement ps = conn.prepareStatement(INSERT_SQL)) {

                    System.out.println("🚀 开始随机生成时间 + 数据并插入 Doris");

                    while (true) {
                        long userId = RANDOM.nextInt(1_000_000);

                        // 1. 随机生成 date (2017-01-01 ~ 2017-03-31)
                        long baseDateMillis = new GregorianCalendar(2017, Calendar.JANUARY, 1).getTimeInMillis();
                        long maxOffset = 90L * 24 * 60 * 60 * 1000; // 90天范围
                        Date date = new Date(baseDateMillis + RANDOM.nextLong(maxOffset));

                        // 2. 随机 timestamp（基于 date，随机加上 0~23 小时、0~59 分钟、0~59 秒）
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(date);
                        cal.add(Calendar.HOUR_OF_DAY, RANDOM.nextInt(24));
                        cal.add(Calendar.MINUTE, RANDOM.nextInt(60));
                        cal.add(Calendar.SECOND, RANDOM.nextInt(60));
                        Date timestamp = cal.getTime();

                        // 3. last_visit_date = timestamp - 随机分钟数
                        cal.setTime(timestamp);
                        cal.add(Calendar.MINUTE, -RANDOM.nextInt(60));
                        Date lastVisit = cal.getTime();

                        String dateStr = new SimpleDateFormat("yyyy-MM-dd").format(date);
                        String timestampStr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(timestamp);
                        String lastVisitStr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(lastVisit);

                        String city = CITIES[RANDOM.nextInt(CITIES.length)];
                        int age = RANDOM.nextInt(60) + 10;
                        int sex = RANDOM.nextInt(2);
                        long cost = RANDOM.nextInt(10_000);
                        int maxDwell = RANDOM.nextInt(600);
                        int minDwell = RANDOM.nextInt(maxDwell + 1);

                        // 设置参数
                        ps.setLong(1, userId);
                        ps.setString(2, dateStr);
                        ps.setString(3, timestampStr);
                        ps.setString(4, city);
                        ps.setInt(5, age);
                        ps.setInt(6, sex);
                        ps.setString(7, lastVisitStr);
                        ps.setLong(8, cost);
                        ps.setInt(9, maxDwell);
                        ps.setInt(10, minDwell);

                        ps.executeUpdate();

                        System.out.printf("✅ 写入: user_id=%d, date=%s, city=%s, age=%d%n", userId, dateStr, city, age);

//                Thread.sleep(1000); // 控速
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        };

        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        executor.submit(runnable);
        executor.submit(runnable);
        executor.submit(runnable);
        executor.submit(runnable);
        executor.submit(runnable);
        executor.submit(runnable);
        executor.submit(runnable);
        executor.submit(runnable);
        executor.submit(runnable);
    }
}
