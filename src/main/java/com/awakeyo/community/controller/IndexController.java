package com.awakeyo.community.controller;
import com.awakeyo.community.dto.PageResult;
import com.awakeyo.community.dto.Question;
import com.awakeyo.community.dto.QuestionDTO;
import com.awakeyo.community.dto.User;
import com.awakeyo.community.mapper.QuestionMapper;
import com.awakeyo.community.mapper.UserMapper;
import com.awakeyo.community.service.QustionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author awakeyoyoyo
 * @className HelloController
 * @description TODO
 * @date 2019-11-30 21:13
 */
@Controller
public class IndexController {
    @Autowired
    private QustionService qustionService;
    /**
     * Method Description
     * @author awakeyoyoyo
     * @date 2019-11-30
     * @params [name, model]
     * @return java.lang.String
     */
    @GetMapping("/")
    public String index(Model model,
                        @RequestParam(name = "pageNo",defaultValue = "1" ,required = false) Integer pageNo,
                        @RequestParam(name = "pageSize",defaultValue ="5",required = false) Integer pageSize){
        PageResult<QuestionDTO> pageResult=qustionService.getList(pageNo,pageSize);
        model.addAttribute("pageResult",pageResult);
        return "index";
    }


}
