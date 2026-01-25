package com.library.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.library.dto.BorrowRecordDTO;
import com.library.entity.Book;
import com.library.entity.BorrowRecord;
import com.library.mapper.BorrowRecordMapper;
import com.library.service.BookService;
import com.library.service.BorrowService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

@Service
public class BorrowServiceImpl extends ServiceImpl<BorrowRecordMapper, BorrowRecord> implements BorrowService {

    private static final Logger logger = LoggerFactory.getLogger(BorrowServiceImpl.class);

    @Autowired
    private BookService bookService;

    @Autowired
    private BorrowRecordMapper borrowRecordMapper;

    @Override
    public IPage<BorrowRecord> getBorrowList(int page, int size, Long userId, String status) {
        Page<BorrowRecord> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<BorrowRecord> wrapper = new LambdaQueryWrapper<>();

        if (userId != null) {
            wrapper.eq(BorrowRecord::getUserId, userId);
        }
        if (status != null && !status.isEmpty()) {
            wrapper.eq(BorrowRecord::getStatus, status);
        }

        wrapper.orderByDesc(BorrowRecord::getBorrowDate);
        return page(pageParam, wrapper);
    }

    @Override
    public IPage<BorrowRecordDTO> getCurrentUserBorrowList(int page, int size) {
        Long userId = getCurrentUserId();
        logger.info("Fetching current user borrow list - userId: {}, page: {}, size: {}", userId, page, size);
        Page<BorrowRecordDTO> pageParam = new Page<>(page, size);
        return borrowRecordMapper.selectBorrowRecordsWithBook(pageParam, userId, "BORROWED");
    }

    @Override
    public IPage<BorrowRecordDTO> getUserBorrowHistoryWithBook(Long userId, int page, int size) {
        logger.info("Fetching user borrow history with book info - userId: {}, page: {}, size: {}", userId, page, size);
        Page<BorrowRecordDTO> pageParam = new Page<>(page, size);
        return borrowRecordMapper.selectUserBorrowHistory(pageParam, userId);
    }

    @Override
    @Transactional
    public BorrowRecord borrowBook(Long userId, Long bookId) {
        logger.info("Borrowing book - userId: {}, bookId: {}", userId, bookId);
        Book book = bookService.getBookById(bookId);
        if (book == null) {
            throw new RuntimeException("图书不存在");
        }
        if (book.getAvailableQuantity() <= 0) {
            throw new RuntimeException("图书库存不足");
        }

        LambdaQueryWrapper<BorrowRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BorrowRecord::getUserId, userId)
               .eq(BorrowRecord::getBookId, bookId)
               .eq(BorrowRecord::getStatus, "BORROWED");
        if (count(wrapper) > 0) {
            throw new RuntimeException("该图书已在借阅中");
        }

        BorrowRecord record = new BorrowRecord();
        record.setUserId(userId);
        record.setBookId(bookId);
        record.setBorrowDate(LocalDateTime.now());
        record.setDueDate(LocalDateTime.now().plusDays(30));
        record.setStatus("BORROWED");
        record.setRenewCount(0);
        save(record);

        bookService.updateAvailableQuantity(bookId, -1);
        logger.info("Book borrowed successfully - userId: {}, bookId: {}, new available quantity: {}", userId, bookId, book.getAvailableQuantity() - 1);
        return record;
    }

    @Override
    @Transactional
    public BorrowRecord returnBook(Long recordId) {
        logger.info("Returning book - recordId: {}", recordId);
        BorrowRecord record = getById(recordId);
        if (record == null) {
            throw new RuntimeException("借阅记录不存在");
        }
        if (!"BORROWED".equals(record.getStatus())) {
            throw new RuntimeException("该图书已归还");
        }

        record.setReturnDate(LocalDateTime.now());
        record.setStatus("RETURNED");
        updateById(record);

        bookService.updateAvailableQuantity(record.getBookId(), 1);
        logger.info("Book returned successfully - recordId: {}, bookId: {}", recordId, record.getBookId());
        return record;
    }

    @Override
    @Transactional
    public BorrowRecord renewBook(Long recordId) {
        logger.info("Renewing book - recordId: {}", recordId);
        BorrowRecord record = getById(recordId);
        if (record == null) {
            throw new RuntimeException("借阅记录不存在");
        }
        if (!"BORROWED".equals(record.getStatus())) {
            throw new RuntimeException("该图书已归还");
        }
        if (record.getRenewCount() >= 3) {
            throw new RuntimeException("续借次数已达上限");
        }

        record.setDueDate(record.getDueDate().plusDays(30));
        record.setRenewCount(record.getRenewCount() + 1);
        updateById(record);
        logger.info("Book renewed successfully - recordId: {}, new due date: {}", recordId, record.getDueDate());
        return record;
    }

    @Override
    public IPage<BorrowRecord> getUserBorrowHistory(Long userId, int page, int size) {
        Page<BorrowRecord> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<BorrowRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BorrowRecord::getUserId, userId);
        wrapper.orderByDesc(BorrowRecord::getBorrowDate);
        return page(pageParam, wrapper);
    }

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof Long) {
            return (Long) authentication.getPrincipal();
        }
        throw new RuntimeException("无法获取当前用户信息");
    }
}
