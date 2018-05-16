package org.lemon.shoutscreen.util;

import com.alibaba.fastjson.JSON;

import org.lemon.commons.HttpUtil;
import org.lemon.shoutscreen.model.ReturnModel;

import java.util.HashMap;
import java.util.List;

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
}
