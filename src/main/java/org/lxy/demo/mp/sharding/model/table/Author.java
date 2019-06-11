package org.lxy.demo.mp.sharding.model.table;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author liuxinyi
 * @date 2019-06-11
 */
@Data
@Builder
@TableName("t_author")
public class Author {
    @TableId
    private String guid;
    private String name;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
