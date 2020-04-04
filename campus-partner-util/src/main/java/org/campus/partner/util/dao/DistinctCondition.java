package org.campus.partner.util.dao;

import org.jooq.Table;

/**
 * 在链接查询中需要使用同数据合并时的数据封装.
 * </p>
 *
 * @author xl
 * @since 1.0.0
 */
public class DistinctCondition {

    // 是否需要合并相同数据
    private Boolean distinct;
    // 选择的字段的表名
    private Table<?> table;

    /**
     * 
     * 无参的构造方法.
     *
     */
    public DistinctCondition() {}

    /**
     * 
     * 带全部参数的构造方法.
     *
     * @param distinct
     *            是否需要合并相同数据
     * @param table
     *            选择的字段的表
     */
    public DistinctCondition(Boolean distinct, Table<?> table) {
        this.distinct = distinct;
        this.table = table;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    public Table<?> getTable() {
        return table;
    }

    public void setTable(Table<?> table) {
        this.table = table;
    }
}