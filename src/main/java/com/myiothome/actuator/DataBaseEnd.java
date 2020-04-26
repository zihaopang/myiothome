package com.myiothome.actuator;

import com.myiothome.util.MyIotHomeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.annotation.WriteOperation;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * 自定义actuator端点
 */
@Component
@Endpoint(id = "database")//http://localhost:8080/myiothome/actuator/id 可访问
public class DataBaseEnd {
    private static final Logger logger = LoggerFactory.getLogger(DataBaseEnd.class);

    @Autowired
    DataSource dataSource;//线程池检查是否连接成功

    @ReadOperation//该方法只能使用get来访问的，@WriteOperation只能post访问
    public String checkConnection(){

        try (
                Connection con =  dataSource.getConnection();
        ){
            return MyIotHomeUtils.getJSONString(0,"数据库连接成功");

        } catch (SQLException e) {
            logger.debug("数据库连接失败，info:"+e.getStackTrace());
            return MyIotHomeUtils.getJSONString(0,"数据库连接失败");
        }
    }
}
