package com.overclockers.fetcher.service;

import com.overclockers.fetcher.parser.ElementParserImpl;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

import static com.overclockers.fetcher.ForumConstants.MAIN_URL;
import static com.overclockers.fetcher.ForumConstants.MARKETPLACE_URL;

@Service
public class FetchingService {

    @Autowired
    ElementParserImpl elementParser;

    public void printFirstPage() throws IOException {
        Document doc = Jsoup.connect(MAIN_URL + MARKETPLACE_URL).get();
        Elements elements = doc.getElementsByAttributeValueMatching("class", "row bg[1,2]$|row bg[1,2]( sticky){1}$");

        for (Element element : elements) {
            System.out.println(elementParser.getTopicCity(element));
            System.out.println(elementParser.getTopicTitle(element));
            System.out.println(elementParser.getTopicLink(element));
            System.out.println(elementParser.getAuthorUsername(element));
            System.out.println(elementParser.getAuthorProfileLink(element));
            System.out.println("--------------------------------------------");
        }
    }

}
