import re
import pylast
import sys


class LastfmNet:
    def __init__(self, key, secret_key):
        self.network = pylast.LastFMNetwork(api_key=key, api_secret=secret_key)


    def get_artist_image(self, artist_name):
        artist_name = self.correct_artist_name(artist_name)
        artist = self.network.get_artist(artist_name)
        try:
            return artist.get_cover_image(size=3)
        except KeyboardInterrupt:
            sys.exit()
        except:
            return None


    def get_album_cover(self, artist_name, album_title):
        artist_name = self.correct_artist_name(artist_name)
        album = self.network.get_album(artist_name, album_title)
        try:
            return album.get_cover_image(size=3)
        except KeyboardInterrupt:
            sys.exit()
        except:
            return self.get_artist_image(artist_name)


    def get_single_cover(self, artist_name, song_name):
        artist_name = re.sub('\s[Ff](ea){,1}[Tt].*', '', artist_name)
        artist_name = self.correct_artist_name(artist_name)
        song_name = self.correct_song_title(artist_name, song_name)
        song = self.network.get_track(artist_name, song_name)
        try:
            album = song.get_album()
            if album:
                return self.get_album_cover(artist_name, album.get_name())
            else:
                return self.get_artist_image(artist_name)
        except KeyboardInterrupt:
            sys.exit()
        except:
            return None


    def correct_artist_name(self, artist_name):
        artist = self.network.get_artist(artist_name)
        return artist.get_correction()


    def correct_song_title(self, artist_name, song_name):
        song = self.network.get_track(artist_name, song_name)
        return song.get_correction()