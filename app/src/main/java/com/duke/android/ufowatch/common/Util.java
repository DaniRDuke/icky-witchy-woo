package com.duke.android.ufowatch.common;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.net.ssl.HttpsURLConnection;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.duke.android.ufowatch.R;
import com.duke.android.ufowatch.UfoWatchMain;
import com.duke.android.ufowatch.domain.Library;
import com.duke.android.ufowatch.domain.Video;
import com.duke.android.ufowatch.ui.UfoWatchWebView;
import com.duke.android.ufowatch.ui.YouTubeCaller;
import com.duke.android.ufowatch.ui.adapter.YouTubeItemsArrayAdapter;
import com.duke.android.ufowatch.ui.phone.VideoInfo;
import com.duke.android.ufowatch.ui.phone.VideoList2;
import com.duke.android.ufowatch.ui.widget.CustomDialog;
import com.duke.android.ufowatch.ui.widget.CustomDialogNewPlaylist;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayer.PlaybackEventListener;
import com.google.android.youtube.player.YouTubePlayer.PlaylistEventListener;
import com.google.android.youtube.player.YouTubePlayer.Provider;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.YouTube.Builder;
import com.google.api.services.youtube.YouTube.PlaylistItems.Insert;
import com.google.api.services.youtube.model.Channel;
import com.google.api.services.youtube.model.ChannelListResponse;
import com.google.api.services.youtube.model.Playlist;
import com.google.api.services.youtube.model.PlaylistItem;
import com.google.api.services.youtube.model.PlaylistItemListResponse;
import com.google.api.services.youtube.model.PlaylistItemSnippet;
import com.google.api.services.youtube.model.PlaylistListResponse;
import com.google.api.services.youtube.model.PlaylistSnippet;
import com.google.api.services.youtube.model.PlaylistStatus;
import com.google.api.services.youtube.model.ResourceId;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.ThumbnailDetails;

public class Util {

	public static final String TAG = "Util";

	private static SharedPreferences mSharedPreferences;
	private static SharedPreferences.Editor mPrefsEditor;

