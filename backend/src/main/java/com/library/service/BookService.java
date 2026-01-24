package com.library.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.library.entity.Book;

public interface BookService extends IService<Book> {
    IPage<Book> getBookList(int page, int size, String keyword);
    Book addBook(Book book);
    Book updateBook(Book book);
    void deleteBook(Long id);
    Book getBookById(Long id);
    void updateAvailableQuantity(Long bookId, int quantity);
}
