package org.lxy.demo.mp.sharding.config;


import io.shardingsphere.api.algorithm.sharding.PreciseShardingValue;
import io.shardingsphere.api.algorithm.sharding.standard.PreciseShardingAlgorithm;
import org.lxy.demo.mp.sharding.component.snowflake.IdGenerator;

import java.time.LocalDateTime;
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

        LocalDateTime localDateTime = IdGenerator.extractCreateTime(idWorker);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMM");
        return preciseShardingValue.getLogicTableName() + "_" + formatter.format(localDateTime);
    }

}
