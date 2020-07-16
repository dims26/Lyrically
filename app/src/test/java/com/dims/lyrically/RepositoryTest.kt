package com.dims.lyrically

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.dims.lyrically.database.FavouritesDao
import com.dims.lyrically.database.HistoryDao
import com.dims.lyrically.database.LyricDatabase
import com.dims.lyrically.models.Favourites
import com.dims.lyrically.models.History
import com.dims.lyrically.models.Song
import com.dims.lyrically.repository.Repository
import com.dims.lyrically.repository.SongListDeserializer
import com.dims.lyrically.utils.LoadState
import com.dims.lyrically.utils.LyricDataProvider
import com.google.gson.GsonBuilder
import com.google.gson.JsonParseException
import com.google.gson.reflect.TypeToken
import com.jraska.livedata.test
import com.nhaarman.mockitokotlin2.*
import kotlinx.coroutines.runBlocking
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class RepositoryTest {

    @get:Rule
    //rule to run all async operations immediately
    val testRule = InstantTaskExecutorRule()
    @get:Rule
    //allows to verify that a certain rule was thrown
    val exceptionRule: ExpectedException = ExpectedException.none()

    private lateinit var db: LyricDatabase
    private lateinit var favDao: FavouritesDao
    private lateinit var histDao: HistoryDao
    private val mockDb = mock<LyricDatabase>()

    private val jsonResponse = """
        {
          "meta": {
            "status": 200
          },
          "response": {
            "hits": [
              {
                "highlights": [],
                "index": "song",
                "type": "song",
                "result": {
                  "annotation_count": 20,
                  "api_path": "/songs/3039923",
                  "full_title": "HUMBLE. by Kendrick Lamar",
                  "header_image_thumbnail_url": "https://images.genius.com/0780c76f8d3ab762a0ad67ac26fa9709.300x169x1.jpg",
                  "header_image_url": "https://images.genius.com/0780c76f8d3ab762a0ad67ac26fa9709.1000x563x1.jpg",
                  "id": 3039923,
                  "lyrics_owner_id": 104344,
                  "lyrics_state": "complete",
                  "path": "/Kendrick-lamar-humble-lyrics",
                  "pyongs_count": 1056,
                  "song_art_image_thumbnail_url": "https://images.genius.com/4387b0bcc88e07676997ba73793cc73c.300x300x1.jpg",
                  "song_art_image_url": "https://images.genius.com/4387b0bcc88e07676997ba73793cc73c.1000x1000x1.jpg",
                  "stats": {
                    "unreviewed_annotations": 0,
                    "concurrents": 3,
                    "hot": false,
                    "pageviews": 10606599
                  },
                  "title": "HUMBLE.",
                  "title_with_featured": "HUMBLE.",
                  "url": "https://genius.com/Kendrick-lamar-humble-lyrics",
                  "primary_artist": {
                    "api_path": "/artists/1421",
                    "header_image_url": "https://images.genius.com/f3a1149475f2406582e3531041680a3c.1000x800x1.jpg",
                    "id": 1421,
                    "image_url": "https://images.genius.com/25d8a9c93ab97e9e6d5d1d9d36e64a53.1000x1000x1.jpg",
                    "is_meme_verified": true,
                    "is_verified": true,
                    "name": "Kendrick Lamar",
                    "url": "https://genius.com/artists/Kendrick-lamar",
                    "iq": 42056
                  }
                }
              },
              {
                "highlights": [],
                "index": "song",
                "type": "song",
                "result": {
                  "annotation_count": 85,
                  "api_path": "/songs/90478",
                  "full_title": "​m.A.A.d city by Kendrick Lamar (Ft. MC Eiht)",
                  "header_image_thumbnail_url": "https://images.genius.com/7ce90585a9da57d4ee67a09f27d8d6bc.300x300x1.jpg",
                  "header_image_url": "https://images.genius.com/7ce90585a9da57d4ee67a09f27d8d6bc.1000x1000x1.jpg",
                  "id": 90478,
                  "lyrics_owner_id": 103971,
                  "lyrics_state": "complete",
                  "path": "/Kendrick-lamar-maad-city-lyrics",
                  "pyongs_count": 2161,
                  "song_art_image_thumbnail_url": "https://images.genius.com/7ce90585a9da57d4ee67a09f27d8d6bc.300x300x1.jpg",
                  "song_art_image_url": "https://images.genius.com/7ce90585a9da57d4ee67a09f27d8d6bc.1000x1000x1.jpg",
                  "stats": {
                    "unreviewed_annotations": 0,
                    "concurrents": 2,
                    "hot": false,
                    "pageviews": 6423992
                  },
                  "title": "​m.A.A.d city",
                  "title_with_featured": "​m.A.A.d city (Ft. MC Eiht)",
                  "url": "https://genius.com/Kendrick-lamar-maad-city-lyrics",
                  "primary_artist": {
                    "api_path": "/artists/1421",
                    "header_image_url": "https://images.genius.com/f3a1149475f2406582e3531041680a3c.1000x800x1.jpg",
                    "id": 1421,
                    "image_url": "https://images.genius.com/25d8a9c93ab97e9e6d5d1d9d36e64a53.1000x1000x1.jpg",
                    "is_meme_verified": true,
                    "is_verified": true,
                    "name": "Kendrick Lamar",
                    "url": "https://genius.com/artists/Kendrick-lamar",
                    "iq": 42056
                  }
                }
              },
              {
                "highlights": [],
                "index": "song",
                "type": "song",
                "result": {
                  "annotation_count": 47,
                  "api_path": "/songs/81159",
                  "full_title": "Swimming Pools (Drank) by Kendrick Lamar",
                  "header_image_thumbnail_url": "https://images.genius.com/08bd7e3fd36bc6cf8fca520c5e475903.300x299x1.jpg",
                  "header_image_url": "https://images.genius.com/08bd7e3fd36bc6cf8fca520c5e475903.923x919x1.jpg",
                  "id": 81159,
                  "lyrics_owner_id": 11524,
                  "lyrics_state": "complete",
                  "path": "/Kendrick-lamar-swimming-pools-drank-lyrics",
                  "pyongs_count": 908,
                  "song_art_image_thumbnail_url": "https://images.rapgenius.com/6ae8dfaad68e239d83a87163903257d1.300x300x1.jpg",
                  "song_art_image_url": "https://images.rapgenius.com/6ae8dfaad68e239d83a87163903257d1.1000x1000x1.jpg",
                  "stats": {
                    "unreviewed_annotations": 0,
                    "concurrents": 2,
                    "hot": false,
                    "pageviews": 5800905
                  },
                  "title": "Swimming Pools (Drank)",
                  "title_with_featured": "Swimming Pools (Drank)",
                  "url": "https://genius.com/Kendrick-lamar-swimming-pools-drank-lyrics",
                  "primary_artist": {
                    "api_path": "/artists/1421",
                    "header_image_url": "https://images.genius.com/f3a1149475f2406582e3531041680a3c.1000x800x1.jpg",
                    "id": 1421,
                    "image_url": "https://images.genius.com/25d8a9c93ab97e9e6d5d1d9d36e64a53.1000x1000x1.jpg",
                    "is_meme_verified": true,
                    "is_verified": true,
                    "name": "Kendrick Lamar",
                    "url": "https://genius.com/artists/Kendrick-lamar",
                    "iq": 42056
                  }
                }
              },
              {
                "highlights": [],
                "index": "song",
                "type": "song",
                "result": {
                  "annotation_count": 37,
                  "api_path": "/songs/3035222",
                  "full_title": "DNA. by Kendrick Lamar",
                  "header_image_thumbnail_url": "https://images.genius.com/f3f77222e1b615e0a10354ea6282ff22.300x300x1.png",
                  "header_image_url": "https://images.genius.com/f3f77222e1b615e0a10354ea6282ff22.1000x1000x1.png",
                  "id": 3035222,
                  "lyrics_owner_id": 104344,
                  "lyrics_state": "complete",
                  "path": "/Kendrick-lamar-dna-lyrics",
                  "pyongs_count": 587,
                  "song_art_image_thumbnail_url": "https://images.genius.com/f3f77222e1b615e0a10354ea6282ff22.300x300x1.png",
                  "song_art_image_url": "https://images.genius.com/f3f77222e1b615e0a10354ea6282ff22.1000x1000x1.png",
                  "stats": {
                    "unreviewed_annotations": 0,
                    "concurrents": 3,
                    "hot": false,
                    "pageviews": 5435039
                  },
                  "title": "DNA.",
                  "title_with_featured": "DNA.",
                  "url": "https://genius.com/Kendrick-lamar-dna-lyrics",
                  "primary_artist": {
                    "api_path": "/artists/1421",
                    "header_image_url": "https://images.genius.com/f3a1149475f2406582e3531041680a3c.1000x800x1.jpg",
                    "id": 1421,
                    "image_url": "https://images.genius.com/25d8a9c93ab97e9e6d5d1d9d36e64a53.1000x1000x1.jpg",
                    "is_meme_verified": true,
                    "is_verified": true,
                    "name": "Kendrick Lamar",
                    "url": "https://genius.com/artists/Kendrick-lamar",
                    "iq": 42056
                  }
                }
              },
              {
                "highlights": [],
                "index": "song",
                "type": "song",
                "result": {
                  "annotation_count": 25,
                  "api_path": "/songs/3047142",
                  "full_title": "XXX. by Kendrick Lamar (Ft. U2)",
                  "header_image_thumbnail_url": "https://images.genius.com/f3f77222e1b615e0a10354ea6282ff22.300x300x1.png",
                  "header_image_url": "https://images.genius.com/f3f77222e1b615e0a10354ea6282ff22.1000x1000x1.png",
                  "id": 3047142,
                  "lyrics_owner_id": 599242,
                  "lyrics_state": "complete",
                  "path": "/Kendrick-lamar-xxx-lyrics",
                  "pyongs_count": 197,
                  "song_art_image_thumbnail_url": "https://images.genius.com/f3f77222e1b615e0a10354ea6282ff22.300x300x1.png",
                  "song_art_image_url": "https://images.genius.com/f3f77222e1b615e0a10354ea6282ff22.1000x1000x1.png",
                  "stats": {
                    "unreviewed_annotations": 0,
                    "hot": false,
                    "pageviews": 4838411
                  },
                  "title": "XXX.",
                  "title_with_featured": "XXX. (Ft. U2)",
                  "url": "https://genius.com/Kendrick-lamar-xxx-lyrics",
                  "primary_artist": {
                    "api_path": "/artists/1421",
                    "header_image_url": "https://images.genius.com/f3a1149475f2406582e3531041680a3c.1000x800x1.jpg",
                    "id": 1421,
                    "image_url": "https://images.genius.com/25d8a9c93ab97e9e6d5d1d9d36e64a53.1000x1000x1.jpg",
                    "is_meme_verified": true,
                    "is_verified": true,
                    "name": "Kendrick Lamar",
                    "url": "https://genius.com/artists/Kendrick-lamar",
                    "iq": 42056
                  }
                }
              },
              {
                "highlights": [],
                "index": "song",
                "type": "song",
                "result": {
                  "annotation_count": 50,
                  "api_path": "/songs/90475",
                  "full_title": "Money Trees by Kendrick Lamar (Ft. Jay Rock)",
                  "header_image_thumbnail_url": "https://images.genius.com/c567238c3d5781eed61a93baff46f678.300x300x1.jpg",
                  "header_image_url": "https://images.genius.com/c567238c3d5781eed61a93baff46f678.1000x1000x1.jpg",
                  "id": 90475,
                  "lyrics_owner_id": 1328,
                  "lyrics_state": "complete",
                  "path": "/Kendrick-lamar-money-trees-lyrics",
                  "pyongs_count": 901,
                  "song_art_image_thumbnail_url": "https://images.genius.com/c567238c3d5781eed61a93baff46f678.300x300x1.jpg",
                  "song_art_image_url": "https://images.genius.com/c567238c3d5781eed61a93baff46f678.1000x1000x1.jpg",
                  "stats": {
                    "unreviewed_annotations": 1,
                    "concurrents": 2,
                    "hot": false,
                    "pageviews": 4895089
                  },
                  "title": "Money Trees",
                  "title_with_featured": "Money Trees (Ft. Jay Rock)",
                  "url": "https://genius.com/Kendrick-lamar-money-trees-lyrics",
                  "primary_artist": {
                    "api_path": "/artists/1421",
                    "header_image_url": "https://images.genius.com/f3a1149475f2406582e3531041680a3c.1000x800x1.jpg",
                    "id": 1421,
                    "image_url": "https://images.genius.com/25d8a9c93ab97e9e6d5d1d9d36e64a53.1000x1000x1.jpg",
                    "is_meme_verified": true,
                    "is_verified": true,
                    "name": "Kendrick Lamar",
                    "url": "https://genius.com/artists/Kendrick-lamar",
                    "iq": 42056
                  }
                }
              },
              {
                "highlights": [],
                "index": "song",
                "type": "song",
                "result": {
                  "annotation_count": 37,
                  "api_path": "/songs/90473",
                  "full_title": "Bitch, Don't Kill My Vibe by Kendrick Lamar",
                  "header_image_thumbnail_url": "https://images.genius.com/71de91b0a7f63cb65044ab728c0b6cef.300x300x1.jpg",
                  "header_image_url": "https://images.genius.com/71de91b0a7f63cb65044ab728c0b6cef.1000x1000x1.jpg",
                  "id": 90473,
                  "lyrics_owner_id": 83406,
                  "lyrics_state": "complete",
                  "path": "/Kendrick-lamar-bitch-dont-kill-my-vibe-lyrics",
                  "pyongs_count": 648,
                  "song_art_image_thumbnail_url": "https://images.genius.com/30b5f019ee61590232df42bd88d3267a.268x268x1.jpg",
                  "song_art_image_url": "https://images.genius.com/30b5f019ee61590232df42bd88d3267a.268x268x1.jpg",
                  "stats": {
                    "unreviewed_annotations": 0,
                    "concurrents": 2,
                    "hot": false,
                    "pageviews": 4629226
                  },
                  "title": "Bitch, Don’t Kill My Vibe",
                  "title_with_featured": "Bitch, Don't Kill My Vibe",
                  "url": "https://genius.com/Kendrick-lamar-bitch-dont-kill-my-vibe-lyrics",
                  "primary_artist": {
                    "api_path": "/artists/1421",
                    "header_image_url": "https://images.genius.com/f3a1149475f2406582e3531041680a3c.1000x800x1.jpg",
                    "id": 1421,
                    "image_url": "https://images.genius.com/25d8a9c93ab97e9e6d5d1d9d36e64a53.1000x1000x1.jpg",
                    "is_meme_verified": true,
                    "is_verified": true,
                    "name": "Kendrick Lamar",
                    "url": "https://genius.com/artists/Kendrick-lamar",
                    "iq": 42056
                  }
                }
              },
              {
                "highlights": [],
                "index": "song",
                "type": "song",
                "result": {
                  "annotation_count": 34,
                  "api_path": "/songs/92856",
                  "full_title": "Poetic Justice by Kendrick Lamar (Ft. Drake)",
                  "header_image_thumbnail_url": "https://images.genius.com/f3db37bb8953d6e671d8bb532da2e855.220x220x1.jpg",
                  "header_image_url": "https://images.genius.com/f3db37bb8953d6e671d8bb532da2e855.220x220x1.jpg",
                  "id": 92856,
                  "lyrics_owner_id": 110771,
                  "lyrics_state": "complete",
                  "path": "/Kendrick-lamar-poetic-justice-lyrics",
                  "pyongs_count": 394,
                  "song_art_image_thumbnail_url": "https://images.genius.com/f3db37bb8953d6e671d8bb532da2e855.220x220x1.jpg",
                  "song_art_image_url": "https://images.genius.com/f3db37bb8953d6e671d8bb532da2e855.220x220x1.jpg",
                  "stats": {
                    "unreviewed_annotations": 0,
                    "concurrents": 4,
                    "hot": false,
                    "pageviews": 4277331
                  },
                  "title": "Poetic Justice",
                  "title_with_featured": "Poetic Justice (Ft. Drake)",
                  "url": "https://genius.com/Kendrick-lamar-poetic-justice-lyrics",
                  "primary_artist": {
                    "api_path": "/artists/1421",
                    "header_image_url": "https://images.genius.com/f3a1149475f2406582e3531041680a3c.1000x800x1.jpg",
                    "id": 1421,
                    "image_url": "https://images.genius.com/25d8a9c93ab97e9e6d5d1d9d36e64a53.1000x1000x1.jpg",
                    "is_meme_verified": true,
                    "is_verified": true,
                    "name": "Kendrick Lamar",
                    "url": "https://genius.com/artists/Kendrick-lamar",
                    "iq": 42056
                  }
                }
              },
              {
                "highlights": [],
                "index": "song",
                "type": "song",
                "result": {
                  "annotation_count": 22,
                  "api_path": "/songs/721659",
                  "full_title": "King Kunta by Kendrick Lamar",
                  "header_image_thumbnail_url": "https://images.genius.com/6c4cde12a02ad49bebff62799680b04e.300x300x1.png",
                  "header_image_url": "https://images.genius.com/6c4cde12a02ad49bebff62799680b04e.1000x1000x1.png",
                  "id": 721659,
                  "lyrics_owner_id": 599242,
                  "lyrics_state": "complete",
                  "path": "/Kendrick-lamar-king-kunta-lyrics",
                  "pyongs_count": 1926,
                  "song_art_image_thumbnail_url": "https://images.genius.com/6c4cde12a02ad49bebff62799680b04e.300x300x1.png",
                  "song_art_image_url": "https://images.genius.com/6c4cde12a02ad49bebff62799680b04e.1000x1000x1.png",
                  "stats": {
                    "unreviewed_annotations": 0,
                    "concurrents": 2,
                    "hot": false,
                    "pageviews": 4185284
                  },
                  "title": "King Kunta",
                  "title_with_featured": "King Kunta",
                  "url": "https://genius.com/Kendrick-lamar-king-kunta-lyrics",
                  "primary_artist": {
                    "api_path": "/artists/1421",
                    "header_image_url": "https://images.genius.com/f3a1149475f2406582e3531041680a3c.1000x800x1.jpg",
                    "id": 1421,
                    "image_url": "https://images.genius.com/25d8a9c93ab97e9e6d5d1d9d36e64a53.1000x1000x1.jpg",
                    "is_meme_verified": true,
                    "is_verified": true,
                    "name": "Kendrick Lamar",
                    "url": "https://genius.com/artists/Kendrick-lamar",
                    "iq": 42056
                  }
                }
              },
              {
                "highlights": [],
                "index": "song",
                "type": "song",
                "result": {
                  "annotation_count": 17,
                  "api_path": "/songs/3047141",
                  "full_title": "LOVE. by Kendrick Lamar (Ft. Zacari)",
                  "header_image_thumbnail_url": "https://images.genius.com/f3f77222e1b615e0a10354ea6282ff22.300x300x1.png",
                  "header_image_url": "https://images.genius.com/f3f77222e1b615e0a10354ea6282ff22.1000x1000x1.png",
                  "id": 3047141,
                  "lyrics_owner_id": 389740,
                  "lyrics_state": "complete",
                  "path": "/Kendrick-lamar-love-lyrics",
                  "pyongs_count": 224,
                  "song_art_image_thumbnail_url": "https://images.genius.com/f3f77222e1b615e0a10354ea6282ff22.300x300x1.png",
                  "song_art_image_url": "https://images.genius.com/f3f77222e1b615e0a10354ea6282ff22.1000x1000x1.png",
                  "stats": {
                    "unreviewed_annotations": 0,
                    "concurrents": 2,
                    "hot": false,
                    "pageviews": 3694727
                  },
                  "title": "LOVE.",
                  "title_with_featured": "LOVE. (Ft. Zacari)",
                  "url": "https://genius.com/Kendrick-lamar-love-lyrics",
                  "primary_artist": {
                    "api_path": "/artists/1421",
                    "header_image_url": "https://images.genius.com/f3a1149475f2406582e3531041680a3c.1000x800x1.jpg",
                    "id": 1421,
                    "image_url": "https://images.genius.com/25d8a9c93ab97e9e6d5d1d9d36e64a53.1000x1000x1.jpg",
                    "is_meme_verified": true,
                    "is_verified": true,
                    "name": "Kendrick Lamar",
                    "url": "https://genius.com/artists/Kendrick-lamar",
                    "iq": 42056
                  }
                }
              }
            ]
          }
        }
    """.trimIndent()


    private fun getFavs(times: Int) : List<Favourites> {
        val favs = mutableListOf<Favourites>()
        for (i in 1..times){
            val favourites = Favourites(i, "...", "...", "...", "...",
                    "...", "...")
            favs.add(favourites)
        }
        return favs
    }
    private fun getHists(times: Int) : List<History> {
        val hists = mutableListOf<History>()
        for (i in 1..times){
            val history = History(i, "...", "...", "...", "...",
                    "...", "...")
            hists.add(history)
        }
        return hists
    }

    @Before
    fun setup(){
        //get applicationContext from Androidx test library
        val context = ApplicationProvider.getApplicationContext<Context>()

        //get in-memory db, allowing tests queries on main thread
        db = Room.inMemoryDatabaseBuilder(context, LyricDatabase::class.java)
                .allowMainThreadQueries()
                .build()
        favDao = spy(db.favouritesDao())
        histDao = spy(db.historyDao())

        //consider breaking repository into two classes for favourites and history so you can pass
        // in the daos instead of the db, and not mock the initial db and return both spied daos
        whenever(mockDb.favouritesDao()).thenReturn(favDao)
        whenever(mockDb.historyDao()).thenReturn(histDao)
    }

    @After
    fun tearDown(){
        db.close()
    }

    @Test
    fun test_getFavouritesEmpty(){
        val repository = Repository(mockDb)
        //because the db has just been created, favourites should be empty
        val expected = 0

        //use livedata testing library function test() to properly handle any lifecycle
        // issues when retrieving the value
        val actual = repository.favourites.test().value().size

        assertEquals(actual, expected)
        verify(favDao)
                .favourites//verify favourites was called
    }

    @Test
    fun test_getFavouritesSingle(){
        val expected = getFavs(1)
        favDao.addFavourite(expected.first())
        val repository = Repository(mockDb)
        //because the db has just been created, favourites should be empty


        val actual = repository.favourites.test().value()

        assertEquals(expected[0].id, actual[0].id)
        verify(favDao)
                .favourites//verify favourites was called
    }

    @Test
    fun test_getFavouritesMultiple(){
        val expected = getFavs(3)
        favDao.addFavourite(expected[0])
        favDao.addFavourite(expected[1])
        favDao.addFavourite(expected[2])
        val repository = Repository(mockDb)
        //because the db has just been created, favourites should be empty

        val actual = repository.favourites.test().value()

        assertEquals(expected, actual)
        verify(favDao)
                .favourites//verify favourites was called
    }

    @Test
    fun test_itemInstancesInFavourites_noInsert() = runBlocking {
        val fav = getFavs(1).first()
        val repository = Repository(mockDb)
        val expected = 0

        val actual = repository.itemInstancesInFavouritesCount(fav.id)

        assertEquals(expected, actual)
        verify(favDao).usersCount(fav.id)
        return@runBlocking
    }

    @Test
    fun test_itemInstancesInFavourites_singleInsert() = runBlocking{
        val fav = getFavs(1).first()
        favDao.addFavourite(fav)
        val repository = Repository(mockDb)
        val expected = 1

        val actual = repository.itemInstancesInFavouritesCount(fav.id)

        assertEquals(expected, actual)
        verify(favDao).usersCount(fav.id)
        return@runBlocking
    }

    @Test
    fun test_itemInstancesInFavourites_multipleInserts() = runBlocking{
        val fav = getFavs(1).first()
        favDao.addFavourite(fav)
        favDao.addFavourite(fav)
        favDao.addFavourite(fav)
        val repository = Repository(mockDb)
        val expected = 1

        val actual = repository.itemInstancesInFavouritesCount(fav.id)

        assertEquals(expected, actual)
        verify(favDao).usersCount(fav.id)
        return@runBlocking
    }

    @Test
    //runBlocking blocks the thread till coroutine completion
    fun test_AddFavourite() = runBlocking{
        val favs = getFavs(1)
        val repository = Repository(mockDb)
        val expected = favs.first()

        repository.addFavourite(expected)

        argumentCaptor<Favourites>()
                .apply {
                    verify(favDao).addFavourite(capture())
                    assertEquals(expected, firstValue)
                }
        verify(favDao).addFavourite(any())
    }

    @Test
    //runBlocking blocks the thread till coroutine completion
    fun test_DeleteFavourite() = runBlocking{
        val favs = getFavs(1)
        val repository = Repository(mockDb)
        val expected = favs.first()

        repository.deleteFromFavourite(expected)

        argumentCaptor<Favourites>()
                .apply {
                    verify(favDao).deleteFromFavourite(capture())
                    assertEquals(expected, firstValue)
                }
        verify(favDao).deleteFromFavourite(any())
    }

    @Test
    fun test_getHistoryEmpty(){
        val repository = Repository(mockDb)
        //because the db has just been created, favourites should be empty
        val expected = 0

        val actual = repository.history.test().value().size

        assertEquals(actual, expected)
        verify(histDao)
                .history//verify favourites was called
    }

    @Test
    fun test_getHistorySingle(){
        val expected = getHists(1)
        histDao.addHistory(expected.first())
        val repository = Repository(mockDb)
        //because the db has just been created, favourites should be empty

        val actual = repository.history.test().value()

        assertEquals(actual[0].id, expected[0].id)
        verify(histDao)
                .history//verify favourites was called
    }

    @Test
    fun test_getHistoryMultiple(){
        val expected = getHists(3)
        histDao.addHistory(expected[0])
        histDao.addHistory(expected[1])
        histDao.addHistory(expected[2])
        val repository = Repository(mockDb)
        //because the db has just been created, favourites should be empty

        val actual = repository.history.test().value()

        assertEquals(expected, actual)
        verify(histDao)
                .history//verify favourites was called
    }

    @Test
    fun test_itemInstancesInHistory_noInsert() = runBlocking {
        val hist = getHists(1).first()
        val repository = Repository(mockDb)
        val expected = 0

        val actual = repository.itemInstancesInHistoryCount(hist.id)

        assertEquals(expected, actual)
        verify(histDao).usersCount(hist.id)
        return@runBlocking
    }

    @Test
    fun test_itemInstancesInHistory_singleInsert() = runBlocking{
        val hist = getHists(1).first()
        histDao.addHistory(hist)
        val repository = Repository(mockDb)
        val expected = 1

        val actual = repository.itemInstancesInHistoryCount(hist.id)

        assertEquals(expected, actual)
        verify(histDao).usersCount(hist.id)
        return@runBlocking
    }

    @Test
    fun test_itemInstancesInHistory_multipleInserts() = runBlocking{
        val hist = getHists(1).first()
        histDao.addHistory(hist)
        histDao.addHistory(hist)
        histDao.addHistory(hist)
        val repository = Repository(mockDb)
        val expected = 1

        val actual = repository.itemInstancesInHistoryCount(hist.id)

        assertEquals(expected, actual)
        verify(histDao).usersCount(hist.id)
        return@runBlocking
    }

    @Test
    //runBlocking blocks the thread till coroutine completion
    fun test_AddHistory() = runBlocking{
        val hist = getHists(1)
        val repository = Repository(mockDb)
        val expected = hist.first()

        repository.addHistory(expected)

        argumentCaptor<History>()
                .apply {
                    verify(histDao).addHistory(capture())
                    assertEquals(expected, firstValue)
                }
        verify(histDao).addHistory(any())
    }

    @Test
    //runBlocking blocks the thread till coroutine completion
    fun test_UpdateHistory() = runBlocking{
        val hist = getHists(1)
        val repository = Repository(mockDb)
        val history = hist.first()
        histDao.addHistory(history)
        val expected = with(history){
            History(id, "changed title", title, songArtImageThumbnailUrl,
                    url, titleWithFeatured, artistName)
        }

        repository.updateHistory(expected)

        argumentCaptor<History>()
                .apply {
                    verify(histDao).updateHistory(capture())
                    assertEquals(expected.fullTitle, firstValue.fullTitle)
                }
        verify(histDao).updateHistory(any())
        return@runBlocking
    }

    @Test
    //runBlocking blocks the thread till coroutine completion
    fun test_DeleteHistory() = runBlocking{
        val hists = getHists(1)
        val repository = Repository(mockDb)
        val expected = hists.first()

        repository.deleteFromHistory(expected)

        argumentCaptor<History>()
                .apply {
                    verify(histDao).deleteFromHistory(capture())
                    assertEquals(expected, firstValue)
                }
        verify(histDao).deleteFromHistory(any())
    }

    @Test
    //runBlocking blocks the thread till coroutine completion
    fun test_ClearHistory() = runBlocking{
        val hists = getHists(3)
        val repository = Repository(mockDb)
        histDao.addHistory(hists[0])
        histDao.addHistory(hists[1])
        histDao.addHistory(hists[2])
        val expected = 0

        repository.clearHistory()
        val actual = repository.history.test().value().size

        verify(histDao).clearHistory()
        assertEquals(expected, actual)
    }

    @Test
    //runBlocking blocks the thread till coroutine completion
    fun test_Search() {
        val repository = spy(Repository(mockDb))
        val indicator = mock<MutableLiveData<LoadState>>()
        val songs = mutableListOf<Song>()
        val provider = mock<LyricDataProvider>()
        val query = "Kendrick"

        repository.search(query, indicator, songs, provider)

        verify(provider).search(eq(query), any())
        verify(repository).getSearchCallback(indicator, songs)
    }

    @Test
    //runBlocking blocks the thread till coroutine completion
    fun test_Callback_onSuccessfulResponse_correctJson() {
        val repository = Repository(mockDb)
        val indicator = mock<MutableLiveData<LoadState>>()
        val songs = mutableListOf<Song>()
        val query = "Kendrick"
        val callback = spy(repository.getSearchCallback(indicator, songs))
        val request = request(query)
        val response = response(request, 200, "successful")
        val call = OkHttpClient().newCall(request)
        val provider = mock<LyricDataProvider>{ lyricDataProvider ->
            on(lyricDataProvider.search(any(), eq(callback)))
                    .doAnswer{
                        callback.onResponse(call, response)
                    }
        }
        val expected = kotlin.run{
            val gsonBuilder = GsonBuilder()
            val deserializer = SongListDeserializer()
            gsonBuilder.registerTypeAdapter(Song::class.java, deserializer)

            val gson = gsonBuilder.create()
            gson.fromJson<List<Song>>(jsonResponse, object : TypeToken<Song>() {}.type)
        }

        provider.search(query, callback)

        assertEquals(expected, songs)
        verify(indicator).postValue(LoadState.LOADED)
    }

    @Test
    //runBlocking blocks the thread till coroutine completion
    fun test_Callback_onSuccessfulResponse_malformedJson() {
        val repository = Repository(mockDb)
        val indicator = mock<MutableLiveData<LoadState>>()
        val songs = mutableListOf<Song>()
        val query = "Kendrick"
        val callback = spy(repository.getSearchCallback(indicator, songs))
        val request = request(query)
        val response = response(request, 200, "malformed response", true)
        val call = OkHttpClient().newCall(request)
        val provider = mock<LyricDataProvider>{ lyricDataProvider ->
            on(lyricDataProvider.search(any(), eq(callback)))
                    .doAnswer{
                        callback.onResponse(call, response)
                    }
        }
        val expected = listOf<Song>()
        exceptionRule.expect(JsonParseException::class.java)

        provider.search(query, callback)

        assertEquals(expected, songs)
        verify(indicator, never()).postValue(any())
    }

    @Test
    //runBlocking blocks the thread till coroutine completion
    fun test_Callback_onUnsuccessfulResponse() {
        val repository = Repository(mockDb)
        val indicator = mock<MutableLiveData<LoadState>>()
        val songs = mutableListOf<Song>()
        val query = "Kendrick"
        val callback = spy(repository.getSearchCallback(indicator, songs))
        val request = request(query)
        val response = response(request, 500, "server error")
        val call = mock<Call>()
        val provider = mock<LyricDataProvider>{ lyricDataProvider ->
            on(lyricDataProvider.search(any(), eq(callback)))
                    .doAnswer{
                        callback.onResponse(call, response)
                    }
        }
        val expected = listOf<Song>()

        provider.search(query, callback)

        assertEquals(expected, songs)
        verify(indicator).postValue(LoadState.ERROR)
    }

    @Test
    //runBlocking blocks the thread till coroutine completion
    fun test_Callback_onFailure() {
        val repository = Repository(mockDb)
        val indicator = mock<MutableLiveData<LoadState>>()
        val songs = mutableListOf<Song>()
        val query = "Kendrick Lamar"
        val callback = spy(repository.getSearchCallback(indicator, songs))
        val call = mock<Call>()
        val ioException = mock<IOException>()
        val provider = mock<LyricDataProvider>{ lyricDataProvider ->
            on(lyricDataProvider.search(any(), eq(callback)))
                    .doAnswer{
                        callback.onFailure(call, ioException)
                    }
        }
        val expected = listOf<Song>()

        provider.search(query, callback)

        assertEquals(expected, songs)
        verify(indicator).postValue(LoadState.ERROR)
    }

    private fun request(query: String): Request {
        return Request.Builder()
                .url("https://genius.p.rapidapi.com/search?q=$query")
                .get()
                .build()
    }

    private fun response(request: Request, code: Int, message: String, hasMalformedJson: Boolean = false): Response {
        return Response.Builder()
                .code(code)
                .request(request)
                .protocol(Protocol.HTTP_2)
                .message(message)
                .body(
                        if (hasMalformedJson) "malformed json".toResponseBody("application/json; charset=utf-8".toMediaType())
                        else jsonResponse.toResponseBody("application/json; charset=utf-8".toMediaType()))
                .build()
    }
}