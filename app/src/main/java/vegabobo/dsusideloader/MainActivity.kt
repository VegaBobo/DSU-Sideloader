package vegabobo.dsusideloader

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private var fragment1: Fragment = HomeFragment()
    private var fragment2: Fragment = PreferencesFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bar)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.fragment_container_view, fragment2, "fragment2").hide(fragment2)
                .add(R.id.fragment_container_view, fragment1, "fragment1")
                .commit()
        } else {
            fragment2 = supportFragmentManager.findFragmentByTag("fragment2") as Fragment
            fragment1 = supportFragmentManager.findFragmentByTag("fragment1") as Fragment
            when (bottomNav.selectedItemId) {
                R.id.tab_home -> {
                    supportFragmentManager.beginTransaction().show(fragment1)
                }
                R.id.tab_config -> {
                    supportFragmentManager.beginTransaction().show(fragment2)
                }
            }
        }

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.tab_home -> {
                    supportFragmentManager.beginTransaction()
                        .show(fragment1).hide(fragment2)
                        .commit()
                }
                R.id.tab_config -> {
                    supportFragmentManager.beginTransaction()
                        .show(fragment2).hide(fragment1)
                        .commit()
                }
            }
            true
        }
    }
}
