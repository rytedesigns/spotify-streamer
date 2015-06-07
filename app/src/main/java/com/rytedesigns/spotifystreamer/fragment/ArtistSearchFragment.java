package com.rytedesigns.spotifystreamer.fragment;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.rytedesigns.spotifystreamer.R;
import com.rytedesigns.spotifystreamer.TopTenTracksActivity;
import com.rytedesigns.spotifystreamer.adapter.ArtistsAdapter;

import java.util.ArrayList;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import kaaes.spotify.webapi.android.models.Pager;

/**
 * This class uses the Spotify Wraper API from https://github.com/kaaes/spotify-web-api-android
 */
public class ArtistSearchFragment extends Fragment {

    public static final java.lang.String KEY_ARTIST_ID = "artistId";

    public static final java.lang.String KEY_ARTIST_NAME = "artistName";

    private final String LOG_TAG = ArtistSearchFragment.class.getSimpleName();

    private ArtistsAdapter mArtistsAdapter;

    private EditText artistEditText;

    public ArtistSearchFragment() {
    }

    @Override
    public void onStart() {
        super.onStart();
        searchForArtist();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_artists, container, false);

        // Found information on updating the array adapters image
        mArtistsAdapter = new ArtistsAdapter(getActivity(), R.layout.list_item_artists, new ArrayList<Artist>());

        ListView artistsListView = (ListView) rootView.findViewById(R.id.listview_artists);

        artistsListView.setAdapter(mArtistsAdapter);

        artistsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getActivity(), "Artist " + mArtistsAdapter.getItem(position).name + " was clicked.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(), TopTenTracksActivity.class);
                intent.putExtra(KEY_ARTIST_ID, mArtistsAdapter.getItem(position).id);
                startActivity(intent);
            }
        });

        artistEditText = (EditText) rootView.findViewById(R.id.artist_edittext);

        artistEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                searchForArtist();
            }
        });

        if (savedInstanceState != null && savedInstanceState.get(KEY_ARTIST_NAME) != null) {
            artistEditText.setText(savedInstanceState.get(KEY_ARTIST_NAME).toString());
        }

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (artistEditText != null && artistEditText.getText().length() > 0) {
            outState.putString(KEY_ARTIST_NAME, artistEditText.getText().toString());
        }

        super.onSaveInstanceState(outState);
    }

    public void searchForArtist() {
        SearchArtistTask searchArtistTask = new SearchArtistTask();
        searchArtistTask.execute(artistEditText.getText().toString());
    }

    public class SearchArtistTask extends AsyncTask<String, Void, Pager<Artist>> {
        @Override
        protected Pager<Artist> doInBackground(String... params) {

            if (params != null && params[0].length() > 0) {
                SpotifyApi api = new SpotifyApi();
                SpotifyService spotify = api.getService();
                ArtistsPager results = spotify.searchArtists(params[0]);

                if (results != null && results.artists != null && results.artists.items.size() > 0) {
                    return results.artists;
                } else {
                    return null;
                }
            } else {
                return null;
            }
        }

        @Override
        protected void onPostExecute(Pager<Artist> artists) {
            if (artists != null) {
                mArtistsAdapter.clear();

                for (Artist artist : artists.items) {
                    Log.d(LOG_TAG, artist.name);
                    Log.d(LOG_TAG, artist.id);
                    Log.d(LOG_TAG, artist.genres.size() + "");
                    if (artist.images.size() == 0) {
                        Log.d(LOG_TAG, "Has zero images!");
                    } else {
                        Log.d(LOG_TAG, artist.images.get(0).url);
                    }
                    Log.d(LOG_TAG, String.valueOf(artist.popularity));
                    mArtistsAdapter.add(artist);
                }
            } else {
                Toast.makeText(getActivity(), "Unable to locate specified artist.\n" +
                        "Verify spelling or try different a name.\n", Toast.LENGTH_LONG).show();
            }
        }
    }
}
