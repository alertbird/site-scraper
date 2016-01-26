package com.sainsburys.coding.service;

import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.common.base.Preconditions;
import com.sainsburys.coding.domain.PageResult;
import com.sainsburys.coding.domain.ProductItem;
import org.springframework.stereotype.Component;

@Component
public class PageSummaryService {

    public PageResult generateSummary(List<ProductItem> items) {
        Preconditions.checkArgument(items != null && !items.isEmpty(), "There is no product items in results.");

        BigDecimal total = BigDecimal.ZERO;
        for (ProductItem item : items) {
            total = total.add(item.getUnitPrice());
        }

        return new PageResult(items, total);
    }

    public String writeSummaryInJson(PageResult result) throws JsonProcessingException {
        Preconditions.checkNotNull("There is no results.");

        try {
            ObjectMapper mapper = new ObjectMapper();
            ObjectWriter writer = mapper.writerWithDefaultPrettyPrinter();
            return writer.writeValueAsString(result);

        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to write page result into json!", e);
        }
    }

}
