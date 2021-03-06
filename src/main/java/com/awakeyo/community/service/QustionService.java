package com.awakeyo.community.service;

import com.awakeyo.community.mapper.UserMapper;
import com.awakeyo.community.pojo.PageResult;
import com.awakeyo.community.pojo.Question;
import com.awakeyo.community.pojo.User;
import com.awakeyo.community.pojo.dto.CommentDTO;
import com.awakeyo.community.pojo.dto.QuestionDTO;
import com.awakeyo.community.exception.CustomizeException;
import com.awakeyo.community.exception.QuestionErrorCode;
import com.awakeyo.community.mapper.QuestionMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author awakeyoyoyo
 * @className QustionService
 * @description TODO
 * @date 2019-12-04 23:08
 */
@Service
public class QustionService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private QuestionMapper questionMapper;
    @Autowired
    private CommentReplyService commentReplyService;
    public PageResult<QuestionDTO> getList(Integer pageNo, Integer pageSize) {
        Integer itemCount=questionMapper.selectAll();
        int  pageCount;
        if (itemCount/pageSize==0){
            pageCount=1;
        }else if (itemCount%pageSize==0){
            pageCount=itemCount/pageSize;
        }else {
            pageCount=itemCount/pageSize+1;
        }
        if (pageNo<1){
            pageNo=1;
        }
        if (pageNo>pageCount){
            pageNo=pageCount;
        }
        Integer pageBegin=pageSize*(pageNo-1);
        List<Question> questions=questionMapper.selectList(pageBegin,pageSize);
        List<QuestionDTO> questionDTOS=new ArrayList<>();
        for (Question question:questions) {
            User user=userMapper.selectByPrimaryKey(question.getCreator());
            QuestionDTO questionDTO=new QuestionDTO();
            BeanUtils.copyProperties(question,questionDTO);
            questionDTO.setUser(user);
            questionDTOS.add(questionDTO);
        }
        PageResult<QuestionDTO> pageResult=new PageResult<>();
        pageResult.init(pageCount,pageNo);
        pageResult.setReslts(questionDTOS);

        return pageResult;
    }
    public PageResult<QuestionDTO> getListSearch(String search,Integer pageNo, Integer pageSize) {
        search=StringUtils.replace(search," ","|");
        Integer itemCount=questionMapper.selectAllSearch(search);
        int  pageCount;
        if (itemCount/pageSize==0){
            pageCount=1;
        }else if (itemCount%pageSize==0){
            pageCount=itemCount/pageSize;
        }else {
            pageCount=itemCount/pageSize+1;
        }
        if (pageNo<1){
            pageNo=1;
        }
        if (pageNo>pageCount){
            pageNo=pageCount;
        }
        Integer pageBegin=pageSize*(pageNo-1);
        List<Question> questions=questionMapper.selectListSearch(search,pageBegin,pageSize);
        List<QuestionDTO> questionDTOS=new ArrayList<>();
        for (Question question:questions) {
            User user=userMapper.selectByPrimaryKey(question.getCreator());
            QuestionDTO questionDTO=new QuestionDTO();
            BeanUtils.copyProperties(question,questionDTO);
            questionDTO.setUser(user);
            questionDTOS.add(questionDTO);
        }
        PageResult<QuestionDTO> pageResult=new PageResult<>();
        pageResult.init(pageCount,pageNo);
        pageResult.setReslts(questionDTOS);

        return pageResult;
    }
    public PageResult<QuestionDTO> getListByUserid(Integer id, Integer pageNo, Integer pageSize) {
        Integer itemCount=questionMapper.selectAllUser(id);
        int  pageCount;
        if (itemCount/pageSize==0){
            pageCount=1;
        }else if (itemCount%pageSize==0){
            pageCount=itemCount/pageSize;
        }else {
            pageCount=itemCount/pageSize+1;
        }
        if (pageNo<1){
            pageNo=1;
        }
        if (pageNo>pageCount){
            pageNo=pageCount;
        }
        Integer pageBegin=pageSize*(pageNo-1);
        List<Question> questions=questionMapper.selectListByUser(id,pageBegin,pageSize);
        List<QuestionDTO> questionDTOS=new ArrayList<>();
        for (Question question:questions) {
            User user=userMapper.selectByPrimaryKey(question.getCreator());
            QuestionDTO questionDTO=new QuestionDTO();
            BeanUtils.copyProperties(question,questionDTO);
            questionDTO.setUser(user);
            questionDTOS.add(questionDTO);
        }
        PageResult<QuestionDTO> pageResult=new PageResult<>();
        pageResult.init(pageCount,pageNo);
        pageResult.setReslts(questionDTOS);
        return pageResult;
    }

    public QuestionDTO getById(Integer id) {
        Question question=questionMapper.selectByPrimaryKey(id);
        if (question==null){
            throw new CustomizeException(QuestionErrorCode.QUESTION_NOT_FOUND.getMessage());
        }
        QuestionDTO questionDTO=new QuestionDTO();
        BeanUtils.copyProperties(question,questionDTO);
        User user=userMapper.selectByPrimaryKey(question.getCreator());
        questionDTO.setUser(user);
        return questionDTO;
    }

    public void insertOrUpdate(Question question) {
        if (question.getId()==null){
            question.setGmtCreate(new Date().getTime());
            question.setGmtModified(new Date().getTime());
            question.setLikeCount(0);
            question.setViewCount(0);
            question.setCommentCount(0);
            questionMapper.insert(question);
        }
        else {
            Question updateQuestion=new Question();
            updateQuestion.setId(question.getId());
            updateQuestion.setDecription(question.getDecription());
            updateQuestion.setTitle(question.getTitle());
            updateQuestion.setTag(question.getTag());
            updateQuestion.setGmtModified(new Date().getTime());
            int row=questionMapper.updateByPrimaryKeySelective(updateQuestion);
            if (row!=1){
                throw new CustomizeException(QuestionErrorCode.QUESTION_NOT_FOUND.getMessage());
            }
        }
    }

    public QuestionDTO getByIdAndIncView(Integer id) {
        int row= questionMapper.updateViewCount(id);
        if (row<=0){
            throw new CustomizeException(QuestionErrorCode.QUESTION_NOT_FOUND.getMessage());
        }
        QuestionDTO questionDTO=new QuestionDTO();
        Question question=questionMapper.selectByPrimaryKey(id);
        BeanUtils.copyProperties(question,questionDTO);
        User user=userMapper.selectByPrimaryKey(question.getCreator());
        questionDTO.setUser(user);
        //评论回复=。=
        List<CommentDTO> comments=commentReplyService.getCommentsReplyTopicId("question",question.getId());
        if (comments==null){
            throw new CustomizeException("别乱调戏接口-。-");
        }
        questionDTO.setCommentDTOs(comments);
        return questionDTO;
    }

    public List<Question> selectRelater(Integer id, String tag) {
        if (StringUtils.isEmpty(tag)){
            return new ArrayList<>();
        }
        tag=StringUtils.replace(tag,",","|");
        List<Question> questions=questionMapper.selectRelated(id,tag);
        return questions;
    }
}
