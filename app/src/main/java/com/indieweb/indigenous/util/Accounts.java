package com.indieweb.indigenous.util;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import androidx.appcompat.app.AlertDialog;
import android.widget.Toast;

import com.indieweb.indigenous.LaunchActivity;
import com.indieweb.indigenous.R;
import com.indieweb.indigenous.indieauth.IndieAuthActivity;
import com.indieweb.indigenous.model.User;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class Accounts {

    private final Context context;

    public Accounts(Context context) {
        this.context = context;
    }

    /**
     * Gets the current user.
     *
     * @return User
     */
    public User getCurrentUser() {
        User user = new User();

        SharedPreferences preferences = context.getSharedPreferences("indigenous", MODE_PRIVATE);
        String accountName = preferences.getString("account", "");
        AccountManager accountManager = AccountManager.get(context);
        Account[] accounts = accountManager.getAccounts();
        if (accounts.length > 0) {
            for (Account account : accounts) {
                if (account.name.equals(accountName)) {
                    user.setValid(true);
                    user.setMe(accountName);

                    String token = "";
                    try {
                        token = accountManager.peekAuthToken(account, IndieAuthActivity.TOKEN_TYPE);
                    }
                    catch (Exception ignored) {}

                    user.setAccessToken(token);
                    user.setAvatar(accountManager.getUserData(account, "author_avatar"));
                    user.setName(accountManager.getUserData(account, "author_name"));
                    user.setTokenEndpoint(accountManager.getUserData(account, "token_endpoint"));
                    user.setAuthorizationEndpoint(accountManager.getUserData(account, "authorization_endpoint"));
                    user.setMicrosubEndpoint(accountManager.getUserData(account, "microsub_endpoint"));
                    user.setMicropubEndpoint(accountManager.getUserData(account, "micropub_endpoint"));
                    user.setMicropubMediaEndpoint(accountManager.getUserData(account, "micropub_media_endpoint"));

                    user.setSyndicationTargets(accountManager.getUserData(account, "syndication_targets"));
                    user.setPostTypes(accountManager.getUserData(account, "post_types"));
                    user.setAccount(account);
                }
            }
        }

        return user;
    }

    /**
     * Switch account dialog.
     *
     * @param activity
     *   The current activity
     */
    public void switchAccount(final Activity activity, final User user) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(String.format(context.getString(R.string.account_switch), user.getMe()));
        builder.setPositiveButton(context.getString(R.string.switch_account),new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int id) {
                Toast.makeText(context, String.format(context.getString(R.string.account_selected), user.getMe()), Toast.LENGTH_SHORT).show();
                SharedPreferences.Editor editor = context.getSharedPreferences("indigenous", MODE_PRIVATE).edit();
                editor.putString("account", user.getAccount().name);
                editor.apply();
                Intent Main = new Intent(context, LaunchActivity.class);
                context.startActivity(Main);
                activity.finish();

            }
        });
        builder.setNegativeButton(context.getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    /**
     * Select account dialog.
     *
     * @param activity
     *   The current activity
     */
    public void selectAccount(final Activity activity) {
        final List<String> accounts = new ArrayList<>();

        final Account[] AllAccounts = this.getAllAccounts();
        for (Account account: AllAccounts) {
            accounts.add(account.name);
        }

        final CharSequence[] accountItems = accounts.toArray(new CharSequence[accounts.size()]);
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(activity.getString(R.string.account_select));

        builder.setCancelable(true);
        builder.setItems(accountItems, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int index) {
                Toast.makeText(context, String.format(context.getString(R.string.account_selected), accounts.get(index)), Toast.LENGTH_SHORT).show();
                SharedPreferences.Editor editor = context.getSharedPreferences("indigenous", MODE_PRIVATE).edit();
                editor.putString("account", accounts.get(index));
                editor.apply();
                Intent Main = new Intent(context, LaunchActivity.class);
                context.startActivity(Main);
                activity.finish();
            }
        });
        builder.show();
    }

    /**
     * Returns all accounts.
     *
     * @return Account[]
     */
    private Account[] getAllAccounts() {
        AccountManager accountManager = AccountManager.get(context);
        return accountManager.getAccounts();
    }

    /**
     * Returns all users.
     *
     * @return User[]
     */
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        AccountManager accountManager = AccountManager.get(context);
        for (Account a : accountManager.getAccounts()) {
            User user = new User();
            user.setAccount(a);
            user.setMe(a.name);
            String token = "";
            try {
                token = accountManager.peekAuthToken(a, IndieAuthActivity.TOKEN_TYPE);
            }
            catch (Exception ignored) {}

            user.setAccessToken(token);
            user.setAvatar(accountManager.getUserData(a, "author_avatar"));
            user.setName(accountManager.getUserData(a, "author_name"));
            user.setTokenEndpoint(accountManager.getUserData(a, "token_endpoint"));
            user.setAuthorizationEndpoint(accountManager.getUserData(a, "authorization_endpoint"));
            user.setMicrosubEndpoint(accountManager.getUserData(a, "microsub_endpoint"));
            user.setMicropubEndpoint(accountManager.getUserData(a, "micropub_endpoint"));
            user.setMicropubMediaEndpoint(accountManager.getUserData(a, "micropub_media_endpoint"));
            users.add(user);
        }
        return users;
    }

}
