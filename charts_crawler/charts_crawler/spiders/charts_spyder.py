import scrapy
from datetime import date
import re
from charts_crawler.items import ChartsCrawlerItem

class BillBoardSpider(scrapy.Spider):
    name = 'Billboard'
    start_urls = ['http://www.billboard.com/archive/charts/']
    allowed_domains = ['billboard.com']

    '''
    # From the chart page extract information about positions and store it in format
    # {'artist': <artist_name>, 'song_name': <song_name>, 'cur_pos': <current chart position>,
    #  'last_week_pos': <last week chart position>, 'delta': <last week and current positions delta>}
    def parse_chart(self, response):
        request_url = response.url.split("/")
        positions = response.xpath('//article[re:test(@class, "chart-row chart-row--\d+ js-chart-row")]')
        for row in positions:
            pos_dict = {}
            artist = row.xpath('.//a[contains(@class, "chart-row__artist")]/text()').extract_first()
            if not artist:
                artist = row.xpath('.//span[contains(@class, "chart-row__artist")]/text()').extract_first()
            artist = re.sub('\n', '', artist)
            pos_dict['Artist name'] = artist
            title = row.xpath('.//h2[contains(@class, "chart-row__song")]/text()').extract_first()
            cur_pos = row.xpath('.//span[contains(@class, "chart-row__current-week")]/text()').extract_first()
            pos_dict['Current position'] = cur_pos
            last_week = row.xpath('.//span[contains(@class, "chart-row__last-week")]/text()').extract_first()
            pos_found = re.search(' \d+', last_week)
            if pos_found:
                last_week_pos = last_week[pos_found.start():pos_found.end()]
                pos_dict['Last week position'] = last_week_pos
                delta = int(last_week_pos) - int(cur_pos)
            else:
                delta = 0
                pos_dict['Last week position'] = 'New'
            pos_dict['Chart gain'] = delta
            if 'hot-100' in request_url:
                pos_dict['Song title'] = title
            elif 'billboard-200' in request_url:
                pos_dict['Album title'] = title
            yield pos_dict
    '''

    def parse_date(self, response):
        #wanted_charts = {'The Hot 100': '/hot-100', 'Billboard 200': '/billboard-200', 'Artist 100': '/artist-100'}
        table = response.xpath('//table[contains(@class, "views-table")]')
        a_list = table.xpath('.//a/@href').extract()
        a_list = [a for a in a_list if a.split('/')[1] == 'charts']
        for a in a_list:
            link = "http://www.billboard.com" + a
            item = ChartsCrawlerItem(link = link, date = a.split('/')[-2], chart = a.split('/')[-1])
            yield item
            #yield scrapy.Request(new_page, callback=self.parse_chart)


    def parse_year(self, response):
        wanted_charts = {'The Hot 100': '/hot-100', 'Billboard 200': '/billboard-200', 'Artist 100': '/artist-100'}
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
