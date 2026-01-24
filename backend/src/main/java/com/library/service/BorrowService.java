package com.library.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.library.entity.BorrowRecord;

public interface BorrowService extends IService<BorrowRecord> {
    IPage<BorrowRecord> getBorrowList(int page, int size, Long userId, String status);
    BorrowRecord borrowBook(Long userId, Long bookId);
    BorrowRecord returnBook(Long recordId);
    BorrowRecord renewBook(Long recordId);
    IPage<BorrowRecord> getUserBorrowHistory(Long userId, int page, int size);
}
