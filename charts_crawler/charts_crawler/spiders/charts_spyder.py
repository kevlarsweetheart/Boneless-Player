import scrapy
from datetime import date

class BillBoardSpider(scrapy.Spider):
    name = 'billboard'
    start_urls = ['http://www.billboard.com/archive/charts/']
    allowed_domains = ['billboard.com']


    def parse_date(self, response):
        table = response.xpath('//table[contains(@class, "views-table")]')
        a_list = table.xpath('.//a/@href').extract()
        a_list = [a for a in a_list if a.split('/')[1] == 'charts']
        print(a_list)


    def parse_year(self, response):
        wanted_charts = {'The Hot 100': '/hot-100', 'Billboard 200': '/billboard-200', 'Radio Songs': '/radio-songs', 'Artist 100': '/artist-100'}
        charts = response.xpath('//span[contains(@class, "field-content")]/a/text()').extract()
        charts = [c for c in charts if c in wanted_charts.keys()]
        for c in charts:
            new_page = response.url + wanted_charts[c]
            yield scrapy.Request(new_page, callback=self.parse_date)


    #заходим на странички архива billboard по годам
    def parse(self, response):
        start_year = 2015
        end_year = date.today().year
        for year in range(start_year, end_year):
            new_page = response.url + '/' + str(year)
            yield scrapy.Request(new_page, callback=self.parse_year)
