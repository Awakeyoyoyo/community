package com.awakeyo.community.mapper;

import com.awakeyo.community.pojo.Question;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface QuestionMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Question record);

    int insertSelective(Question record);

    Question selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Question record);

    int updateByPrimaryKeyWithBLOBs(Question record);

    int updateByPrimaryKey(Question record);

    List<Question> selectList(@Param("pageBegin") Integer pageBegin, @Param("pageSize") Integer pageSize);

    Integer selectAll();

    List<Question> selectListByUser(@Param("userId") Integer userId,@Param("pageBegin") Integer pageBegin, @Param("pageSize") Integer pageSize);

    Integer updateViewCount(Integer id);

    Integer updateReplyCountByTopicId(Integer topicId);

    List<Question> selectRelated(@Param("id") Integer id,@Param("tag") String tag);

    Integer selectAllSearch(String search);

    List<Question> selectListSearch(@Param("search") String search, @Param("pageBegin") Integer pageBegin, @Param("pageSize") Integer pageSize);

    Integer selectAllUser(Integer id);
}