package bg.sofia.uni.fmi.issuetracker.controller.common;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

public class PaginationLinkHeader {
    private final Page<?> page;
    private final String baseUri;
    private final boolean withSort;

    public PaginationLinkHeader(Page<?> page, String baseUri) {
        this(page, baseUri, true);
    }

    public PaginationLinkHeader(Page<?> page, String baseUri, boolean withSort) {
        this.page = page;
        this.baseUri = baseUri;
        this.withSort = withSort;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(buildLink(1, "first"));
        sb.append(buildLink(page.getTotalPages(), "last"));
        if (page.hasPrevious()) {
            sb.append(buildLink(page.getNumber(), "prev"));
        }
        if (page.hasNext()) {
            sb.append(buildLink(page.getNumber() + 1, "next"));
        }

        return sb.toString().replaceAll(", $", "");
    }

    String buildLink(int pageNumber, String relation) {
        String url = "%s?page_number=%d&page_size=%d".formatted(baseUri, pageNumber, page.getSize());
        if (withSort) {
            Sort.Order order = page.getSort().get().findFirst().get();
            url += "&order_by=%s&asc=%s".formatted(order.getProperty(), order.isAscending());
        }

        return "<%s>; rel=\"%s\", ".formatted(url, relation);
    }
}
