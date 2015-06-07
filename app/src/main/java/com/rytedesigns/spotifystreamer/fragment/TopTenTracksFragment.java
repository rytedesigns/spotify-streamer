package com.rytedesigns.spotifystreamer.fragment;

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

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnItemClick;
import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * This class uses the Spotify Wraper API from https://github.com/kaaes/spotify-web-api-android
 */
public class TopTenTracksFragment extends Fragment {

    private final String LOG_TAG = TopTenTracksFragment.class.getSimpleName();

    private TracksAdapter mTrackAdapter;

    private String artistId = null;

    @InjectView(R.id.listview_tracks)
    public ListView trackListView;

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

        ButterKnife.inject(this, rootView);

        // Found information on updating the array adapters image
        mTrackAdapter = new TracksAdapter(getActivity(), R.layout.list_item_tracks, new ArrayList<Track>());

        trackListView.setAdapter(mTrackAdapter);

        return rootView;
    }

    @OnItemClick(R.id.listview_tracks)
    public void onTrackItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        Toast.makeText(getActivity(), "Track " + mTrackAdapter.getItem(position).name + " was clicked.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStart() {
        super.onStart();

        if (artistId != null && artistId.length() > 0) {
            SpotifyApi api = new SpotifyApi();
            SpotifyService spotify = api.getService();
            spotify.getArtistTopTrack(artistId, new Callback<Tracks>() {
                @Override
                public void success(final Tracks tracks, Response response) {
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            updateArrayAdapter(tracks);
                        }
                    };
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(runnable);
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    if (error.getKind() == RetrofitError.Kind.NETWORK) {
                        Runnable runnable = new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getActivity(), R.string.toast_message_unable_to_reach_spotify, Toast.LENGTH_SHORT).show();
                            }
                        };
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(runnable);
                        }
                    }
                }
            });
        }
    }

    public void updateArrayAdapter(Tracks tracks)
    {
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
        } else {
            Toast.makeText(getActivity(), R.string.toast_message_unable_to_find_top_ten_tracks, Toast.LENGTH_LONG).show();
        }
    }
}
