package org.campus.partner.util.page;

import java.util.ArrayList;
import java.util.List;

import org.springframework.hateoas.Link;

/**
 * 分页工具.
 * </p>
 *
 * @author xl
 * @since 1.0.0
 */
public class PageUtil {

    /**
     * 
     * 分页资源链接.
     *
     * @param offset
     *            分页偏移量
     * @param limit
     *            分页大小
     * @param totalElements
     *            总元素数
     * @param uri
     *            链接URI
     * @return 分页资源链接
     * @author xl
     * @since 1.0.0
     */
    public static List<Link> pagingLinks(Integer offset, Integer limit, Integer totalElements, String uri) {
        return pagingLinks(offset, limit, totalElements, uri, null);
    }

    /**
     * 
     * 分页资源链接.
     *
     * @param offset
     *            分页偏移量
     * @param limit
     *            分页大小
     * @param totalElements
     *            总元素数
     * @param uri
     *            链接URI
     * @param queryString
     *            查询参数
     * @return 分页资源链接
     * @author xl
     * @since 1.0.0
     */
    public static List<Link> pagingLinks(Integer offset, Integer limit, Integer totalElements, String uri,
            String queryString) {
        List<Link> links = new ArrayList<Link>();
        if (offset < 0) {
            throw new IllegalArgumentException("the argument 'offset' must greater than or equal  0");
        }
        if (limit < 0) {
            throw new IllegalArgumentException("the argument 'limit' offset must greater than or equal 1");
        }
        if (offset > totalElements) {
            throw new IllegalArgumentException("the argument 'offset' must less than 'totalElements'");
        }
        String finalUri = uri;
        if (finalUri.contains("?")) {
            finalUri = finalUri + "&";
        } else {
            finalUri = finalUri + "?";
        }
        if (queryString != null) {
            finalUri = finalUri + queryString.replaceAll("limit=\\d+&?", "")
                    .replaceAll("offset=\\d+&?", "");
            if (!finalUri.endsWith("&")) {
                finalUri += "&";
            }
        }
        StringBuilder sb = new StringBuilder(finalUri);
        int startIndex = sb.length();
        Link self = new Link(sb.append(paging(offset, limit))
                .toString(), Link.REL_SELF);
        links.add(self);
        sb.delete(startIndex, sb.length());
        Link first = new Link(sb.append(paging(0, limit))
                .toString(), Link.REL_FIRST);
        links.add(first);
        sb.delete(startIndex, sb.length());
        Link last = null;
        if (totalElements - limit <= 0) {
            last = new Link(sb.append(paging(0, limit))
                    .toString(), Link.REL_LAST);
        } else {
            last = new Link(sb.append(paging(totalElements - limit, limit))
                    .toString(), Link.REL_LAST);
        }
        links.add(last);
        sb.delete(startIndex, sb.length());
        Link next; // href must not be null or empty;
        if (offset + limit < totalElements) {
            next = new Link(sb.append(paging(offset + limit, limit))
                    .toString(), Link.REL_NEXT);
            links.add(next);
        }
        sb.delete(startIndex, sb.length());
        Link prev;
        if (offset != 0) {
            if (offset - limit <= 0) {
                prev = new Link(sb.append(paging(0, limit))
                        .toString(), Link.REL_PREVIOUS);
                links.add(prev);
            } else {
                prev = new Link(sb.append(paging(offset - limit, limit))
                        .toString(), Link.REL_PREVIOUS);
                links.add(prev);
            }
        }
        sb.delete(startIndex, sb.length());
        return links;
    }

    /**
     * 
     * 组装限制条件.
     *
     * @param offset
     *            偏移量
     * @param limit
     *            每页限制
     * @return 组装好的限制条件
     * @author xl
     * @since 1.0.0
     */
    private static String paging(Integer offset, Integer limit) {
        return new StringBuilder("offset=").append(offset)
                .append("&limit=")
                .append(limit)
                .toString();
    }

}
