package oak.oakapplication;

        import android.content.Context;
        import android.content.Intent;
        import android.content.pm.PackageManager;
        import android.media.Ringtone;
        import android.media.RingtoneManager;
        import android.net.Uri;
        import android.os.Build;
        import android.os.Bundle;
        import android.preference.EditTextPreference;
        import android.preference.ListPreference;
        import android.preference.Preference;
        import android.preference.PreferenceFragment;
        import android.preference.PreferenceManager;
        import android.preference.RingtonePreference;
        import android.text.TextUtils;
        import android.view.MenuItem;

public class SettingsActivity extends AppCompatPreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // load settings fragment
        getFragmentManager().beginTransaction().replace(android.R.id.content, new MainPreferenceFragment()).commit();
    }

    public static class MainPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_main);

            Preference sendFeedback = findPreference("key_send_feedback");
            sendFeedback.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference preference) {
                    sendFeedback(getActivity());
                    return true;
                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    public static void sendFeedback(Context context) {
        String TO = "urbanisti.ba@gmail.com";
        Intent mail = new Intent(Intent.ACTION_SEND);
        mail.putExtra(Intent.EXTRA_EMAIL, new String[]{TO});
        mail.setType("message/rfc822");

        context.startActivity(mail);
    }
}