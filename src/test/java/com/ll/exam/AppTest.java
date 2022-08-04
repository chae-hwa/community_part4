package com.ll.exam;

import com.ll.exam.article.dto.ArticleDto;
import com.ll.exam.article.service.ArticleService;
import com.ll.exam.mymap.MyMap;
import com.ll.exam.util.Ut;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AppTest {
   @BeforeAll
   public void BeforeAll() {
      MyMap myMap = Container.getObj(MyMap.class);

      // 모든 DB 처리시에, 처리되는 SQL을 콘솔에 출력
      myMap.setDevMode(true);
   }

   @BeforeEach
   public void beforeEach() {
      // 게시물 테이블을 깔끔하게 삭제한다.
      // DELETE FROM article; // 보다 TRUNCATE article; 로 삭제하는게 더 깔끔하고 흔적이 남지 않는다.
      truncateArticleTable();
      // 게시물 3개를 만든다.
      // 테스트에 필요한 샘플데이터를 만든다고 보면 된다.
      makeArticleTestData();
   }

   private void makeArticleTestData() {
      MyMap myMap = Container.getObj(MyMap.class);

      IntStream.rangeClosed(1, 3).forEach(no -> {
         boolean isBlind = false;
         String title = "제목%d".formatted(no);
         String body = "내용%d".formatted(no);

         myMap.run("""
                    INSERT INTO article
                    SET createdDate = NOW(),
                    modifiedDate = NOW(),
                    title = ?,
                    `body` = ?,
                    isBlind = ?
                    """, title, body, isBlind);
      });
   }

   private void truncateArticleTable() {
      MyMap myMap = Container.getObj(MyMap.class);
      // 테이블을 깔끔하게 지워준다.
      myMap.run("TRUNCATE article");
   }
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

   @Test
   public void write(){
      ArticleService articleService = Container.getObj(ArticleService.class);

      long newArticleId = articleService.write("제목 new", "내용 new", false);

      ArticleDto articleDto = articleService.getArticleById(newArticleId);

      assertThat(articleDto.getId()).isEqualTo(newArticleId);
      assertThat(articleDto.getTitle()).isEqualTo("제목 new");
      assertThat(articleDto.getBody()).isEqualTo("내용 new");
      assertThat(articleDto.getCreatedDate()).isNotNull();
      assertThat(articleDto.getModifiedDate()).isNotNull();
      assertThat(articleDto.isBlind()).isEqualTo(false);
   }

   @Test
   public void modify(){
      ArticleService articleService = Container.getObj(ArticleService.class);

      articleService.modify(1,"제목 new","내용 new",true);

      ArticleDto articleDto = articleService.getArticleById(1);

      assertThat(articleDto.getId()).isEqualTo(1);
      assertThat(articleDto.getTitle()).isEqualTo("제목 new");
      assertThat(articleDto.getBody()).isEqualTo("내용 new");
      assertThat(articleDto.isBlind()).isNotNull();


      long diffSeconds = ChronoUnit.SECONDS.between(articleDto.getModifiedDate(), LocalDateTime.now());
      assertThat(diffSeconds).isLessThanOrEqualTo(1L);
   }
   @Test
   public void delete(){
      ArticleService articleService = Container.getObj(ArticleService.class);

      articleService.delete(1);

      ArticleDto articleDto = articleService.getArticleById(1);

      assertThat(articleDto).isNull();
   }

}

