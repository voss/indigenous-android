# Indigenous for Android

An app with extensions for sharing information to micropub endpoints and reading from microsub endpoints.

Install from Google Play: https://play.google.com/store/apps/details?id=com.indieweb.indigenous  
Install from F-Droid: https://f-droid.org/en/packages/com.indieweb.indigenous/

No builds will be uploaded anymore to GitHub, use F-Droid for that.

More information at https://indieweb.org/Indigenous and https://indigenous.abode.pub/android/

## Functionality

- Login with multiple domains, discover indieauth, micropub, microsub and media endpoints.
  The microsub endpoint is optional so you can simply use this as a micropub 
  client too.
- Micropub
  - post types: article, note, reply, repost, like, bookmark, event, issue and rsvp
  - add image, tags, toggle syndication targets
  - send images to media endpoint
  - share location on note, article and event
  - Toggle post-status (published vs draft)
  - Save articles, notes or replies as draft
  - Query, update and delete: get a list of posts and update basic properties (experimental)
- Microsub
  - read channels, with pull to refresh
  - read items per channel, with pull to refresh, response actions per item
  - reply, like, repost, rsvp, bookmark, upload or add feed directly
  - listen to audio or watch video if available 
  - view fullscreen images, zoom and pinch
  - Manage channels and feeds
  - Background service to check for new posts and trigger notifications
- Share intents: receive text or images from other apps to directly share

## Screenshot

<img src="https://realize.be/sites/default/files/2019-02/1550590120900.jpg" width="400" />

## Known issues

#### Authentication loop

When authenticating for the first time, you might see a message "Authentication successul" but then
returning to the Sign in screen. While the account has been created, the default was not set. In
this case, the sign in screen will allow you to select the account.

See https://github.com/swentel/indigenous-android/issues/84  
This happens sometimes, but not always.

#### Known syndication targets not parsed

See https://github.com/swentel/indigenous-android/issues/152

#### Authenticating

Some browsers (e.g. the default browser on LineageOS), won't allow Indigenous to listen to the callback
to verify the authorization code. Install a browser like Firefox (any flavor) so you can login.

## iOS

https://indigenous.abode.pub/ios/  
https://github.com/EdwardHinkle/indigenous-ios

## Other clients

https://github.com/pstuifzand/micropub-android
