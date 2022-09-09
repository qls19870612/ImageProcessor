package com.ejjiu.image.controllers.images.icon;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.ejjiu.common.file.FileOperator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 *
 * 创建人  liangsong
 * 创建时间 2022/08/24 10:35
 */
public class PhpArray2Json {
    public static char mLeft = '[';
    public static char mRight = ']';
    public static final Pattern minMiddleBracket = Pattern.compile("\\[([^\\[\\]]+)]");
    private static final Logger logger = LoggerFactory.getLogger(PhpArray2Json.class);
    
    public static void main(String[] args) {
        File root = new File("D:\\workspace\\icon-workshop\\app\\Platforms");
        File toRoot = new File("D:\\workspace\\JAVA-TOOLS-BC\\src\\main\\resource\\iconConfig");
        FileOperator.traverseFiles(root, entry -> {
            if (entry.getName().equals("Icon.php") || entry.getName().equals("Splash.php")) {
                toJson(entry, toRoot, root);
                return false;
            }
            return true;
        });
    }
    
    private static void toJson(File entry, File toRoot, File root) {
        String s = FileOperator.readFiles(entry);
        Matcher matcher = minMiddleBracket.matcher(s);
        JSONArray jsonArray = new JSONArray();
        while (matcher.find()) {
            
            String group = matcher.group(1).trim();
            String[] propertyArr = group.split(",");
            
            JSONObject jsonObject = new JSONObject();
            for (String kv : propertyArr) {
                String[] kvArr = kv.split("=>");
                if (kvArr.length < 2) {
                    logger.debug("toJson kv:{}", kv);
                    continue;
                }
                String key = kvArr[0].trim().replaceAll("\'", "");
                String valueTrim = kvArr[1].trim();
                if (valueTrim.endsWith("\'")) {
                    
                    String value = valueTrim.replaceAll("\'", "");
                    jsonObject.put(key, value);
                } else if (valueTrim.contains(".")) {
                    double value = Double.parseDouble(valueTrim);
                    jsonObject.put(key, value);
                } else if (valueTrim.equals("true")) {
                    
                    jsonObject.put(key, true);
                } else if (valueTrim.equals("false")) {
                    jsonObject.put(key, false);
                } else if (valueTrim.equals("Design::IOS_LEVEL_5")) {
                    jsonObject.put(key, "5+");
                    
                } else if (valueTrim.equals("Design::IOS_LEVEL_7")) {
                    jsonObject.put(key, "7+");
                } else {
                    int value = Integer.parseInt(valueTrim);
                    jsonObject.put(key, value);
                    
                    
                }
                
            }
            if (jsonObject.size() > 0) {
                jsonArray.add(jsonObject);
            }
        }
        if (jsonArray.size() > 0) {
            String shortPath = entry.getAbsolutePath().replace(root.getAbsolutePath(), "");
            File target = new File(toRoot.getAbsolutePath() + shortPath.replace(".php", ".json"));
            FileOperator.writeFile(target, jsonArray.toString(SerializerFeature.PrettyFormat));
        }
        
    }
}
