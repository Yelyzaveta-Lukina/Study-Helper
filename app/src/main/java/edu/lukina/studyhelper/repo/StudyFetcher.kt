package edu.lukina.studyhelper.repo

/**
 * Created by Yelyzaveta Lukina on 09/30/2025.
 */

// Imports the Context class, which provides access to application-specific resources and classes.
import android.content.Context
// Imports the Uri class, used for building and parsing URI references.
import android.net.Uri
// Imports the Log class, used for sending log output.
import android.util.Log
// Imports the Request class from the Volley library, representing a network request.
import com.android.volley.Request
// Imports the VolleyError class, which represents an error that occurred during a Volley network request.
import com.android.volley.VolleyError
// Imports the JsonObjectRequest class,
// a Volley request for retrieving a JSONObject response body at a given URL.
import com.android.volley.toolbox.JsonObjectRequest
// Imports the Volley class, the main entry point for the Volley library.
import com.android.volley.toolbox.Volley
// Imports the Question data class from your project's model package.
import edu.lukina.studyhelper.model.Question
// Imports the Subject data class from your project's model package.
import edu.lukina.studyhelper.model.Subject
// Imports JSONException, an exception that indicates a problem with the JSON API.
import org.json.JSONException
// Imports JSONObject, a modifiable set of name/value mappings.
import org.json.JSONObject

// Defines a constant for the base URL of the web API.
const val WEBAPI_BASE_URL = "https://wp.zybooks.com/study-helper.php"
// Defines a constant for the log tag, used to identify log messages from this class.
const val TAG = "StudyFetcher"

// Declares the StudyFetcher class, which is responsible for fetching data from the web API.
class StudyFetcher(val context: Context) {

    // Defines an interface to be used as a callback for when study data is received.
    interface OnStudyDataReceivedListener {
        // A function to be called when a list of subjects has been successfully received.
        fun onSubjectsReceived(subjectList: List<Subject>)
        // A function to be called when questions for a specific subject have been successfully received.
        fun onQuestionsReceived(subject: Subject, questionList: List<Question>)
        // A function to be called when a network error occurs.
        fun onErrorResponse(error: VolleyError)
    }

    // Creates a new Volley request queue, which will manage the network requests for this class.
    private var requestQueue = Volley.newRequestQueue(context)

    // A function to initiate fetching the list of all subjects from the web API.
    fun fetchSubjects(listener: OnStudyDataReceivedListener) {
        // Builds the URL for the subjects request by adding a query parameter to the base URL.
        val url = Uri.parse(WEBAPI_BASE_URL).buildUpon()
            .appendQueryParameter("type", "subjects").build().toString()

        // Creates a new JsonObjectRequest to fetch the subjects.
        val request = JsonObjectRequest(
            // Specifies the HTTP request method, which is GET.
            Request.Method.GET, url, null,
            // Defines a lambda function to be executed on a successful response.
            // It calls onSubjectsReceived with the parsed JSON.
            { response -> listener.onSubjectsReceived(jsonToSubjects(response)) },
            // Defines a lambda function to be executed on an error response. It calls onErrorResponse.
            { error -> listener.onErrorResponse(error) })

        // Adds the created request to the request queue to be executed.
        requestQueue.add(request)
    }

    // A private function to parse a JSONObject and convert it into a list of Subject objects.
    private fun jsonToSubjects(json: JSONObject): List<Subject> {

        // Creates a new mutable list to hold the Subject objects.
        val subjectList = mutableListOf<Subject>()

        // Starts a try-catch block to handle potential JSON parsing errors.
        try {
            // Gets the "subjects" JSON array from the main JSON object.
            val subjectArray = json.getJSONArray("subjects")
            // Loops through each element in the JSON array.
            for (i in 0 until subjectArray.length()) {
                // Gets the JSON object at the current position in the array.
                val subjectObj = subjectArray.getJSONObject(i)
                // Creates a new Subject object using the data from the JSON object.
                val subject = Subject(
                    // Extracts the "subject" string value.
                    text = subjectObj.getString("subject"),
                    // Extracts the "updatetime" long value.
                    updateTime = subjectObj.getLong("updatetime"))
                // Adds the newly created Subject object to the list.
                subjectList.add(subject)
            }
            // Catches any JSONException that might occur during parsing.
        } catch (e: JSONException) {
            // Logs an error message if a field is missing from the JSON data.
            Log.e(TAG, "Field missing in the JSON data: ${e.message}")
        }

        // Returns the list of subjects.
        return subjectList
    }

    // A function to initiate fetching questions for a specific subject.
    fun fetchQuestions(subject: Subject, listener: OnStudyDataReceivedListener) {
        // Builds the URL for the questions request, including the subject name as a parameter.
        val url = Uri.parse(WEBAPI_BASE_URL).buildUpon()
            .appendQueryParameter("type", "questions")
            .appendQueryParameter("subject", subject.text)
            .build().toString()

        // Creates a new JsonObjectRequest to fetch the questions.
        val jsObjRequest = JsonObjectRequest(
            // Specifies the HTTP request method, which is GET.
            Request.Method.GET, url, null,
            // Defines a lambda for a successful response.
            // It calls onQuestionsReceived with the parsed questions.
            { response -> listener.onQuestionsReceived(subject, jsonToQuestions(response)) },
            // Defines a lambda for an error response. It calls onErrorResponse.
            { error -> listener.onErrorResponse(error) })

        // Adds the request to the queue to be executed.
        requestQueue.add(jsObjRequest)
    }

    // A private function to parse a JSONObject and convert it into a list of Question objects.
    private fun jsonToQuestions(json: JSONObject): List<Question> {

        // Creates a new mutable list to hold the Question objects.
        val questionList = mutableListOf<Question>()

        // Starts a try-catch block to handle potential JSON parsing errors.
        try {
            // Gets the "questions" JSON array from the main JSON object.
            val questionArray = json.getJSONArray("questions")
            // Loops through each element in the JSON array.
            for (i in 0 until questionArray.length()) {
                // Gets the JSON object at the current position in the array.
                val questionObj = questionArray.getJSONObject(i)
                // Creates a new Question object using the data from the JSON object.
                val question = Question(
                    // Extracts the "question" string value.
                    text = questionObj.getString("question"),
                    // Extracts the "answer" string value.
                    answer = questionObj.getString("answer"),
                    // Sets the subjectId to 0, as it will be assigned later when saved to the database.
                    subjectId = 0)
                // Adds the newly created Question object to the list.
                questionList.add(question)
            }
            // Catches any JSONException that might occur during parsing.
        } catch (e: JSONException) {
            // Logs an error message if a field is missing from the JSON data.
            Log.e(TAG, "Field missing in the JSON data: ${e.message}")
        }

        // Returns the list of questions.
        return questionList
    }
}
