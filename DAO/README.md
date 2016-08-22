# DAO
- 数据库用户名和密码在jdbc.properties配置文件里面
- 如果没有建库,使用create_db.sql里面的sql语句创建数据库
- 如果没有建表,使用create_table.sql里面的sql语句建表
- 新建或更新表后，修改配置文件generatorConfig.xml，运行util里面的MBG，自动生成model和Mapper接口