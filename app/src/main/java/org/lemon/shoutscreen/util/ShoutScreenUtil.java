package org.lemon.shoutscreen.util;

import com.alibaba.fastjson.JSON;

import org.lemon.commons.AppOption;
import org.lemon.commons.HttpUtil;
import org.lemon.shoutscreen.model.ReturnModel;
import org.lemon.shoutscreen.model.UserModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Lemon on 2017/9/13.
 */

public class ShoutScreenUtil {
    private ReturnModel returnModel;
    /**
     返回上屏叫号列表
     */
    public List findByStatus() throws Exception {
        returnModel=new ReturnModel();
        HashMap<String,String> pairs=new HashMap<>();
        pairs.put("userId",new AppOption().getOption(AppOption.APP_OPTION_USER));
        returnModel = ( ReturnModel ) HttpUtil.get( "shout/findByStatus.do", pairs, ReturnModel.class );
        if ( returnModel.getSuccess () ) {
            if(returnModel.getObject ()!=null) {
                return JSON.parseObject(returnModel.getObject().toString(), List.class);
            }else{
                return null;
            }
        } else {
            throw new Exception ( returnModel.getException () );
        }
    }
    /**
     * 验证密码
     * @param userId
     * @param password
     * @return
     */
    public boolean confirmPasswd(String userId,String password) throws Exception {
        returnModel=new ReturnModel();
        HashMap<String,String> pairs=new HashMap<>();
        pairs.put("id", userId);
        pairs.put("password", password);
        try {
            Map map=(Map) HttpUtil.get("login1.do", pairs, Map.class);
            if(null!=map){
                String ret= (String) map.get("info");
                if("success".equals(ret)){
                    return true;
                }else {
                    throw new Exception(ret);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception ("异常:" + e.getMessage () );
        }
        return false;
    }
    public UserModel getUserInfoById(String id) {
        HashMap<String,String> pairs=new HashMap<>();
        pairs.put("id", id);
        try {
            String info= (String) HttpUtil.get("user/findById.do", pairs, String.class);
            return JSON.parseObject(info,UserModel.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
