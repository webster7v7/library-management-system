package com.library.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.library.entity.Book;
import com.library.mapper.BookMapper;
import com.library.service.BookService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BookServiceImpl extends ServiceImpl<BookMapper, Book> implements BookService {

    @Override
    public IPage<Book> getBookList(int page, int size, String keyword) {
        Page<Book> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Book> wrapper = new LambdaQueryWrapper<>();
        
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w
                .like(Book::getTitle, keyword)
                .or()
                .like(Book::getAuthor, keyword)
                .or()
                .like(Book::getIsbn, keyword)
            );
        }
        
        return page(pageParam, wrapper);
    }

    @Override
    @Transactional
    public Book addBook(Book book) {
        if (book.getAvailableQuantity() == null) {
            book.setAvailableQuantity(book.getTotalQuantity());
        }
        save(book);
        return book;
    }

    @Override
    @Transactional
    public Book updateBook(Book book) {
        updateById(book);
        return book;
    }

    @Override
    @Transactional
    public void deleteBook(Long id) {
        removeById(id);
    }

    @Override
    public Book getBookById(Long id) {
        return getById(id);
    }

    @Override
    @Transactional
    public void updateAvailableQuantity(Long bookId, int quantity) {
        Book book = getById(bookId);
        if (book != null) {
            book.setAvailableQuantity(book.getAvailableQuantity() + quantity);
            updateById(book);
        }
    }
}
