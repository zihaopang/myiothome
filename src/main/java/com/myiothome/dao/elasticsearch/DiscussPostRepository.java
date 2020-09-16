package com.myiothome.dao.elasticsearch;

import com.myiothome.entity.DiscussPost;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

//继承ElasticsearchRepository，声明搜索的类别和主键类型
@Repository//注解不是Mapper
public interface DiscussPostRepository extends ElasticsearchRepository<DiscussPost, Integer> {
}
