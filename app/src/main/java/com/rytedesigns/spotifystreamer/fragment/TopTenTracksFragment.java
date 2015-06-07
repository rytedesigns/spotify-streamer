package com.rytedesigns.spotifystreamer.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.rytedesigns.spotifystreamer.R;
import com.rytedesigns.spotifystreamer.adapter.TracksAdapter;

import java.util.ArrayList;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;

/**
 * This class uses the Spotify Wraper API from https://github.com/kaaes/spotify-web-api-android
 */
public class TopTenTracksFragment extends Fragment {

    private final String LOG_TAG = TopTenTracksFragment.class.getSimpleName();

    private TracksAdapter mTrackAdapter;

    private String artistId = null;

    public TopTenTracksFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getActivity().getIntent().getExtras();
        if (extras != null) {
            artistId = extras.getString(ArtistSearchFragment.KEY_ARTIST_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_top_ten_tracks, container, false);

        // Found information on updating the array adapters image
        mTrackAdapter = new TracksAdapter(getActivity(), R.layout.list_item_tracks, new ArrayList<Track>());

        ListView artistsListView = (ListView) rootView.findViewById(R.id.listview_tracks);

        artistsListView.setAdapter(mTrackAdapter);

        artistsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getActivity(), "Track " + mTrackAdapter.getItem(position).name + " was clicked.", Toast.LENGTH_SHORT).show();
            }
        });

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        SearchArtistTopTrackTask searchArtistTask = new SearchArtistTopTrackTask();
        searchArtistTask.execute(artistId);
    }

    public class SearchArtistTopTrackTask extends AsyncTask<String, Void, Tracks> {
        @Override
        protected Tracks doInBackground(String... params) {

            if (params != null && params.length > 0) {
                SpotifyApi api = new SpotifyApi();
                SpotifyService spotify = api.getService();

                Tracks results = spotify.getArtistTopTrack(params[0]);

                if (results != null && results.tracks != null && results.tracks.size() > 0) {
                    return results;
                } else {
                    return null;
                }
            } else {
                return null;
            }
        }

        @Override
        protected void onPostExecute(Tracks tracks) {
            if (tracks != null) {
                mTrackAdapter.clear();

                for (Track track : tracks.tracks) {
                    Log.d(LOG_TAG, track.name);
                    if (track.album.images.size() == 0) {
                        Log.d(LOG_TAG, "Has zero images!");
                    } else {
                        Log.d(LOG_TAG, track.album.images.get(0).url);
                    }

                    mTrackAdapter.add(track);
                }
            }
        }
    }
}
