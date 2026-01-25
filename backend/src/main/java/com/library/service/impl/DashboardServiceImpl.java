package com.library.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.library.dto.DashboardResponse;
import com.library.entity.Book;
import com.library.entity.BorrowRecord;
import com.library.entity.User;
import com.library.mapper.BookMapper;
import com.library.mapper.BorrowRecordMapper;
import com.library.mapper.UserMapper;
import com.library.service.BookService;
import com.library.service.BorrowService;
import com.library.service.DashboardService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DashboardServiceImpl implements DashboardService {

    private static final Logger logger = LoggerFactory.getLogger(DashboardServiceImpl.class);

    @Autowired
    private BookMapper bookMapper;

    @Autowired
    private BorrowRecordMapper borrowRecordMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    public DashboardResponse getDashboardData() {
        logger.info("Fetching dashboard data");

        DashboardResponse response = new DashboardResponse();

        // 统计数据
        DashboardResponse.DashboardStats stats = new DashboardResponse.DashboardStats();

        // 总图书数
        Long totalBooksCount = bookMapper.selectCount(null);
        stats.setTotalBooks(totalBooksCount != null ? totalBooksCount.intValue() : 0);
        logger.debug("Total books count: {}", stats.getTotalBooks());

        // 可借阅数（所有图书的availableQuantity之和）
        List<Book> allBooks = bookMapper.selectList(null);
        int availableBooksCount = allBooks.stream()
                .mapToInt(Book::getAvailableQuantity)
                .sum();
        stats.setAvailableBooks(availableBooksCount);
        logger.debug("Available books count: {}", stats.getAvailableBooks());

        // 借阅中（status='BORROWED'的记录数）
        LambdaQueryWrapper<BorrowRecord> borrowedWrapper = new LambdaQueryWrapper<>();
        borrowedWrapper.eq(BorrowRecord::getStatus, "BORROWED");
        Long borrowedCount = borrowRecordMapper.selectCount(borrowedWrapper);
        stats.setBorrowedBooks(borrowedCount != null ? borrowedCount.intValue() : 0);
        logger.debug("Borrowed books count: {}", stats.getBorrowedBooks());

        // 用户总数
        Long totalUsersCount = userMapper.selectCount(null);
        stats.setTotalUsers(totalUsersCount != null ? totalUsersCount.intValue() : 0);
        logger.debug("Total users count: {}", stats.getTotalUsers());

        response.setStats(stats);

        // 最新借阅记录（最新的5条）
        Page<BorrowRecord> pageParam = new Page<>(1, 5);
        LambdaQueryWrapper<BorrowRecord> recordWrapper = new LambdaQueryWrapper<>();
        recordWrapper.orderByDesc(BorrowRecord::getBorrowDate);
        IPage<BorrowRecord> recordPage = borrowRecordMapper.selectPage(pageParam, recordWrapper);

        List<DashboardResponse.BorrowRecordDetail> recordDetails = recordPage.getRecords().stream()
                .map(record -> {
                    DashboardResponse.BorrowRecordDetail detail = new DashboardResponse.BorrowRecordDetail();
                    detail.setId(record.getId());

                    // 获取图书信息
                    Book book = bookMapper.selectById(record.getBookId());
                    detail.setBookTitle(book != null ? book.getTitle() : "未知图书");

                    // 获取用户信息
                    User user = userMapper.selectById(record.getUserId());
                    detail.setUsername(user != null ? user.getRealName() : "未知用户");

                    // 格式化日期
                    detail.setBorrowDate(record.getBorrowDate().toLocalDate().toString());

                    detail.setStatus(record.getStatus());

                    return detail;
                })
                .collect(Collectors.toList());

        response.setRecentRecords(recordDetails);
        logger.info("Dashboard data fetched successfully");
        return response;
    }
}
