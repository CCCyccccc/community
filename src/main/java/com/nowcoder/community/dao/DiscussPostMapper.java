package com.nowcoder.community.dao;

import com.nowcoder.community.entity.DiscussPost;
import org.apache.ibatis.annotations.Lang;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

// 分页查询结果
@Mapper
public interface DiscussPostMapper {
    // offset:每行起始行号，limit每页限制
    List<DiscussPost> selectDiscussPosts(int userId, int offset, int limit);
    //@Parm 用于给参数取别名
    //动态条件 且 有且只有一个条件才实现
    int selectDiscussPostRows(@Param("userId") int userId);
}
