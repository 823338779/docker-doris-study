CREATE TABLE IF NOT EXISTS cjl.example_range_tbl
(
    `user_id`         BIGINT   NOT NULL COMMENT "用户id",
    `date`            DATE     NOT NULL COMMENT "数据灌入日期时间",
    `timestamp`       DATETIME NOT NULL COMMENT "数据灌入的时间戳",
    `city`            VARCHAR(20) COMMENT "用户所在城市",
    `age`             SMALLINT COMMENT "用户年龄",
    `sex`             TINYINT COMMENT "用户性别",
    `last_visit_date` DATETIME REPLACE DEFAULT "1970-01-01 00:00:00" COMMENT "用户最后一次访问时间",
    `cost`            BIGINT SUM DEFAULT "0" COMMENT "用户总消费",
    `max_dwell_time`  INT MAX DEFAULT "0" COMMENT "用户最大停留时间",
    `min_dwell_time`  INT MIN DEFAULT "99999" COMMENT "用户最小停留时间"
    )
    ENGINE=OLAP
    AGGREGATE KEY (`user_id`, `date`, `timestamp`, `city`, `age`, `sex`)
    PARTITION BY RANGE (`date`)
(
    PARTITION `p201701` VALUES LESS THAN ("2017-02-01"),
    PARTITION `p201702` VALUES LESS THAN ("2017-03-01"),
    PARTITION `p201703` VALUES LESS THAN ("2017-04-01")
    )
    DISTRIBUTED BY HASH(`user_id`) BUCKETS 16
    PROPERTIES
(
    "replication_num" = "1"
);




-- 查询语句

SELECT
    t1.user_id,
    COUNT(*) AS visit_cnt,
    MAX(t1.cost) AS max_cost,
    MIN(t2.min_dwell_time) AS min_dwell,
    AVG(t2.age) AS avg_age
FROM
    (
        SELECT *
        FROM example_range_tbl
        WHERE age BETWEEN 20 AND 60
    ) t1
        cross JOIN
    (
        SELECT *
        FROM example_range_tbl
        WHERE age IS NOT NULL
    ) t2
GROUP BY
    t1.user_id
ORDER BY
    visit_cnt DESC

