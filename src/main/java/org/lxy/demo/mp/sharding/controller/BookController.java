package org.lxy.demo.mp.sharding.controller;

import org.apache.commons.lang3.RandomStringUtils;
import org.lxy.demo.mp.sharding.component.snowflake.IdGenerator;
import org.lxy.demo.mp.sharding.mapper.BookMapper;
import org.lxy.demo.mp.sharding.model.table.Book;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

/**
 * @author liuxinyi
 * @date 2019-06-12
 */
@RestController
@RequestMapping("/book")
public class BookController {

    @Autowired
    private BookMapper bookMapper;

    @RequestMapping("/add")
    public Object addBook() {
        Book book = Book.builder()
                .guid(IdGenerator.nextId())
                .name(RandomStringUtils.randomAlphabetic(15))
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();
        bookMapper.insert(book);
        return book;
    }

    @RequestMapping("/queryName/{guid}")
    public Object queryName(@PathVariable("guid") String guid) {
        return bookMapper.queryNameByGuid(guid);
    }
}
