import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kotlinapk.R
//import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : AppCompatActivity() {

    private val eventsList = mutableListOf<Event>()
    private lateinit var adapter: EventsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        adapter = EventsAdapter(eventsList)

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        fetchEvents()
    }

    private fun fetchEvents() {
        GlobalScope.launch(Dispatchers.IO) {
            val url = URL("http://panel.mait.ac.in:8002/events")
            val connection = url.openConnection() as HttpURLConnection
            try {
                val bufferedReader = BufferedReader(InputStreamReader(connection.inputStream))
                val stringBuilder = StringBuilder()
                var line: String?
                while (bufferedReader.readLine().also { line = it } != null) {
                    stringBuilder.append(line).append("\n")
                }
                bufferedReader.close()

                val jsonArray = JSONArray(stringBuilder.toString())
                for (i in 0 until jsonArray.length()) {
                    val jsonObject = jsonArray.getJSONObject(i)
                    val event = Event(
                        jsonObject.getString("title"),
                        jsonObject.getString("date"),
                        jsonObject.getString("description")
                    )
                    eventsList.add(event)
                }

                withContext(Dispatchers.Main) {
                    adapter.notifyDataSetChanged()
                }
            } finally {
                connection.disconnect()
            }
        }
    }
}
