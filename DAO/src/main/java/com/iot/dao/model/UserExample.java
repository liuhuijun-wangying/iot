package com.iot.dao.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class UserExample {
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table user
     *
     * @mbggenerated
     */
    protected String orderByClause;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table user
     *
     * @mbggenerated
     */
    protected boolean distinct;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table user
     *
     * @mbggenerated
     */
    protected List<Criteria> oredCriteria;

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table user
     *
     * @mbggenerated
     */
    public UserExample() {
        oredCriteria = new ArrayList<Criteria>();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table user
     *
     * @mbggenerated
     */
    public void setOrderByClause(String orderByClause) {
        this.orderByClause = orderByClause;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table user
     *
     * @mbggenerated
     */
    public String getOrderByClause() {
        return orderByClause;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table user
     *
     * @mbggenerated
     */
    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table user
     *
     * @mbggenerated
     */
    public boolean isDistinct() {
        return distinct;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table user
     *
     * @mbggenerated
     */
    public List<Criteria> getOredCriteria() {
        return oredCriteria;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table user
     *
     * @mbggenerated
     */
    public void or(Criteria criteria) {
        oredCriteria.add(criteria);
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table user
     *
     * @mbggenerated
     */
    public Criteria or() {
        Criteria criteria = createCriteriaInternal();
        oredCriteria.add(criteria);
        return criteria;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table user
     *
     * @mbggenerated
     */
    public Criteria createCriteria() {
        Criteria criteria = createCriteriaInternal();
        if (oredCriteria.size() == 0) {
            oredCriteria.add(criteria);
        }
        return criteria;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table user
     *
     * @mbggenerated
     */
    protected Criteria createCriteriaInternal() {
        Criteria criteria = new Criteria();
        return criteria;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table user
     *
     * @mbggenerated
     */
    public void clear() {
        oredCriteria.clear();
        orderByClause = null;
        distinct = false;
    }

    /**
     * This class was generated by MyBatis Generator.
     * This class corresponds to the database table user
     *
     * @mbggenerated
     */
    protected abstract static class GeneratedCriteria {
        protected List<Criterion> criteria;

        protected GeneratedCriteria() {
            super();
            criteria = new ArrayList<Criterion>();
        }

        public boolean isValid() {
            return criteria.size() > 0;
        }

        public List<Criterion> getAllCriteria() {
            return criteria;
        }

        public List<Criterion> getCriteria() {
            return criteria;
        }

        protected void addCriterion(String condition) {
            if (condition == null) {
                throw new RuntimeException("Value for condition cannot be null");
            }
            criteria.add(new Criterion(condition));
        }

        protected void addCriterion(String condition, Object value, String property) {
            if (value == null) {
                throw new RuntimeException("Value for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value));
        }

        protected void addCriterion(String condition, Object value1, Object value2, String property) {
            if (value1 == null || value2 == null) {
                throw new RuntimeException("Between values for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value1, value2));
        }

        public Criteria andIdIsNull() {
            addCriterion("id is null");
            return (Criteria) this;
        }

        public Criteria andIdIsNotNull() {
            addCriterion("id is not null");
            return (Criteria) this;
        }

        public Criteria andIdEqualTo(Integer value) {
            addCriterion("id =", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotEqualTo(Integer value) {
            addCriterion("id <>", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdGreaterThan(Integer value) {
            addCriterion("id >", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdGreaterThanOrEqualTo(Integer value) {
            addCriterion("id >=", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLessThan(Integer value) {
            addCriterion("id <", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLessThanOrEqualTo(Integer value) {
            addCriterion("id <=", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdIn(List<Integer> values) {
            addCriterion("id in", values, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotIn(List<Integer> values) {
            addCriterion("id not in", values, "id");
            return (Criteria) this;
        }

        public Criteria andIdBetween(Integer value1, Integer value2) {
            addCriterion("id between", value1, value2, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotBetween(Integer value1, Integer value2) {
            addCriterion("id not between", value1, value2, "id");
            return (Criteria) this;
        }

        public Criteria andUsernameIsNull() {
            addCriterion("username is null");
            return (Criteria) this;
        }

        public Criteria andUsernameIsNotNull() {
            addCriterion("username is not null");
            return (Criteria) this;
        }

        public Criteria andUsernameEqualTo(String value) {
            addCriterion("username =", value, "username");
            return (Criteria) this;
        }

        public Criteria andUsernameNotEqualTo(String value) {
            addCriterion("username <>", value, "username");
            return (Criteria) this;
        }

        public Criteria andUsernameGreaterThan(String value) {
            addCriterion("username >", value, "username");
            return (Criteria) this;
        }

        public Criteria andUsernameGreaterThanOrEqualTo(String value) {
            addCriterion("username >=", value, "username");
            return (Criteria) this;
        }

        public Criteria andUsernameLessThan(String value) {
            addCriterion("username <", value, "username");
            return (Criteria) this;
        }

        public Criteria andUsernameLessThanOrEqualTo(String value) {
            addCriterion("username <=", value, "username");
            return (Criteria) this;
        }

        public Criteria andUsernameLike(String value) {
            addCriterion("username like", value, "username");
            return (Criteria) this;
        }

        public Criteria andUsernameNotLike(String value) {
            addCriterion("username not like", value, "username");
            return (Criteria) this;
        }

        public Criteria andUsernameIn(List<String> values) {
            addCriterion("username in", values, "username");
            return (Criteria) this;
        }

        public Criteria andUsernameNotIn(List<String> values) {
            addCriterion("username not in", values, "username");
            return (Criteria) this;
        }

        public Criteria andUsernameBetween(String value1, String value2) {
            addCriterion("username between", value1, value2, "username");
            return (Criteria) this;
        }

        public Criteria andUsernameNotBetween(String value1, String value2) {
            addCriterion("username not between", value1, value2, "username");
            return (Criteria) this;
        }

        public Criteria andPasswordIsNull() {
            addCriterion("password is null");
            return (Criteria) this;
        }

        public Criteria andPasswordIsNotNull() {
            addCriterion("password is not null");
            return (Criteria) this;
        }

        public Criteria andPasswordEqualTo(String value) {
            addCriterion("password =", value, "password");
            return (Criteria) this;
        }

        public Criteria andPasswordNotEqualTo(String value) {
            addCriterion("password <>", value, "password");
            return (Criteria) this;
        }

        public Criteria andPasswordGreaterThan(String value) {
            addCriterion("password >", value, "password");
            return (Criteria) this;
        }

        public Criteria andPasswordGreaterThanOrEqualTo(String value) {
            addCriterion("password >=", value, "password");
            return (Criteria) this;
        }

        public Criteria andPasswordLessThan(String value) {
            addCriterion("password <", value, "password");
            return (Criteria) this;
        }

        public Criteria andPasswordLessThanOrEqualTo(String value) {
            addCriterion("password <=", value, "password");
            return (Criteria) this;
        }

        public Criteria andPasswordLike(String value) {
            addCriterion("password like", value, "password");
            return (Criteria) this;
        }

        public Criteria andPasswordNotLike(String value) {
            addCriterion("password not like", value, "password");
            return (Criteria) this;
        }

        public Criteria andPasswordIn(List<String> values) {
            addCriterion("password in", values, "password");
            return (Criteria) this;
        }

        public Criteria andPasswordNotIn(List<String> values) {
            addCriterion("password not in", values, "password");
            return (Criteria) this;
        }

        public Criteria andPasswordBetween(String value1, String value2) {
            addCriterion("password between", value1, value2, "password");
            return (Criteria) this;
        }

        public Criteria andPasswordNotBetween(String value1, String value2) {
            addCriterion("password not between", value1, value2, "password");
            return (Criteria) this;
        }

        public Criteria andGroupIsNull() {
            addCriterion("group is null");
            return (Criteria) this;
        }

        public Criteria andGroupIsNotNull() {
            addCriterion("group is not null");
            return (Criteria) this;
        }

        public Criteria andGroupEqualTo(String value) {
            addCriterion("group =", value, "group");
            return (Criteria) this;
        }

        public Criteria andGroupNotEqualTo(String value) {
            addCriterion("group <>", value, "group");
            return (Criteria) this;
        }

        public Criteria andGroupGreaterThan(String value) {
            addCriterion("group >", value, "group");
            return (Criteria) this;
        }

        public Criteria andGroupGreaterThanOrEqualTo(String value) {
            addCriterion("group >=", value, "group");
            return (Criteria) this;
        }

        public Criteria andGroupLessThan(String value) {
            addCriterion("group <", value, "group");
            return (Criteria) this;
        }

        public Criteria andGroupLessThanOrEqualTo(String value) {
            addCriterion("group <=", value, "group");
            return (Criteria) this;
        }

        public Criteria andGroupLike(String value) {
            addCriterion("group like", value, "group");
            return (Criteria) this;
        }

        public Criteria andGroupNotLike(String value) {
            addCriterion("group not like", value, "group");
            return (Criteria) this;
        }

        public Criteria andGroupIn(List<String> values) {
            addCriterion("group in", values, "group");
            return (Criteria) this;
        }

        public Criteria andGroupNotIn(List<String> values) {
            addCriterion("group not in", values, "group");
            return (Criteria) this;
        }

        public Criteria andGroupBetween(String value1, String value2) {
            addCriterion("group between", value1, value2, "group");
            return (Criteria) this;
        }

        public Criteria andGroupNotBetween(String value1, String value2) {
            addCriterion("group not between", value1, value2, "group");
            return (Criteria) this;
        }

        public Criteria andExtrainfoIsNull() {
            addCriterion("extraInfo is null");
            return (Criteria) this;
        }

        public Criteria andExtrainfoIsNotNull() {
            addCriterion("extraInfo is not null");
            return (Criteria) this;
        }

        public Criteria andExtrainfoEqualTo(String value) {
            addCriterion("extraInfo =", value, "extrainfo");
            return (Criteria) this;
        }

        public Criteria andExtrainfoNotEqualTo(String value) {
            addCriterion("extraInfo <>", value, "extrainfo");
            return (Criteria) this;
        }

        public Criteria andExtrainfoGreaterThan(String value) {
            addCriterion("extraInfo >", value, "extrainfo");
            return (Criteria) this;
        }

        public Criteria andExtrainfoGreaterThanOrEqualTo(String value) {
            addCriterion("extraInfo >=", value, "extrainfo");
            return (Criteria) this;
        }

        public Criteria andExtrainfoLessThan(String value) {
            addCriterion("extraInfo <", value, "extrainfo");
            return (Criteria) this;
        }

        public Criteria andExtrainfoLessThanOrEqualTo(String value) {
            addCriterion("extraInfo <=", value, "extrainfo");
            return (Criteria) this;
        }

        public Criteria andExtrainfoLike(String value) {
            addCriterion("extraInfo like", value, "extrainfo");
            return (Criteria) this;
        }

        public Criteria andExtrainfoNotLike(String value) {
            addCriterion("extraInfo not like", value, "extrainfo");
            return (Criteria) this;
        }

        public Criteria andExtrainfoIn(List<String> values) {
            addCriterion("extraInfo in", values, "extrainfo");
            return (Criteria) this;
        }

        public Criteria andExtrainfoNotIn(List<String> values) {
            addCriterion("extraInfo not in", values, "extrainfo");
            return (Criteria) this;
        }

        public Criteria andExtrainfoBetween(String value1, String value2) {
            addCriterion("extraInfo between", value1, value2, "extrainfo");
            return (Criteria) this;
        }

        public Criteria andExtrainfoNotBetween(String value1, String value2) {
            addCriterion("extraInfo not between", value1, value2, "extrainfo");
            return (Criteria) this;
        }

        public Criteria andRegtimeIsNull() {
            addCriterion("regTime is null");
            return (Criteria) this;
        }

        public Criteria andRegtimeIsNotNull() {
            addCriterion("regTime is not null");
            return (Criteria) this;
        }

        public Criteria andRegtimeEqualTo(Date value) {
            addCriterion("regTime =", value, "regtime");
            return (Criteria) this;
        }

        public Criteria andRegtimeNotEqualTo(Date value) {
            addCriterion("regTime <>", value, "regtime");
            return (Criteria) this;
        }

        public Criteria andRegtimeGreaterThan(Date value) {
            addCriterion("regTime >", value, "regtime");
            return (Criteria) this;
        }

        public Criteria andRegtimeGreaterThanOrEqualTo(Date value) {
            addCriterion("regTime >=", value, "regtime");
            return (Criteria) this;
        }

        public Criteria andRegtimeLessThan(Date value) {
            addCriterion("regTime <", value, "regtime");
            return (Criteria) this;
        }

        public Criteria andRegtimeLessThanOrEqualTo(Date value) {
            addCriterion("regTime <=", value, "regtime");
            return (Criteria) this;
        }

        public Criteria andRegtimeIn(List<Date> values) {
            addCriterion("regTime in", values, "regtime");
            return (Criteria) this;
        }

        public Criteria andRegtimeNotIn(List<Date> values) {
            addCriterion("regTime not in", values, "regtime");
            return (Criteria) this;
        }

        public Criteria andRegtimeBetween(Date value1, Date value2) {
            addCriterion("regTime between", value1, value2, "regtime");
            return (Criteria) this;
        }

        public Criteria andRegtimeNotBetween(Date value1, Date value2) {
            addCriterion("regTime not between", value1, value2, "regtime");
            return (Criteria) this;
        }
    }

    /**
     * This class was generated by MyBatis Generator.
     * This class corresponds to the database table user
     *
     * @mbggenerated do_not_delete_during_merge
     */
    public static class Criteria extends GeneratedCriteria {

        protected Criteria() {
            super();
        }
    }

    /**
     * This class was generated by MyBatis Generator.
     * This class corresponds to the database table user
     *
     * @mbggenerated
     */
    public static class Criterion {
        private String condition;

        private Object value;

        private Object secondValue;

        private boolean noValue;

        private boolean singleValue;

        private boolean betweenValue;

        private boolean listValue;

        private String typeHandler;

        public String getCondition() {
            return condition;
        }

        public Object getValue() {
            return value;
        }

        public Object getSecondValue() {
            return secondValue;
        }

        public boolean isNoValue() {
            return noValue;
        }

        public boolean isSingleValue() {
            return singleValue;
        }

        public boolean isBetweenValue() {
            return betweenValue;
        }

        public boolean isListValue() {
            return listValue;
        }

        public String getTypeHandler() {
            return typeHandler;
        }

        protected Criterion(String condition) {
            super();
            this.condition = condition;
            this.typeHandler = null;
            this.noValue = true;
        }

        protected Criterion(String condition, Object value, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.typeHandler = typeHandler;
            if (value instanceof List<?>) {
                this.listValue = true;
            } else {
                this.singleValue = true;
            }
        }

        protected Criterion(String condition, Object value) {
            this(condition, value, null);
        }

        protected Criterion(String condition, Object value, Object secondValue, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.secondValue = secondValue;
            this.typeHandler = typeHandler;
            this.betweenValue = true;
        }

        protected Criterion(String condition, Object value, Object secondValue) {
            this(condition, value, secondValue, null);
        }
    }
}