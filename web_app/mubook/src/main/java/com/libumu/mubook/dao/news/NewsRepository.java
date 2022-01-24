package com.libumu.mubook.dao.news;

import java.sql.Date;
import java.util.List;

import com.libumu.mubook.entities.News;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NewsRepository extends JpaRepository<News, Long> {
    List<News> findAllByInitDateIsLessThanEqualAndEndDateGreaterThanEqual(
            Date initDate, Date endDate);
    News getNewsByDescription(String description);
}
