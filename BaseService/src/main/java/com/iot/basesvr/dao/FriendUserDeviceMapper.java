package com.iot.basesvr.dao;

import com.iot.basesvr.model.FriendUserDevice;
import com.iot.basesvr.model.FriendUserDeviceExample;
import java.util.List;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.DeleteProvider;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.annotations.UpdateProvider;
import org.apache.ibatis.type.JdbcType;

public interface FriendUserDeviceMapper {
    @SelectProvider(type=FriendUserDeviceSqlProvider.class, method="countByExample")
    int countByExample(FriendUserDeviceExample example);

    @DeleteProvider(type=FriendUserDeviceSqlProvider.class, method="deleteByExample")
    int deleteByExample(FriendUserDeviceExample example);

    @Delete({
        "delete from friend_user_device",
        "where id = #{id,jdbcType=INTEGER}"
    })
    int deleteByPrimaryKey(Integer id);

    @Insert({
        "insert into friend_user_device (username, device_id, ",
        "status, updateTime)",
        "values (#{username,jdbcType=VARCHAR}, #{deviceId,jdbcType=VARCHAR}, ",
        "#{status,jdbcType=BIT}, #{updatetime,jdbcType=TIMESTAMP})"
    })
    @Options(useGeneratedKeys=true,keyProperty="id")
    int insert(FriendUserDevice record);

    @InsertProvider(type=FriendUserDeviceSqlProvider.class, method="insertSelective")
    @Options(useGeneratedKeys=true,keyProperty="id")
    int insertSelective(FriendUserDevice record);

    @SelectProvider(type=FriendUserDeviceSqlProvider.class, method="selectByExample")
    @Results({
        @Result(column="id", property="id", jdbcType=JdbcType.INTEGER, id=true),
        @Result(column="username", property="username", jdbcType=JdbcType.VARCHAR),
        @Result(column="device_id", property="deviceId", jdbcType=JdbcType.VARCHAR),
        @Result(column="status", property="status", jdbcType=JdbcType.BIT),
        @Result(column="updateTime", property="updatetime", jdbcType=JdbcType.TIMESTAMP)
    })
    List<FriendUserDevice> selectByExample(FriendUserDeviceExample example);

    @Select({
        "select",
        "id, username, device_id, status, updateTime",
        "from friend_user_device",
        "where id = #{id,jdbcType=INTEGER}"
    })
    @Results({
        @Result(column="id", property="id", jdbcType=JdbcType.INTEGER, id=true),
        @Result(column="username", property="username", jdbcType=JdbcType.VARCHAR),
        @Result(column="device_id", property="deviceId", jdbcType=JdbcType.VARCHAR),
        @Result(column="status", property="status", jdbcType=JdbcType.BIT),
        @Result(column="updateTime", property="updatetime", jdbcType=JdbcType.TIMESTAMP)
    })
    FriendUserDevice selectByPrimaryKey(Integer id);

    @UpdateProvider(type=FriendUserDeviceSqlProvider.class, method="updateByExampleSelective")
    int updateByExampleSelective(@Param("record") FriendUserDevice record, @Param("example") FriendUserDeviceExample example);

    @UpdateProvider(type=FriendUserDeviceSqlProvider.class, method="updateByExample")
    int updateByExample(@Param("record") FriendUserDevice record, @Param("example") FriendUserDeviceExample example);

    @UpdateProvider(type=FriendUserDeviceSqlProvider.class, method="updateByPrimaryKeySelective")
    int updateByPrimaryKeySelective(FriendUserDevice record);

    @Update({
        "update friend_user_device",
        "set username = #{username,jdbcType=VARCHAR},",
          "device_id = #{deviceId,jdbcType=VARCHAR},",
          "status = #{status,jdbcType=BIT},",
          "updateTime = #{updatetime,jdbcType=TIMESTAMP}",
        "where id = #{id,jdbcType=INTEGER}"
    })
    int updateByPrimaryKey(FriendUserDevice record);
}