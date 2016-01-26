package com.sainsburys.coding;

import static org.slf4j.LoggerFactory.getLogger;

import java.util.List;

import com.sainsburys.coding.domain.PageResult;
import com.sainsburys.coding.domain.ProductItem;
import com.sainsburys.coding.service.PageSummaryService;
import com.sainsburys.coding.service.SiteScraperService;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Profile("!test")
@Component
public class SiteScraperRunner implements CommandLineRunner {

    private static final Logger logger = getLogger(SiteScraperRunner.class);

    @Autowired
    private SiteScraperService scraperService;
    @Autowired
    private PageSummaryService summaryService;

    @Override
    public void run(String... args) throws Exception {
        logger.info("Site Scraper is running ...");

        List<ProductItem> items = scraperService.retrieveItemsOnPage();

        PageResult result = summaryService.generateSummary(items);

        String jsonResult = summaryService.writeSummaryInJson(result);

        logger.info("The results are " + jsonResult);
    }

}
