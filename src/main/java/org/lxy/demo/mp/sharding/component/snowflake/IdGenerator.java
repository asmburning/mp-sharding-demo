package org.lxy.demo.mp.sharding.component.snowflake;

import lombok.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 *
 */
public class IdGenerator {

    private static Sequence worker = new Sequence();

    public static Long getId() {
        return worker.nextId();
    }

    public static String nextId() {
        return worker.nextId() + "";
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    @Builder
    public static class Structure {
        private LocalDateTime date;
        private Integer sequence;
        private Integer workerId;
        private Integer dataCenter;
    }


    public static Structure parse(Long longId) {
        String id = Long.toBinaryString(longId);
        int len = id.length();
        int sequenceStart = len < worker.getWorkerIdShift() ? 0 : len - worker.getWorkerIdShift();
        int workerStart = len < worker.getDatacenterIdShift() ? 0 : len - worker.getDatacenterIdShift();
        int timeStart = len < worker.getTimestampLeftShift() ? 0 : len - worker.getTimestampLeftShift();
        String sequence = id.substring(sequenceStart, len);
        String workerId = sequenceStart == 0 ? "0" : id.substring(workerStart, sequenceStart);
        String dataCenterId = workerStart == 0 ? "0" : id.substring(timeStart, workerStart);
        String time = timeStart == 0 ? "0" : id.substring(0, timeStart);
        int sequenceInt = Integer.valueOf(sequence, 2);
        int workerIdInt = Integer.valueOf(workerId, 2);
        int dataCenterIdInt = Integer.valueOf(dataCenterId, 2);
        long diffTime = Long.parseLong(time, 2);
        long timeLong = diffTime + worker.getTwepoch();
        Instant instant = Instant.ofEpochMilli(timeLong);
        LocalDateTime date = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());

        return Structure.builder()
                .sequence(sequenceInt)
                .workerId(workerIdInt)
                .dataCenter(dataCenterIdInt)
                .date(date)
                .build();
    }

    public static LocalDateTime extractCreateTime(Long longId) {
        Structure structure = null;
        try {
            structure = parse(longId);
        } catch (Exception e) {
        }

        return structure == null ? null : structure.getDate();
    }

}
