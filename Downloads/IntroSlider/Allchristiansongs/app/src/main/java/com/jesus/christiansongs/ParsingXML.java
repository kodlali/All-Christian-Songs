package com.jesus.christiansongs;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.jesus.christiansongs.player.MyDebug;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.net.URL;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class ParsingXML {
    DocumentBuilderFactory builderFactory;
    DocumentBuilder docBuilder;
    Document doc;
    String input;
    NodeList nList;

    //Vector<MinistryEngine> minstries_Vect;
    Vector<SongInfo> songInfo_Vect;

    public void parsingData(int s) {
        songInfo_Vect = new Vector<SongInfo>();
        switch (s) {
            case 0:
                input = "http://www.kodaliapps.com/allchristiansongs/telugu_songs.xml";
                //input = context.getResources().openRawResource(R.raw.telugu_songs);
                break;
            case 1:
                input = "http://www.kodaliapps.com/allchristiansongs/english_songs.xml";
                //input = context.getResources().openRawResource(R.raw.english_songs);
                break;
            case 2:
                input = "http://www.kodaliapps.com/allchristiansongs/hindi_songs.xml";
                //input = context.getResources().openRawResource(R.raw.hindi_songs);
                break;
            case 3:
                input = "http://www.kodaliapps.com/allchristiansongs/malayalam_songs.xml";
                //input = context.getResources().openRawResource(R.raw.malayalam_songs);
                break;
            case 4:
                input = "http://www.kodaliapps.com/allchristiansongs/gujarathi_songs.xml";
                //input = context.getResources().openRawResource(R.raw.gujarathi_songs);
                break;
            case 5:
                input = "http://www.kodaliapps.com/allchristiansongs/kannada_songs.xml";
                //input = context.getResources().openRawResource(R.raw.kannada_songs);
                break;
            case 6:
                input = "http://www.kodaliapps.com/allchristiansongs/tamil_songs.xml";
                //input = context.getResources().openRawResource(R.raw.tamil_songs);
                break;
        }

    }

    public void parsingDatak(int s) throws  Exception {
        parsingData(s);
        builderFactory = DocumentBuilderFactory.newInstance();
        docBuilder = builderFactory.newDocumentBuilder();
        if (null != input) {
            Log.i("ParsingXML", "=================input is notttttt nullllllllll");
            URL url = new URL(input);
            doc = docBuilder.parse(new InputSource(url.openStream()));
            doSeperation(doc);
        }
    }


    private void doSeperation(Document docObject) {
        NodeList nodes = docObject.getElementsByTagName("Ministry");
        Log.i ("Minisgtry sizes ", nodes.getLength()+"" );
        for (int i = 0; i < nodes.getLength(); i++) {
            MinistryEngine ministry = new MinistryEngine();
            Element ministry_ele = (Element) nodes.item(i);
            ministry.setMinistryName(ministry_ele.getAttribute("ministry_name"));

            NodeList albums = ministry_ele.getElementsByTagName("Album");
            Log.i ("Album sizes ", albums.getLength()+"" );
            for (int j = 0; j < albums.getLength(); j++) {
                Element album_ele = (Element) albums.item(j);
                String album_name = album_ele.getAttribute("albumname");
                ministry.setAlbums(album_name);
                NodeList songs = album_ele.getElementsByTagName("song");

                songInfo_Vect = new Vector<SongInfo>();
                Log.i ("songs Vector sizes ", songs.getLength()+"" );
                for (int k = 0; k < songs.getLength(); k++) {
                    Element songs_ele = (Element) songs.item(k);
                    SongInfo songinfo = new SongInfo();
                    songinfo.setName(songs_ele.getAttribute("song_name").toString());
                    songinfo.setUrl(songs_ele.getAttribute("song_link"));
                    songInfo_Vect.add(songinfo);
                }
                ministry.setAlbumSongs(album_name, songInfo_Vect);
            }
            MyDebug.minstryVect.add(ministry);
        }

    }

    }
