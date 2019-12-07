package com.awakeyo.community.controller;

import com.awakeyo.community.dto.Question;
import com.awakeyo.community.dto.User;
import com.awakeyo.community.mapper.QuestionMapper;
import com.awakeyo.community.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * @author awakeyoyoyo
 * @className PublishController
 * @description TODO
 * @date 2019-12-03 20:33
 */
@Controller
public class PublishController {
    @Autowired
    private QuestionMapper questionMapper;
    @Autowired
    private UserMapper userMapper;
    @GetMapping("/publish")
    public String publish(){
        return "publish";
    }
    @PostMapping("/publish")
    public String doPublish(
            @RequestParam(value = "title",required = false) String title,
            @RequestParam(value = "description",required = false) String description,
            @RequestParam(value = "tag",required = false) String tag,
            HttpServletRequest request,
            Model model
    ){
        model.addAttribute("title",title);
        model.addAttribute("description",description);
        model.addAttribute("tag",tag);
        User user=null;
        Cookie[] cookies = request.getCookies();
        for (Cookie c : cookies) {
            if (c.getName().equals("token")) {
                String token = c.getValue();
                if (token != null) {
                    user = userMapper.findByToken(token);
                    if (user != null) {
                        request.getSession().setAttribute("user", user);
                    }
                    break;
                }
            }
        }
        if (user==null){
            model.addAttribute("error","用户未登陆！！！！");
            return "publish";
        }
        if (title==null||title==""){
            model.addAttribute("error","标题不能为空！！！！");
            return "publish";
        }
        if (description==null||description==""){
            model.addAttribute("error","问题描述不能为空！！！！");
            return "publish";
        }
        if (tag==null||tag==""){
            model.addAttribute("error","标签不能为空！！！！");
            return "publish";
        }
        Question question=new Question();
        question.setTitle(title);
        question.setDecription(description);
        question.setTag(tag);
        question.setCreator(user.getId());
        question.setLikeCount(0);
        question.setViewCount(0);
        question.setCommentCount(0);
        question.setGmtCreate(new Date().getTime());
        question.setGmtModified(new Date().getTime());
        questionMapper.insert(question);
        return "redirect:/";
    }
}