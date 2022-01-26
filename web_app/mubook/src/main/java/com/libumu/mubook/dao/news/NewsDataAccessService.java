package com.libumu.mubook.dao.news;

import com.libumu.mubook.entities.News;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NewsDataAccessService implements NewsDao {

    @Autowired
    private NewsRepository repository;

    @Override
    public List<News> getAllNews() {
        return (List<News>) repository.findAll();
    }

    @Override
    public List<News> getActiveNews() {
        java.util.Date utilDate = new  java.util.Date();
        java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
        return repository.findAllByInitDateIsLessThanEqualAndEndDateGreaterThanEqual(sqlDate,sqlDate);
    }

    @Override
    public News getNews(long id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public void editNews(News news) {
        repository.save(news);
    }

    @Override
    public void deleteNews(long id) {
        repository.deleteById(id);
    }

    @Override
    public void deleteNews(News news) {
        repository.delete(news);
    }

    @Override
    public void addNews(News news) {
        repository.save(news);
    }

    @Override
    public News getNewsByDescription(String description) {
        return repository.getNewsByDescription(description);
    }
}
