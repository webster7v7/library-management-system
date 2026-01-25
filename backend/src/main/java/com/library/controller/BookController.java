package com.library.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.library.common.Result;
import com.library.entity.Book;
import com.library.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/books")
@Tag(name = "图书管理", description = "图书相关接口")
public class BookController {

    private static final Logger logger = LoggerFactory.getLogger(BookController.class);

    @Autowired
    private BookService bookService;

    @GetMapping
    @Operation(summary = "获取图书列表")
    public Result<IPage<Book>> getBookList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword) {
        try {
            logger.info("Fetching book list - page: {}, size: {}, keyword: {}", page, size, keyword);
            IPage<Book> result = bookService.getBookList(page, size, keyword);
            return Result.success(result);
        } catch (Exception e) {
            logger.error("Failed to fetch book list", e);
            return Result.error(500, "获取图书列表失败: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取图书详情")
    public Result<Book> getBookById(@PathVariable Long id) {
        try {
            logger.info("Fetching book details - id: {}", id);
            Book book = bookService.getBookById(id);
            if (book == null) {
                return Result.error(404, "图书不存在");
            }
            return Result.success(book);
        } catch (Exception e) {
            logger.error("Failed to fetch book details - id: {}", id, e);
            return Result.error(500, "获取图书详情失败: " + e.getMessage());
        }
    }

    @PostMapping
    @Operation(summary = "添加图书")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Book> addBook(@RequestBody Book book) {
        try {
            logger.info("Adding new book - title: {}, isbn: {}", book.getTitle(), book.getIsbn());
            Book savedBook = bookService.addBook(book);
            return Result.success("添加成功", savedBook);
        } catch (RuntimeException e) {
            logger.error("Failed to add book - title: {}", book.getTitle(), e);
            return Result.error(400, e.getMessage());
        } catch (Exception e) {
            logger.error("Failed to add book - title: {}", book.getTitle(), e);
            return Result.error(500, "添加图书失败: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新图书")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Book> updateBook(@PathVariable Long id, @RequestBody Book book) {
        try {
            logger.info("Updating book - id: {}, title: {}", id, book.getTitle());
            book.setId(id);
            Book updatedBook = bookService.updateBook(book);
            return Result.success("更新成功", updatedBook);
        } catch (RuntimeException e) {
            logger.error("Failed to update book - id: {}", id, e);
            return Result.error(400, e.getMessage());
        } catch (Exception e) {
            logger.error("Failed to update book - id: {}", id, e);
            return Result.error(500, "更新图书失败: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除图书")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> deleteBook(@PathVariable Long id) {
        try {
            logger.info("Deleting book - id: {}", id);
            bookService.deleteBook(id);
            return Result.success("删除成功", null);
        } catch (RuntimeException e) {
            logger.error("Failed to delete book - id: {}", id, e);
            return Result.error(400, e.getMessage());
        } catch (Exception e) {
            logger.error("Failed to delete book - id: {}", id, e);
            return Result.error(500, "删除图书失败: " + e.getMessage());
        }
    }
}
