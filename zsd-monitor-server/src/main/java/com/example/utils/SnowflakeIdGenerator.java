package com.example.utils;

import org.springframework.stereotype.Component;

/**
 * 雪花算法ID生成器
 * 用于生成唯一的ID，具有时间戳、数据中心ID、工作机器ID和序列号。
 */
@Component
public class SnowflakeIdGenerator {
    private static final long START_TIMESTAMP = 1691087910202L; // 起始时间戳，代表某个固定时刻

    private static final long DATA_CENTER_ID_BITS = 5L; // 数据中心ID占用的位数
    private static final long WORKER_ID_BITS = 5L; // 工作机器ID占用的位数
    private static final long SEQUENCE_BITS = 12L; // 序列号占用的位数

    private static final long MAX_DATA_CENTER_ID = ~(-1L << DATA_CENTER_ID_BITS); // 最大数据中心ID
    private static final long MAX_WORKER_ID = ~(-1L << WORKER_ID_BITS); // 最大工作机器ID
    private static final long MAX_SEQUENCE = ~(-1L << SEQUENCE_BITS); // 最大序列号

    private static final long WORKER_ID_SHIFT = SEQUENCE_BITS; // 工作机器ID左移的位数
    private static final long DATA_CENTER_ID_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS; // 数据中心ID左移的位数
    private static final long TIMESTAMP_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS + DATA_CENTER_ID_BITS; // 时间戳左移的位数

    private final long dataCenterId; // 数据中心ID
    private final long workerId; // 工作机器ID
    private long lastTimestamp = -1L; // 上次生成ID的时间戳
    private long sequence = 0L; // 当前序列号

    // 默认构造函数，使用数据中心ID和工作机器ID为1
    public SnowflakeIdGenerator(){
        this(1, 1);
    }

    // 构造函数，初始化数据中心ID和工作机器ID
    private SnowflakeIdGenerator(long dataCenterId, long workerId) {
        // 校验数据中心ID和工作机器ID是否合法
        if (dataCenterId > MAX_DATA_CENTER_ID || dataCenterId < 0) {
            throw new IllegalArgumentException("Data center ID can't be greater than " + MAX_DATA_CENTER_ID + " or less than 0");
        }
        if (workerId > MAX_WORKER_ID || workerId < 0) {
            throw new IllegalArgumentException("Worker ID can't be greater than " + MAX_WORKER_ID + " or less than 0");
        }
        this.dataCenterId = dataCenterId;
        this.workerId = workerId;
    }

    /**
     * 生成一个新的雪花算法ID加锁
     * 生成时会判断当前时间戳与上次生成ID的时间戳，确保ID不重复
     * @return 雪花ID
     */
    public synchronized long nextId() {
        long timestamp = getCurrentTimestamp(); // 获取当前时间戳
        // 如果当前时间戳小于上次生成ID的时间戳，说明系统时间回退，抛出异常
        if (timestamp < lastTimestamp) {
            throw new IllegalStateException("Clock moved backwards. Refusing to generate ID.");
        }
        // 如果当前时间戳与上次时间戳相同，说明当前毫秒内生成多个ID，增加序列号
        if (timestamp == lastTimestamp) {
            sequence = (sequence + 1) & MAX_SEQUENCE; // 序列号自增，并确保不超过最大值
            // 如果序列号溢出，等待下一毫秒
            if (sequence == 0) {
                timestamp = getNextTimestamp(lastTimestamp); // 等待到下一毫秒
            }
        } else {
            sequence = 0L; // 如果时间戳不同，重置序列号
        }
        lastTimestamp = timestamp; // 更新上次生成ID的时间戳
        // 根据雪花算法公式，生成一个唯一的ID
        return ((timestamp - START_TIMESTAMP) << TIMESTAMP_SHIFT) |
                (dataCenterId << DATA_CENTER_ID_SHIFT) |
                (workerId << WORKER_ID_SHIFT) |
                sequence;
    }

    // 获取当前时间戳（毫秒）
    private long getCurrentTimestamp() {
        return System.currentTimeMillis();
    }

    // 等待直到获取到一个新的时间戳
    private long getNextTimestamp(long lastTimestamp) {
        long timestamp = getCurrentTimestamp();
        // 如果当前时间戳小于等于上次生成的时间戳，持续等待
        while (timestamp <= lastTimestamp) {
            timestamp = getCurrentTimestamp();
        }
        return timestamp;
    }
}
