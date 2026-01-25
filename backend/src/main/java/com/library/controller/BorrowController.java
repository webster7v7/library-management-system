package com.library.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.library.common.Result;
import com.library.entity.BorrowRecord;
import com.library.service.BorrowService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/borrow")
@Tag(name = "借阅管理", description = "借阅相关接口")
public class BorrowController {

    private static final Logger logger = LoggerFactory.getLogger(BorrowController.class);

    @Autowired
    private BorrowService borrowService;

    @GetMapping
    @Operation(summary = "获取借阅列表")
    public Result<IPage<BorrowRecord>> getBorrowList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String status) {
        try {
            logger.info("Fetching borrow list - page: {}, size: {}, userId: {}, status: {}", page, size, userId, status);
            IPage<BorrowRecord> result = borrowService.getBorrowList(page, size, userId, status);
            return Result.success(result);
        } catch (Exception e) {
            logger.error("Failed to fetch borrow list", e);
            return Result.error(500, "获取借阅列表失败: " + e.getMessage());
        }
    }

    @PostMapping("/{bookId}")
    @Operation(summary = "借阅图书")
    public Result<BorrowRecord> borrowBook(@PathVariable Long bookId) {
        try {
            // 获取当前登录用户的ID
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Long userId = (Long) authentication.getPrincipal();

            logger.info("User {} borrowing book - bookId: {}", userId, bookId);
            BorrowRecord record = borrowService.borrowBook(userId, bookId);
            return Result.success("借阅成功", record);
        } catch (RuntimeException e) {
            logger.error("Failed to borrow book - bookId: {}", bookId, e);
            return Result.error(400, e.getMessage());
        } catch (Exception e) {
            logger.error("Failed to borrow book - bookId: {}", bookId, e);
            return Result.error(500, "借阅失败: " + e.getMessage());
        }
    }

    @PutMapping("/return/{recordId}")
    @Operation(summary = "归还图书")
    public Result<BorrowRecord> returnBook(@PathVariable Long recordId) {
        try {
            logger.info("Returning book - recordId: {}", recordId);
            BorrowRecord record = borrowService.returnBook(recordId);
            return Result.success("归还成功", record);
        } catch (RuntimeException e) {
            logger.error("Failed to return book - recordId: {}", recordId, e);
            return Result.error(400, e.getMessage());
        } catch (Exception e) {
            logger.error("Failed to return book - recordId: {}", recordId, e);
            return Result.error(500, "归还失败: " + e.getMessage());
        }
    }

    @PutMapping("/renew/{recordId}")
    @Operation(summary = "续借图书")
    public Result<BorrowRecord> renewBook(@PathVariable Long recordId) {
        try {
            logger.info("Renewing book - recordId: {}", recordId);
            BorrowRecord record = borrowService.renewBook(recordId);
            return Result.success("续借成功", record);
        } catch (RuntimeException e) {
            logger.error("Failed to renew book - recordId: {}", recordId, e);
            return Result.error(400, e.getMessage());
        } catch (Exception e) {
            logger.error("Failed to renew book - recordId: {}", recordId, e);
            return Result.error(500, "续借失败: " + e.getMessage());
        }
    }

    @GetMapping("/history")
    @Operation(summary = "获取借阅历史")
    public Result<IPage<BorrowRecord>> getBorrowHistory(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Long userId = null;
        try {
            // 获取当前登录用户的ID
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            userId = (Long) authentication.getPrincipal();

            logger.info("Fetching borrow history for user - userId: {}", userId);
            IPage<BorrowRecord> result = borrowService.getUserBorrowHistory(userId, page, size);
            return Result.success(result);
        } catch (Exception e) {
            logger.error("Failed to fetch borrow history - userId: {}", userId, e);
            return Result.error(500, "获取借阅历史失败: " + e.getMessage());
        }
    }
}
