package com.iot.basesvr.dao;

import com.iot.basesvr.model.FriendUserUser;
import com.iot.basesvr.model.FriendUserUserExample;
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

public interface FriendUserUserMapper {
    @SelectProvider(type=FriendUserUserSqlProvider.class, method="countByExample")
    int countByExample(FriendUserUserExample example);

    @DeleteProvider(type=FriendUserUserSqlProvider.class, method="deleteByExample")
    int deleteByExample(FriendUserUserExample example);

    @Delete({
        "delete from friend_user_user",
        "where id = #{id,jdbcType=INTEGER}"
    })
    int deleteByPrimaryKey(Integer id);

    @Insert({
        "insert into friend_user_user (friendKey, status, ",
        "updateTime)",
        "values (#{friendkey,jdbcType=VARCHAR}, #{status,jdbcType=BIT}, ",
        "#{updatetime,jdbcType=TIMESTAMP})"
    })
    @Options(useGeneratedKeys=true,keyProperty="id")
    int insert(FriendUserUser record);

    @InsertProvider(type=FriendUserUserSqlProvider.class, method="insertSelective")
    @Options(useGeneratedKeys=true,keyProperty="id")
    int insertSelective(FriendUserUser record);

    @SelectProvider(type=FriendUserUserSqlProvider.class, method="selectByExample")
    @Results({
        @Result(column="id", property="id", jdbcType=JdbcType.INTEGER, id=true),
        @Result(column="friendKey", property="friendkey", jdbcType=JdbcType.VARCHAR),
        @Result(column="status", property="status", jdbcType=JdbcType.BIT),
        @Result(column="updateTime", property="updatetime", jdbcType=JdbcType.TIMESTAMP)
    })
    List<FriendUserUser> selectByExample(FriendUserUserExample example);

    @Select({
        "select",
        "id, friendKey, status, updateTime",
        "from friend_user_user",
        "where id = #{id,jdbcType=INTEGER}"
    })
    @Results({
        @Result(column="id", property="id", jdbcType=JdbcType.INTEGER, id=true),
        @Result(column="friendKey", property="friendkey", jdbcType=JdbcType.VARCHAR),
        @Result(column="status", property="status", jdbcType=JdbcType.BIT),
        @Result(column="updateTime", property="updatetime", jdbcType=JdbcType.TIMESTAMP)
    })
    FriendUserUser selectByPrimaryKey(Integer id);

    @UpdateProvider(type=FriendUserUserSqlProvider.class, method="updateByExampleSelective")
    int updateByExampleSelective(@Param("record") FriendUserUser record, @Param("example") FriendUserUserExample example);

    @UpdateProvider(type=FriendUserUserSqlProvider.class, method="updateByExample")
    int updateByExample(@Param("record") FriendUserUser record, @Param("example") FriendUserUserExample example);

    @UpdateProvider(type=FriendUserUserSqlProvider.class, method="updateByPrimaryKeySelective")
    int updateByPrimaryKeySelective(FriendUserUser record);

    @Update({
        "update friend_user_user",
        "set friendKey = #{friendkey,jdbcType=VARCHAR},",
          "status = #{status,jdbcType=BIT},",
          "updateTime = #{updatetime,jdbcType=TIMESTAMP}",
        "where id = #{id,jdbcType=INTEGER}"
    })
    int updateByPrimaryKey(FriendUserUser record);
}