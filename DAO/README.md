- 数据库用户名和密码在jdbc.properties配置文件里面
- 如果没有建库,使用create_db.sql里面的sql语句创建数据库
- 使用create_table.sql里面的sql语句建表
- 新建或更新表后，修改配置文件generatorConfig.xml，运行util里面的MBG，自动生成model、mapper.xml和Mapper接口
- 生成mapper.xml文件后，移动到resources下的mapper中，并在mybatis配置文件中增加一个mapper