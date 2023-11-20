package com.nowcoder.community.util;

import jakarta.annotation.PostConstruct;
import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

@Component
public class SensitiveFilter {

    private static final Logger logger = LoggerFactory.getLogger(SensitiveFilter.class);

    // 替换符
    private static final String REPLACEMENT = "***";

    // 根节点
    private TrieNode rootNode = new TrieNode();

    // 初始化 , 构造器构造以后就初始化
    @PostConstruct
    private void init(){
        try(
                InputStream is = this.getClass().getClassLoader().getResourceAsStream("sensitive-words.txt");
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        ){
            String keyword;
            while((keyword = reader.readLine())!= null){
                // 添加到前缀树
                this.addKeyword(keyword);
            }
        }catch (IOException e){
            logger.error("加载敏感词失败" + e.getMessage());
        }
    }

    // 将一个敏感词加入到前缀树
    private void addKeyword(String keyword){
        TrieNode tempNode = rootNode;
        for(int i = 0; i < keyword.length(); i++){
            char c = keyword.charAt(i);
            TrieNode subnode = tempNode.getSubNode(c);
            if(subnode == null){
                // 初始化子节点
                subnode = new TrieNode();
                tempNode.addSubNode(c,subnode);
            }

            // 指向子节点， 进入下一轮循环
            tempNode = subnode;

            // 设置结束标识
            if(i == keyword.length() - 1){
                tempNode.setKeywordEnd(true);
            }
        }
    }

    /**
     * 过滤敏感词
     * @param text 带过滤的文本
     * @return 过滤后的文本
     * */
    public String filter(String text){
        if(StringUtils.isBlank(text)){
            return null;
        }
        // 指针1
        TrieNode tempNode = rootNode;
        // 指针2 字符开始指针
        int begin = 0;
        // 指针3 字符结尾指针
        int end = 0;
        StringBuilder sb = new StringBuilder();
        while(begin < text.length()) {
            char c = text.charAt(end);
            // 判断是否为特殊字符
            if (isSymbol(c)) {
                // 若begin位于根节点, 将此符号计入结果, 让指针2向下走一步
                if (tempNode == rootNode) {
                    sb.append(c);
                    begin++;
                }
                // 无论符号在开头或者中
                end++;
                continue;
            }

            // 检查下级节点
            tempNode = tempNode.getSubNode(c);
            if (tempNode == null) {
                // 以begin开头的字符串不是敏感词
                sb.append(text.charAt(begin));
                // 进入下一个位置
                end = ++begin;
                // 重新指向根节点
                tempNode = rootNode;
            } else if (tempNode.isKeywordEnd()) {
                sb.append(REPLACEMENT);
                //进入下一个位置
                begin = ++end;
                // 重新指向根节点
                tempNode = rootNode;
            } else {
                // 检查下一个字符
                if (end < text.length() - 1) {
                    end++;
                } else if (end == text.length() - 1) {
                    sb.append(text.charAt(begin));
                    end = ++begin;
                }
            }
        }
        // 将最后一批字符计入结果
        sb.append(text.substring(begin));
        return sb.toString();
    }

    // 判断是否为特殊符号
    private boolean isSymbol(Character c){
        //  0x2E80 ~ 0x9FFF 为东亚文字范围
        return !CharUtils.isAsciiAlphanumeric(c) && (c < 0x2E80 || c > 0x9FFF);
    }
    // 定义前缀树
    private class TrieNode{

        // 关键词结束的表示
        private boolean isKeywordEnd = false;

        // 孩子节点(key, 下级节点字符， value 是孩子节点
        private Map<Character, TrieNode> subNodes = new HashMap<>();

        public boolean isKeywordEnd() {
            return isKeywordEnd;
        }

        public void setKeywordEnd(boolean keywordEnd) {
            isKeywordEnd = keywordEnd;
        }

        // 添加子节点
        public void addSubNode(Character c, TrieNode node){
            subNodes.put(c, node);
        }

        // 获取子节点
        public TrieNode getSubNode(Character c){
            return subNodes.get(c);
        }
    }// TrieNode

}
