package com.indieweb.indigenous;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.indieweb.indigenous.general.AboutFragment;
import com.indieweb.indigenous.general.SettingsActivity;
import com.indieweb.indigenous.indieauth.UsersFragment;
import com.indieweb.indigenous.micropub.draft.DraftFragment;
import com.indieweb.indigenous.micropub.post.ArticleActivity;
import com.indieweb.indigenous.micropub.post.BookmarkActivity;
import com.indieweb.indigenous.micropub.post.EventActivity;
import com.indieweb.indigenous.micropub.post.IssueActivity;
import com.indieweb.indigenous.micropub.post.LikeActivity;
import com.indieweb.indigenous.micropub.post.NoteActivity;
import com.indieweb.indigenous.micropub.post.ReplyActivity;
import com.indieweb.indigenous.micropub.post.RepostActivity;
import com.indieweb.indigenous.micropub.post.RsvpActivity;
import com.indieweb.indigenous.micropub.post.UploadActivity;
import com.indieweb.indigenous.micropub.source.PostListFragment;
import com.indieweb.indigenous.microsub.channel.ChannelFragment;
import com.indieweb.indigenous.model.User;
import com.indieweb.indigenous.util.Accounts;
import com.indieweb.indigenous.util.Preferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    String incomingText = "";
    String incomingImage = "";
    NavigationView navigationView;
    User user;

    /**
     * Set first navigation view.
     */
    private void setFirstItemNavigationView() {
        navigationView.setCheckedItem(R.id.nav_reader);
        navigationView.getMenu().performIdentifierAction(R.id.nav_reader, 0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        user = new Accounts(this).getCurrentUser();

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        setFirstItemNavigationView();

        // Set user information.
        User user = new Accounts(getApplicationContext()).getCurrentUser();
        View headerView = navigationView.getHeaderView(0);
        TextView authorUrl = headerView.findViewById(R.id.navAuthorUrl);
        if (authorUrl != null) {
            authorUrl.setVisibility(View.VISIBLE);
            authorUrl.setText(user.getMe());
        }

        if (user.getName().length() > 0) {
            TextView authorName = headerView.findViewById(R.id.navAuthorName);
            if (authorName != null) {
                authorName.setVisibility(View.VISIBLE);
                authorName.setText(user.getName());
            }
        }

        if (user.getAvatar().length() > 0) {
            ImageView authorAvatar = headerView.findViewById(R.id.navAuthorAvatar);
            if (authorAvatar != null) {
                Glide.with(getApplicationContext())
                        .load(user.getAvatar())
                        .apply(RequestOptions.circleCropTransform())
                        .into(authorAvatar);
            }
        }

        // Hide Media if micropub media endpoint is empty.
        Menu menu = navigationView.getMenu();
        String micropubMediaEndpoint = user.getMicropubMediaEndpoint();
        if (micropubMediaEndpoint == null || micropubMediaEndpoint.length() == 0) {
            menu.removeItem(R.id.nav_upload);
        }

        // Hide Posts if setting is not enabled.
        if (!Preferences.getPreference(this, "pref_key_source_post_list", false)) {
            menu.removeItem(R.id.nav_posts);
        }

        // Hide post types if configured.
        if (Preferences.getPreference(this, "pref_key_post_type_hide", false)) {

            String postTypes = user.getPostTypes();
            ArrayList<String> postTypeList = new ArrayList<>();
            if (postTypes != null && postTypes.length() > 0) {
                try {
                    JSONObject object;
                    JSONArray itemList = new JSONArray(postTypes);

                    for (int i = 0; i < itemList.length(); i++) {
                        object = itemList.getJSONObject(i);
                        String type = object.getString("type");
                        postTypeList.add(type);
                    }

                }
                catch (JSONException ignored) { }
            }

            // Loop over menu items.
            String menuType = null;
            for (int i = 0; i < menu.size(); i++){
                int id = menu.getItem(i).getItemId();
                switch (id) {
                    case R.id.createNote:
                        menuType = "note";
                        break;
                    case R.id.createArticle:
                        menuType = "article";
                        break;
                    case R.id.createLike:
                        menuType = "like";
                        break;
                    case R.id.createBookmark:
                        menuType = "bookmark";
                        break;
                    case R.id.createReply:
                        menuType = "reply";
                        break;
                    case R.id.createRepost:
                        menuType = "repost";
                        break;
                    case R.id.createEvent:
                        menuType = "event";
                        break;
                    case R.id.createRSVP:
                        menuType = "rsvp";
                        break;
                    case R.id.createIssue:
                        menuType = "issue";
                        break;
                }

                if (menuType != null && !postTypeList.contains(menuType)) {
                    menu.removeItem(id);
                }

                // Reset.
                menuType = null;
            }
        }

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        // TODO close drawer for certain settings, or do not set selected.

        boolean close = false;
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        Menu drawerMenu = navigationView.getMenu();

        Fragment fragment = null;
        switch (item.getItemId()) {

            case R.id.nav_reader:
                close = true;
                fragment = new ChannelFragment();
                // TODO if the user does not have a micosub endpoint, set it to an info
                // or set visibility to 0 in that fragment.
                break;

            case R.id.nav_create:
                drawerMenu.setGroupVisible(R.id.navMainGroup, false);
                drawerMenu.setGroupVisible(R.id.navPostGroup, true);
                break;

            case R.id.nav_main_menu:
                drawerMenu.setGroupVisible(R.id.navMainGroup, true);
                drawerMenu.setGroupVisible(R.id.navPostGroup, false);
                break;

            case R.id.nav_drafts:
                close = true;
                fragment = new DraftFragment();
                break;

            case R.id.nav_posts:
                close = true;
                fragment = new PostListFragment();
                break;

            case R.id.nav_accounts:
                close = true;
                fragment = new UsersFragment();
                break;

            case R.id.nav_settings:
                Intent settings = new Intent(getBaseContext(), SettingsActivity.class);
                startActivity(settings);
                break;

            case R.id.nav_about:
                close = true;
                fragment = new AboutFragment();
                break;

            case R.id.createArticle:
                Intent CreateArticle = new Intent(getBaseContext(), ArticleActivity.class);
                if (incomingText != null && incomingText.length() > 0) {
                    CreateArticle.putExtra("incomingText", incomingText);
                }
                if (incomingImage != null && incomingImage.length() > 0) {
                    CreateArticle.putExtra("incomingImage", incomingImage);
                }
                startActivity(CreateArticle);
                break;

            case R.id.createNote:
                Intent CreateNote = new Intent(getBaseContext(), NoteActivity.class);
                if (incomingText.length() > 0) {
                    CreateNote.putExtra("incomingText", incomingText);
                }
                if (incomingImage.length() > 0) {
                    CreateNote.putExtra("incomingImage", incomingImage);
                }
                startActivity(CreateNote);
                break;

            case R.id.createLike:
                Intent CreateLike = new Intent(getBaseContext(), LikeActivity.class);
                if (incomingText.length() > 0) {
                    CreateLike.putExtra("incomingText", incomingText);
                }
                startActivity(CreateLike);
                break;

            case R.id.createReply:
                Intent CreateReply = new Intent(getBaseContext(), ReplyActivity.class);
                if (incomingText.length() > 0) {
                    CreateReply.putExtra("incomingText", incomingText);
                }
                startActivity(CreateReply);
                break;

            case R.id.createBookmark:
                Intent CreateBookmark = new Intent(getBaseContext(), BookmarkActivity.class);
                if (incomingText.length() > 0) {
                    CreateBookmark.putExtra("incomingText", incomingText);
                }
                startActivity(CreateBookmark);
                break;

            case R.id.createRepost:
                Intent CreateRepost = new Intent(getBaseContext(), RepostActivity.class);
                if (incomingText.length() > 0) {
                    CreateRepost.putExtra("incomingText", incomingText);
                }
                startActivity(CreateRepost);
                break;

            case R.id.createEvent:
                Intent CreateEvent = new Intent(getBaseContext(), EventActivity.class);
                if (incomingText.length() > 0) {
                    CreateEvent.putExtra("incomingText", incomingText);
                }
                startActivity(CreateEvent);
                break;

            case R.id.createRSVP:
                Intent CreateRSVP = new Intent(getBaseContext(), RsvpActivity.class);
                if (incomingText.length() > 0) {
                    CreateRSVP.putExtra("incomingText", incomingText);
                }
                startActivity(CreateRSVP);
                break;

            case R.id.createIssue:
                Intent CreateIssue = new Intent(getBaseContext(), IssueActivity.class);
                if (incomingText.length() > 0) {
                    CreateIssue.putExtra("incomingText", incomingText);
                }
                startActivity(CreateIssue);
                break;

            case R.id.nav_upload:
                Intent CreateMedia = new Intent(getBaseContext(), UploadActivity.class);
                // TODO should upload move to post group ?
                if (incomingImage.length() > 0) {
                    CreateMedia.putExtra("incomingImage", incomingImage);
                }
                startActivity(CreateMedia);
                break;
        }

        if (close) {
            drawer.closeDrawer(GravityCompat.START);
        }

        // Update main content frame.
        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, fragment);
            ft.commit();
        }

        return true;
    }

    /**
     * Open the navigation drawer.
     *
     * @param id
     *   The menu item id to perform an action on.
     */
    public void openDrawer(int id) {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer != null) {
            drawer.openDrawer(Gravity.START);
        }

        if (id > 0) {
            navigationView.setCheckedItem(id);
            navigationView.getMenu().performIdentifierAction(id, 0);
        }
    }
}
