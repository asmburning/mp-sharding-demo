package org.lxy.demo.mp.sharding.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.lxy.demo.mp.sharding.model.table.Book;

/**
 * @author liuxinyi
 * @date 2019-06-12
 */
public interface BookMapper extends BaseMapper<Book> {

    String queryNameByGuid(String guid);
}
