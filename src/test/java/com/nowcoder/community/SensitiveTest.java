package com.nowcoder.community;

import com.nowcoder.community.util.SensitiveFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class SensitiveTest {
    @Autowired
    SensitiveFilter sensitiveFilter;

    @Test
    public void sensitivetest(){
        String text = "我是$$黄$$牛，赌$$博找我，日你$$滴";
        String filter_text = sensitiveFilter.filter(text);
        System.out.println(filter_text);
    }
}
