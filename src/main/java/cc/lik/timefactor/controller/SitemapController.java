package cc.lik.timefactor.controller;

import cc.lik.timefactor.service.SitemapService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/sitemap")
@RequiredArgsConstructor
public class SitemapController {
    private final SitemapService sitemapService;

    @GetMapping(value = "/sitemap.xml", produces = MediaType.APPLICATION_XML_VALUE)
    public Mono<String> getSitemap() {
        return sitemapService.generateSitemapXml();
    }
}
