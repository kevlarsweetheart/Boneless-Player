import scrapy
from datetime import date
import re
from charts_crawler.addons.lastfm import LastfmNet
from charts_crawler.items import ChartsCrawlerItem



class BillboardLinksSpider(scrapy.Spider):
    name = 'Billboard_charts'
    start_urls = ['http://www.billboard.com/charts/hot-100',
            'http://www.billboard.com/charts/billboard-200',
            'http://www.billboard.com/charts/artist-100']
    allowed_domains = ['www.billboard.com']


    def parse(self, response):
        url = response.url
        _url = url.split('/')
        item = None
        print(url)
        if len(_url) == 6:
            item = ChartsCrawlerItem(link = url, date = _url[-1], chart = _url[-2], source = 'billboard')
        elif len(_url) == 5:
            date_div = response.xpath('.//div[contains(@class, "chart-data-header")]')
            date = date_div.xpath(".//time/@datetime").extract_first()
            item = ChartsCrawlerItem(link = url, date = date, chart = _url[-1], source = 'billboard')

        yield item
        new_page = response.xpath('//a[contains(@title, "Previous Week")]/@href').extract_first()
        if new_page is not None:
            new_page = 'http://' + self.allowed_domains[0] + new_page
            print(new_page)
            yield scrapy.Request(new_page, callback=self.parse)




class BillBoardSpider(scrapy.Spider):
    name = 'Billboard'
    start_urls = ['http://www.billboard.com/archive/charts/']
    allowed_domains = ['billboard.com']

    # From the chart page extract information about positions and store it in format
    # {'artist': <artist_name>, 'song_name': <song_name>, 'cur_pos': <current chart position>,
    #  'last_week_pos': <last week chart position>, 'delta': <last week and current positions delta>
    #  'Cover image url': <image url from Lastfm>}
    def parse_chart(self, response):
        request_url = response.url.split("/")
        positions = response.xpath('//article[re:test(@class, "chart-row chart-row--\d+ js-chart-row")]')

        LAST_API_KEY = "15cfe9d04bf5918f1496ef67a6cac301"
        LAST_API_SECRET = "64558d3fec7a53679253d31ab0ce6779"
        last_net = LastfmNet(key=LAST_API_KEY, secret_key=LAST_API_SECRET)

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
            pos_found = re.search('\d+', last_week)
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
                pos_dict['Cover image url'] = last_net.get_single_cover(artist, title)
            elif 'billboard-200' in request_url:
                pos_dict['Album title'] = title
                pos_dict['Cover image url'] = last_net.get_album_cover(artist, title)
            elif 'artist-100' in request_url:
                pos_dict['Cover image url'] = last_net.get_artist_image(artist)
            yield pos_dict


    def parse_date(self, response):
        table = response.xpath('//table[contains(@class, "views-table")]')
        a_list = table.xpath('.//a/@href').extract()
        a_list = [a for a in a_list if a.split('/')[1] == 'charts']
        for a in a_list:
            new_page = "http://www.billboard.com" + a
            yield scrapy.Request(new_page, callback=self.parse_chart)


    def parse_year(self, response):
        wanted_charts = {'The Hot 100': '/hot-100', 'Billboard 200': '/billboard-200', 'Artist 100': '/artist-100'}
        charts = response.xpath('//span[contains(@class, "field-content")]/a/text()').extract()
        charts = [c for c in charts if c in wanted_charts.keys()]
        for c in charts:
            new_page = response.url + wanted_charts[c]
            yield scrapy.Request(new_page, callback=self.parse_date)


    #Visit pages of the Billboard archive year by year
    def parse(self, response):
        start_year = 2015
        end_year = date.today().year
        for year in range(start_year, end_year):
            new_page = response.url + '/' + str(year)
            yield scrapy.Request(new_page, callback=self.parse_year)