	public static BroadcastReceiver createBroadcastReceiver(Context context) {
		final Context ctx = context;
		BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				Class<? extends Context> cl = ctx.getClass();
				if (cl == VideoInfo.class) {
					((VideoInfo) ctx).finish();
				} else if (cl == VideoList2.class) {
					((VideoList2) ctx).finish();
				} else if (cl == UfoWatchWebView.class) {
					((UfoWatchWebView) ctx).finish();
				}
			}
		};

		return mBroadcastReceiver;
	}

	public static String getSharedPreferenceString(Context ctx, String key, String defaultValue) {
		mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(ctx);
		return mSharedPreferences.getString(key, defaultValue);
	}

	public static void putSharedPreferenceString(Context ctx, String key, String value) {
		mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(ctx);
		mPrefsEditor = mSharedPreferences.edit();
		mPrefsEditor.putString(key, value);
		mPrefsEditor.commit();
	}

	public static void removeYouTubeAccess(Context ctx) {
		mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(ctx);
		mPrefsEditor = mSharedPreferences.edit();
		mPrefsEditor.remove(SharedConstants.KEY_ACCESS_TOKEN).remove(SharedConstants.KEY_REFRESH_TOKEN)
				.remove(SharedConstants.KEY_USER_CHANNEL_ID);
		mPrefsEditor.commit();
	}

	public static String formatSecondsAsHHMMSS(int secs) {
		int minutes = secs / 60; // automatically cast to int
		int seconds = secs - minutes * 60; // subtract off whole minutes
		int hours = minutes / 60; // automatically cast to int
		minutes = minutes - hours * 60; // subtract off whole hours

		if (hours > 0) {
			return String.format(Locale.US, "%d hr %d min %d sec", hours, minutes, seconds);
		}
		return String.format(Locale.US, "%d min %d sec", minutes, seconds);
	}

	public static String formatDuration(String duration) {
		return duration.replace("PT", "").replace("H", "H ").replace("M", "M ");
	}

	public static String getDatePart(String videoDateTime) {
		return (videoDateTime != null) ? videoDateTime.substring(0, 10) : "";
	}

	public static Spannable createAboutSpannable(Context ctx) {
		String label = ctx.getResources().getString(R.string.msg_about_copyright) + "<br/><br/>"
				+ ctx.getResources().getString(R.string.msg_about_feedback) + "<br/>";

		String subLabel = "• " + ctx.getResources().getString(R.string.msg_supports_android_version);
		subLabel += "<br/><br/>";
		subLabel += ctx.getResources().getString(R.string.msg_about_library_usage);
		subLabel += "<br/><br/>";
		subLabel += "• " + ctx.getResources().getString(R.string.msg_about_viewpager);

		Spannable span = new SpannableString(Html.fromHtml(label + "<br/>" + subLabel).toString());
		span.setSpan(new RelativeSizeSpan(0.8f), label.length() - 25, span.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

		return span;
	}

	public static Bundle createBundleVideoSearchByWordOrPhrase(String searchWordOrPhrase) {
		Bundle bundle = new Bundle();
		bundle.putString(SharedConstants.VIDEO_SEARCH_PARAM_NAME_KEYWORDS, SharedConstants.VIDEO_SEARCH_KEYWORDS + "+"
				+ searchWordOrPhrase);
		return bundle;
	}

	public static Bundle createBundleVideoSearchByTime(long startTime, long endTime) {
		Bundle bundle = new Bundle();
		bundle.putString(SharedConstants.VIDEO_SEARCH_PARAM_NAME_KEYWORDS, SharedConstants.VIDEO_SEARCH_KEYWORDS);
		// bundle.putString(SharedConstants.VIDEO_SEARCH_PARAM_NAME_TIME, time);
		bundle.putLong(SharedConstants.VIDEO_SEARCH_PARAM_NAME_START_TIME, startTime);
		bundle.putLong(SharedConstants.VIDEO_SEARCH_PARAM_NAME_END_TIME, endTime);
		return bundle;
	}

	public static Bundle createBundleVideoSearchByTimeWTitle(long startTime, long endTime, String pageTitle) {
		Bundle bundle = createBundleVideoSearchByTime(startTime, endTime);
		bundle.putString(SharedConstants.KEY_TITLE, pageTitle);
		return bundle;
	}

	public static long getTodayStartTimeInMilliseconds() {
		final Calendar c = Calendar.getInstance();
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);

		return c.getTimeInMillis();

	}

	public static Intent createIntentWebView(Context ctx, String url, String pageTitle) {
		Intent i = new Intent(ctx, UfoWatchWebView.class);
		Bundle bundle = Util.createBundleWebsite(url, pageTitle);
		i.putExtras(bundle);
		return i;
	}

	public static Bundle createBundleWebsite(String url, String pageTitle) {
		Bundle bundle = new Bundle();
		bundle.putString(SharedConstants.KEY_URL, url);
		bundle.putString(SharedConstants.KEY_TITLE, pageTitle);
		return bundle;
	}

	public static AlertDialog createDialogAbout(Context context) {
		final Context ctx = context;

		AlertDialog alertDialog = new AlertDialog.Builder(ctx).create();

		String versionName;
		PackageInfo packageInfo;
		try {
			packageInfo = ctx.getPackageManager().getPackageInfo(ctx.getPackageName(), 0);
			versionName = packageInfo.versionName;
		} catch (NameNotFoundException e) {
			versionName = "";
		}

		alertDialog.setTitle(ctx.getResources().getString(R.string.title_app_name) + " " + versionName);
		alertDialog.setMessage(Util.createAboutSpannable(ctx));

		alertDialog.setIcon(R.drawable.ic_launcher);

		alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, ctx.getResources().getString(R.string.lbl_btn_about_email),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
						emailIntent.setType("text/plain");
						String aEmailList[] = {ctx.getResources().getString(R.string.contact_email)};
						emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, aEmailList);
						emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
								ctx.getResources().getString(R.string.title_app_name));
						ctx.startActivity(Intent.createChooser(emailIntent,
								ctx.getResources().getString(R.string.msg_send_email_using)));
					}
				});

		alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE,
				ctx.getResources().getString(R.string.lbl_btn_about_website), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {

						Class<? extends Context> cl = ctx.getClass();
						// if this isn't BusStationMain then it is definitely
						// a handset Activity
						if (cl != UfoWatchMain.class) {
							Intent i = new Intent(ctx, UfoWatchWebView.class);
							i.putExtra(SharedConstants.KEY_URL,
									ctx.getResources().getString(R.string.url_dukecreations_ufowatch));
							i.putExtra(SharedConstants.KEY_TITLE,
									ctx.getResources().getString(R.string.subtitle_website_name));
							if (cl == VideoInfo.class) {
								((VideoInfo) ctx).startActivity(i);
							} else if (cl == VideoList2.class) {
								((VideoList2) ctx).startActivity(i);
							} else if (cl == UfoWatchWebView.class) {
								((UfoWatchWebView) ctx).startActivity(i);
							}

						} // otherwise this is the main menu either on handset
							// else if (!((UfoWatchMain) ctx).isTablet()) {
							// Intent i = new Intent(ctx,
							// UfoWatchWebView.class);
							// i.putExtra(SharedConstants.KEY_URL,
							// ctx.getResources().getString(R.string.url_dukecreations_ufowatch));
							// i.putExtra(SharedConstants.KEY_ACTION_BAR_TITLE,
							// ctx.getResources().getString(R.string.subtitle_website_name));
							// ((FragmentActivity) ctx).startActivity(i);
							// } // or on tablet
							// else {
							// ((UfoWatchMain) ctx).displayWebsite(
							// ctx.getResources().getString(R.string.title_website_name),
							// ctx.getResources()
							// .getString(R.string.subtitle_website_name),
							// ctx.getResources().getString(R.string.url_dukecreations_ufowatch));
							// }
						else {
							Intent i = new Intent(ctx, UfoWatchWebView.class);
							i.putExtra(SharedConstants.KEY_URL,
									ctx.getResources().getString(R.string.url_dukecreations_ufowatch));
							i.putExtra(SharedConstants.KEY_TITLE,
									ctx.getResources().getString(R.string.subtitle_website_name));
							((FragmentActivity) ctx).startActivity(i);
						}
					}
				});

		alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, ctx.getResources().getString(R.string.lbl_btn_close),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						// do nothing
					}
				});

		return alertDialog;

	}

	// public static AlertDialog createDialogAlertMessage(Context context,
	// String title, String message) {
	// final Context ctx = context;
	//
	// AlertDialog alertDialog = new AlertDialog.Builder(ctx).create();
	//
	// alertDialog.setTitle(title);
	// alertDialog.setMessage(message);
	//
	// alertDialog.setIcon(R.drawable.ic_launcher);
	//
	// alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL,
	// ctx.getResources().getString(R.string.lbl_btn_close),
	// new DialogInterface.OnClickListener() {
	// public void onClick(DialogInterface dialog, int which) {
	// dialog.cancel();
	// }
	// });
	//
	// return alertDialog;
	//
	// }

	public static AlertDialog createDialogGetPermission(Context context) {
		final Context ctx = context;

		AlertDialog alertDialog = new AlertDialog.Builder(ctx).create();
		alertDialog.setTitle(ctx.getResources().getString(R.string.title_youtube_permission));
		alertDialog.setMessage(ctx.getResources().getString(R.string.msg_grant_permission));

		alertDialog.setIcon(R.drawable.ic_launcher);

		alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				Bundle bundle = Util.createBundleWebsite("https://accounts.google.com/o/oauth2/auth?" + "client_id="
						+ ctx.getResources().getString(R.string.oauth2_client_id)
						+ "&redirect_uri=urn:ietf:wg:oauth:2.0:oob&" + "scope=https://www.googleapis.com/auth/youtube&"
						+ "response_type=code&" + "access_type=offline",
						ctx.getResources().getString(R.string.title_youtube_permission));
				Intent i = new Intent(ctx, UfoWatchWebView.class);
				i.putExtras(bundle);
				ctx.startActivity(i);
			}
		});

		alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, ctx.getResources().getString(R.string.lbl_btn_close),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						// do nothing
					}
				});

		return alertDialog;

	}
	public static CustomDialog createDialogYouTube(Context context, Video video) {

		final Context ctx = context;
		final Video mVideo = video;
		final CustomDialog customDialog;

		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(ctx, R.array.youTubeActions,
				R.layout.list_item_simple);

		OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				String chosen = (String) parent.getItemAtPosition(position);

				Intent i;
				String accToken = Util.getSharedPreferenceString(ctx, SharedConstants.KEY_ACCESS_TOKEN, null);
				// Log.d(TAG, "accToken: " + accToken);
				if (accToken == null) {
					AlertDialog dialog = Util.createDialogGetPermission(ctx);
					dialog.show();
				} else if (chosen.equalsIgnoreCase(ctx.getResources().getString(R.string.youtube_action_favorite))) {
					Bundle bundle = new Bundle();
					bundle.putSerializable(SharedConstants.KEY_VIDEO, mVideo);
					bundle.putString(SharedConstants.KEY_YOUTUBE_CHOICE, SharedConstants.VIDEO_FAVORITE);
					i = new Intent(ctx, YouTubeCaller.class);
					i.putExtras(bundle);
					ctx.startActivity(i);
				} else if (chosen.equalsIgnoreCase(ctx.getResources().getString(R.string.youtube_action_like))) {
					Bundle bundle = new Bundle();
					bundle.putSerializable(SharedConstants.KEY_VIDEO, mVideo);
					bundle.putString(SharedConstants.KEY_YOUTUBE_CHOICE, SharedConstants.VIDEO_RATING_LIKE);
					i = new Intent(ctx, YouTubeCaller.class);
					i.putExtras(bundle);
					ctx.startActivity(i);
				} else if (chosen.equalsIgnoreCase(ctx.getResources().getString(R.string.youtube_action_dislike))) {
					Bundle bundle = new Bundle();
					bundle.putSerializable(SharedConstants.KEY_VIDEO, mVideo);
					bundle.putString(SharedConstants.KEY_YOUTUBE_CHOICE, SharedConstants.VIDEO_RATING_DISLIKE);
					i = new Intent(ctx, YouTubeCaller.class);
					i.putExtras(bundle);
					ctx.startActivity(i);
				} else if (chosen.equalsIgnoreCase(ctx.getResources().getString(R.string.youtube_action_watch_later))) {
					Bundle bundle = new Bundle();
					bundle.putSerializable(SharedConstants.KEY_VIDEO, mVideo);
					bundle.putString(SharedConstants.KEY_YOUTUBE_CHOICE, SharedConstants.VIDEO_WATCH_LATER);
					i = new Intent(ctx, YouTubeCaller.class);
					i.putExtras(bundle);
					ctx.startActivity(i);
				} else if (chosen.equalsIgnoreCase(ctx.getResources().getString(R.string.youtube_action_subscribe))) {
					Bundle bundle = new Bundle();
					bundle.putSerializable(SharedConstants.KEY_VIDEO, mVideo);
					bundle.putString(SharedConstants.KEY_YOUTUBE_CHOICE, SharedConstants.CHANNEL_SUBSCRIBE);
					i = new Intent(ctx, YouTubeCaller.class);
					i.putExtras(bundle);
					ctx.startActivity(i);
				} else if (chosen.equalsIgnoreCase(ctx.getResources()
						.getString(R.string.youtube_action_add_to_playlist))) {
					Bundle bundle = new Bundle();
					bundle.putSerializable(SharedConstants.KEY_VIDEO, mVideo);
					bundle.putString(SharedConstants.KEY_YOUTUBE_CHOICE, SharedConstants.VIDEO_ADD_TO);
					i = new Intent(ctx, YouTubeCaller.class);
					i.putExtras(bundle);
					ctx.startActivity(i);
				} else if (chosen.equalsIgnoreCase(ctx.getResources()
						.getString(R.string.youtube_action_create_playlist))) {
					// include the textview, edittext buttons etc.
					CustomDialogNewPlaylist dialog = Util.createCustomDialogNewPlaylist(ctx);
					dialog.show();
				}

			}

		};

		customDialog = Util.createCustomDialogList(ctx, ctx.getResources().getString(R.string.title_dialog_youtube),
				adapter, onItemClickListener);

		return customDialog;

	}

	public static CustomDialogNewPlaylist createCustomDialogNewPlaylist(Context context) {
		CustomDialogNewPlaylist customDialogNewPlaylist = new CustomDialogNewPlaylist(context);
		setWindowRight(customDialogNewPlaylist);
		return customDialogNewPlaylist;
	}

	public static JSONObject getAuthorizationTokens(String authCode, Context context) {

		final Context ctx = context;
		JSONObject jsonObj = null;

		HttpsURLConnection urlConnection = null;
		try {

			urlConnection = Util.createHttpsURLConnectionPost("https://accounts.google.com/o/oauth2/token");

			// assemble the parameters string
			StringBuilder paramString = new StringBuilder();
			paramString.append("code=").append(authCode).append("&");
			paramString.append("client_id=").append(ctx.getResources().getString(R.string.oauth2_client_id))
					.append("&");
			paramString.append("redirect_uri=urn:ietf:wg:oauth:2.0:oob").append("&");
			paramString.append("grant_type=authorization_code");

			Util.writeParamsToConnectionOutputStream(urlConnection, paramString.toString());

			// Connect
			urlConnection.connect();

			String results = Util.getResultsFromConnectionInputStream(urlConnection);
			Log.d(TAG, "getAuthorizationTokens result:" + results);

			jsonObj = new JSONObject(results);

		} catch (MalformedURLException e1) {
			Log.e(TAG, "MalformedURLException");
		} catch (IOException e) {
			Log.e(TAG, "IOException");
		} catch (JSONException e) {
			Log.e(TAG, "JSONException");
		} finally {
			urlConnection.disconnect();
		}

		return jsonObj;

	}

	/**
	 * @param context
	 * @return true if refresh is necessary
	 */
	public static boolean tokenRefreshIsNecessary(Context context) {
		final Context ctx = context;

		HttpsURLConnection urlConnection = null;

		// if "error" in the response then refresh the token
		String accessToken = Util.getSharedPreferenceString(ctx, SharedConstants.KEY_REFRESH_TOKEN, null);
		if (accessToken != null) {
			try {
				Log.d(TAG, "tokenRefreshIsNecessary create connection");
				String url = "https://www.googleapis.com/oauth2/v1/tokeninfo?access_token=" + accessToken;
				Log.d(TAG, "tokenRefreshIsNecessary connect: " + url);
				urlConnection = Util.createHttpsURLConnectionPost(url);
				// Log.d(TAG,
				// "tokenRefreshIsNecessary write params to output stream");
				// Util.writeParamsToConnectionOutputStream(urlConnection,
				// "access_token=" + accessToken);
				// Log.d(TAG, "tokenRefreshIsNecessary connect");
				urlConnection.connect();
				// Log.d(TAG, "tokenRefreshIsNecessary get results");
				String results = Util.getResultsFromConnectionInputStream(urlConnection);
				Log.d(TAG, "tokenRefreshIsNecessary results:" + results);

				if (results.contains("error")) {
					Log.d(TAG, "tokenRefreshIsNecessary results contains error");
					return true;
				}

			} catch (MalformedURLException e1) {
				Log.e(TAG, "MalformedURLException");
			} catch (IOException e) {
				Log.e(TAG, "IOException: " + e.getStackTrace());
			} finally {
				urlConnection.disconnect();
			}
		}

		// HttpEntity entity = null;
		// // if "error" in the response then get a refresh the token
		// String accessToken = Util.getSharedPreferenceString(ctx,
		// SharedConstants.KEY_REFRESH_TOKEN, null);
		// if (accessToken != null) {
		// HttpClient httpclient = new DefaultHttpClient();
		// HttpPost httppost = new
		// HttpPost("https://www.googleapis.com/oauth2/v1/tokeninfo?access_token="
		// + accessToken);
		// HttpResponse response;
		// try {
		// response = httpclient.execute(httppost);
		// entity = response.getEntity();
		// // if error is found it means invalid credentials
		// if (EntityUtils.toString(entity).contains("error")) {
		// return true;
		// }
		// } catch (IOException e) {
		// ;
		// }
		// }

		return false;
	}

	/**
	 * Get a new access token for OAuth2 authentication and save it in shared
	 * preferences as SharedConstants.KEY_ACCESS_TOKEN.
	 * 
	 * @param refreshToken
	 *            User's refresh token.
	 * @return The access token
	 */
	public static String getNewAccessToken(Context context) {
		final Context ctx = context;

		Log.d(TAG, "getNewAccessToken");
		String refreshToken = Util.getSharedPreferenceString(ctx, SharedConstants.KEY_REFRESH_TOKEN, "");

		String accessToken = null;
		JSONObject jsonObj = null;

		HttpsURLConnection urlConnection = null;
		try {

			urlConnection = Util.createHttpsURLConnectionPost("https://accounts.google.com/o/oauth2/token");

			// assemble the parameters string
			StringBuilder paramString = new StringBuilder();
			paramString.append("client_id=").append(ctx.getResources().getString(R.string.oauth2_client_id))
					.append("&");
			paramString.append("refresh_token=").append(refreshToken).append("&");
			paramString.append("grant_type=refresh_token");

			Util.writeParamsToConnectionOutputStream(urlConnection, paramString.toString());

			// Connect
			urlConnection.connect();

			String results = Util.getResultsFromConnectionInputStream(urlConnection);
			Log.d(TAG, "getNewAccessToken results: " + results);

			if (!results.isEmpty()) {

				jsonObj = new JSONObject(results);
				accessToken = jsonObj.getString("access_token");
				Util.putSharedPreferenceString(ctx, SharedConstants.KEY_ACCESS_TOKEN, accessToken);
				Log.d(TAG, "getNewAccessToken SAVED ACCESS TOKEN: " + accessToken);

			}

		} catch (MalformedURLException e1) {
			Log.e(TAG, "MalformedURLException");
		} catch (IOException e) {
			Log.e(TAG, "IOException");
		} catch (JSONException e) {
			Log.e(TAG, "JSONException");
		} finally {
			urlConnection.disconnect();
		}

		Log.d(TAG, "getNewAccessToken return access token: " + accessToken);
		return accessToken;

		// HttpClient httpclient = new DefaultHttpClient();
		// HttpPost httppost = new
		// HttpPost("https://accounts.google.com/o/oauth2/token");
		//
		// JSONObject jsonObj = null;
		// // Log.d(TAG, "getNewAccessToken HERE");
		// try {
		// // Add your data
		// List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
		// nameValuePairs.add(new BasicNameValuePair("client_id",
		// ctx.getResources().getString(
		// R.string.oauth2_client_id)));
		// nameValuePairs.add(new BasicNameValuePair("refresh_token",
		// refreshToken));
		// nameValuePairs.add(new BasicNameValuePair("grant_type",
		// "refresh_token"));
		//
		// httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		//
		// // Execute HTTP Post Request
		// HttpResponse response = httpclient.execute(httppost);
		// HttpEntity entity = response.getEntity();
		// if (entity != null) {
		// InputStream instream = entity.getContent();
		// BufferedInputStream mBufInputStream = new
		// BufferedInputStream(instream);
		// StringBuffer sb = new StringBuffer();
		// int current = 0;
		// while ((current = mBufInputStream.read()) != -1) {
		// sb.append((char) current);
		// }
		// // Log.d(TAG, "getNewAccessToken result:" + sb.toString());
		//
		// jsonObj = new JSONObject(sb.toString());
		// String accessToken = jsonObj.getString("access_token");
		// Util.putSharedPreferenceString(ctx, SharedConstants.KEY_ACCESS_TOKEN,
		// accessToken);
		// // Log.d(TAG, "New ACCESS TOKEN: " + accessToken);
		// return accessToken;
		//
		// }
		//
		// } catch (IOException e) {
		// Log.e(TAG, "getNewAccessToken IOException " + e);
		// } catch (JSONException e) {
		// Log.e(TAG, "getNewAccessToken JSONException " + e);
		// }
		//
		// return null;
	}

	public static void storeUsersChannelId(Context context) throws IOException {
		final Context ctx = context;
		YouTube mYouTube;
		String channelId = null;

		mYouTube = Util.createYouTube(ctx);

		YouTube.Channels.List channelList = mYouTube.channels().list("id").setMine(true).setFields("items/id");
		channelList.setRequestHeaders(Util.createAuthorizedHttpHeaders(ctx));

		ChannelListResponse rsp = channelList.execute();
		Channel ch = rsp.getItems().get(0);
		channelId = ch.getId();
		Util.putSharedPreferenceString(ctx, SharedConstants.KEY_USER_CHANNEL_ID, channelId);
		// Log.d(TAG, "Saved user's Channel Id: " + channelId);

	}

	public static String getUsersPlaylistId(String playlistType, Context context) {
		final Context ctx = context;

		String accessToken = Util.getSharedPreferenceString(ctx, SharedConstants.KEY_ACCESS_TOKEN, "");

		HttpsURLConnection urlConnection = null;
		JSONObject jsonObj = null;
		String playlistId = null;

		try {

			urlConnection = Util
					.createHttpsURLConnectionGet("https://www.googleapis.com/youtube/v3/channels?part=contentDetails&mine=true&fields=items%2FcontentDetails");
			urlConnection.addRequestProperty("Authorization", "Bearer " + accessToken);
			urlConnection.addRequestProperty("Host", "www.googleapis.com");

			// Connect
			urlConnection.connect();

			String results = Util.getResultsFromConnectionInputStream(urlConnection);
			Log.d(TAG, "getUsersPlaylistId result:" + results);

			if (!results.isEmpty()) {

				jsonObj = new JSONObject(results);
				// if error and it was because of invalid credentials
				// then get new access token and request again
				if (errorIsInMessage(jsonObj)) {
					String reason = Util.getErrorReason(jsonObj);
					if (SharedConstants.ERROR_REASON_INVALIDCREDENTIALS.equalsIgnoreCase(reason)) {
						// Log.d(TAG,
						// "getUsersPlaylistId: Invalid credentials, so get a new access token");
						accessToken = Util.getNewAccessToken(ctx);
						if (accessToken == null) {
							// We need to reauthorize first
							return SharedConstants.REAUTHORIZATION_REQUIRED;
						}

						// use new access token
						urlConnection.setRequestProperty("Authorization", "Bearer " + accessToken);
						urlConnection.connect();

						results = Util.getResultsFromConnectionInputStream(urlConnection);
						jsonObj = new JSONObject(results);
					}
				}

			}

			// get the items array
			playlistId = jsonObj.getJSONArray("items").getJSONObject(0).getJSONObject("contentDetails")
					.getJSONObject("relatedPlaylists").getString(playlistType);
			// Log.d(TAG, "Playlist Id: " + playlistId);

		} catch (MalformedURLException e1) {
			Log.e(TAG, "MalformedURLException");
		} catch (IOException e) {
			Log.e(TAG, "IOException");
		} catch (JSONException e) {
			Log.e(TAG, "JSONException");
		} finally {
			urlConnection.disconnect();
		}

		return playlistId;

		// HttpClient httpclient = new DefaultHttpClient();
		// HttpUriRequest request = new HttpGet(
		// "https://www.googleapis.com/youtube/v3/channels?part=contentDetails&mine=true&fields=items%2FcontentDetails");
		//
		// request.addHeader("Authorization", "Bearer " + accessToken);
		// request.addHeader("Host", "www.googleapis.com");
		//
		// JSONObject jsonObj = null;
		// String playlistId = null;
		// try {
		// HttpResponse response = httpclient.execute(request);
		// HttpEntity entity = response.getEntity();
		// if (entity != null) {
		// jsonObj = new JSONObject(EntityUtils.toString(entity));
		// // if error and it was because of invalid credentials
		// // then get new access token and request again
		// if (errorIsInMessage(jsonObj)) {
		// String reason = Util.getErrorReason(jsonObj);
		// if
		// (SharedConstants.ERROR_REASON_INVALIDCREDENTIALS.equalsIgnoreCase(reason))
		// {
		// // Log.d(TAG,
		// //
		// "getUsersPlaylistId: Invalid credentials, so get a new access token");
		// accessToken = Util.getNewAccessToken(ctx);
		// if (accessToken == null) {
		// // We need to reauthorize first
		// return SharedConstants.REAUTHORIZATION_REQUIRED;
		// }
		// request.setHeader("Authorization", "Bearer " + accessToken);
		// response = httpclient.execute(request);
		// entity = response.getEntity();
		// jsonObj = new JSONObject(EntityUtils.toString(entity));
		// }
		// }
		// }
		//
		// // get the items array
		// playlistId =
		// jsonObj.getJSONArray("items").getJSONObject(0).getJSONObject("contentDetails")
		// .getJSONObject("relatedPlaylists").getString(playlistType);
		// // Log.d(TAG, "Playlist Id: " + playlistId);
		// } catch (IOException e) {
		// Log.e(TAG, "getUsersPlaylistId IOException " + e);
		// } catch (JSONException e) {
		// Log.e(TAG, "getUsersPlaylistId JSONException " + e);
		// }
		//
		// return playlistId;
	}

	public static List<Playlist> getPlaylistsForChannel(Context context, String channelId) {
		final Context ctx = context;

		YouTube youTube = Util.createYouTube(ctx);

		List<Playlist> list = null;
		try {

			YouTube.Playlists.List playlistList = youTube.playlists().list("id,snippet");
			playlistList.setChannelId(channelId);
			playlistList.setKey(SharedConstants.UFO_WATCH_API_KEY);
			playlistList.setFields("items(id,snippet/title)");
			playlistList.setMaxResults(Long.valueOf("30"));
			PlaylistListResponse listResp = playlistList.execute();
			// Log.d(TAG, listResp.toPrettyString());
			list = listResp.getItems();

			// ****************
		} catch (IOException e) {
			Log.e("Error getting playlist.", e.getMessage());
			return null;
		}

		return list;

	}

	public static String rateVideo(String videoId, String rating, Context context) {

		final Context ctx = context;

		HttpsURLConnection urlConnection = null;
		String message = null;
		String successMsg = null;
		JSONObject jsonObj = null;

		if (SharedConstants.VIDEO_RATING_LIKE.equalsIgnoreCase(rating)) {
			successMsg = ctx.getResources().getString(R.string.msg_video_liked);
		} else if (SharedConstants.VIDEO_RATING_DISLIKE.equalsIgnoreCase(rating)) {
			successMsg = ctx.getResources().getString(R.string.msg_video_disliked);
		} else if (SharedConstants.VIDEO_RATING_NONE.equalsIgnoreCase(rating)) {
			successMsg = ctx.getResources().getString(R.string.msg_video_ratingremoved);
		}

		try {

			urlConnection = Util.createHttpsURLConnectionPost(SharedConstants.URL_YOUTUBE_RATE_VIDEO + "id=" + videoId
					+ "&rating=" + rating);

			String accessToken = Util.getSharedPreferenceString(ctx, SharedConstants.KEY_ACCESS_TOKEN, "");

			Log.d(TAG, "rateVideo: adding access token to request: " + accessToken);
			urlConnection.addRequestProperty("Authorization", "Bearer " + accessToken);

			// Connect
			urlConnection.connect();

			// SUCCESS = 204 No Content
			final int statusCode = urlConnection.getResponseCode();
			Log.d(TAG, "rateVideo: statusCode " + statusCode);

			if (statusCode != HttpURLConnection.HTTP_NO_CONTENT) {

				String results = Util.getResultsFromConnectionInputStream(urlConnection);
				Log.d(TAG, "rateVideo response result:" + results);

				if (results.isEmpty()) {
					Log.d(TAG, "rateVideo: results are empty");
				} else {
					jsonObj = new JSONObject(results);
					Log.d(TAG, "rateVideo: response jsonObj: " + jsonObj.toString(4));
					// if there is an error in the response
					if (errorIsInMessage(jsonObj)) {
						String reason = Util.getErrorReason(jsonObj);
						if (SharedConstants.ERROR_REASON_INVALIDCREDENTIALS.equalsIgnoreCase(reason)) {
							Log.d(TAG, "rateVideo: Invalid credentials, so get a new access token");
							accessToken = Util.getNewAccessToken(ctx);
							if (accessToken == null) {
								// We need to re-authorize first
								Log.d(TAG, "rateVideo: accessToken is null");
								message = SharedConstants.REAUTHORIZATION_REQUIRED;
							} else {
								Log.d(TAG, "rateVideo: Persisted token: " + accessToken);
								// use new access token
								urlConnection.setRequestProperty("Authorization", "Bearer " + accessToken);
								urlConnection.connect();

								results = Util.getResultsFromConnectionInputStream(urlConnection);
								jsonObj = new JSONObject(results);

								if (!results.isEmpty()) {
									Log.d(TAG, "rateVideo (again) response result:" + results);
									jsonObj = new JSONObject(results);
									Log.d(TAG, "rateVideo: got json From Entity");
									if (!errorIsInMessage(jsonObj)) {
										message = successMsg;
									} else {
										Log.d(TAG, "rateVideo: error was in message, getting reason");
										reason = Util.getErrorReason(jsonObj);
										Log.d(TAG,
												"rateVideo: ERROR REASON: " + reason + " ERROR MESSAGE: "
														+ Util.getErrorMessage(jsonObj));
									}
								}
							}
						} else if (SharedConstants.ERROR_REASON_VIDEONOTFOUND.equalsIgnoreCase(reason)) {
							message = SharedConstants.ERROR_REASON_VIDEONOTFOUND_MSG;
						} else if (SharedConstants.ERROR_REASON_RATINGFORBIDDEN.equalsIgnoreCase(reason)) {
							message = SharedConstants.ERROR_REASON_RATINGFORBIDDEN_MSG;
						} else {
							message = Util.getErrorMessage(jsonObj);
						}
					}
				}
			} else {
				message = successMsg;
			}

		} catch (MalformedURLException e1) {
			Log.e(TAG, "MalformedURLException");
		} catch (IOException e) {
			Log.e(TAG, "IOException");
		} catch (JSONException e) {
			Log.e(TAG, "JSONException");
		} finally {
			urlConnection.disconnect();
		}

		if (message == null) {
			message = ctx.getResources().getString(R.string.msg_error_there_is_a_problem);
		}
		return message;

		// HttpClient httpclient = new DefaultHttpClient();
		// HttpPost httppost = new
		// HttpPost(SharedConstants.URL_YOUTUBE_RATE_VIDEO + "id=" + videoId +
		// "&rating=" + rating);
		//
		// String message = null;
		// String successMsg = null;
		// JSONObject jsonObj = null;
		//
		// if (SharedConstants.VIDEO_RATING_LIKE.equalsIgnoreCase(rating)) {
		// successMsg = ctx.getResources().getString(R.string.msg_video_liked);
		// } else if
		// (SharedConstants.VIDEO_RATING_DISLIKE.equalsIgnoreCase(rating)) {
		// successMsg =
		// ctx.getResources().getString(R.string.msg_video_disliked);
		// } else if
		// (SharedConstants.VIDEO_RATING_NONE.equalsIgnoreCase(rating)) {
		// successMsg =
		// ctx.getResources().getString(R.string.msg_video_ratingremoved);
		// }
		//
		// String accessToken = Util.getSharedPreferenceString(ctx,
		// SharedConstants.KEY_ACCESS_TOKEN, "");
		// httppost.setHeader("Authorization", "Bearer " + accessToken);
		// // Log.d(TAG, "rateVideo: Using access token: " + accessToken);
		//
		// try {
		// // Execute HTTP Post Request
		// HttpResponse response = httpclient.execute(httppost);
		// // Log.d(TAG, "rateVideo: initial response " + response.toString());
		// // SUCCESS = 204 No Content
		// int statusCode = response.getStatusLine().getStatusCode();
		// // Log.d(TAG, "rateVideo: statusCode " + statusCode);
		//
		// if (statusCode != SharedConstants.SUCCESS_STATUS_VIDEO_RATING) {
		//
		// HttpEntity entity = response.getEntity();
		// // EntityUtils.toString(entity);
		// if (entity == null) {
		// // Log.d(TAG, "rateVideo: response entity is null");
		// } else {
		// // Log.d(TAG, "rateVideo: response entity: " +
		// // entity.toString());
		// jsonObj = new JSONObject(EntityUtils.toString(entity));
		// // Log.d(TAG, "rateVideo: response jsonObj: " +
		// // jsonObj.toString(4));
		// // if there is an error in the response
		// if (errorIsInMessage(jsonObj)) {
		// String reason = Util.getErrorReason(jsonObj);
		// if
		// (SharedConstants.ERROR_REASON_INVALIDCREDENTIALS.equalsIgnoreCase(reason))
		// {
		// // Log.d(TAG,
		// // "rateVideo: Invalid credentials, so get a new access token");
		// accessToken = Util.getNewAccessToken(ctx);
		// if (accessToken == null) {
		// // We need to re-authorize first
		// // Log.d(TAG, "rateVideo: accessToken is null");
		// message = SharedConstants.REAUTHORIZATION_REQUIRED;
		// } else {
		// // Log.d(TAG, "rateVideo: Persisted token: " +
		// // accessToken);
		// httppost.setHeader("Authorization", "Bearer " + accessToken);
		// response = httpclient.execute(httppost);
		// entity = response.getEntity();
		// if (entity != null) {
		// // Log.d(TAG, "rateVideo: response entity: "
		// // + entity.toString());
		// jsonObj = new JSONObject(EntityUtils.toString(entity));
		// // Log.d(TAG,
		// // "rateVideo: got json From Entity");
		// if (!errorIsInMessage(jsonObj)) {
		// message = successMsg;
		// } else {
		// // Log.d(TAG,
		// // "rateVideo: error was in message, getting reason");
		// reason = Util.getErrorReason(jsonObj);
		// // Log.d(TAG,
		// // "rateVideo: ERROR REASON: " + reason
		// // + " ERROR MESSAGE: "
		// // + Util.getErrorMessage(jsonObj));
		// }
		// }
		// }
		// } else if
		// (SharedConstants.ERROR_REASON_VIDEONOTFOUND.equalsIgnoreCase(reason))
		// {
		// message = SharedConstants.ERROR_REASON_VIDEONOTFOUND_MSG;
		// } else if
		// (SharedConstants.ERROR_REASON_RATINGFORBIDDEN.equalsIgnoreCase(reason))
		// {
		// message = SharedConstants.ERROR_REASON_RATINGFORBIDDEN_MSG;
		// } else {
		// message = Util.getErrorMessage(jsonObj);
		// }
		// }
		// }
		// } else {
		// message = successMsg;
		// }
		//
		// } catch (IOException e) {
		// Log.e(TAG, "IOException");
		// } catch (JSONException e) {
		// Log.e(TAG, "JSONException");
		// }
		// if (message == null) {
		// message =
		// ctx.getResources().getString(R.string.msg_error_there_is_a_problem);
		// }
		// return message;
	}

	public static String insertVideoInPlaylist(String playlistId, String videoId, Context context) {

		JSONObject jsonObj = null;
		Insert insert = null;
		// PlaylistItem returnedItem = null;
		final Context ctx = context;
		String message = ctx.getResources().getString(R.string.msg_video_added_to_playlist);
		String channelId = Util.getSharedPreferenceString(ctx, SharedConstants.KEY_USER_CHANNEL_ID, "");

		YouTube mYouTube = Util.createYouTube(ctx);

		ResourceId resourceId = new ResourceId();
		resourceId.setKind("youtube#video");
		resourceId.setVideoId(videoId);

		PlaylistItemSnippet snip = new PlaylistItemSnippet();
		snip.setPlaylistId(playlistId);
		snip.setChannelId(channelId);
		snip.setResourceId(resourceId);
		// add it to the top
		snip.setPosition((long) 0);

		PlaylistItem item = new PlaylistItem();
		item.setSnippet(snip);

		try {
			// Log.d(TAG, "insertVideoInPlaylist " + item.toPrettyString());
			insert = mYouTube.playlistItems().insert("snippet", item).setFields("id,snippet");

			insert.setRequestHeaders(Util.createAuthorizedHttpHeaders(ctx));

			insert.execute();
		} catch (IOException e1) {
			try {
				String returnedError = e1.toString();
				// Log.e(TAG, "insertVideoInPlaylist IOException follows:");
				// Log.e(TAG, returnedError);
				returnedError = returnedError.substring(returnedError.indexOf("{"));
				// Log.e(TAG,
				// "insertVideoInPlaylist new returned error follows:");
				// Log.e(TAG, returnedError);

				jsonObj = new JSONObject(returnedError);
				Log.e(TAG, "insertVideoInPlaylist Able to create JSONObject:");

				// if there is an error in the response
				if (errorIsInMessage(jsonObj)) {
					String reason = Util.getErrorReason(jsonObj);
					if (SharedConstants.ERROR_REASON_INVALIDCREDENTIALS.equalsIgnoreCase(reason)) {
						// Log.d(TAG,
						// "insertVideoInPlaylist: Invalid credentials, so get a new access token");
						// getNewAccessToken saves token in preferences
						if (Util.getNewAccessToken(ctx) == null) {
							// We need to reauthorize first
							return SharedConstants.REAUTHORIZATION_REQUIRED;
						}
						insert = mYouTube.playlistItems().insert("snippet", item).setFields("id");
						insert.setRequestHeaders(Util.createAuthorizedHttpHeaders(ctx));
						insert.execute();
						// Log.d(TAG,
						// "insertVideoInPlaylist: inserted after new token: " +
						// returnedItem.getId());
					} else if (SharedConstants.ERROR_REASON_VIDEOALREADYINPLAYLIST.equalsIgnoreCase(reason)) {
						message = SharedConstants.ERROR_REASON_VIDEOALREADYINPLAYLIST_MSG;
					} else if (SharedConstants.ERROR_REASON_PLAYLISTNOTFOUND.equalsIgnoreCase(reason)) {
						message = SharedConstants.ERROR_REASON_PLAYLISTNOTFOUND_MSG;
					} else if (SharedConstants.ERROR_REASON_VIDEONOTFOUND.equalsIgnoreCase(reason)) {
						message = SharedConstants.ERROR_REASON_VIDEONOTFOUND_MSG;
					} else {
						message = Util.getErrorMessage(jsonObj);
					}
				}

			} catch (JSONException e) {
				Log.e(TAG, "insertVideoInPlaylist JSONException " + e);
				message = "Error modifying playlist. (c3)";
			} catch (IOException e) {
				Log.e(TAG, "insertVideoInPlaylist IOException " + e);
				message = "Error modifying playlist. (c2)";
			}

		}
		return message;

	}

	public static String insertNewPlaylist(String playlistTitle, Context context) {

		JSONObject jsonObj = null;
		com.google.api.services.youtube.YouTube.Playlists.Insert insert = null;
		// Playlist returnedItem = null;
		final Context ctx = context;
		String message = "Playlist \"" + playlistTitle + "\" has been created.";
		String channelId = Util.getSharedPreferenceString(ctx, SharedConstants.KEY_USER_CHANNEL_ID, "");

		YouTube mYouTube = Util.createYouTube(ctx);

		PlaylistSnippet snip = new PlaylistSnippet();
		snip.setTitle(playlistTitle);
		snip.setChannelId(channelId);
		snip.setDescription("This playlist was created from \"UFO Watch\".");

		PlaylistStatus playlistStatus = new PlaylistStatus();
		playlistStatus.setPrivacyStatus("public");

		Playlist playlist = new Playlist();
		playlist.setSnippet(snip);
		playlist.setStatus(playlistStatus);

		try {
			// Log.d(TAG, "insertNewPlaylist " + playlist.toPrettyString());
			insert = mYouTube.playlists().insert("snippet,status", playlist).setFields("id,snippet");
			insert.setRequestHeaders(Util.createAuthorizedHttpHeaders(ctx));

			insert.execute();
			// Log.d(TAG, "insertNewPlaylist: inserted playlist ID: " +
			// returnedItem.getId() + " Title: "
			// + returnedItem.getSnippet().getTitle() + " Description: "
			// + returnedItem.getSnippet().getDescription());
		} catch (IOException e1) {
			try {
				String returnedError = e1.toString();
				// Log.d(TAG, "insertNewPlaylist IOException follows:");
				// Log.d(TAG, returnedError);
				returnedError = returnedError.substring(returnedError.indexOf("{"));
				// Log.d(TAG, "insertNewPlaylist returned error follows... " +
				// returnedError);

				jsonObj = new JSONObject(returnedError);

				// if there is an error in the response
				if (errorIsInMessage(jsonObj)) {
					String reason = Util.getErrorReason(jsonObj);
					if (SharedConstants.ERROR_REASON_INVALIDCREDENTIALS.equalsIgnoreCase(reason)) {
						// Log.d(TAG,
						// "insertNewPlaylist: Invalid credentials, so get a new access token");
						// getNewAccessToken saves token in preferences
						if (Util.getNewAccessToken(ctx) == null) {
							// We need to reauthorize first
							return SharedConstants.REAUTHORIZATION_REQUIRED;
						}
						insert = mYouTube.playlists().insert("snippet", playlist).setFields("id,snippet");
						insert.setRequestHeaders(Util.createAuthorizedHttpHeaders(ctx));
						insert.execute();
						// Log.d(TAG,
						// "insertNewPlaylist: inserted after new token: " +
						// returnedItem.getId());
					} else if (SharedConstants.ERROR_REASON_PLAYLISTINVALIDSNIPPET.equalsIgnoreCase(reason)) {
						message = SharedConstants.ERROR_REASON_PLAYLISTINVALIDSNIPPET_MSG;
					} else if (SharedConstants.ERROR_REASON_PLAYLISTTITLEREQUIRED.equalsIgnoreCase(reason)) {
						message = SharedConstants.ERROR_REASON_PLAYLISTTITLEREQUIRED_MSG;
					} else {
						message = Util.getErrorMessage(jsonObj);
					}
				}

			} catch (JSONException e) {
				Log.e(TAG, "insertNewPlaylist JSONException " + e);
				message = "Error creating playlist. (c3)";
			} catch (IOException e) {
				Log.e(TAG, "insertNewPlaylist IOException " + e);
				message = "Error creating playlist. (c2)";
			}

		}
		return message;

	}

	public static String subscribeToChannel(String channelId, Context context) {
		final Context ctx = context;

		String message = ctx.getResources().getString(R.string.msg_subscribed);

		String accessToken = Util.getSharedPreferenceString(ctx, SharedConstants.KEY_ACCESS_TOKEN, "");

		JSONObject jsonObj = null;
		HttpsURLConnection urlConnection = null;

		try {

			urlConnection = Util.createHttpsURLConnectionPost("https://www.googleapis.com/youtube/v3/subscriptions?"
					+ SharedConstants.AUTHORIZATION_SCOPES_PARAMS + "&part=snippet&fields=snippet/title");

			urlConnection.addRequestProperty("Authorization", "Bearer " + accessToken);
			urlConnection.addRequestProperty("Host", "www.googleapis.com");

			String body = "{\"kind\":\"youtube#subscription\",\"snippet\":{\"resourceId\":{\"channelId\":\""
					+ channelId + "\",\"kind\":\"youtube#channel\"}}}";

			urlConnection.setRequestProperty("Content-Type", "application/json");

			Util.writeParamsToConnectionOutputStream(urlConnection, body);

			// Connect
			urlConnection.connect();

			String results = Util.getResultsFromConnectionInputStream(urlConnection);

			if (!results.isEmpty()) {
				jsonObj = new JSONObject(results);

				// if there is an error in the response
				if (errorIsInMessage(jsonObj)) {
					String reason = Util.getErrorReason(jsonObj);
					if (SharedConstants.ERROR_REASON_INVALIDCREDENTIALS.equalsIgnoreCase(reason)) {
						// Log.d(TAG,
						// "subscribeToChannel: Invalid credentials, so get a new access token");
						// set a new token
						accessToken = Util.getNewAccessToken(ctx);
						if (accessToken == null) {
							// We need to reauthorize first
							message = SharedConstants.REAUTHORIZATION_REQUIRED;
						} else {
							urlConnection.setRequestProperty("Authorization", "Bearer " + accessToken);
							urlConnection.connect();
							results = Util.getResultsFromConnectionInputStream(urlConnection);
							jsonObj = new JSONObject(results);

						}
					} else if (SharedConstants.ERROR_REASON_SUBSCRIPTIONDUPLICATE.equalsIgnoreCase(reason)) {
						message = SharedConstants.ERROR_REASON_SUBSCRIPTIONDUPLICATE_MSG;
					} else if (SharedConstants.ERROR_REASON_SUBSCRIPTIONFORBIDDEN.equalsIgnoreCase(reason)) {
						message = SharedConstants.ERROR_REASON_SUBSCRIPTIONFORBIDDEN_MSG;
					} else {
						message = Util.getErrorMessage(jsonObj);
					}
				} else {

					String title = ((JSONObject) jsonObj.get("snippet")).getString("title");
					message += " \"" + title + "\"";
				}
			}

		} catch (MalformedURLException e1) {
			Log.e(TAG, "MalformedURLException");
		} catch (IOException e) {
			Log.e(TAG, "IOException");
		} catch (JSONException e) {
			Log.e(TAG, "JSONException");
		} finally {
			urlConnection.disconnect();
		}

		return message;

		// HttpClient httpclient = new DefaultHttpClient();
		// HttpPost httppost = new
		// HttpPost("https://www.googleapis.com/youtube/v3/subscriptions?"
		// + SharedConstants.AUTHORIZATION_SCOPES_PARAMS +
		// "&part=snippet&fields=snippet/title");
		//
		// httppost.addHeader("Authorization", "Bearer " + accessToken);
		// httppost.addHeader("Host", "www.googleapis.com");
		//
		// String body =
		// "{\"kind\":\"youtube#subscription\",\"snippet\":{\"resourceId\":{\"channelId\":\""
		// + channelId
		// + "\",\"kind\":\"youtube#channel\"}}}";
		//
		// // Log.d(TAG, "subscribeToChannel POSTED: " + body);
		//
		// JSONObject jsonObj = null;
		// // Log.d(TAG, "subscribeToChannel HERE");
		// try {
		// StringEntity se = new StringEntity(body, "utf-8");
		// se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE,
		// "application/json"));
		// httppost.setEntity(se);
		//
		// // Execute HTTP Post Request
		// HttpResponse response = httpclient.execute(httppost);
		// HttpEntity entity = response.getEntity();
		// if (entity != null) {
		// jsonObj = new JSONObject(EntityUtils.toString(entity));
		//
		// // if there is an error in the response
		// if (errorIsInMessage(jsonObj)) {
		// String reason = Util.getErrorReason(jsonObj);
		// if
		// (SharedConstants.ERROR_REASON_INVALIDCREDENTIALS.equalsIgnoreCase(reason))
		// {
		// // Log.d(TAG,
		// //
		// "subscribeToChannel: Invalid credentials, so get a new access token");
		// // set a new token
		// accessToken = Util.getNewAccessToken(ctx);
		// if (accessToken == null) {
		// // We need to reauthorize first
		// message = SharedConstants.REAUTHORIZATION_REQUIRED;
		// } else {
		// httppost.setHeader("Authorization", "Bearer " + accessToken);
		// response = httpclient.execute(httppost);
		// entity = response.getEntity();
		// jsonObj = new JSONObject(EntityUtils.toString(entity));
		// }
		// } else if
		// (SharedConstants.ERROR_REASON_SUBSCRIPTIONDUPLICATE.equalsIgnoreCase(reason))
		// {
		// message = SharedConstants.ERROR_REASON_SUBSCRIPTIONDUPLICATE_MSG;
		// } else if
		// (SharedConstants.ERROR_REASON_SUBSCRIPTIONFORBIDDEN.equalsIgnoreCase(reason))
		// {
		// message = SharedConstants.ERROR_REASON_SUBSCRIPTIONFORBIDDEN_MSG;
		// } else {
		// message = Util.getErrorMessage(jsonObj);
		// }
		// } else {
		//
		// String title = ((JSONObject)
		// jsonObj.get("snippet")).getString("title");
		// message += " \"" + title + "\"";
		// }
		// }
		//
		// } catch (IOException e) {
		// Log.e(TAG, "subscribeToChannel IOException " + e);
		// message = "Error subscribing. (c2)";
		// } catch (JSONException e) {
		// Log.e(TAG, "subscribeToChannel JSONException " + e);
		// message = "Error subscribing. (c3)";
		// }
		//
		// return message;

	}

	public static YouTube createYouTube(Context context) {
		final Context ctx = context;
		Builder builder = new Builder(new NetHttpTransport(), new JacksonFactory(), null);
		return builder.setApplicationName(ctx.getResources().getString(R.string.title_app_name)).build();
	}

	/**
	 * Sets up an HttpsURLConnection that POSTs and receives data.
	 * 
	 * @param urlString
	 * @return
	 * @throws IOException
	 */
	public static HttpsURLConnection createHttpsURLConnectionPost(String urlString) throws IOException {
		URL url = new URL(urlString);

		// Get the connection
		HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
		// because we are posting
		urlConnection.setDoOutput(true);
		// to allow receiving data
		urlConnection.setDoInput(true);
		urlConnection.setChunkedStreamingMode(0);

		return urlConnection;
	}

	/**
	 * Sets up an HttpsURLConnection that GETs data.
	 * 
	 * @param urlString
	 * @return
	 * @throws IOException
	 */
	public static HttpsURLConnection createHttpsURLConnectionGet(String urlString) throws IOException {
		URL url = new URL(urlString);

		// Get the connection
		HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
		// because we are posting
		urlConnection.setDoOutput(false);
		// to allow receiving data
		urlConnection.setDoInput(true);
		urlConnection.setChunkedStreamingMode(0);

		return urlConnection;
	}

	/**
	 * Write the parameters to the connection output stream with UTF-8 character
	 * encoding.
	 * 
	 * @param urlConnection
	 * @param paramString
	 * @throws IOException
	 */
	public static void writeParamsToConnectionOutputStream(HttpsURLConnection urlConnection, String paramString)
			throws IOException {
		// write the parameters to the output
		OutputStream os = urlConnection.getOutputStream();
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
		writer.write(paramString);
		writer.flush();
		writer.close();
		os.close();
	}

	/**
	 * Get the response string back from the https connection. Returns the
	 * results or empty string.
	 * 
	 * @param urlConnection
	 * @return
	 * @throws IOException
	 */
	public static String getResultsFromConnectionInputStream(HttpsURLConnection urlConnection) throws IOException {
		BufferedInputStream mBufInputStream = null;
		try {
			Log.d(TAG, "getResultsFromConnectionInputStream");
			mBufInputStream = new BufferedInputStream(urlConnection.getInputStream());
		} catch (IOException ioe) {
			Log.d(TAG, "getResultsFromConnectionInputStream get error stream");
			mBufInputStream = new BufferedInputStream(urlConnection.getErrorStream());
		}
		StringBuffer sb = new StringBuffer();
		int current = 0;
		while ((current = mBufInputStream.read()) != -1) {
			sb.append((char) current);
		}
		// Log.d(TAG,
		// "getResultsFromConnectionInputStream string buffer length: " +
		// sb.length());
		return sb.toString();
	}

	public static YouTubePlayer.OnInitializedListener createPlayerListenerForVideo(Context context, String videoId) {
		final Context ctx = context;
		final String vidId = videoId;

		YouTubePlayer.OnInitializedListener listener = new YouTubePlayer.OnInitializedListener() {

			@Override
			public void onInitializationSuccess(Provider provider, YouTubePlayer player, boolean wasRestored) {
				if (!wasRestored) {
					player.loadVideo(vidId);
					player.setShowFullscreenButton(false);
				}
			}

			@Override
			public void onInitializationFailure(Provider provider, YouTubeInitializationResult result) {
				Util.showCenteredToast(ctx, "There was a problem (" + result.name() + ")");
				// Util.createDialogAlertMessage(ctx, "Sorry",
				// result.name()).show();
			}
		};

		return listener;

	}

	public static YouTubePlayer.OnInitializedListener createPlayerListenerForVideoList(Context context,
			List<String> videoIds) {
		final Context ctx = context;
		final List<String> mVideoIds = videoIds;

		YouTubePlayer.OnInitializedListener listener = new YouTubePlayer.OnInitializedListener() {

			@Override
			public void onInitializationSuccess(Provider provider, YouTubePlayer player, boolean wasRestored) {
				if (!wasRestored) {
					player.loadVideos(mVideoIds);
					player.setShowFullscreenButton(false);

					((UfoWatchMain) ctx).setPlaylistPosition(0);

					PlaylistEventListener playlistEventListener = new PlaylistEventListener() {

						@Override
						public void onNext() {
							// increment the position in the list
							((UfoWatchMain) ctx).setNextPlaylistPosition();
						}

						@Override
						public void onPlaylistEnded() {
							// get the next list if there is one
							((UfoWatchMain) ctx).getNextList();
						}

						@Override
						public void onPrevious() {
							// decrement the position in the list
							((UfoWatchMain) ctx).setPreviousPlaylistPosition();
						}
					};

					PlaybackEventListener playbackEventListener = new PlaybackEventListener() {

						@Override
						public void onPlaying() {
							// When vid starts playing reset backgrounds in list
							((UfoWatchMain) ctx).resetPlaylistBackgrounds();
						}

						@Override
						public void onBuffering(boolean arg0) {
							;
						}

						@Override
						public void onPaused() {
							;
						}

						@Override
						public void onSeekTo(int arg0) {
							;
						}

						@Override
						public void onStopped() {
							;
						}

					};

					player.setPlaylistEventListener(playlistEventListener);
					player.setPlaybackEventListener(playbackEventListener);

				}
			}

			@Override
			public void onInitializationFailure(Provider provider, YouTubeInitializationResult result) {
				Util.showCenteredToast(ctx, result.name());
			}
		};

		return listener;

	}

	/**
	 * @param youTube
	 * @param nextPageToken
	 * @param prevPageToken
	 * @param keywords
	 * @param resultsPerPage
	 * @param startTime
	 *            in milliseconds
	 * @param endTime
	 *            in milliseconds
	 * @return
	 * @throws IOException
	 */
	public static YouTube.Search.List createSearchList(Context ctx, String nextPageToken, String prevPageToken,
			String keywords, long resultsPerPage, long startTime, long endTime) throws IOException {

		YouTube youTube = Util.createYouTube(ctx);

		YouTube.Search.List search = youTube.search().list("id,snippet");
		// YouTube.Search.List search = youTube.search().list("id");
		search.setFields("nextPageToken,prevPageToken,pageInfo/totalResults,pageInfo/resultsPerPage,items(id/kind,id/videoId,snippet/title,snippet/channelTitle,snippet/publishedAt,snippet/thumbnails/default/url,snippet/thumbnails/high/url)");
		// search.setFields("nextPageToken,prevPageToken,pageInfo/totalResults,pageInfo/resultsPerPage,items(id/kind,id/videoId)");
		search.setKey(SharedConstants.UFO_WATCH_API_KEY);
		search.setType("video");
		search.setVideoEmbeddable("true");

		// search.setVideoDuration("any");

		if (nextPageToken != null) {
			search.setPageToken(nextPageToken);
		} else if (prevPageToken != null) {
			search.setPageToken(prevPageToken);
		}

		if (keywords != null) {
			// Log.d(TAG, "SearchVideosTask keywords: " + keywords);
			search.setQ(keywords);
		}

		search.setMaxResults(resultsPerPage);

		// if this is not the simple ufo search then order by relevance
		if (keywords == null || !keywords.equalsIgnoreCase(SharedConstants.VIDEO_SEARCH_KEYWORDS)) {
			search.setOrder("relevance");
		} else {
			search.setOrder("date");
		}

		// if this is a time based search, there must at least be a start time
		if (startTime > 0) {
			// we have set a time span so let's order by relevance
			search.setOrder("relevance");

			search.setPublishedAfter(Util.createClientDateTime(startTime));

			if (endTime > 0) {
				search.setPublishedBefore(Util.createClientDateTime(endTime));
			}
			// Log.d(TAG, "OUT TIME SEARCH");
		}

		return search;

	}

	public static Library getVideosFromSearchResponse(SearchListResponse searchResponse, Context ctx) {
		List<SearchResult> searchResultList = searchResponse.getItems();

		// Create a list to store our videos in
		// ArrayList<Video> videos = new ArrayList<Video>();
		Library videos = new Library();

		// com.google.api.services.youtube.model.Video youtubeVid = null;
		for (SearchResult singleVideo : searchResultList) {
			ResourceId rId = singleVideo.getId();

			// Double checks the kind is video.
			if (rId.getKind().equals("youtube#video")) {

				// try {
				// youtubeVid = Util.getVideo(rId.getVideoId(), ctx, false);
				// } catch (IOException e) {
				// Log.e(TAG, "Trouble getting video ID:" + rId.getVideoId());
				// }

				Video vid = new Video();
				vid.setId(rId.getVideoId());
				vid.setTitle(singleVideo.getSnippet().getTitle());
				// vid.setTitle(youtubeVid.getSnippet().getTitle());
				Log.d(TAG, "Video title: " + vid.getTitle());

				ThumbnailDetails thumbnailDetails = singleVideo.getSnippet().getThumbnails();
				// ThumbnailDetails thumbnailDetails =
				// youtubeVid.getSnippet().getThumbnails();
				if (thumbnailDetails != null) {
					Log.d(TAG, "Thumbnail URL Default: " + thumbnailDetails.getDefault().getUrl());
					Log.d(TAG, "Thumbnail URL HighDef: " + thumbnailDetails.getHigh().getUrl());
					vid.setThumbUrl(thumbnailDetails.getDefault().getUrl());
					vid.setThumbUrlHq(thumbnailDetails.getHigh().getUrl());
				} else {
					Log.d(TAG, "ThumbnailDetails is null.");
				}

				vid.setChannelTitle(singleVideo.getSnippet().getChannelTitle());
				vid.setUploadedDt(Util.formatPublishedAtTime(singleVideo.getSnippet().getPublishedAt()));
				// vid.setUploadedDt(Util.formatPublishedAtTime(youtubeVid.getSnippet().getPublishedAt()));

				videos.add(vid);

			}
		}

		return videos;

	}

	public static YouTube.PlaylistItems.List createPlaylistItemsList(YouTube youTube, String playlistId,
			String nextPageToken, String prevPageToken, long maxResults) throws IOException {

		YouTube.PlaylistItems.List playlistItemList = youTube.playlistItems().list("snippet");
		playlistItemList.setPlaylistId(playlistId);
		playlistItemList.setKey(SharedConstants.UFO_WATCH_API_KEY);
		playlistItemList
				.setFields("nextPageToken,prevPageToken,pageInfo/totalResults,pageInfo/resultsPerPage,items(snippet/resourceId/kind,snippet/resourceId/videoId,snippet/title,snippet/publishedAt,snippet/channelTitle,snippet/thumbnails/default,snippet/thumbnails/high)");

		playlistItemList.setMaxResults(maxResults);

		if (nextPageToken != null) {
			playlistItemList.setPageToken(nextPageToken);
		} else if (prevPageToken != null) {
			playlistItemList.setPageToken(prevPageToken);
		}

		return playlistItemList;

	}

	public static Library getVideosFromPlaylistItems(PlaylistItemListResponse resp) {
		List<PlaylistItem> playlistItems = resp.getItems();

		Library videos = new Library();

		for (PlaylistItem playlistItem : playlistItems) {
			PlaylistItemSnippet snippet = playlistItem.getSnippet();

			if (snippet.getResourceId().getKind().equals("youtube#video")) {

				Video vid = new Video();
				vid.setId(snippet.getResourceId().getVideoId());
				vid.setTitle(snippet.getTitle());

				ThumbnailDetails thumbnailDetails = snippet.getThumbnails();
				if (thumbnailDetails != null) {
					vid.setThumbUrl(thumbnailDetails.getDefault().getUrl());
					vid.setThumbUrlHq(thumbnailDetails.getHigh().getUrl());
				}

				// Display channel title in video playlist
				vid.setChannelTitle(snippet.getChannelTitle());
				// Display date added to playlist in video playlist
				vid.setAddedToPlaylistDt(Util.formatPublishedAtDateOnly(snippet.getPublishedAt()));

				videos.add(vid);

			}
		}

		return videos;

	}

	public static DateTime createClientDateTime(long timeInMilliseconds) {
		return new DateTime(new Date(timeInMilliseconds));
	}

	public static String formatPublishedAtTime(DateTime publishedAt) {
		String formattedTime = null;
		if (publishedAt != null) {

			Long utcTime = publishedAt.getValue();
			DateTime localDateTime = new DateTime(new Date(utcTime));
			// showing date only
			formattedTime = Util.getDatePart(localDateTime.toString());

		}
		return formattedTime;
	}

	public static String formatPublishedAtDateOnly(DateTime publishedAt) {
		String szPublishedAt = null;
		if (publishedAt != null) {
			szPublishedAt = publishedAt.toString();
		}
		return formatStandardDateTimeToDateOnly(szPublishedAt);
	}

	public static String formatStandardDateTimeToDateOnly(String standardDateTime) {
		// Log.d(TAG, "formatStandardDateTimeToDateOnly() " + standardDateTime);
		if (standardDateTime != null) {
			// making sure the 'T' is the one in front of timestamp (T0,T1 or
			// T2)
			// not in the day or month
			int pos = standardDateTime.indexOf("T0");
			if (pos == -1) {
				pos = standardDateTime.indexOf("T1");
				if (pos == -1) {
					pos = standardDateTime.indexOf("T2");
					if (pos == -1) {
						pos = standardDateTime.indexOf(":");
						if (pos != -1) {
							pos = pos - 3;
						}
					}
				}
			}
			if (pos != -1) {
				standardDateTime = standardDateTime.substring(0, pos);
			}
		}
		return standardDateTime;
	}

	public static List<String> getVideoIdsFromPlaylistItems(PlaylistItemListResponse resp) {
		List<PlaylistItem> playlistItems = resp.getItems();

		List<String> videoIds = new ArrayList<String>();

		for (PlaylistItem playlistItem : playlistItems) {
			PlaylistItemSnippet snippet = playlistItem.getSnippet();

			if (snippet.getResourceId().getKind().equals("youtube#video")) {
				videoIds.add(snippet.getResourceId().getVideoId());
			}
		}

		return videoIds;

	}

	public static YouTube.Videos.List createVideosList(Context ctx, String videoId) throws IOException {

		YouTube youTube = Util.createYouTube(ctx);

		YouTube.Videos.List vidList = youTube.videos().list("snippet, contentDetails, statistics");
		vidList.setKey(SharedConstants.UFO_WATCH_API_KEY);
		vidList.setId(videoId);
		vidList.setFields("items(contentDetails/duration,statistics/viewCount,statistics/likeCount,statistics/dislikeCount,snippet/description,snippet/publishedAt,snippet/channelId,snippet/channelTitle)");
		return vidList;
	}

	public static com.google.api.services.youtube.model.Video getVideo(String videoId, Context ctx,
			boolean allInformation) throws IOException {

		YouTube youTube = Util.createYouTube(ctx);
		YouTube.Videos.List vidList = null;

		if (!allInformation) {
			vidList = youTube.videos().list("snippet");
			vidList.setFields("items(snippet/publishedAt,snippet/channelTitle,snippet/title,snippet/thumbnails)");
		} else {
			vidList = youTube.videos().list("snippet, contentDetails, statistics");
			vidList.setFields("items(contentDetails/duration,statistics/viewCount,statistics/likeCount,statistics/dislikeCount,snippet/description,snippet/publishedAt,snippet/channelId,snippet/channelTitle)");
		}
		vidList.setKey(SharedConstants.UFO_WATCH_API_KEY);
		vidList.setId(videoId);

		List<com.google.api.services.youtube.model.Video> videos = vidList.execute().getItems();

		if (videos.isEmpty()) {
			return null;
		}
		return videos.get(0);

	}

	public static com.duke.android.ufowatch.domain.Video copyYoutubeVideoToUfoWatchVideo(
			com.duke.android.ufowatch.domain.Video watchVideo, com.google.api.services.youtube.model.Video tubeVideo) {

		com.duke.android.ufowatch.domain.Video mVideo = watchVideo;

		mVideo.setViewCount(tubeVideo.getStatistics().getViewCount());
		mVideo.setLikes(tubeVideo.getStatistics().getLikeCount());
		mVideo.setDislikes(tubeVideo.getStatistics().getDislikeCount());
		mVideo.setDuration(Util.formatDuration(tubeVideo.getContentDetails().getDuration()));
		mVideo.setDescription(tubeVideo.getSnippet().getDescription());
		mVideo.setChannelId(tubeVideo.getSnippet().getChannelId());

		mVideo.setChannelTitle(tubeVideo.getSnippet().getChannelTitle());

		mVideo.setUploadedDt(Util.formatPublishedAtTime(tubeVideo.getSnippet().getPublishedAt()));

		return mVideo;
	}

	public static HttpHeaders createAuthorizedHttpHeaders(Context context) {
		final Context ctx = context;
		HttpHeaders httpHeaders = new HttpHeaders();
		String authHeader = "Bearer " + Util.getSharedPreferenceString(ctx, SharedConstants.KEY_ACCESS_TOKEN, "");
		// Log.d(TAG, "makeAuthorizedHeaders authHeader: " + authHeader);
		httpHeaders.setAuthorization(authHeader);
		httpHeaders.setUserAgent("UFO Watch");
		return httpHeaders;
	}

	public static void showCenteredToast(Context context, String message) {
		final Context ctx = context;
		Toast toast = Toast.makeText(ctx, message, Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();
	}

	public static boolean errorIsInMessage(JSONObject jsonObj) {
		try {
			// First look for an error object
			// if no error object look for an errors array
			// Log.d(TAG, "isErrorInMessage START");
			jsonObj.getJSONObject("error");
			// Log.d(TAG, "isErrorInMessage: YES (found error object)");
			return true;
		} catch (JSONException e) {
			try {
				jsonObj.getJSONArray("errors");
				// Log.d(TAG, "isErrorInMessage: YES (found errors array)");
				return true;
			} catch (JSONException e1) {
				Log.e(TAG, "errorIsInMessage: NO (no errors array)... " + e1);
			}
			return false;
		}
	}

	public static String getErrorReason(JSONObject jsonObj) {
		String errorReason = null;
		try {
			// First look in an error object
			// if no error object look for in an errors array
			// Log.d(TAG, "getErrorReason START");
			errorReason = jsonObj.getJSONObject("error").getJSONArray("errors").getJSONObject(0).getString("reason");
			// Log.d(TAG, "getErrorReason errorReason from error(errors): " +
			// errorReason);
		} catch (JSONException e1) {
			// Log.d(TAG, "getErrorReason caught " + e1.getMessage());
			try {
				// Log.d(TAG, "getErrorReason reason from errors");
				errorReason = jsonObj.getJSONArray("errors").getJSONObject(0).getString("reason");
				// Log.d(TAG, "getErrorReason reason from errors: " +
				// errorReason);
			} catch (JSONException e) {
				// Log.d(TAG, "getErrorReason: NOT FOUND... " + e);
				// String errorCode;
				try {
					jsonObj.getJSONObject("error").getString("code");
					// Log.d(TAG, "getErrorReason: Error code " + errorCode);
				} catch (JSONException e2) {
					Log.e(TAG, "getErrorReason: No error code ");
				}
			}
		}
		return errorReason;
	}

	public static String getErrorMessage(JSONObject jsonObj) {
		try {
			String errorMsg = jsonObj.getJSONObject("error").getString("message");
			// Log.d(TAG, "getErrorMessage message: " + errorMsg);
			return (errorMsg);
		} catch (JSONException e1) {
			Log.e(TAG, "getErrorMessage exception (perhaps no message found) " + e1);
			return null;
		}

	}

	/**
	 * @param context
	 * @param dialogTitle
	 * @param arrayAdapter
	 * @param onItemClickListener
	 * @return customDialog
	 */
	public static CustomDialog createCustomDialogList(Context context, String dialogTitle,
			ArrayAdapter<CharSequence> arrayAdapter, OnItemClickListener onItemClickListener) {
		return createCustomDialogList(context, dialogTitle, arrayAdapter, onItemClickListener, null, null);
	}

	/**
	 * @param context
	 * @param dialogTitle
	 * @param arrayAdapter1
	 * @param onItemClickListener1
	 * @param arrayAdapter2
	 * @param onItemClickListener2
	 * @return customDialog
	 */
	public static CustomDialog createCustomDialogList(Context context, String dialogTitle,
			ArrayAdapter<CharSequence> arrayAdapter1, OnItemClickListener onItemClickListener1,
			ArrayAdapter<?> arrayAdapter2, OnItemClickListener onItemClickListener2) {
		CustomDialog customDialog = new CustomDialog(context, dialogTitle, arrayAdapter1, onItemClickListener1,
				arrayAdapter2, onItemClickListener2);
		setWindowRight(customDialog);
		return customDialog;
	}

	/**
	 * @param context
	 * @param dialogTitle
	 * @param youTubeItemsArrayAdapter
	 * @param onItemClickListener
	 * @return customDialog
	 */
	public static CustomDialog createCustomDialogList(Context context, String dialogTitle,
			YouTubeItemsArrayAdapter youTubeItemsArrayAdapter, OnItemClickListener onItemClickListener) {
		Log.d(TAG, "createCustomDialogList for " + dialogTitle);
		CustomDialog customDialog = new CustomDialog(context, dialogTitle, youTubeItemsArrayAdapter,
				onItemClickListener);
		setWindowRight(customDialog);
		return customDialog;
	}

	public static void setWindowRight(Dialog dialog) {
		Window window = dialog.getWindow();
		WindowManager.LayoutParams wlp = window.getAttributes();
		// change from Gravity.RIGHT to Gravity.END to ensure
		// correct behavior on 3/28/15
		wlp.gravity = Gravity.END;
		wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
		window.setAttributes(wlp);
	}

	public static String truncateStringAddEllipsis(String string, int characterLimit) {
		return string.length() > characterLimit ? string.substring(0, characterLimit) + "..." : string;
	}

	public static boolean isJellyBeanOrAbove() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;
	}

	public static boolean isLollipopOrAbove() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
	}

	public static String urlEncode(String stringToEncode) {
		String encodedString = "";
		try {
			encodedString = java.net.URLEncoder.encode(stringToEncode, "UTF-8");
			return encodedString;
		} catch (UnsupportedEncodingException e) {
			return stringToEncode;
		}
	}

}
