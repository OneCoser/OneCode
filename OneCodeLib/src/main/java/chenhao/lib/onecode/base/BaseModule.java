package chenhao.lib.onecode.base;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.serializer.SerializerFeature;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import chenhao.lib.onecode.utils.StringUtils;

public abstract class BaseModule {

    @Override
    public String toString() {
        return BaseModule.toJSONString(this);
    }

    public JSONObject toJSONObject() {
        return BaseModule.parseObject(toString());
    }

    public Map<String,String> toStrMap(){
        JSONObject object=toJSONObject();
        Map<String,String> map=new HashMap<>();
        for (String key : object.keySet()) {
            String value =object.getString(key);
            map.put(key, value);
        }
        return map;
    }

    public static final String toJSONString(Object object) {
        if (null!=object){
            try {
                return JSON.toJSONString(object,
                        SerializerFeature.WriteNullStringAsEmpty,
                        SerializerFeature.WriteNullNumberAsZero,
                        SerializerFeature.WriteNullBooleanAsFalse,
                        SerializerFeature.WriteNullListAsEmpty,
                        SerializerFeature.WriteMapNullValue,
                        SerializerFeature.DisableCircularReferenceDetect
                );
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return "";
    }

    public static final JSONObject parseObject(String text) {
        if (StringUtils.isNotEmpty(text)){
            try {
                return JSON.parseObject(text,Feature.InitStringFieldAsEmpty);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return null;
    }

    public static final <T> T parseObject(String text, Class<T> clazz) {
        if (StringUtils.isNotEmpty(text)&&null!=clazz){
            try {
                return JSON.parseObject(text,clazz,Feature.InitStringFieldAsEmpty);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return null;
    }

    public static final <T> List<T> parseArray(String text, Class<T> clazz) {
        if (StringUtils.isNotEmpty(text)&&null!=clazz){
            try {
                return JSON.parseArray(text,clazz);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return null;
    }

}
