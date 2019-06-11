package org.lxy.demo.mp.sharding.config;


import io.shardingsphere.api.algorithm.sharding.PreciseShardingValue;
import io.shardingsphere.api.algorithm.sharding.standard.PreciseShardingAlgorithm;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collection;

public class IdWorkerMonthTableShardingStrategy implements PreciseShardingAlgorithm<String> {


    /**
     * 时间起始标记点，作为基准，一般取系统的最近时间（一旦确定不能变动）
     */
    private final long twepoch = 1288834974657L;


    @Override
    public String doSharding(Collection<String> collection, PreciseShardingValue<String> preciseShardingValue) {
        Long idWorker = Long.parseLong(preciseShardingValue.getValue());

        LocalDateTime localDateTime = parseIdWorkerToLocalDateTime(idWorker);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMM");
        return preciseShardingValue.getLogicTableName() + "_" + formatter.format(localDateTime);
    }

    /**
     * idWorker 结构
     * 1[符号位] + 41[毫秒位] + 5[dataCenter标识位] + 5[机器ID] + 12位[毫秒内累加数]
     * 不需要精确的时间, 只取41位毫秒获取日期
     *
     * @param idWorker
     * @return
     */
    private LocalDateTime parseIdWorkerToLocalDateTime(Long idWorker) {
        String binaryString = Long.toBinaryString(idWorker);
        Long milliSeconds = Long.parseLong(binaryString.substring(1, 42), 2);

        Instant instant = Instant.ofEpochMilli(milliSeconds + twepoch);
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    }
}
