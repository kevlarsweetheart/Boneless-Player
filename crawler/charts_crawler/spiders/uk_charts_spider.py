import scrapy
from charts_crawler.items import ChartsCrawlerItem

class UKChartsSpider(scrapy.Spider):
    name = 'uk_charts'
    start_urls = ['http://www.officialcharts.com/charts/singles-chart/',
            'http://www.officialcharts.com/charts/albums-chart/',
            'http://www.officialcharts.com/charts/end-of-year-singles-chart/',
            'http://www.officialcharts.com/charts/end-of-year-artist-albums-chart/',
            'http://www.officialcharts.com/charts/albums-streaming-chart/',
            'http://www.officialcharts.com/charts/audio-streaming-chart/']
    allowed_domains = ['www.officialcharts.com']


    def parse(self, response):
        url = response.url
        _url = url.split('/')
        item = None
        print(url)
        if len(_url) > 6:
            item = ChartsCrawlerItem(link = url, date = _url[-3], chart = _url[-4], source = 'uk_charts')
        else:
            date_selector = response.xpath('.//div[contains(@class, "date-selector")]')[0]
            date_day = date_selector.xpath('.//select[contains(@name, "Day")]/option[@selected]/@value').extract_first()
            date_month = date_selector.xpath('.//select[contains(@name, "Month")]/option[@selected]/@value').extract_first()
            date_year = date_selector.xpath('.//select[contains(@name, "Year")]/option[@selected]/@value').extract_first()
            date = date_year + date_month + date_day
            item = ChartsCrawlerItem(link = url, date = date, chart = _url[-2], source = 'uk_charts')

        yield item
        new_page = response.xpath('//a[contains(@class, "prev")]/@href').extract_first()
        if new_page is not None:
            new_page = 'http://' + self.allowed_domains[0] + new_page
            print(new_page)
            yield scrapy.Request(new_page, callback=self.parse)
