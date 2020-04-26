package com.myiothome;

import com.fasterxml.jackson.databind.util.ArrayBuilders;
import com.myiothome.dao.elasticsearch.DiscussPostRepository;
import com.myiothome.entity.DiscussPost;
import com.myiothome.service.DiscussPostService;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchResultMapper;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
//因为是测试类，所以需要使用和main中相同的环境
@ContextConfiguration(classes = MyiothomeApplication.class)
public class ElasticSearchTests {
    @Autowired
    DiscussPostRepository discussPostRepository;
    @Autowired
    DiscussPostService discussPostService;
    @Autowired
    ElasticsearchTemplate elasticsearchTemplate;

    @Test
    public void testInsert(){
        discussPostRepository.save(discussPostService.findDiscussPostByPostId(18));
        discussPostRepository.save(discussPostService.findDiscussPostByPostId(19));
        discussPostRepository.save(discussPostService.findDiscussPostByPostId(20));
        discussPostRepository.save(discussPostService.findDiscussPostByPostId(21));
    }

    @Test
    public void insertList(){
        discussPostRepository.saveAll(discussPostService.findDiscussPosts(4,0,0));
    }

    @Test
    public void testUpdate(){
        DiscussPost discussPost = discussPostService.findDiscussPostByPostId(18);
        discussPost.setContent("我就是不爽！");
        discussPostRepository.save(discussPost);
    }

    @Test
    public void deleteTest(){
        discussPostRepository.deleteById(18);
        //discussPostRepository.deleteAll();
    }

    @Test
    public void testSearchByRepository(){
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.multiMatchQuery("哈哈","content","title"))
                .withSort(SortBuilders.fieldSort("type").order(SortOrder.DESC))//按照类别，分数，创建时间排序
                .withSort(SortBuilders.fieldSort("score").order(SortOrder.DESC))
                .withSort(SortBuilders.fieldSort("createTime").order(SortOrder.DESC))
                .withPageable(PageRequest.of(0,10))//一页，十个数据
                .withHighlightFields(
                        new HighlightBuilder.Field("title").preTags("<em>").postTags("</em"),//搜索结果会前后增减em显示红色
                        new HighlightBuilder.Field("content").preTags("<em>").postTags("</em")
                ).build();

        Page<DiscussPost> page = discussPostRepository.search(searchQuery);
        System.out.println(page.getTotalElements());
        System.out.println(page.getTotalPages());
        System.out.println(page.getNumber());
        System.out.println(page.getSize());

        for(DiscussPost discussPost:page){
            System.out.println(discussPost);
        }
    }

    //为搜索结果加上高亮显示
    @Test
    public void testSearchByTemplate(){//前后都和testSearchByRepository一样，中间page设置不一样
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.multiMatchQuery("哈哈","content","title"))
                .withSort(SortBuilders.fieldSort("type").order(SortOrder.DESC))//按照类别，分数，创建时间排序
                .withSort(SortBuilders.fieldSort("score").order(SortOrder.DESC))
                .withSort(SortBuilders.fieldSort("createTime").order(SortOrder.DESC))
                .withPageable(PageRequest.of(0,10))//一页，十个数据
                .withHighlightFields(
                        new HighlightBuilder.Field("title").preTags("<em>").postTags("</em"),//搜索结果会前后增减em显示红色
                        new HighlightBuilder.Field("content").preTags("<em>").postTags("</em")
                ).build();

        Page<DiscussPost> page = elasticsearchTemplate.queryForPage(searchQuery, DiscussPost.class, new SearchResultMapper() {
            @Override
            public <T> AggregatedPage<T> mapResults(SearchResponse response, Class<T> aClass, Pageable pageable) {
                SearchHits hits = response.getHits();
                if (hits.getTotalHits() <= 0) {
                    return null;
                }

                List<DiscussPost> list = new ArrayList<>();
                for (SearchHit hit : hits) {
                    DiscussPost post = new DiscussPost();

                    String id = hit.getSourceAsMap().get("id").toString();
                    post.setId(Integer.valueOf(id));

                    String userId = hit.getSourceAsMap().get("userId").toString();
                    post.setUserId(Integer.valueOf(userId));

                    String title = hit.getSourceAsMap().get("title").toString();
                    post.setTitle(title);

                    String content = hit.getSourceAsMap().get("content").toString();
                    post.setContent(content);

                    String status = hit.getSourceAsMap().get("status").toString();
                    post.setStatus(Integer.valueOf(status));

                    String createTime = hit.getSourceAsMap().get("createTime").toString();
                    post.setCreateTime(new Date(Long.valueOf(createTime)));

                    String commentCount = hit.getSourceAsMap().get("commentCount").toString();
                    post.setCommentCount(Integer.valueOf(commentCount));

                    // 处理高亮显示的结果
                    HighlightField titleField = hit.getHighlightFields().get("title");
                    if (titleField != null) {
                        post.setTitle(titleField.getFragments()[0].toString());
                    }

                    HighlightField contentField = hit.getHighlightFields().get("content");
                    if (contentField != null) {
                        post.setContent(contentField.getFragments()[0].toString());
                    }

                    list.add(post);
                }

                return new AggregatedPageImpl(list, pageable,
                        hits.getTotalHits(), response.getAggregations(), response.getScrollId(), hits.getMaxScore());
            }

            @Override
            public <T> T mapSearchHit(SearchHit searchHit, Class<T> aClass) {
                return  null;
            }
        });

        System.out.println(page.getTotalElements());
        System.out.println(page.getTotalPages());
        System.out.println(page.getNumber());
        System.out.println(page.getSize());

        for(DiscussPost discussPost:page){
            System.out.println(discussPost);
        }
    }
}
