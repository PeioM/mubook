package com.libumu.mubook.dao.news;

import com.libumu.mubook.entities.News;

import java.util.List;

public interface NewsDao {

    public List<News> getAllNews();
    public List<News> getActiveNews();
    public News getNews(long id);
    public void editNews(News news);
    public void deleteNews(long id);
    public void deleteNews(News news);
    public void addNews(News news);
    News getNewsByDescription(String description);

}
