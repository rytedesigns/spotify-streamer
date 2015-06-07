package com.rytedesigns.spotifystreamer.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.rytedesigns.spotifystreamer.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import kaaes.spotify.webapi.android.models.Image;
import kaaes.spotify.webapi.android.models.Track;

/**
 * com.rytedesigns.spotifystreamer.adapter
 * <p/>
 * Copyright 2015 Ryan Todd Ellenberger
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * This class uses the Spotify Wraper API from https://github.com/kaaes/spotify-web-api-android
 * This class uses the Picaso library from Square.
 *
 */
public class TracksAdapter extends ArrayAdapter<Track> {

    private final String LOG_TAG = TracksAdapter.class.getSimpleName();

    @InjectView(R.id.album_imageview)
    ImageView albumImageView;

    @InjectView(R.id.track_textview)
    TextView trackNameTextView;

    @InjectView(R.id.album_textview)
    TextView albumNameTextView;

    public TracksAdapter(Context context, int resource) {
        super(context, resource);
    }

    public TracksAdapter(Context context, int resource, int textViewResourceId) {
        super(context, resource, textViewResourceId);
    }

    public TracksAdapter(Context context, int resource, Track[] objects) {
        super(context, resource, objects);
    }

    public TracksAdapter(Context context, int resource, int textViewResourceId, Track[] objects) {
        super(context, resource, textViewResourceId, objects);
    }

    public TracksAdapter(Context context, int resource, List<Track> objects) {
        super(context, resource, objects);
    }

    public TracksAdapter(Context context, int resource, int textViewResourceId, List<Track> objects) {
        super(context, resource, textViewResourceId, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View rootView = convertView;

        if (rootView == null) {
            LayoutInflater mInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rootView = mInflater.inflate(R.layout.list_item_tracks, parent, false);
        }

        ButterKnife.inject(this, rootView);

        Track track = getItem(position);

        if (getItem(position) == null) {
            track = new Track();
        }

        if (track.album.images.size() > 0) {
            for (Image image : track.album.images) {
                Log.d(LOG_TAG, "Artist:" + track.name + " height:" + image.height + " width:" + image.width + " URL:" + image.url);
                if (image.width > 200) {
                    Picasso.with(getContext())
                            .load(image.url)
                            .resizeDimen(R.dimen.image_width, R.dimen.image_height)
                            .error(R.mipmap.ic_launcher)
                            .placeholder(R.mipmap.ic_launcher)
                            .into(albumImageView);
                }
            }
        } else {
            Picasso.with(getContext())
                    .load(R.mipmap.ic_launcher)
                    .resizeDimen(R.dimen.image_width, R.dimen.image_height)
                    .into(albumImageView);
        }

        // Track Name
        String trackName = getItem(position).name;

        // Set the Track Name TextView.
        trackNameTextView.setText(trackName);

        // Album Name
        String albumName = getItem(position).album.name;

        // Set the Artist Name TextView.
        albumNameTextView.setText(albumName);

        return rootView;
    }
}
