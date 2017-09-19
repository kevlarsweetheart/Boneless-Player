# -*- coding: utf-8 -*-

# Define here the models for your scraped items
#
# See documentation in:
# http://doc.scrapy.org/en/latest/topics/items.html
import scrapy


class ChartsCrawlerItem(scrapy.Item):
    link = scrapy.Field()
    date = scrapy.Field()
    chart = scrapy.Field()
    source = scrapy.Field()
