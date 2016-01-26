package com.sainsburys.coding.service;

import static org.slf4j.LoggerFactory.getLogger;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlDivision;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlParagraph;
import com.google.common.base.Preconditions;
import com.sainsburys.coding.domain.ProductItem;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SiteScraperService {

    private static final Logger logger = getLogger(SiteScraperService.class);

    @Value("${sainsburys.fruits.url}")
    private String siteUrl;

    public String getSiteUrl() {
        return siteUrl;
    }

    public HtmlPage retrieveHtmlPage(String url) {
        Preconditions.checkNotNull("Product page URL cannot be null!");
        HtmlPage page;
        logger.info("Loading page from {}", url);
        try (final WebClient webClient = new WebClient()) {
            webClient.getOptions().setJavaScriptEnabled(true);
            webClient.getOptions().setThrowExceptionOnScriptError(false);
            page = webClient.getPage(url);

        } catch (IOException e) {
            logger.error("Failed to load page from " + url, e);
            page = null;
        }

        return page;
    }

    public List<ProductItem> retrieveItemsOnPage() throws IOException {
        logger.info("Retrieving items from site ({}).", siteUrl);
        List<ProductItem> items = new ArrayList<>();

        HtmlPage page = this.retrieveHtmlPage(siteUrl);
        List<HtmlDivision> productDivs = (List<HtmlDivision>) page.getByXPath("//div[@class='productInner']");
        for (HtmlDivision productDiv : productDivs) {
            ProductItem item = retrieveProductItemFromDiv(productDiv);
            items.add(item);
        }

        return items;
    }

    private ProductItem retrieveProductItemFromDiv(HtmlDivision productDiv) {
        HtmlAnchor itemAnchor = (HtmlAnchor) productDiv.getByXPath(".//a").get(0);
        String productTitle = itemAnchor.asText();
        String productUrl = itemAnchor.getHrefAttribute();

        HtmlPage productPage = this.retrieveHtmlPage(productUrl);
        String productDescription = retrieveProductDescription(productPage);
        String fileSize = calculateProductPageSize(productPage);

        HtmlParagraph priceParagraph = (HtmlParagraph) productDiv.getByXPath(".//p[@class='pricePerUnit']").get(0);
        String unitPriceString = priceParagraph.getFirstChild().asText();
        BigDecimal unitPrice;
        String priceValue = unitPriceString.substring(unitPriceString.indexOf("\u00a3") + 1); // removed the pound sign
        try {
            unitPrice = BigDecimal.valueOf(Double.parseDouble(priceValue));
        } catch (NumberFormatException e) {
            logger.error("Failed to parse {} to get unit price.", priceValue);
            unitPrice = BigDecimal.ZERO;
        }

        return new ProductItem.Builder()
                .withTitle(productTitle)
                .withSize(fileSize)
                .withUnitPrice(unitPrice)
                .withDescription(productDescription)
                .build();
    }

    private String calculateProductPageSize(HtmlPage page) {
        Preconditions.checkNotNull("Product page URL cannot be null!");

        double fileSize;
        try {
            double bytes = page.getWebResponse().getContentAsString().length();
            fileSize = bytes / 1024;
        } catch (Exception e) {
            fileSize = 0;
        }

        return String.format("%.1fkb", fileSize);
    }

    private String retrieveProductDescription(HtmlPage page) {
        Preconditions.checkNotNull("Product page cannot be null!");

        String description;
        try {
            DomElement divInformation = page.getElementById("information");
            description = divInformation.getChildNodes().get(1).getFirstChild().getFirstChild().getNextSibling().getFirstChild().asText();
        } catch (Exception e) {
            logger.error("Failed to retrieve product description!", e);
            description = null;
        }

        return description;
    }

}
