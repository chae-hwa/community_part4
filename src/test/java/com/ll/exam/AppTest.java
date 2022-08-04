package com.ll.exam;

import com.ll.exam.article.controller.ArticleController;
import com.ll.exam.article.dto.ArticleDto;
import com.ll.exam.article.repository.ArticleRepository;
import com.ll.exam.article.service.ArticleService;
import com.ll.exam.home.controller.HomeController;
import com.ll.exam.util.Ut;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class AppTest {
   @Test
    public void 존재한다(){
       ArticleService articleService = Container.getObj(ArticleService.class);

       assertThat(articleService).isNotNull();
   }

   @Test
    public void getArticles(){
       ArticleService articleService = Container.getObj(ArticleService.class);

       List<ArticleDto> articleDtoList = articleService.getArticles();
       assertThat(articleDtoList.size()).isEqualTo(3);
   }

   @Test
   public void getArticleById(){
      ArticleService articleService = Container.getObj(ArticleService.class);

      ArticleDto articleDto = articleService.getArticleById(1);

      assertThat(articleDto.getId()).isEqualTo(1L);
      assertThat(articleDto.getTitle()).isEqualTo("제목1");
      assertThat(articleDto.getBody()).isEqualTo("내용1");
      assertThat(articleDto.getCreatedDate()).isNotNull();
      assertThat(articleDto.getModifiedDate()).isNotNull();
      assertThat(articleDto.isBlind()).isFalse();
   }

   @Test
   public void Count(){
      ArticleService articleService = Container.getObj(ArticleService.class);

      long articlesCount = articleService.getArticlesCount();
      assertThat(articlesCount).isEqualTo(3);
   }
}

