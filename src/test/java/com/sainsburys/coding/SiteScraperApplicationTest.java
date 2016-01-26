package com.sainsburys.coding;

import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.BDDAssertions.then;
import static org.mockito.BDDMockito.given;

import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.sainsburys.coding.domain.PageResult;
import com.sainsburys.coding.domain.ProductItem;
import com.sainsburys.coding.service.PageSummaryService;
import com.sainsburys.coding.service.SiteScraperService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = SiteScraperApplication.class)
public class SiteScraperApplicationTest {

    public static final String FRUIT_1_URL = "http://www.sainsburys.co.uk/shop/gb/groceries/ripe---ready/sainsburys-avocado--ripe---ready-x2";
    public static final String FRUIT_2_URL = "http://www.sainsburys.co.uk/shop/gb/groceries/ripe---ready/sainsburys-avocados--ripe---ready-x4";
    public static final String RESULT_JSON = "{\n" +
            "  \"results\" : [ {\n" +
            "    \"title\" : \"Sainsbury's Avocado, Ripe & Ready x2\",\n" +
            "    \"size\" : \"12.1kb\",\n" +
            "    \"unit_price\" : 1.80,\n" +
            "    \"description\" : \"Avocados\"\n" +
            "  }, {\n" +
            "    \"title\" : \"Sainsbury's Avocados, Ripe & Ready x4\",\n" +
            "    \"size\" : \"7.3kb\",\n" +
            "    \"unit_price\" : 3.20,\n" +
            "    \"description\" : \"Avocados\"\n" +
            "  } ],\n" +
            "  \"total\" : 5.00\n" +
            "}";

    @Autowired
    @Spy
    private SiteScraperService scraperService;

    @Autowired
    private PageSummaryService summaryService;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void should_ReturnAllProductsInJson_When_RipeFruitPage() throws Exception {
        // Given
        String siteUrl = scraperService.getSiteUrl();
        given(scraperService.retrieveHtmlPage(siteUrl)).willReturn(getPageFromFile(siteUrl));
        given(scraperService.retrieveHtmlPage(FRUIT_1_URL)).willReturn(getPageFromFile("/src/test/resources/fruit1.html"));
        given(scraperService.retrieveHtmlPage(FRUIT_2_URL)).willReturn(getPageFromFile("/src/test/resources/fruit2.html"));

        // When
        List<ProductItem> items = scraperService.retrieveItemsOnPage();
        PageResult pageResult = summaryService.generateSummary(items);
        String jsonResult = summaryService.writeSummaryInJson(pageResult);

        // Then
        then(items).hasSize(2);
        then(pageResult).hasFieldOrPropertyWithValue("total", new BigDecimal("5.00"));
        then(jsonResult).isEqualToIgnoringWhitespace(RESULT_JSON);
    }

    private HtmlPage getPageFromFile(String partialPath) throws java.io.IOException {
        HtmlPage mockPage;
        String fullFileUrl = getFullFileUrl(partialPath);
        try (final WebClient webClient = new WebClient()) {
            mockPage = webClient.getPage(fullFileUrl);
        }
        return mockPage;
    }

    private String getFullFileUrl(String partialPath) throws FileNotFoundException, MalformedURLException {
        Path filePath = Paths.get(System.getProperty("user.dir"), partialPath);
        if (!Files.exists(filePath)) {
            throw new FileNotFoundException("Failed to find the mock site file: " + filePath.toString());
        }
        return filePath.toFile().toURI().toURL().toString();
    }

    @Test
    public void shouldLoadHtmlPage() throws Exception {
        // Given
        String fullFileUrl = getFullFileUrl("/src/test/resources/fruit1.html");

        // When
        HtmlPage page = scraperService.retrieveHtmlPage(fullFileUrl);

        // Then
        then(page).isNotNull();
    }

    @Test
    public void shouldGeneratePageSummaryWithCorrectTotal() throws Exception {
        // Given
        ProductItem item1 = givenProductItem("FruitTitle1", "25kb", new BigDecimal("3.50"), "Good Fruits 1.");
        ProductItem item2 = givenProductItem("FruitTitle2", "52kb", new BigDecimal("7.50"), "Good Fruits 2.");
        ProductItem item3 = givenProductItem("FruitTitle3", "18kb", new BigDecimal("1.25"), "Good Fruits 3.");
        ArrayList<ProductItem> productItems = newArrayList(item1, item2, item3);

        // When
        PageResult result = summaryService.generateSummary(productItems);

        // Then
        then(result.getResults()).hasSize(3);
        then(result).hasFieldOrPropertyWithValue("total", new BigDecimal("12.25"));
    }

    private ProductItem givenProductItem(String title, String size, BigDecimal unitPrice, String description) {
        return new ProductItem.Builder()
                .withTitle(title)
                .withSize(size)
                .withUnitPrice(unitPrice)
                .withDescription(description)
                .build();
    }
}
