package org.campus.partner.util.dao;

/**
 * 查询列表记录时通用的条件封装.
 *
 * @author xl
 * @since 1.0.0
 */
public class SelectConditions {
    private String fields;
    private String filters;
    private Integer limit;
    private Integer offset;
    private String sorts;
    private String extraFields;

    /**
     * 类SelectConditions的构造函数.
     *
     * @param fields
     *            返回数据包含字段；为空值或null的时候返回所有字段
     * @param filters
     *            条件过滤"key=value"的csv格式
     * @param limit
     *            分页参数，当前数据的页大小，默认为20
     * @param offset
     *            分页参数，当前数据的偏移量，默认为0
     * @param sorts
     *            排序，例如："+id"（按id字段升序）或者"-cost"（按cost字段降序）
     */
    public SelectConditions(String fields, String filters, int limit, int offset, String sorts) {
        super();
        this.fields = fields;
        this.filters = filters;
        this.limit = limit;
        this.offset = offset;
        this.sorts = sorts;
    }

    /**
     * 类SelectConditions的构造函数.
     *
     * @param fields
     *            返回数据包含字段；为空值或null的时候返回所有字段
     * @param filters
     *            条件过滤"key=value"的csv格式
     * @param limit
     *            分页参数，当前数据的页大小，默认为20
     * @param offset
     *            分页参数，当前数据的偏移量，默认为0
     * @param sorts
     *            排序，例如："+id"（按id字段升序）或者"-cost"（按cost字段降序）
     * @param extraFields
     *            返回数据包含字段；[不方便写到fields中的额外字段]
     */
    public SelectConditions(String fields, String filters, int limit, int offset, String sorts, String extraFields) {
        super();
        this.fields = fields;
        this.filters = filters;
        this.limit = limit;
        this.offset = offset;
        this.sorts = sorts;
        this.extraFields = extraFields;
    }

    public String getExtraFields() {
        return extraFields;
    }

    public void setExtraFields(String extraFields) {
        this.extraFields = extraFields;
    }

    public SelectConditions() {
        super();
    }

    public String getFields() {
        return fields;
    }

    public void setFields(String fields) {
        this.fields = fields;
    }

    public String getFilters() {
        return filters;
    }

    public void setFilters(String filters) {
        this.filters = filters;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public Integer getOffset() {
        return offset;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    public String getSorts() {
        return sorts;
    }

    public void setSorts(String sorts) {
        this.sorts = sorts;
    }
}