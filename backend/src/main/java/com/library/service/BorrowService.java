package com.library.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.library.dto.BorrowRecordDTO;
import com.library.entity.BorrowRecord;

public interface BorrowService extends IService<BorrowRecord> {
    IPage<BorrowRecord> getBorrowList(int page, int size, Long userId, String status);
    IPage<BorrowRecordDTO> getCurrentUserBorrowList(int page, int size);
    IPage<BorrowRecordDTO> getUserBorrowHistoryWithBook(Long userId, int page, int size);
    BorrowRecord borrowBook(Long userId, Long bookId);
    BorrowRecord returnBook(Long recordId);
    BorrowRecord renewBook(Long recordId);
    IPage<BorrowRecord> getUserBorrowHistory(Long userId, int page, int size);
}
