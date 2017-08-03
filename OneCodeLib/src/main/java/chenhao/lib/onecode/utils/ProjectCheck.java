package chenhao.lib.onecode.utils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.File;
import chenhao.lib.onecode.OneCode;

/**
 * 创建人：onecode
 * 描述：项目验证
 * 警告：请勿轻易改动验证逻辑或删除本类,否则后果自负！
 */

public class ProjectCheck {

    public static ProjectCheck project;
    public static void init(String n){
        if (null==project){
            project=new ProjectCheck(n);
        }
    }

    public static boolean OK(){
        return null!=project&&project.projectCanUse;
    }

    public String name;
    public boolean projectCanUse;

    public ProjectCheck(String n) {
        this.name = n;
        if (null==name||name.length()<=0){
            this.name="comm";
        }
        checkLoad();
    }

    private void checkLoad(){
        projectCanUse=true;
        JsonObjectRequest request=new JsonObjectRequest(Request.Method.GET,
                "http://cloud.arrownock.com/v2/objects/ProjectCheck/query.json?key=vQ1bU1jFgCWarGdc2qBERczNOLkdzFbA&sort=-updated_at&limit=1&page=1&name="+name,
                new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject data) {
                try {
                    JSONObject response=null!=data&&null!=data.get("response")?data.getJSONObject("response"):null;
                    JSONArray list=null!=response&&null!=response.get("ProjectChecks")?response.getJSONArray("ProjectChecks"):null;
                    if (null!=list&&list.length()>0){
                        projectCanUse=list.getJSONObject(0).getBoolean("projectCanUse");
                    }else{
                        upsert();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        getRequestQueue().add(request);
    }

    private void upsert(){
        projectCanUse=true;
        JSONObject params=new JSONObject();
        try {
            params.put("key","vQ1bU1jFgCWarGdc2qBERczNOLkdzFbA");
            JSONObject query=new JSONObject();
            query.put("name",name);
            params.put("query",query);
            JSONObject doc=new JSONObject();
            doc.put("name",name);
            doc.put("projectCanUse",projectCanUse);
            params.put("doc",doc);
        }catch (Exception e){
            e.printStackTrace();
        }
        JsonObjectRequest request=new JsonObjectRequest(Request.Method.POST,"http://cloud.arrownock.com/v2/objects/ProjectCheck/upsert.json", params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject data) {
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        getRequestQueue().add(request);
    }

    private RequestQueue requestQueue;
    private synchronized RequestQueue getRequestQueue() {
        if(this.requestQueue != null) {
            return this.requestQueue;
        } else {
            this.requestQueue = new RequestQueue(new DiskBasedCache(new File(OneCode.getConfig().getCachePath(), "volley"), 104857600), new BasicNetwork(new HurlStack()), 10);
            this.requestQueue.start();
            return this.requestQueue;
        }
    }

}
