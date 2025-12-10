package edu.lukina.studyhelper

/**
 * Created by Yelyzaveta Lukina on 09/30/2025.
 */

// Imports the Bundle class, used for passing data between Android components.
import android.os.Bundle
// Imports AppCompatActivity, a base class for activities
// that use the support library action bar features.
import androidx.appcompat.app.AppCompatActivity
// Imports PreferenceFragmentCompat, a fragment for displaying a hierarchy of Preference objects.
import androidx.preference.PreferenceFragmentCompat
// Imports AppCompatDelegate, used for extending AppCompat's functionality to all activities,
// like managing night mode.
import androidx.appcompat.app.AppCompatDelegate
// Imports the Toolbar class for implementing a custom app bar.
import androidx.appcompat.widget.Toolbar
// Imports SwitchPreferenceCompat, a Preference that provides a two-state toggleable option.
import androidx.preference.SwitchPreferenceCompat

// Declares the SettingsActivity class, inheriting from AppCompatActivity to gain Activity functionality.
class SettingsActivity : AppCompatActivity() {

    // Overrides the onCreate method, which is called when the activity is first created.
    override fun onCreate(savedInstanceState: Bundle?) {
        // Calls the superclass's implementation of onCreate.
        super.onCreate(savedInstanceState)
        // Sets the user interface layout for this activity from the specified XML resource file.
        setContentView(R.layout.settings_activity)

        // Find the Toolbar from your layout by its ID.
        val toolbar: Toolbar = findViewById(R.id.toolbar)

        // Set this Toolbar as the official ActionBar for the activity.
        setSupportActionBar(toolbar)

        // Enable the back arrow on the toolbar (only needs to be called once).
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // This check ensures the fragment is only added the first time the activity is created.
        if (savedInstanceState == null) {
            // Gets the FragmentManager for interacting with fragments associated with this activity.
            supportFragmentManager
                // Begins a new transaction for adding, removing, or replacing fragments.
                .beginTransaction()
                // Replaces the content of the container with the given ID
                // with a new instance of SettingsFragment.
                .replace(R.id.settings, SettingsFragment())
                // Commits the transaction, applying the changes.
                .commit()
        }
    }

    // This method handles clicks on the back arrow.
    override fun onSupportNavigateUp(): Boolean {
        // This will act like the phone's back button, taking the user to the previous screen.
        onBackPressedDispatcher.onBackPressed()
        // Returns true to indicate that the click event was handled.
        return true
    }

    // Declares an inner class SettingsFragment that inherits from PreferenceFragmentCompat.
    class SettingsFragment : PreferenceFragmentCompat() {
        // Overrides the method where the preferences for the fragment are created.
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            // Loads the preferences from the specified XML resource file.
            setPreferencesFromResource(R.xml.root_preferences, rootKey)

            // Finds the SwitchPreferenceCompat with the key "dark_theme" in the preference hierarchy.
            val themePref: SwitchPreferenceCompat? = findPreference("dark_theme")
            // Sets a listener to be notified of changes to this preference's value.
            themePref?.setOnPreferenceChangeListener { preference, newValue ->

                // Turn on or off night mode.
                // Checks if the new value of the switch is true (checked).
                if (newValue == true) {
                    // If true, sets the default night mode for the entire application to ON.
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                } else {
                    // If false, sets the default night mode for the entire application to OFF.
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                }
                // Returns true to indicate the preference change should be saved.
                true
            }
        }
    }
}
