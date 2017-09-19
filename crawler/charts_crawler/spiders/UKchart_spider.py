import scrapy
from charts_crawler.addons.lastfm import LastfmNet
import re


class UKchartSpider(scrapy.Spider):
    name = 'UKcharts'
    start_urls = ['http://www.officialcharts.com/']
    allowed_domains = ['officialcharts.com']


    def chart_type_checker(self, url):
        for part in url:
            if re.search('(singles)|(audio)', part):
                return 0
            elif re.search('albums', part):
                return 1


    # From the chart page extract information about positions and store it in format
    # {'artist': <artist_name>, 'song_name': <song_name>, 'cur_pos': <current chart position>,
    #  'last_week_pos': <last week chart position>, 'delta': <last week and current positions delta>
    #  'Cover image url': <image url from Lastfm>}
    def parse_chart(self, response):
        request_url = response.url.split("/")
        chart_table = response.xpath('//section[contains(@class, "chart")]/table[contains(@class, "chart-positions")]')
        positions = chart_table.xpath('.//tr[not(@id) and not(@class)]')

        LAST_API_KEY = "15cfe9d04bf5918f1496ef67a6cac301"
        LAST_API_SECRET = "64558d3fec7a53679253d31ab0ce6779"
        last_net = LastfmNet(key=LAST_API_KEY, secret_key=LAST_API_SECRET)

        for row in positions:

            if row.xpath('.//div[contains(@class, "adspace")]'):
                continue

            pos_dict = {}
            artist = row.xpath('.//div[contains(@class, "artist")]/a/text()').extract_first()
            pos_dict['Artist name'] = artist
            title = row.xpath('.//div[contains(@class, "title")]/a/text()').extract_first()
            cur_pos = row.xpath('.//span[contains(@class, "position")]/text()').extract_first()
            pos_dict['Current position'] = cur_pos
            last_week = row.xpath('.//span[contains(@class, "last-week")]/text()').extract_first()
            pos_found = re.search('\d+', last_week)
            if pos_found:
                last_week_pos = last_week[pos_found.start():pos_found.end()]
                pos_dict['Last week position'] = last_week_pos
                delta = int(last_week_pos) - int(cur_pos)
            else:
                delta = 0
                last_week = re.sub('\n\s*', '', last_week)
                pos_dict['Last week position'] = last_week
            pos_dict['Chart gain'] = delta
            if self.chart_type_checker(request_url) == 0:
                pos_dict['Song title'] = title
                pos_dict['Cover image url'] = last_net.get_single_cover(artist, title)
            elif self.chart_type_checker(request_url):
                pos_dict['Album title'] = title
                pos_dict['Cover image url'] = last_net.get_album_cover(artist, title)
            yield pos_dict


    def parse(self, response):
        page = 'http://www.officialcharts.com/charts/singles-chart/'
        yield scrapy.Request(page, callback=self.parse_chart)