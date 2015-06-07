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

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnItemClick;
import butterknife.OnTextChanged;
import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import kaaes.spotify.webapi.android.models.Pager;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * This class uses the Spotify Wraper API from https://github.com/kaaes/spotify-web-api-android
 */
public class ArtistSearchFragment extends Fragment {

    public static final java.lang.String KEY_ARTIST_ID = "artistId";

    public static final java.lang.String KEY_ARTIST_NAME = "artistName";

    private final String LOG_TAG = ArtistSearchFragment.class.getSimpleName();

    private ArtistsAdapter mArtistsAdapter;

    @InjectView(R.id.artist_edittext)
    public EditText artistEditText;

    @InjectView(R.id.listview_artists)
    public ListView artistsListView;

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

        ButterKnife.inject(this, rootView);

        // Found information on updating the array adapters image
        mArtistsAdapter = new ArtistsAdapter(getActivity(), R.layout.list_item_artists, new ArrayList<Artist>());

        artistsListView.setAdapter(mArtistsAdapter);

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

    @OnItemClick(R.id.listview_artists)
    public void onArtistItemClicked(AdapterView<?> parent, View view, int position, long id)
    {
        Toast.makeText(getActivity(), "Artist " + mArtistsAdapter.getItem(position).name + " was clicked.", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(getActivity(), TopTenTracksActivity.class);
        intent.putExtra(KEY_ARTIST_ID, mArtistsAdapter.getItem(position).id);
        startActivity(intent);
    }

    @OnTextChanged(R.id.artist_edittext)
    public void searchForArtist() {
        if (artistEditText != null && artistEditText.getText().length() > 0) {
            SpotifyApi api = new SpotifyApi();
            SpotifyService spotify = api.getService();
            spotify.searchArtists(artistEditText.getText().toString(), new Callback<ArtistsPager>() {
                @Override
                public void success(final ArtistsPager artistsPager, Response response) {
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            updateArrayAdapter(artistsPager);
                        }
                    };
                    if(getActivity() != null)
                    {
                        getActivity().runOnUiThread(runnable);
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    if (error.getKind() == RetrofitError.Kind.NETWORK)
                    {
                        Runnable runnable = new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getActivity(), R.string.toast_message_unable_to_reach_spotify, Toast.LENGTH_SHORT).show();
                            }
                        };
                        if(getActivity() != null)
                        {
                            getActivity().runOnUiThread(runnable);
                        }
                    }
                }
            });
        }
    }

    public void updateArrayAdapter(ArtistsPager artistsPager)
    {
        if (artistsPager != null && artistsPager.artists != null) {
            mArtistsAdapter.clear();

            for (Artist artist : artistsPager.artists.items) {
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
            Toast.makeText(getActivity(), getActivity().getString(R.string.toast_message_unable_to_locate_artist), Toast.LENGTH_LONG).show();
        }
    }
}
